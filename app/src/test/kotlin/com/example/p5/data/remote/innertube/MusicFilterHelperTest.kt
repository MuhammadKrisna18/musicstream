package com.example.p5.data.remote.innertube

import com.example.p5.domain.model.Song
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MusicFilterHelperTest {

    private fun createSong(
        id: String,
        title: String,
        artist: String = "Artist",
        durationSeconds: Int = 200,
        viewCount: Long = 1000L
    ): Song {
        return Song(
            id = id,
            title = title,
            artist = artist,
            durationSeconds = durationSeconds,
            durationFormatted = "${durationSeconds / 60}:${durationSeconds % 60}",
            thumbnailUrl = "thumb",
            webUrl = "web",
            viewCount = viewCount
        )
    }

    @Test
    fun isValidMusicTrack_acceptsStandardSong() {
        val song = createSong("1", "Gereja Tua", "Panbers", 210)
        assertTrue(MusicFilterHelper.isValidMusicTrack(song))
    }

    @Test
    fun isValidMusicTrack_rejectsLongCompilations() {
        val compilation1 = createSong("1", "Top 100 Songs 2026 (Billboard)", "ELIE.D", 3620)
        assertFalse(MusicFilterHelper.isValidMusicTrack(compilation1))

        val compilation2 = createSong("2", "Raim Laode Full Album", "Raim Laode", 2400)
        assertFalse(MusicFilterHelper.isValidMusicTrack(compilation2))

        val compilation3 = createSong("3", "Kompilasi Lagu Pop 1 Jam Nonstop", "Various", 250)
        assertFalse(MusicFilterHelper.isValidMusicTrack(compilation3))

        val compilation4 = createSong("4", "Single Track", "Playlist musik indonesia", 200)
        assertFalse(MusicFilterHelper.isValidMusicTrack(compilation4))

        val compilation5 = createSong("5", "Lagu Pop", "Artist", 550)
        assertFalse(MusicFilterHelper.isValidMusicTrack(compilation5))
    }

    @Test
    fun isValidMusicTrack_rejectsPodcastsAndTalkshows() {
        val podcast1 = createSong("1", "Podcast Ngobrol Santai", "Channel Podcast", 300)
        assertFalse(MusicFilterHelper.isValidMusicTrack(podcast1))

        val talkshow = createSong("2", "Eps. 45 - Wawancara Eksklusif", "Talkshow ID", 500)
        assertFalse(MusicFilterHelper.isValidMusicTrack(talkshow))

        val standup = createSong("3", "Stand Up Comedy Spesial", "Komika", 400)
        assertFalse(MusicFilterHelper.isValidMusicTrack(standup))
    }

    @Test
    fun isValidMusicTrack_rejectsTooShortTracks() {
        val ringtone = createSong("1", "Intro Ringtone", "Artist", 20)
        assertFalse(MusicFilterHelper.isValidMusicTrack(ringtone))
    }

    @Test
    fun normalizeTitle_cleansNoiseWatermarks() {
        val rawTitle = "Raiso Ngapusi [Official Music Video] | Lirikvibes id"
        val normalized = MusicFilterHelper.normalizeTitle(rawTitle)
        assertEquals("raiso ngapusi", normalized)
    }

    @Test
    fun deduplicateSongs_removesFanReuploadsWithSameCoreTitle() {
        val songs = listOf(
            createSong("s1", "Raiso Ngapusi 2 (feat. Ervinsof)", "Tekomlaku", 210, 100000L),
            createSong("s2", "Raiso Ngapusi (feat. Ervin Sofiani)", "Tekomlaku", 200, 200000L),
            createSong("s3", "TEKOMLAKU - RAISO NGAPUSI 2 | Lirikvibes id", "Tekomlaku", 210, 50000L),
            createSong("s4", "RAISO NGAPUSI - TEKOMLAKU Ft SerlySyer", "Tekomlaku", 200, 40000L),
            createSong("s5", "Kapusan", "Tekomlaku", 220, 80000L)
        )

        val deduplicated = MusicFilterHelper.deduplicateSongs(songs)

        assertEquals(3, deduplicated.size)
        assertEquals("s1", deduplicated[0].id)
        assertEquals("s2", deduplicated[1].id)
        assertEquals("s5", deduplicated[2].id)
    }
}
