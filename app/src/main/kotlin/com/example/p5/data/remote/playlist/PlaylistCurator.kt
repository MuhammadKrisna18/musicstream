package com.example.p5.data.remote.playlist

import com.example.p5.data.remote.YouTubeNativeDataSource
import com.example.p5.domain.model.Playlist
import com.example.p5.domain.model.PlaylistType
import com.example.p5.domain.model.Song
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

class PlaylistCurator(
    private val youTubeDataSource: YouTubeNativeDataSource
) {
    private val playlistTrackCache = ConcurrentHashMap<String, List<Song>>()

    val baseCuratedPlaylists: List<Playlist> = listOf(
        Playlist(
            id = "genre_pop_hits",
            title = "Best of Pop Indonesia",
            subtitle = "Koleksi lagu pop terpopuler",
            thumbnailUrl = "https://i.ytimg.com/vi/qYJg_y0B9_w/hqdefault.jpg",
            type = PlaylistType.GENRE,
            query = "Lagu Pop Indonesia hits terpopuler"
        ),
        Playlist(
            id = "genre_indie_senja",
            title = "Indie Senja Pilihan",
            subtitle = "Melodi syahdu pengiring sore",
            thumbnailUrl = "https://i.ytimg.com/vi/uXfGvW2iGZ8/hqdefault.jpg",
            type = PlaylistType.GENRE,
            query = "Lagu Indie Indonesia senja hits"
        ),
        Playlist(
            id = "genre_rock_terbaik",
            title = "Rock Indonesia Terbaik",
            subtitle = "Distorsi bertenaga rock lokal",
            thumbnailUrl = "https://i.ytimg.com/vi/0pFn_E5f_L8/hqdefault.jpg",
            type = PlaylistType.GENRE,
            query = "Lagu Rock Indonesia terbaik"
        ),
        Playlist(
            id = "genre_dangdut_koplo",
            title = "Koplo & Dangdut Hits",
            subtitle = "Irama kendang paling asyik",
            thumbnailUrl = "https://i.ytimg.com/vi/6pWn3Y9b4eA/hqdefault.jpg",
            type = PlaylistType.GENRE,
            query = "Lagu Dangdut Koplo terpopuler"
        ),
        Playlist(
            id = "genre_jazz_santai",
            title = "Jazz & Soul Cafe",
            subtitle = "Suasana tenang pengiring santai",
            thumbnailUrl = "https://i.ytimg.com/vi/y5vR4wE3X8M/hqdefault.jpg",
            type = PlaylistType.GENRE,
            query = "Lagu Jazz Indonesia santai cafe"
        ),
        Playlist(
            id = "artist_tulus",
            title = "This is Tulus",
            subtitle = "Karya musik terbaik dari Tulus",
            thumbnailUrl = "https://i.ytimg.com/vi/wO6L_Yn1d4g/hqdefault.jpg",
            type = PlaylistType.ARTIST,
            query = "Tulus official hits singles"
        ),
        Playlist(
            id = "artist_sheila07",
            title = "This is Sheila on 7",
            subtitle = "Koleksi nostalgia Sheila on 7",
            thumbnailUrl = "https://i.ytimg.com/vi/bX7j-q8oB0M/hqdefault.jpg",
            type = PlaylistType.ARTIST,
            query = "Sheila on 7 official hits singles"
        ),
        Playlist(
            id = "artist_denny_caknan",
            title = "This is Denny Caknan",
            subtitle = "Deretan hits populer Denny Caknan",
            thumbnailUrl = "https://i.ytimg.com/vi/t4j5F9k4l6M/hqdefault.jpg",
            type = PlaylistType.ARTIST,
            query = "Denny Caknan official hits singles"
        ),
        Playlist(
            id = "artist_dewa19",
            title = "This is Dewa 19",
            subtitle = "Mahakarya legendaris Dewa 19",
            thumbnailUrl = "https://i.ytimg.com/vi/jZ8_s1D9y0w/hqdefault.jpg",
            type = PlaylistType.ARTIST,
            query = "Dewa 19 official hits singles"
        ),
        Playlist(
            id = "artist_yovie_nuno",
            title = "This is Yovie & Nuno",
            subtitle = "Lagu cinta terbaik Yovie & Nuno",
            thumbnailUrl = "https://i.ytimg.com/vi/g4N6m8k2b1s/hqdefault.jpg",
            type = PlaylistType.ARTIST,
            query = "Yovie & Nuno official hits singles"
        ),
        Playlist(
            id = "artist_raisa",
            title = "This is Raisa",
            subtitle = "Alunan vokal merdu Raisa",
            thumbnailUrl = "https://i.ytimg.com/vi/p8N2x6L5q9M/hqdefault.jpg",
            type = PlaylistType.ARTIST,
            query = "Raisa official hits singles"
        ),
        Playlist(
            id = "random_daily_mix",
            title = "Daily Mix Indonesia",
            subtitle = "Paduan lagu pilihan hari ini",
            thumbnailUrl = "https://i.ytimg.com/vi/r6P9n0W2b4c/hqdefault.jpg",
            type = PlaylistType.RANDOM,
            query = "Top music hits Indonesia 2026"
        ),
        Playlist(
            id = "random_acoustic_chill",
            title = "Acoustic Chill Mix",
            subtitle = "Pilihan petikan gitar santai",
            thumbnailUrl = "https://i.ytimg.com/vi/1j6k8l9o0pA/hqdefault.jpg",
            type = PlaylistType.RANDOM,
            query = "Acoustic chill pop hits singles"
        ),
        Playlist(
            id = "random_semangat_pagi",
            title = "Semangat Pagi Mix",
            subtitle = "Lagu berenergi penyemangat hari",
            thumbnailUrl = "https://i.ytimg.com/vi/4m5n6o7p8qR/hqdefault.jpg",
            type = PlaylistType.RANDOM,
            query = "Lagu upbeat Indonesia semangat"
        )
    )

    fun getCuratedPlaylists(): List<Playlist> {
        return baseCuratedPlaylists
    }

    suspend fun getPlaylistTracks(playlist: Playlist): List<Song> {
        val cached = playlistTrackCache[playlist.id]
        if (!cached.isNullOrEmpty()) {
            return cached
        }

        val query = playlist.query.ifBlank { "Lagu ${playlist.title}" }
        val rawSongs = youTubeDataSource.search(query, limit = 25)

        val cleanSongs = rawSongs.filter { song ->
            song.durationSeconds in 90..480 && !isExcludedCompilation(song.title)
        }

        val result = if (cleanSongs.isNotEmpty()) cleanSongs else rawSongs
        if (result.isNotEmpty()) {
            playlistTrackCache[playlist.id] = result
        }
        return result
    }

    fun searchPlaylists(query: String): List<Playlist> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return emptyList()

        val lowerQuery = trimmed.lowercase(Locale.ROOT)
        val matched = baseCuratedPlaylists.filter { playlist ->
            playlist.title.lowercase(Locale.ROOT).contains(lowerQuery) ||
                playlist.subtitle.lowercase(Locale.ROOT).contains(lowerQuery) ||
                playlist.query.lowercase(Locale.ROOT).contains(lowerQuery)
        }

        if (matched.isNotEmpty()) {
            return matched
        }

        if (trimmed.length >= 3) {
            val capitalized = trimmed.split(" ").joinToString(" ") { word ->
                word.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }
            }
            return listOf(
                Playlist(
                    id = "dynamic_artist_${lowerQuery.replace("\\s+".toRegex(), "_")}",
                    title = "This is $capitalized",
                    subtitle = "Koleksi lagu terbaik $capitalized",
                    thumbnailUrl = "",
                    type = PlaylistType.ARTIST,
                    query = "$trimmed official hits singles"
                )
            )
        }

        return emptyList()
    }

    private fun isExcludedCompilation(title: String): Boolean {
        val lower = title.lowercase(Locale.ROOT)
        return lower.contains("full album") ||
            lower.contains("kompilasi") ||
            lower.contains("1 jam") ||
            lower.contains("kumpulan lagu") ||
            lower.contains("nonstop") ||
            lower.contains("top hits spotify")
    }
}
