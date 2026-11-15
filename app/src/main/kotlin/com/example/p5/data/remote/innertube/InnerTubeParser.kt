package com.example.p5.data.remote.innertube

import com.example.p5.data.config.MusicConfig
import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.MoodTag
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.PlaylistType
import com.example.p5.domain.model.Song
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

data class ParsedBrowse(
    val sections: List<HomeSection>,
    val moodTags: List<MoodTag>,
    val categories: List<Category>,
    val playlists: List<Playlist> = emptyList()
)

data class SearchPayload(
    val songs: List<Song>,
    val playlists: List<Playlist>
)

class InnerTubeParser {

    fun parseSearchPayload(jsonString: String, limit: Int): SearchPayload {
        val root = JSONObject(jsonString)
        val contents = root.optJSONObject("contents")
            ?.optJSONObject("twoColumnSearchResultsRenderer")
            ?.optJSONObject("primaryContents")
            ?.optJSONObject("sectionListRenderer")
            ?.optJSONArray("contents") ?: JSONArray()

        val songs = mutableListOf<Song>()
        val playlists = mutableListOf<Playlist>()

        for (i in 0 until contents.length()) {
            val section = contents.optJSONObject(i) ?: continue
            val itemSection = section.optJSONObject("itemSectionRenderer") ?: continue
            val items = itemSection.optJSONArray("contents") ?: continue

            for (j in 0 until items.length()) {
                val item = items.optJSONObject(j) ?: continue

                val pr = item.optJSONObject("playlistRenderer")
                if (pr != null && playlists.size < 5) {
                    val playlistId = pr.optString("playlistId")
                    if (playlistId.isNotBlank()) {
                        val title = pr.optJSONObject("title")?.optString("simpleText")
                            ?: pr.optJSONObject("title")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text")
                            ?: ""
                        if (title.isNotBlank()) {
                            val owner = pr.optJSONObject("shortBylineText")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text")
                                ?: pr.optJSONObject("longBylineText")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text")
                                ?: "YouTube Music"
                            val videoCount = pr.optString("videoCount")
                                ?: pr.optJSONObject("videoCountText")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text")
                                ?: ""
                            val subtitle = if (videoCount.isNotBlank()) "$owner • $videoCount" else owner

                            val thumbs = pr.optJSONObject("thumbnails")?.optJSONArray("thumbnails")
                                ?: pr.optJSONObject("thumbnail")?.optJSONArray("thumbnails")
                                ?: pr.optJSONArray("thumbnails")
                            val thumbUrl = if (thumbs != null && thumbs.length() > 0) {
                                thumbs.optJSONObject(thumbs.length() - 1)?.optString("url") ?: ""
                            } else ""

                            val type = when {
                                title.contains("mix", ignoreCase = true) || title.contains("radio", ignoreCase = true) -> PlaylistType.RANDOM
                                title.contains("pop", ignoreCase = true) || title.contains("rock", ignoreCase = true) ||
                                    title.contains("indie", ignoreCase = true) || title.contains("jazz", ignoreCase = true) ||
                                    title.contains("dangdut", ignoreCase = true) || title.contains("koplo", ignoreCase = true) -> PlaylistType.GENRE
                                else -> PlaylistType.ARTIST
                            }

                            playlists.add(
                                Playlist(
                                    id = playlistId,
                                    title = title,
                                    subtitle = subtitle,
                                    thumbnailUrl = thumbUrl,
                                    type = type,
                                    query = title
                                )
                            )
                        }
                    }
                }

                val vr = item.optJSONObject("videoRenderer") ?: continue
                if (songs.size >= limit) continue

                val videoId = vr.optString("videoId")
                if (videoId.isEmpty()) continue

                val titleRuns = vr.optJSONObject("title")?.optJSONArray("runs")
                val title = titleRuns?.optJSONObject(0)?.optString("text") ?: ""
                if (title.isEmpty()) continue

                val ownerRuns = vr.optJSONObject("ownerText")?.optJSONArray("runs")
                    ?: vr.optJSONObject("longBylineText")?.optJSONArray("runs")
                val artist = ownerRuns?.optJSONObject(0)?.optString("text") ?: "YouTube Music"

                val durationSimple = vr.optJSONObject("lengthText")?.optString("simpleText")
                    ?: vr.optJSONObject("lengthText")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text")
                    ?: ""
                val durationFormatted = if (durationSimple.isNotEmpty()) durationSimple.replace('.', ':') else "3:30"
                val durationSec = InnerTubeFormatter.parseDurationToSeconds(durationFormatted)

                val isLikelyCompilation = durationSec > 600 || (durationSec > 360 && (
                    title.contains("playlist", ignoreCase = true) ||
                    title.contains("full album", ignoreCase = true) ||
                    title.contains("kompilasi", ignoreCase = true) ||
                    title.contains("kumpulan lagu", ignoreCase = true) ||
                    title.contains("top hits spotify", ignoreCase = true) ||
                    title.contains("nonstop", ignoreCase = true)
                ))

                if (isLikelyCompilation) continue

                val thumbnails = vr.optJSONObject("thumbnail")?.optJSONArray("thumbnails")
                val thumbnailUrl = if (thumbnails != null && thumbnails.length() > 0) {
                    thumbnails.optJSONObject(thumbnails.length() - 1)?.optString("url")
                        ?: MusicConfig.THUMBNAIL_HQ_TEMPLATE.format(videoId)
                } else {
                    MusicConfig.THUMBNAIL_HQ_TEMPLATE.format(videoId)
                }

                val viewCountText = vr.optJSONObject("viewCountText")?.optString("simpleText")
                    ?: vr.optJSONObject("viewCountText")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text")
                    ?: ""
                val viewCount = viewCountText.replace("[^0-9]".toRegex(), "").toLongOrNull() ?: 0L

                songs.add(
                    Song(
                        id = videoId,
                        title = title,
                        artist = artist,
                        durationSeconds = durationSec,
                        durationFormatted = durationFormatted,
                        thumbnailUrl = thumbnailUrl,
                        webUrl = MusicConfig.WATCH_URL_TEMPLATE.format(videoId),
                        viewCount = viewCount
                    )
                )
            }
        }

        return SearchPayload(songs, playlists)
    }

    fun parseSearchResults(jsonString: String, limit: Int): List<Song> {
        return parseSearchPayload(jsonString, limit).songs
    }

    fun parseBrowseResponse(jsonString: String, prefixId: String): ParsedBrowse {
        val root = JSONObject(jsonString)
        val sections = mutableListOf<HomeSection>()
        val parsedMoodTags = mutableListOf<MoodTag>()
        val parsedCategories = mutableListOf<Category>()
        val parsedPlaylists = mutableListOf<Playlist>()

        val contentsRoot = root.optJSONObject("contents")

        val twoCol = contentsRoot?.optJSONObject("twoColumnBrowseResultsRenderer")
        if (twoCol != null) {
            val secContents = twoCol.optJSONObject("secondaryContents")
                ?.optJSONObject("sectionListRenderer")
                ?.optJSONArray("contents") ?: JSONArray()

            for (i in 0 until secContents.length()) {
                val playlistShelf = secContents.optJSONObject(i)?.optJSONObject("musicPlaylistShelfRenderer") ?: continue
                val items = playlistShelf.optJSONArray("contents") ?: continue
                val tracks = mutableListOf<Song>()

                for (j in 0 until items.length()) {
                    val item = items.optJSONObject(j)?.optJSONObject("musicResponsiveListItemRenderer") ?: continue
                    parseSongFromResponsive(item)?.let { tracks.add(it) }
                }

                if (tracks.isNotEmpty()) {
                    sections.add(
                        HomeSection(
                            id = "${prefixId}_$i",
                            title = "Top Charts Indonesia",
                            subtitle = "Tangga lagu resmi paling populer di Indonesia",
                            query = "Top Hits Indonesia",
                            tracks = tracks.take(20)
                        )
                    )
                }
            }
            return ParsedBrowse(sections, parsedMoodTags, parsedCategories, parsedPlaylists)
        }

        val tabs = contentsRoot
            ?.optJSONObject("singleColumnBrowseResultsRenderer")
            ?.optJSONArray("tabs") ?: JSONArray()

        val contents = if (tabs.length() > 0) {
            tabs.optJSONObject(0)
                ?.optJSONObject("tabRenderer")
                ?.optJSONObject("content")
                ?.optJSONObject("sectionListRenderer")
                ?.optJSONArray("contents") ?: JSONArray()
        } else {
            JSONArray()
        }

        for (i in 0 until contents.length()) {
            val sectionObj = contents.optJSONObject(i) ?: continue

            val grid = sectionObj.optJSONObject("gridRenderer")
            if (grid != null) {
                val gridHeader = grid.optJSONObject("header")?.optJSONObject("gridHeaderRenderer")
                val gridTitle = gridHeader?.optJSONObject("title")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: ""
                val isMood = gridTitle.contains("mood", ignoreCase = true) ||
                    gridTitle.contains("suasana", ignoreCase = true) ||
                    gridTitle.contains("momen", ignoreCase = true) ||
                    gridTitle.contains("aktivitas", ignoreCase = true) ||
                    InnerTubeFormatter.isExcludedMoodOrActivity(gridTitle)
                val items = grid.optJSONArray("items") ?: JSONArray()
                for (j in 0 until items.length()) {
                    val item = items.optJSONObject(j) ?: continue
                    val navButton = item.optJSONObject("musicNavigationButtonRenderer") ?: continue
                    val btnText = navButton.optJSONObject("buttonText")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: ""
                    if (btnText.isBlank()) continue

                    val stripeColor = navButton.optJSONObject("solid")?.optLong("leftStripeColor", 0L) ?: 0L
                    val colorHex = if (stripeColor != 0L) {
                        String.format(Locale.ROOT, "#%06X", 0xFFFFFF and stripeColor.toInt())
                    } else {
                        "#00E5BF"
                    }

                    val browseEndpoint = navButton.optJSONObject("clickCommand")?.optJSONObject("browseEndpoint")
                    val browseId = browseEndpoint?.optString("browseId") ?: ""
                    val params = browseEndpoint?.optString("params") ?: ""

                    val isItemMood = isMood || InnerTubeFormatter.isExcludedMoodOrActivity(btnText)
                    val itemId = if (isItemMood) "mood_${btnText.lowercase(Locale.ROOT).replace("\\s+".toRegex(), "_")}"
                    else "genre_${btnText.lowercase(Locale.ROOT).replace("\\s+".toRegex(), "_")}"
                    val icon = InnerTubeFormatter.mapMoodToIcon(btnText)

                    if (isItemMood) {
                        parsedMoodTags.add(
                            MoodTag(
                                id = itemId,
                                label = btnText,
                                query = "Lagu $btnText",
                                browseId = browseId,
                                params = params
                            )
                        )
                    } else {
                        parsedCategories.add(
                            Category(
                                id = itemId,
                                title = btnText,
                                query = "Lagu $btnText",
                                icon = icon,
                                startColor = colorHex,
                                endColor = "#1A1A1E",
                                browseId = browseId,
                                params = params
                            )
                        )
                    }
                }
                continue
            }

            val carousel = sectionObj.optJSONObject("musicCarouselShelfRenderer") ?: continue
            val header = carousel.optJSONObject("header")?.optJSONObject("musicCarouselShelfBasicHeaderRenderer")

            val title = header?.optJSONObject("title")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: ""
            var subtitle = header?.optJSONObject("strapline")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: ""

            if (subtitle.isBlank()) {
                subtitle = InnerTubeFormatter.getDefaultSubtitle(title)
            }

            val items = carousel.optJSONArray("contents") ?: continue
            val tracks = mutableListOf<Song>()

            for (j in 0 until items.length()) {
                val item = items.optJSONObject(j) ?: continue

                val navButton = item.optJSONObject("musicNavigationButtonRenderer")
                if (navButton != null) {
                    val btnText = navButton.optJSONObject("buttonText")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: ""
                    val stripeColor = navButton.optJSONObject("solid")?.optLong("leftStripeColor", 0L) ?: 0L
                    val colorHex = if (stripeColor != 0L) {
                        String.format(Locale.ROOT, "#%06X", 0xFFFFFF and stripeColor.toInt())
                    } else {
                        "#00E5BF"
                    }

                    val browseEndpoint = navButton.optJSONObject("clickCommand")?.optJSONObject("browseEndpoint")
                    val browseId = browseEndpoint?.optString("browseId") ?: ""
                    val params = browseEndpoint?.optString("params") ?: ""

                    if (btnText.isNotBlank()) {
                        val isItemMood = InnerTubeFormatter.isExcludedMoodOrActivity(btnText) ||
                            title.contains("mood", ignoreCase = true) ||
                            title.contains("suasana", ignoreCase = true) ||
                            title.contains("momen", ignoreCase = true) ||
                            title.contains("aktivitas", ignoreCase = true)
                        val moodId = "mood_${btnText.lowercase(Locale.ROOT).replace("\\s+".toRegex(), "_")}"
                        if (parsedMoodTags.size < 20) {
                            parsedMoodTags.add(
                                MoodTag(
                                    id = moodId,
                                    label = btnText,
                                    query = "Lagu $btnText",
                                    browseId = browseId,
                                    params = params
                                )
                            )
                        }
                        if (!isItemMood && parsedCategories.size < 30) {
                            val genreId = "genre_${btnText.lowercase(Locale.ROOT).replace("\\s+".toRegex(), "_")}"
                            val icon = InnerTubeFormatter.mapMoodToIcon(btnText)
                            parsedCategories.add(
                                Category(
                                    id = genreId,
                                    title = btnText,
                                    query = "Lagu $btnText",
                                    icon = icon,
                                    startColor = colorHex,
                                    endColor = "#1A1A1E",
                                    browseId = browseId,
                                    params = params
                                )
                            )
                        }
                    }
                    continue
                }

                val responsive = item.optJSONObject("musicResponsiveListItemRenderer")
                if (responsive != null) {
                    parseSongFromResponsive(responsive)?.let { tracks.add(it) }
                    continue
                }

                val twoRow = item.optJSONObject("musicTwoRowItemRenderer")
                if (twoRow != null) {
                    val navEndpoint = twoRow.optJSONObject("navigationEndpoint")
                    val browseEndpoint = navEndpoint?.optJSONObject("browseEndpoint")
                    val browseId = browseEndpoint?.optString("browseId") ?: ""

                    if (browseId.startsWith("VL") || browseId.startsWith("PL") || browseId.startsWith("RDEM")) {
                        val pTitle = twoRow.optJSONObject("title")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: ""
                        val pSubtitle = twoRow.optJSONObject("subtitle")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: "YouTube Music"
                        val pThumbs = twoRow.optJSONObject("thumbnailRenderer")
                            ?.optJSONObject("musicThumbnailRenderer")
                            ?.optJSONObject("thumbnail")
                            ?.optJSONArray("thumbnails")
                        val pThumbUrl = if (pThumbs != null && pThumbs.length() > 0) {
                            pThumbs.optJSONObject(pThumbs.length() - 1)?.optString("url") ?: ""
                        } else ""

                        if (pTitle.isNotBlank()) {
                            parsedPlaylists.add(
                                Playlist(
                                    id = browseId,
                                    title = pTitle,
                                    subtitle = pSubtitle,
                                    thumbnailUrl = pThumbUrl,
                                    type = when {
                                        pTitle.contains("mix", ignoreCase = true) || pTitle.contains("radio", ignoreCase = true) -> PlaylistType.RANDOM
                                        pTitle.contains("pop", ignoreCase = true) || pTitle.contains("rock", ignoreCase = true) ||
                                            pTitle.contains("indie", ignoreCase = true) || pTitle.contains("jazz", ignoreCase = true) ||
                                            pTitle.contains("dangdut", ignoreCase = true) || pTitle.contains("koplo", ignoreCase = true) -> PlaylistType.GENRE
                                        else -> PlaylistType.ARTIST
                                    },
                                    query = pTitle
                                )
                            )
                        }
                    } else {
                        parseSongFromTwoRow(twoRow)?.let { tracks.add(it) }
                    }
                    continue
                }
            }

            if (tracks.isNotEmpty() && title.isNotBlank()) {
                sections.add(
                    HomeSection(
                        id = "${prefixId}_$i",
                        title = title,
                        subtitle = subtitle,
                        query = title,
                        tracks = tracks
                    )
                )
            }
        }

        return ParsedBrowse(sections, parsedMoodTags, parsedCategories, parsedPlaylists)
    }

    fun parsePlaylistTracks(jsonString: String): List<Song> {
        val root = JSONObject(jsonString)
        val songs = mutableListOf<Song>()
        val contentsRoot = root.optJSONObject("contents")

        val twoCol = contentsRoot?.optJSONObject("twoColumnBrowseResultsRenderer")
        val secContents = twoCol?.optJSONObject("secondaryContents")
            ?.optJSONObject("sectionListRenderer")
            ?.optJSONArray("contents")

        if (secContents != null) {
            for (i in 0 until secContents.length()) {
                val playlistShelf = secContents.optJSONObject(i)?.optJSONObject("musicPlaylistShelfRenderer") ?: continue
                val items = playlistShelf.optJSONArray("contents") ?: continue
                for (j in 0 until items.length()) {
                    val item = items.optJSONObject(j)?.optJSONObject("musicResponsiveListItemRenderer") ?: continue
                    parseSongFromResponsive(item)?.let { songs.add(it) }
                }
            }
        }

        if (songs.isEmpty()) {
            val singleCol = contentsRoot?.optJSONObject("singleColumnBrowseResultsRenderer")
            val tabs = singleCol?.optJSONArray("tabs") ?: JSONArray()
            if (tabs.length() > 0) {
                val tabContents = tabs.optJSONObject(0)
                    ?.optJSONObject("tabRenderer")
                    ?.optJSONObject("content")
                    ?.optJSONObject("sectionListRenderer")
                    ?.optJSONArray("contents") ?: JSONArray()
                for (i in 0 until tabContents.length()) {
                    val playlistShelf = tabContents.optJSONObject(i)?.optJSONObject("musicPlaylistShelfRenderer") ?: continue
                    val items = playlistShelf.optJSONArray("contents") ?: continue
                    for (j in 0 until items.length()) {
                        val item = items.optJSONObject(j)?.optJSONObject("musicResponsiveListItemRenderer") ?: continue
                        parseSongFromResponsive(item)?.let { songs.add(it) }
                    }
                }
            }
        }

        val cleanSongs = songs.filter { song ->
            song.durationSeconds in 90..480 && !InnerTubeFormatter.isExcludedCompilation(song.title)
        }

        return if (cleanSongs.isNotEmpty()) cleanSongs else songs
    }

    fun parseSongFromResponsive(responsive: JSONObject): Song? {
        val flexColumns = responsive.optJSONArray("flexColumns") ?: return null
        if (flexColumns.length() == 0) return null

        val col0 = flexColumns.optJSONObject(0)?.optJSONObject("musicResponsiveListItemFlexColumnRenderer")
        val title = col0?.optJSONObject("text")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: ""
        if (title.isBlank()) return null

        var artist = "YouTube Music"
        var durationFormatted = "3:30"
        var viewCount = 0L

        if (flexColumns.length() > 1) {
            val col1 = flexColumns.optJSONObject(1)?.optJSONObject("musicResponsiveListItemFlexColumnRenderer")
            val runs = col1?.optJSONObject("text")?.optJSONArray("runs")
            if (runs != null) {
                val texts = mutableListOf<String>()
                for (k in 0 until runs.length()) {
                    val t = runs.optJSONObject(k)?.optString("text", "") ?: ""
                    if (t.isNotBlank() && t != " • ") {
                        texts.add(t)
                    }
                }
                if (texts.isNotEmpty()) {
                    artist = texts[0]
                }
                for (t in texts) {
                    if (t.matches(Regex("^\\d{1,2}:\\d{2}$"))) {
                        durationFormatted = t
                    } else if (t.contains("x ditonton", ignoreCase = true) || t.contains("views", ignoreCase = true) || t.contains("x diputar", ignoreCase = true)) {
                        val digits = t.replace("[^0-9]".toRegex(), "").toLongOrNull()
                        if (digits != null) {
                            viewCount = if (t.contains("jt", ignoreCase = true) || t.contains("m", ignoreCase = true)) digits * 1_000_000L
                            else if (t.contains("rb", ignoreCase = true) || t.contains("k", ignoreCase = true)) digits * 1_000L
                            else digits
                        }
                    }
                }
            }
        }

        var videoId = responsive.optJSONObject("playlistItemData")?.optString("videoId") ?: ""
        if (videoId.isBlank()) {
            videoId = responsive.optJSONObject("overlay")
                ?.optJSONObject("musicItemThumbnailOverlayRenderer")
                ?.optJSONObject("content")
                ?.optJSONObject("musicPlayButtonRenderer")
                ?.optJSONObject("playNavigationEndpoint")
                ?.optJSONObject("watchEndpoint")
                ?.optString("videoId") ?: ""
        }
        if (videoId.isBlank()) return null

        val thumbs = responsive.optJSONObject("thumbnail")
            ?.optJSONObject("musicThumbnailRenderer")
            ?.optJSONObject("thumbnail")
            ?.optJSONArray("thumbnails")

        val thumbnailUrl = if (thumbs != null && thumbs.length() > 0) {
            thumbs.optJSONObject(thumbs.length() - 1)?.optString("url")
                ?: MusicConfig.THUMBNAIL_HQ_TEMPLATE.format(videoId)
        } else {
            MusicConfig.THUMBNAIL_HQ_TEMPLATE.format(videoId)
        }

        val durationSec = InnerTubeFormatter.parseDurationToSeconds(durationFormatted)

        return Song(
            id = videoId,
            title = title,
            artist = artist,
            durationSeconds = durationSec,
            durationFormatted = durationFormatted,
            thumbnailUrl = thumbnailUrl,
            webUrl = MusicConfig.WATCH_URL_TEMPLATE.format(videoId),
            viewCount = viewCount
        )
    }

    fun parseSongFromTwoRow(twoRow: JSONObject): Song? {
        val title = twoRow.optJSONObject("title")?.optJSONArray("runs")?.optJSONObject(0)?.optString("text") ?: ""
        if (title.isBlank()) return null

        val subRuns = twoRow.optJSONObject("subtitle")?.optJSONArray("runs")
        val artist = subRuns?.optJSONObject(0)?.optString("text") ?: "YouTube Music"

        var videoId = twoRow.optJSONObject("navigationEndpoint")
            ?.optJSONObject("watchEndpoint")
            ?.optString("videoId") ?: ""

        if (videoId.isBlank()) {
            videoId = twoRow.optJSONObject("thumbnailOverlay")
                ?.optJSONObject("musicItemThumbnailOverlayRenderer")
                ?.optJSONObject("content")
                ?.optJSONObject("musicPlayButtonRenderer")
                ?.optJSONObject("playNavigationEndpoint")
                ?.optJSONObject("watchEndpoint")
                ?.optString("videoId") ?: ""
        }

        if (videoId.isBlank()) return null

        val thumbs = twoRow.optJSONObject("thumbnailRenderer")
            ?.optJSONObject("musicThumbnailRenderer")
            ?.optJSONObject("thumbnail")
            ?.optJSONArray("thumbnails")

        val thumbnailUrl = if (thumbs != null && thumbs.length() > 0) {
            thumbs.optJSONObject(thumbs.length() - 1)?.optString("url")
                ?: MusicConfig.THUMBNAIL_HQ_TEMPLATE.format(videoId)
        } else {
            MusicConfig.THUMBNAIL_HQ_TEMPLATE.format(videoId)
        }

        return Song(
            id = videoId,
            title = title,
            artist = artist,
            durationSeconds = 210,
            durationFormatted = "3:30",
            thumbnailUrl = thumbnailUrl,
            webUrl = MusicConfig.WATCH_URL_TEMPLATE.format(videoId)
        )
    }

    fun parsePlayerResponse(videoId: String, jsonString: String): AudioStream {
        val root = JSONObject(jsonString)
        val playabilityStatus = root.optJSONObject("playabilityStatus")
        val status = playabilityStatus?.optString("status") ?: ""
        if (status != "OK") {
            val reason = playabilityStatus?.optString("reason") ?: "Stream unavailable ($status)"
            throw IllegalStateException(reason)
        }

        val streamingData = root.optJSONObject("streamingData") ?: throw IllegalStateException("No streamingData found")
        val adaptiveFormats = streamingData.optJSONArray("adaptiveFormats") ?: JSONArray()

        var bestAudioUrl: String? = null
        var bestBitrate: Double? = null
        var bestFormat: String = "m4a"

        for (i in 0 until adaptiveFormats.length()) {
            val formatObj = adaptiveFormats.getJSONObject(i)
            val mimeType = formatObj.optString("mimeType", "")
            if (!mimeType.contains("audio")) continue

            val url = formatObj.optString("url")
            if (url.isNullOrEmpty()) continue

            val itag = formatObj.optInt("itag", 0)
            val bitrate = formatObj.optDouble("bitrate", 0.0)

            if (itag == 140) {
                bestAudioUrl = url
                bestBitrate = bitrate
                bestFormat = "m4a"
                break
            } else if (bestAudioUrl == null || bitrate > (bestBitrate ?: 0.0)) {
                bestAudioUrl = url
                bestBitrate = bitrate
                bestFormat = if (mimeType.contains("mp4")) "m4a" else "webm"
            }
        }

        val finalUrl = bestAudioUrl ?: throw IllegalStateException("No playable direct audio stream found")

        val videoDetails = root.optJSONObject("videoDetails")
        val title = videoDetails?.optString("title") ?: "Unknown Title"
        val author = videoDetails?.optString("author") ?: "YouTube Music"
        val lengthSeconds = videoDetails?.optString("lengthSeconds")?.toIntOrNull() ?: 0
        val thumbUrl = MusicConfig.THUMBNAIL_HQ_TEMPLATE.format(videoId)

        return AudioStream(
            id = videoId,
            title = title,
            artist = author,
            streamUrl = finalUrl,
            durationSeconds = lengthSeconds,
            durationFormatted = InnerTubeFormatter.formatSeconds(lengthSeconds),
            thumbnailUrl = thumbUrl,
            format = bestFormat,
            bitrate = bestBitrate
        )
    }
}
