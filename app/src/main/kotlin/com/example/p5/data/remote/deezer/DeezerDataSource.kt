package com.example.p5.data.remote.deezer

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class DeezerDataSource(
    private val okHttpClient: OkHttpClient,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private var cachedChart: DeezerChartData? = null
    private var lastFetchTimeMs: Long = 0L
    private var cachedGenres: List<DeezerGenre> = emptyList()
    private var lastGenresFetchTimeMs: Long = 0L
    private val cacheTtlMs: Long = 15 * 60 * 1000L

    suspend fun getGenres(forceRefresh: Boolean = false): List<DeezerGenre> = withContext(ioDispatcher) {
        val now = System.currentTimeMillis()
        if (!forceRefresh && cachedGenres.isNotEmpty() && (now - lastGenresFetchTimeMs) < cacheTtlMs) {
            return@withContext cachedGenres
        }

        val result = try {
            fetchGenresData()
        } catch (_: Exception) {
            cachedGenres
        }

        cachedGenres = result
        lastGenresFetchTimeMs = now
        result
    }

    private fun fetchGenresData(): List<DeezerGenre> {
        val request = Request.Builder()
            .url(DEEZER_GENRES_URL)
            .get()
            .addHeader("User-Agent", "Mozilla/5.0")
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("Deezer genres request failed: ${response.code}")
        }

        val jsonString = response.body?.string() ?: throw IOException("Empty response from Deezer")
        return parseGenresJson(jsonString)
    }

    private fun parseGenresJson(jsonString: String): List<DeezerGenre> {
        val root = JSONObject(jsonString)
        val data = root.optJSONArray("data") ?: return emptyList()
        val genres = mutableListOf<DeezerGenre>()
        for (i in 0 until data.length()) {
            val item = data.optJSONObject(i) ?: continue
            val id = item.optLong("id")
            val name = item.optString("name")
            if (id == 0L || id == 95L || name.isBlank()) continue
            val picture = item.optString("picture_xl").takeIf { !it.isNullOrBlank() }
                ?: item.optString("picture_big").takeIf { !it.isNullOrBlank() }
                ?: item.optString("picture_medium").orEmpty()
            genres.add(DeezerGenre(id, name, picture))
        }
        return genres
    }

    suspend fun getChart(forceRefresh: Boolean = false): DeezerChartData = withContext(ioDispatcher) {
        val now = System.currentTimeMillis()
        if (!forceRefresh && cachedChart != null && (now - lastFetchTimeMs) < cacheTtlMs) {
            return@withContext cachedChart!!
        }

        val result = try {
            fetchChartData()
        } catch (_: Exception) {
            cachedChart ?: DeezerChartData()
        }

        cachedChart = result
        lastFetchTimeMs = now
        result
    }

    private fun fetchChartData(): DeezerChartData {
        val request = Request.Builder()
            .url(DEEZER_CHART_URL)
            .get()
            .addHeader("User-Agent", "Mozilla/5.0")
            .build()

        val response = okHttpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            throw IOException("Deezer chart request failed: ${response.code}")
        }

        val jsonString = response.body?.string() ?: throw IOException("Empty response from Deezer")
        return parseChartJson(jsonString)
    }

    private fun parseChartJson(jsonString: String): DeezerChartData {
        val root = JSONObject(jsonString)

        val tracksList = mutableListOf<DeezerTrack>()
        val tracksObj = root.optJSONObject("tracks")
        val tracksData = tracksObj?.optJSONArray("data")
        if (tracksData != null) {
            for (i in 0 until tracksData.length()) {
                val item = tracksData.optJSONObject(i) ?: continue
                val id = item.optLong("id")
                val title = item.optString("title")
                val artist = item.optJSONObject("artist")?.optString("name").orEmpty()
                val album = item.optJSONObject("album")
                val albumTitle = album?.optString("title").orEmpty()
                val cover = album?.optString("cover_xl").takeIf { !it.isNullOrBlank() }
                    ?: album?.optString("cover_medium").orEmpty()

                if (title.isNotBlank() && artist.isNotBlank()) {
                    tracksList.add(DeezerTrack(id, title, artist, albumTitle, cover))
                }
            }
        }

        val artistsList = mutableListOf<DeezerArtist>()
        val artistsObj = root.optJSONObject("artists")
        val artistsData = artistsObj?.optJSONArray("data")
        if (artistsData != null) {
            for (i in 0 until artistsData.length()) {
                val item = artistsData.optJSONObject(i) ?: continue
                val id = item.optLong("id")
                val name = item.optString("name")
                val picture = item.optString("picture_xl").takeIf { !it.isNullOrBlank() }
                    ?: item.optString("picture_medium").orEmpty()

                if (name.isNotBlank()) {
                    artistsList.add(DeezerArtist(id, name, picture))
                }
            }
        }

        val playlistsList = mutableListOf<DeezerPlaylist>()
        val playlistsObj = root.optJSONObject("playlists")
        val playlistsData = playlistsObj?.optJSONArray("data")
        if (playlistsData != null) {
            for (i in 0 until playlistsData.length()) {
                val item = playlistsData.optJSONObject(i) ?: continue
                val id = item.optLong("id")
                val title = item.optString("title")
                val picture = item.optString("picture_xl").takeIf { !it.isNullOrBlank() }
                    ?: item.optString("picture_medium").orEmpty()
                val nbTracks = item.optInt("nb_tracks", 0)

                if (title.isNotBlank()) {
                    playlistsList.add(DeezerPlaylist(id, title, picture, nbTracks))
                }
            }
        }

        return DeezerChartData(
            tracks = tracksList,
            artists = artistsList,
            playlists = playlistsList
        )
    }

    companion object {
        private const val DEEZER_CHART_URL = "https://api.deezer.com/chart"
        private const val DEEZER_GENRES_URL = "https://api.deezer.com/genre"
    }
}
