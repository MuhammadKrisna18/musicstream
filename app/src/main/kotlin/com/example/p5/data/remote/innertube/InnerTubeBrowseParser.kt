package com.example.p5.data.remote.innertube

import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.HomeSectionStyle
import com.example.p5.domain.model.MoodTag
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.Song
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

class InnerTubeBrowseParser(
    private val itemParser: InnerTubeItemParser
) {

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
                    itemParser.parseSongFromResponsive(item)?.let { tracks.add(it) }
                }

                if (tracks.isNotEmpty()) {
                    sections.add(
                        HomeSection(
                            id = "${prefixId}_$i",
                            title = "Top Charts Indonesia",
                            subtitle = "Tangga lagu resmi paling populer di Indonesia",
                            query = "Top Hits Indonesia",
                            tracks = tracks.take(20),
                            style = HomeSectionStyle.RANKED_CHARTS
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
                parseGridSection(grid, parsedMoodTags, parsedCategories)
                continue
            }

            val carousel = sectionObj.optJSONObject("musicCarouselShelfRenderer") ?: continue
            val header = carousel.optJSONObject("header")?.optJSONObject("musicCarouselShelfBasicHeaderRenderer")

            val title = InnerTubeJsonHelper.extractFirstRunText(header, "title")
            var subtitle = InnerTubeJsonHelper.extractFirstRunText(header, "strapline")

            if (subtitle.isBlank()) {
                subtitle = InnerTubeFormatter.getDefaultSubtitle(title)
            }

            val items = carousel.optJSONArray("contents") ?: continue
            val tracks = mutableListOf<Song>()

            for (j in 0 until items.length()) {
                val item = items.optJSONObject(j) ?: continue

                val navButton = item.optJSONObject("musicNavigationButtonRenderer")
                if (navButton != null) {
                    parseCarouselNavButton(navButton, title, parsedMoodTags, parsedCategories)
                    continue
                }

                val responsive = item.optJSONObject("musicResponsiveListItemRenderer")
                if (responsive != null) {
                    itemParser.parseSongFromResponsive(responsive)?.let { tracks.add(it) }
                    continue
                }

                val twoRow = item.optJSONObject("musicTwoRowItemRenderer")
                if (twoRow != null) {
                    val browseId = twoRow.optJSONObject("navigationEndpoint")
                        ?.optJSONObject("browseEndpoint")
                        ?.optString("browseId").orEmpty()

                    if (InnerTubeJsonHelper.isBrowsePlaylistId(browseId)) {
                        itemParser.parsePlaylistFromTwoRow(twoRow, browseId)?.let { parsedPlaylists.add(it) }
                    } else {
                        itemParser.parseSongFromTwoRow(twoRow)?.let { tracks.add(it) }
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
                        tracks = tracks,
                        style = inferSectionStyle(title, prefixId)
                    )
                )
            }
        }

        return ParsedBrowse(sections, parsedMoodTags, parsedCategories, parsedPlaylists)
    }

    private fun inferSectionStyle(title: String, prefixId: String): HomeSectionStyle {
        val lower = title.lowercase(Locale.ROOT)
        return when {
            prefixId == "chart" ||
                lower.contains("chart") ||
                lower.contains("tangga") ||
                lower.contains("top ") ||
                lower.contains("trending") -> HomeSectionStyle.RANKED_CHARTS
            lower.contains("cepat") ||
                lower.contains("quick") ||
                lower.contains("pilihan") ||
                lower.contains("dengarkan lagi") ||
                lower.contains("listen again") -> HomeSectionStyle.QUICK_PICKS_GRID
            else -> HomeSectionStyle.STANDARD_CARDS
        }
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
            extractSongsFromPlaylistShelves(secContents, songs)
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
                extractSongsFromPlaylistShelves(tabContents, songs)
            }
        }

        val cleanSongs = songs.filter { song ->
            song.durationSeconds in 90..480 && !InnerTubeFormatter.isExcludedCompilation(song.title)
        }

        return if (cleanSongs.isNotEmpty()) cleanSongs else songs
    }

    private fun extractSongsFromPlaylistShelves(shelvesContainer: JSONArray, destination: MutableList<Song>) {
        for (i in 0 until shelvesContainer.length()) {
            val playlistShelf = shelvesContainer.optJSONObject(i)?.optJSONObject("musicPlaylistShelfRenderer") ?: continue
            val items = playlistShelf.optJSONArray("contents") ?: continue
            for (j in 0 until items.length()) {
                val item = items.optJSONObject(j)?.optJSONObject("musicResponsiveListItemRenderer") ?: continue
                itemParser.parseSongFromResponsive(item)?.let { destination.add(it) }
            }
        }
    }

    private fun parseGridSection(
        grid: JSONObject,
        parsedMoodTags: MutableList<MoodTag>,
        parsedCategories: MutableList<Category>
    ) {
        val gridHeader = grid.optJSONObject("header")?.optJSONObject("gridHeaderRenderer")
        val gridTitle = InnerTubeJsonHelper.extractFirstRunText(gridHeader, "title")
        val isMood = isMoodHeaderTitle(gridTitle) || InnerTubeFormatter.isExcludedMoodOrActivity(gridTitle)
        val items = grid.optJSONArray("items") ?: JSONArray()

        for (j in 0 until items.length()) {
            val item = items.optJSONObject(j) ?: continue
            val navButton = item.optJSONObject("musicNavigationButtonRenderer") ?: continue
            val btnText = InnerTubeJsonHelper.extractFirstRunText(navButton, "buttonText")
            if (btnText.isBlank()) continue

            val stripeColor = navButton.optJSONObject("solid")?.optLong("leftStripeColor", 0L) ?: 0L
            val colorHex = InnerTubeJsonHelper.formatStripeColorHex(stripeColor)

            val browseEndpoint = navButton.optJSONObject("clickCommand")?.optJSONObject("browseEndpoint")
            val browseId = browseEndpoint?.optString("browseId").orEmpty()
            val params = browseEndpoint?.optString("params").orEmpty()

            val isItemMood = isMood || InnerTubeFormatter.isExcludedMoodOrActivity(btnText)
            val slug = btnText.lowercase(Locale.ROOT).replace("\\s+".toRegex(), "_")
            val itemId = if (isItemMood) "mood_$slug" else "genre_$slug"
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
    }

    private fun parseCarouselNavButton(
        navButton: JSONObject,
        carouselTitle: String,
        parsedMoodTags: MutableList<MoodTag>,
        parsedCategories: MutableList<Category>
    ) {
        val btnText = InnerTubeJsonHelper.extractFirstRunText(navButton, "buttonText")
        if (btnText.isBlank()) return

        val stripeColor = navButton.optJSONObject("solid")?.optLong("leftStripeColor", 0L) ?: 0L
        val colorHex = InnerTubeJsonHelper.formatStripeColorHex(stripeColor)

        val browseEndpoint = navButton.optJSONObject("clickCommand")?.optJSONObject("browseEndpoint")
        val browseId = browseEndpoint?.optString("browseId").orEmpty()
        val params = browseEndpoint?.optString("params").orEmpty()

        val isItemMood = InnerTubeFormatter.isExcludedMoodOrActivity(btnText) || isMoodHeaderTitle(carouselTitle)
        val slug = btnText.lowercase(Locale.ROOT).replace("\\s+".toRegex(), "_")

        if (parsedMoodTags.size < 20) {
            parsedMoodTags.add(
                MoodTag(
                    id = "mood_$slug",
                    label = btnText,
                    query = "Lagu $btnText",
                    browseId = browseId,
                    params = params
                )
            )
        }
        if (!isItemMood && parsedCategories.size < 30) {
            parsedCategories.add(
                Category(
                    id = "genre_$slug",
                    title = btnText,
                    query = "Lagu $btnText",
                    icon = InnerTubeFormatter.mapMoodToIcon(btnText),
                    startColor = colorHex,
                    endColor = "#1A1A1E",
                    browseId = browseId,
                    params = params
                )
            )
        }
    }

    private fun isMoodHeaderTitle(title: String): Boolean {
        return title.contains("mood", ignoreCase = true) ||
            title.contains("suasana", ignoreCase = true) ||
            title.contains("momen", ignoreCase = true) ||
            title.contains("aktivitas", ignoreCase = true)
    }
}
