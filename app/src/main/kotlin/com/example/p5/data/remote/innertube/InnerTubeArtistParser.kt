package com.example.p5.data.remote.innertube

import com.example.p5.domain.model.ArtistProfile
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.PlaylistType
import com.example.p5.domain.model.Song
import org.json.JSONArray
import org.json.JSONObject

class InnerTubeArtistParser(
    private val itemParser: InnerTubeItemParser
) {

    fun parseArtistProfile(browseId: String, jsonString: String): ArtistProfile {
        val root = JSONObject(jsonString)
        val header = root.optJSONObject("header")
        val headerObj = header?.optJSONObject("musicImmersiveHeaderRenderer")
            ?: header?.optJSONObject("musicVisualHeaderRenderer")

        val name = InnerTubeJsonHelper.extractFirstRunText(headerObj, "title")
            .ifBlank { root.optJSONObject("microformat")?.optJSONObject("microformatDataRenderer")?.optString("title").orEmpty() }
            .ifBlank { "Artist" }

        val description = InnerTubeJsonHelper.extractFirstRunText(headerObj, "description")
        val avatarUrl = InnerTubeJsonHelper.extractMusicThumbnailUrl(headerObj, "thumbnail")

        val subButton = headerObj?.optJSONObject("subscriptionButton")?.optJSONObject("subscribeButtonRenderer")
        val subsText = InnerTubeJsonHelper.extractSimpleOrFirstRunText(subButton?.optJSONObject("subscriberCountText"))

        val topSongs = mutableListOf<Song>()
        val albums = mutableListOf<Playlist>()
        val singles = mutableListOf<Playlist>()
        var allSongsPlaylistId: String? = null

        val singleCol = root.optJSONObject("contents")?.optJSONObject("singleColumnBrowseResultsRenderer")
        val tabs = singleCol?.optJSONArray("tabs") ?: JSONArray()
        val tabContent = if (tabs.length() > 0) {
            tabs.optJSONObject(0)
                ?.optJSONObject("tabRenderer")
                ?.optJSONObject("content")
                ?.optJSONObject("sectionListRenderer")
        } else {
            null
        }
        val sections = tabContent?.optJSONArray("contents") ?: JSONArray()

        for (i in 0 until sections.length()) {
            val section = sections.optJSONObject(i) ?: continue

            val shelf = section.optJSONObject("musicShelfRenderer")
            if (shelf != null) {
                if (allSongsPlaylistId == null) {
                    allSongsPlaylistId = shelf.optJSONObject("bottomEndpoint")
                        ?.optJSONObject("browseEndpoint")
                        ?.optString("browseId")
                }
                val shelfItems = shelf.optJSONArray("contents") ?: JSONArray()
                for (j in 0 until shelfItems.length()) {
                    val item = shelfItems.optJSONObject(j)?.optJSONObject("musicResponsiveListItemRenderer") ?: continue
                    itemParser.parseSongFromResponsive(item)?.let { song ->
                        if (topSongs.none { it.id == song.id }) {
                            topSongs.add(song)
                        }
                    }
                }
            }

            val carousel = section.optJSONObject("musicCarouselShelfRenderer")
            if (carousel != null) {
                parseArtistCarousel(carousel, name, topSongs, albums, singles)
            }
        }

        return ArtistProfile(
            id = browseId,
            name = name,
            avatarUrl = avatarUrl,
            bannerUrl = avatarUrl,
            description = description,
            subscriberCount = subsText,
            topSongs = topSongs,
            albums = albums,
            singles = singles,
            allSongsPlaylistId = allSongsPlaylistId
        )
    }

    private fun parseArtistCarousel(
        carousel: JSONObject,
        artistName: String,
        topSongs: MutableList<Song>,
        albums: MutableList<Playlist>,
        singles: MutableList<Playlist>
    ) {
        val headerBasic = carousel.optJSONObject("header")?.optJSONObject("musicCarouselShelfBasicHeaderRenderer")
        val carouselTitle = InnerTubeJsonHelper.extractFirstRunText(headerBasic, "title")
        val carouselItems = carousel.optJSONArray("contents") ?: JSONArray()

        val isAlbumSection = carouselTitle.contains("Album", ignoreCase = true)
        val isSingleSection = carouselTitle.contains("Single", ignoreCase = true) || carouselTitle.contains("EP", ignoreCase = true)
        val isVideoSection = carouselTitle.contains("Video", ignoreCase = true) || carouselTitle.contains("Lagu", ignoreCase = true)

        for (j in 0 until carouselItems.length()) {
            val twoRow = carouselItems.optJSONObject(j)?.optJSONObject("musicTwoRowItemRenderer") ?: continue
            val bId = twoRow.optJSONObject("navigationEndpoint")
                ?.optJSONObject("browseEndpoint")
                ?.optString("browseId").orEmpty()

            if (isAlbumSection && bId.isNotBlank()) {
                if (albums.none { it.id == bId }) {
                    itemParser.parsePlaylistFromTwoRow(
                        twoRow = twoRow,
                        browseId = bId,
                        defaultSubtitle = artistName,
                        forcedType = PlaylistType.GENRE
                    )?.let { albums.add(it) }
                }
            } else if (isSingleSection && bId.isNotBlank()) {
                if (singles.none { it.id == bId }) {
                    itemParser.parsePlaylistFromTwoRow(
                        twoRow = twoRow,
                        browseId = bId,
                        defaultSubtitle = artistName,
                        forcedType = PlaylistType.GENRE
                    )?.let { singles.add(it) }
                }
            } else if (isVideoSection) {
                itemParser.parseSongFromTwoRow(twoRow)?.let { song ->
                    if (topSongs.none { it.id == song.id }) {
                        topSongs.add(song)
                    }
                }
            }
        }
    }
}
