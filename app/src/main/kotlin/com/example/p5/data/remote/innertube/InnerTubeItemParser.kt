package com.example.p5.data.remote.innertube

import com.example.p5.data.config.MusicConfig
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.PlaylistType
import com.example.p5.domain.model.Song
import org.json.JSONObject

class InnerTubeItemParser {

    fun parseSongFromResponsive(responsive: JSONObject): Song? {
        val flexColumns = responsive.optJSONArray("flexColumns") ?: return null
        if (flexColumns.length() == 0) return null

        val col0 = flexColumns.optJSONObject(0)?.optJSONObject("musicResponsiveListItemFlexColumnRenderer")
        val title = InnerTubeJsonHelper.extractFirstRunText(col0, "text")
        if (title.isBlank()) return null

        var artist = "YouTube Music"
        var durationFormatted = ""
        var viewCount = 0L
        var channelId: String? = null

        val fixedColumns = responsive.optJSONArray("fixedColumns")
        if (fixedColumns != null) {
            for (i in 0 until fixedColumns.length()) {
                val fixedCol = fixedColumns.optJSONObject(i)?.optJSONObject("musicResponsiveListItemFixedColumnRenderer")
                val fixedRuns = fixedCol?.optJSONObject("text")?.optJSONArray("runs")
                if (fixedRuns != null) {
                    for (k in 0 until fixedRuns.length()) {
                        val text = fixedRuns.optJSONObject(k)?.optString("text")?.trim().orEmpty()
                        if (InnerTubeJsonHelper.isDurationFormat(text)) {
                            durationFormatted = text
                            break
                        }
                    }
                }
                if (durationFormatted.isNotBlank()) break
            }
        }

        for (colIdx in 1 until flexColumns.length()) {
            val col = flexColumns.optJSONObject(colIdx)?.optJSONObject("musicResponsiveListItemFlexColumnRenderer")
            val runs = col?.optJSONObject("text")?.optJSONArray("runs") ?: continue
            val texts = mutableListOf<String>()
            for (k in 0 until runs.length()) {
                val runObj = runs.optJSONObject(k) ?: continue
                val t = runObj.optString("text", "").trim()
                if (t.isNotBlank() && t != "•") {
                    texts.add(t)
                }
                if (channelId == null) {
                    val bId = runObj.optJSONObject("navigationEndpoint")?.optJSONObject("browseEndpoint")?.optString("browseId")
                    if (InnerTubeJsonHelper.isArtistChannelId(bId)) {
                        channelId = bId
                    }
                }
            }

            if (colIdx == 1 && texts.isNotEmpty() && artist == "YouTube Music") {
                val candidateArtist = texts.firstOrNull { isCandidateArtistName(it) }
                if (!candidateArtist.isNullOrBlank()) {
                    artist = candidateArtist
                }
            }

            for (t in texts) {
                if (durationFormatted.isBlank() && InnerTubeJsonHelper.isDurationFormat(t)) {
                    durationFormatted = t
                } else if (viewCount == 0L && InnerTubeJsonHelper.isViewCountText(t)) {
                    viewCount = InnerTubeFormatter.parseViewCount(t)
                }
            }
        }

        if (durationFormatted.isBlank()) {
            val lengthText = InnerTubeJsonHelper.extractSimpleOrFirstRunText(responsive.optJSONObject("lengthText"))
            if (lengthText.isNotBlank()) {
                durationFormatted = lengthText.replace('.', ':').trim()
            }
        }

        var videoId = responsive.optJSONObject("playlistItemData")?.optString("videoId").orEmpty()
        if (videoId.isBlank()) {
            videoId = responsive.optJSONObject("overlay")
                ?.optJSONObject("musicItemThumbnailOverlayRenderer")
                ?.optJSONObject("content")
                ?.optJSONObject("musicPlayButtonRenderer")
                ?.optJSONObject("playNavigationEndpoint")
                ?.optJSONObject("watchEndpoint")
                ?.optString("videoId").orEmpty()
        }
        if (videoId.isBlank()) return null

        val thumbnailUrl = InnerTubeJsonHelper.extractMusicThumbnailUrl(responsive, "thumbnail", videoId)
        val durationSec = InnerTubeFormatter.parseDurationToSeconds(durationFormatted)

        return Song(
            id = videoId,
            title = title,
            artist = artist,
            durationSeconds = durationSec,
            durationFormatted = durationFormatted,
            thumbnailUrl = thumbnailUrl,
            webUrl = MusicConfig.WATCH_URL_TEMPLATE.format(videoId),
            viewCount = viewCount,
            channelId = channelId
        )
    }

    fun parseSongFromTwoRow(twoRow: JSONObject): Song? {
        val title = InnerTubeJsonHelper.extractFirstRunText(twoRow, "title")
        if (title.isBlank()) return null

        var artist = "YouTube Music"
        var channelId: String? = null
        var durationFormatted = ""
        var viewCount = 0L

        val subRuns = twoRow.optJSONObject("subtitle")?.optJSONArray("runs")
        if (subRuns != null) {
            val texts = mutableListOf<String>()
            for (i in 0 until subRuns.length()) {
                val runObj = subRuns.optJSONObject(i) ?: continue
                val text = runObj.optString("text", "").trim()
                if (text.isNotBlank() && text != "•") {
                    texts.add(text)
                }
                if (channelId == null) {
                    val bId = runObj.optJSONObject("navigationEndpoint")?.optJSONObject("browseEndpoint")?.optString("browseId")
                    if (InnerTubeJsonHelper.isArtistChannelId(bId)) {
                        channelId = bId
                    }
                }
            }

            val candidateArtist = texts.firstOrNull { isCandidateArtistName(it) }
            if (!candidateArtist.isNullOrBlank()) {
                artist = candidateArtist
            }

            for (text in texts) {
                if (durationFormatted.isBlank() && InnerTubeJsonHelper.isDurationFormat(text)) {
                    durationFormatted = text
                } else if (viewCount == 0L && InnerTubeJsonHelper.isViewCountText(text)) {
                    viewCount = InnerTubeFormatter.parseViewCount(text)
                }
            }
        }

        if (channelId == null) {
            channelId = twoRow.optJSONObject("navigationEndpoint")?.optJSONObject("browseEndpoint")?.optString("browseId")
        }

        if (durationFormatted.isBlank()) {
            val overlayTime = twoRow.optJSONObject("thumbnailOverlay")
                ?.optJSONObject("thumbnailOverlayTimeStatusRenderer")
                ?.optJSONObject("text")
                ?.optString("simpleText")
            if (!overlayTime.isNullOrBlank()) {
                durationFormatted = overlayTime.replace('.', ':').trim()
            }
        }

        var videoId = twoRow.optJSONObject("navigationEndpoint")
            ?.optJSONObject("watchEndpoint")
            ?.optString("videoId").orEmpty()

        if (videoId.isBlank()) {
            videoId = twoRow.optJSONObject("thumbnailOverlay")
                ?.optJSONObject("musicItemThumbnailOverlayRenderer")
                ?.optJSONObject("content")
                ?.optJSONObject("musicPlayButtonRenderer")
                ?.optJSONObject("playNavigationEndpoint")
                ?.optJSONObject("watchEndpoint")
                ?.optString("videoId").orEmpty()
        }

        if (videoId.isBlank()) return null

        val thumbnailUrl = InnerTubeJsonHelper.extractMusicThumbnailUrl(twoRow, "thumbnailRenderer", videoId)
        val durationSec = InnerTubeFormatter.parseDurationToSeconds(durationFormatted)

        return Song(
            id = videoId,
            title = title,
            artist = artist,
            durationSeconds = durationSec,
            durationFormatted = durationFormatted,
            thumbnailUrl = thumbnailUrl,
            webUrl = MusicConfig.WATCH_URL_TEMPLATE.format(videoId),
            viewCount = viewCount,
            channelId = channelId
        )
    }

    fun parseSongFromVideoRenderer(vr: JSONObject): Song? {
        val videoId = vr.optString("videoId")
        if (videoId.isEmpty()) return null

        val title = InnerTubeJsonHelper.extractFirstRunText(vr, "title")
        if (title.isEmpty()) return null

        val ownerRuns = vr.optJSONObject("ownerText")?.optJSONArray("runs")
            ?: vr.optJSONObject("longBylineText")?.optJSONArray("runs")
        val artist = ownerRuns?.optJSONObject(0)?.optString("text") ?: "YouTube Music"

        val durationSimple = InnerTubeJsonHelper.extractSimpleOrFirstRunText(vr.optJSONObject("lengthText"))
        val durationFormatted = if (durationSimple.isNotEmpty()) durationSimple.replace('.', ':').trim() else ""
        val durationSec = InnerTubeFormatter.parseDurationToSeconds(durationFormatted)

        val isLikelyCompilation = durationSec > 600 || (durationSec > 360 && (
            title.contains("playlist", ignoreCase = true) ||
                title.contains("full album", ignoreCase = true) ||
                title.contains("kompilasi", ignoreCase = true) ||
                title.contains("kumpulan lagu", ignoreCase = true) ||
                title.contains("top hits spotify", ignoreCase = true) ||
                title.contains("nonstop", ignoreCase = true)
            ))

        if (isLikelyCompilation) return null

        val thumbnails = vr.optJSONObject("thumbnail")?.optJSONArray("thumbnails")
        val thumbnailUrl = InnerTubeJsonHelper.extractLastThumbnailUrl(thumbnails, videoId)

        val viewCountText = InnerTubeJsonHelper.extractSimpleOrFirstRunText(vr.optJSONObject("viewCountText"))
        val viewCount = InnerTubeFormatter.parseViewCount(viewCountText)

        val channelId = vr.optJSONObject("ownerText")?.optJSONArray("runs")?.optJSONObject(0)?.optJSONObject("navigationEndpoint")?.optJSONObject("browseEndpoint")?.optString("browseId")
            ?: vr.optJSONObject("longBylineText")?.optJSONArray("runs")?.optJSONObject(0)?.optJSONObject("navigationEndpoint")?.optJSONObject("browseEndpoint")?.optString("browseId")

        return Song(
            id = videoId,
            title = title,
            artist = artist,
            durationSeconds = durationSec,
            durationFormatted = durationFormatted,
            thumbnailUrl = thumbnailUrl,
            webUrl = MusicConfig.WATCH_URL_TEMPLATE.format(videoId),
            viewCount = viewCount,
            channelId = channelId
        )
    }

    fun parsePlaylistFromPlaylistRenderer(pr: JSONObject): Playlist? {
        val playlistId = pr.optString("playlistId")
        if (playlistId.isBlank()) return null

        val title = InnerTubeJsonHelper.extractSimpleOrFirstRunText(pr.optJSONObject("title"))
        if (title.isBlank()) return null

        val owner = InnerTubeJsonHelper.extractFirstRunText(pr, "shortBylineText")
            .ifBlank { InnerTubeJsonHelper.extractFirstRunText(pr, "longBylineText") }
            .ifBlank { "YouTube Music" }

        val videoCount = pr.optString("videoCount")
            .ifBlank { InnerTubeJsonHelper.extractFirstRunText(pr, "videoCountText") }

        val subtitle = if (videoCount.isNotBlank()) "$owner • $videoCount" else owner

        val thumbs = pr.optJSONObject("thumbnails")?.optJSONArray("thumbnails")
            ?: pr.optJSONObject("thumbnail")?.optJSONArray("thumbnails")
            ?: pr.optJSONArray("thumbnails")
        val thumbUrl = InnerTubeJsonHelper.extractLastThumbnailUrl(thumbs)

        return Playlist(
            id = playlistId,
            title = title,
            subtitle = subtitle,
            thumbnailUrl = thumbUrl,
            type = InnerTubeJsonHelper.inferPlaylistType(title),
            query = title
        )
    }

    fun parsePlaylistFromResponsive(responsive: JSONObject): Playlist? {
        val browseId = responsive.optJSONObject("navigationEndpoint")
            ?.optJSONObject("browseEndpoint")
            ?.optString("browseId").orEmpty()
        if (!InnerTubeJsonHelper.isSearchPlaylistBrowseId(browseId)) return null

        val flexColumns = responsive.optJSONArray("flexColumns")
        val col0 = flexColumns?.optJSONObject(0)?.optJSONObject("musicResponsiveListItemFlexColumnRenderer")
        val title = InnerTubeJsonHelper.extractFirstRunText(col0, "text")
        if (title.isBlank()) return null

        val runs = flexColumns?.optJSONObject(1)
            ?.optJSONObject("musicResponsiveListItemFlexColumnRenderer")
            ?.optJSONObject("text")
            ?.optJSONArray("runs")
        val subtitleBuilder = StringBuilder()
        if (runs != null) {
            for (r in 0 until runs.length()) {
                subtitleBuilder.append(runs.optJSONObject(r)?.optString("text").orEmpty())
            }
        }
        val subtitle = if (subtitleBuilder.isNotBlank()) subtitleBuilder.toString() else "YouTube Music"
        val thumbUrl = InnerTubeJsonHelper.extractMusicThumbnailUrl(responsive, "thumbnail")

        return Playlist(
            id = browseId,
            title = title,
            subtitle = subtitle,
            thumbnailUrl = thumbUrl,
            type = InnerTubeJsonHelper.inferPlaylistType(title),
            query = title
        )
    }

    fun parsePlaylistFromTwoRow(
        twoRow: JSONObject,
        browseId: String,
        defaultSubtitle: String = "YouTube Music",
        forcedType: PlaylistType? = null
    ): Playlist? {
        val title = InnerTubeJsonHelper.extractFirstRunText(twoRow, "title")
        if (title.isBlank()) return null

        val subtitle = InnerTubeJsonHelper.extractFirstRunText(twoRow, "subtitle")
            .ifBlank { defaultSubtitle }
        val thumbUrl = InnerTubeJsonHelper.extractMusicThumbnailUrl(twoRow, "thumbnailRenderer")

        return Playlist(
            id = browseId,
            title = title,
            subtitle = subtitle,
            thumbnailUrl = thumbUrl,
            type = forcedType ?: InnerTubeJsonHelper.inferPlaylistType(title),
            query = title
        )
    }

    private fun isCandidateArtistName(text: String): Boolean {
        return text != "Lagu" &&
            text != "Song" &&
            text != "Video" &&
            !InnerTubeJsonHelper.isDurationFormat(text) &&
            !InnerTubeJsonHelper.isViewCountText(text)
    }
}
