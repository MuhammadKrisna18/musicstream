package com.example.p5.data.remote.innertube

import com.example.p5.domain.model.ArtistProfile
import com.example.p5.domain.model.AudioStream
import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeSection
import com.example.p5.domain.model.MoodTag
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.Song
import org.json.JSONObject

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

class InnerTubeParser(
    private val itemParser: InnerTubeItemParser = InnerTubeItemParser(),
    private val searchParser: InnerTubeSearchParser = InnerTubeSearchParser(itemParser),
    private val browseParser: InnerTubeBrowseParser = InnerTubeBrowseParser(itemParser),
    private val playerParser: InnerTubePlayerParser = InnerTubePlayerParser(),
    private val artistParser: InnerTubeArtistParser = InnerTubeArtistParser(itemParser)
) {

    fun parseSearchPayload(jsonString: String, limit: Int): SearchPayload {
        return searchParser.parseSearchPayload(jsonString, limit)
    }

    fun parseSearchResults(jsonString: String, limit: Int): List<Song> {
        return searchParser.parseSearchPayload(jsonString, limit).songs
    }

    fun parseBrowseResponse(jsonString: String, prefixId: String): ParsedBrowse {
        return browseParser.parseBrowseResponse(jsonString, prefixId)
    }

    fun parsePlaylistTracks(jsonString: String): List<Song> {
        return browseParser.parsePlaylistTracks(jsonString)
    }

    fun parseSongFromResponsive(responsive: JSONObject): Song? {
        return itemParser.parseSongFromResponsive(responsive)
    }

    fun parseSongFromTwoRow(twoRow: JSONObject): Song? {
        return itemParser.parseSongFromTwoRow(twoRow)
    }

    fun parsePlayerResponse(videoId: String, jsonString: String): AudioStream {
        return playerParser.parsePlayerResponse(videoId, jsonString)
    }

    fun parseArtistProfile(browseId: String, jsonString: String): ArtistProfile {
        return artistParser.parseArtistProfile(browseId, jsonString)
    }
}
