package com.example.p5.data.remote.innertube

import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.Song
import org.json.JSONArray
import org.json.JSONObject

class InnerTubeSearchParser(
    private val itemParser: InnerTubeItemParser
) {

    fun parseSearchPayload(jsonString: String, limit: Int): SearchPayload {
        val root = JSONObject(jsonString)
        val contentsJson = root.optJSONObject("contents") ?: return SearchPayload(emptyList(), emptyList())

        val twoCol = contentsJson.optJSONObject("twoColumnSearchResultsRenderer")
        if (twoCol != null) {
            return parseTwoColumnSearchResults(twoCol, limit)
        }

        val tabbed = contentsJson.optJSONObject("tabbedSearchResultsRenderer")
        if (tabbed != null) {
            return parseTabbedSearchResults(tabbed, limit)
        }

        return SearchPayload(emptyList(), emptyList())
    }

    private fun parseTwoColumnSearchResults(twoCol: JSONObject, limit: Int): SearchPayload {
        val contents = twoCol.optJSONObject("primaryContents")
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
                    itemParser.parsePlaylistFromPlaylistRenderer(pr)?.let {
                        if (!MusicFilterHelper.isExcludedPlaylist(it.title)) playlists.add(it)
                    }
                }

                val vr = item.optJSONObject("videoRenderer") ?: continue
                if (songs.size >= limit) continue

                itemParser.parseSongFromVideoRenderer(vr)?.let {
                    if (MusicFilterHelper.isValidMusicTrack(it)) songs.add(it)
                }
            }
        }

        return SearchPayload(MusicFilterHelper.deduplicateSongs(songs), playlists)
    }

    private fun parseTabbedSearchResults(tabbed: JSONObject, limit: Int): SearchPayload {
        val tabs = tabbed.optJSONArray("tabs") ?: JSONArray()
        if (tabs.length() == 0) return SearchPayload(emptyList(), emptyList())

        val tabContents = tabs.optJSONObject(0)
            ?.optJSONObject("tabRenderer")
            ?.optJSONObject("content")
            ?.optJSONObject("sectionListRenderer")
            ?.optJSONArray("contents") ?: JSONArray()

        val songs = mutableListOf<Song>()
        val playlists = mutableListOf<Playlist>()

        for (i in 0 until tabContents.length()) {
            val section = tabContents.optJSONObject(i) ?: continue
            val items = extractSectionItems(section)

            for (item in items) {
                val responsive = item.optJSONObject("musicResponsiveListItemRenderer")
                if (responsive != null) {
                    val parsedSong = itemParser.parseSongFromResponsive(responsive)
                    if (parsedSong != null && songs.size < limit) {
                        if (MusicFilterHelper.isValidMusicTrack(parsedSong) && songs.none { it.id == parsedSong.id }) {
                            songs.add(parsedSong)
                        }
                    } else if (playlists.size < 5) {
                        itemParser.parsePlaylistFromResponsive(responsive)?.let { playlist ->
                            if (!MusicFilterHelper.isExcludedPlaylist(playlist.title) && playlists.none { it.id == playlist.id }) {
                                playlists.add(playlist)
                            }
                        }
                    }
                }

                val twoRow = item.optJSONObject("musicTwoRowItemRenderer")
                if (twoRow != null) {
                    val browseId = twoRow.optJSONObject("navigationEndpoint")
                        ?.optJSONObject("browseEndpoint")
                        ?.optString("browseId").orEmpty()
                    if (InnerTubeJsonHelper.isSearchPlaylistBrowseId(browseId)) {
                        if (playlists.size < 5) {
                            itemParser.parsePlaylistFromTwoRow(twoRow, browseId)?.let { playlist ->
                                if (!MusicFilterHelper.isExcludedPlaylist(playlist.title) && playlists.none { it.id == playlist.id }) {
                                    playlists.add(playlist)
                                }
                            }
                        }
                    } else if (songs.size < limit) {
                        itemParser.parseSongFromTwoRow(twoRow)?.let { song ->
                            if (MusicFilterHelper.isValidMusicTrack(song) && songs.none { it.id == song.id }) {
                                songs.add(song)
                            }
                        }
                    }
                }
            }
        }

        return SearchPayload(MusicFilterHelper.deduplicateSongs(songs), playlists)
    }

    private fun extractSectionItems(section: JSONObject): List<JSONObject> {
        val items = mutableListOf<JSONObject>()

        val shelfContents = section.optJSONObject("musicShelfRenderer")?.optJSONArray("contents")
        if (shelfContents != null) {
            for (k in 0 until shelfContents.length()) {
                shelfContents.optJSONObject(k)?.let { items.add(it) }
            }
        }

        val secContents = section.optJSONObject("itemSectionRenderer")?.optJSONArray("contents")
        if (secContents != null) {
            for (k in 0 until secContents.length()) {
                secContents.optJSONObject(k)?.let { items.add(it) }
            }
        }

        return items
    }
}
