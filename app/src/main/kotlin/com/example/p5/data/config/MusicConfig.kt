package com.example.p5.data.config

import com.example.p5.domain.model.Category
import com.example.p5.domain.model.MoodTag

object MusicConfig {
    const val DEFAULT_VISITOR_DATA = "CgtRRENOdVRKd3FPcyjWjobXBjIKCgJJRBIEGgAgUGKrAgqoAkN0c0JDQVVTalFFQkRCNlBERGgwUlRKTWNXaUhzNkRRZl9XVkNGQ0RoQ0VyODZvdHlsNHdERUZ4aTNQWVRKVTBteXpETF9wQW84bVVhdFZqOFBocnNycGp2aWZCa1hVd043dGpMVzNLZGFuVHNxQmFrX2syS2JxcTVfT3N5eEVwQ0tZYWxseGF3bWFUZXBiM3hJeGtuZ3cyVmZsU3ZHeERDanRRd2ttMk52eUZEeDRRYWNJSXFFOE5HLWxtY3NOTGlIQVNPZkVvQlRKRkFSeFk5V3F6OWxHU3V4dmVJSmloenJjR3hwNHFGaUwwTk1KRGtYV0FDMmdFSjQ5dllSbG83ZVkwb0lUeVR6OFA5R3lEOFIwUUtvTlc1WnRNVldmdWpXQnBodklL"
    const val DEFAULT_STS = 20751

    const val YOUTUBE_SEARCH_URL = "https://www.youtube.com/youtubei/v1/search"
    const val YOUTUBE_PLAYER_URL = "https://www.youtube.com/youtubei/v1/player"
    const val YOUTUBE_BROWSE_URL = "https://music.youtube.com/youtubei/v1/browse"
    const val YOUTUBE_BASE_ORIGIN = "https://www.youtube.com"
    const val YOUTUBE_MUSIC_ORIGIN = "https://music.youtube.com"
    const val YOUTUBE_MUSIC_REFERER = "https://music.youtube.com/"
    const val THUMBNAIL_HQ_TEMPLATE = "https://i.ytimg.com/vi/%s/hqdefault.jpg"
    const val WATCH_URL_TEMPLATE = "https://www.youtube.com/watch?v=%s"

    val chartPlaylistIds: List<String> = listOf(
        "VLPL4fGSI1pDJn4lW6MJA2q37CfnraqAizqE",
        "VLPL4fGSI1pDJn40q9u1gXg7m3A0PZp0o28S",
        "VLPLFgquLnL59alW3xmYiWRaoz0o56HTr",
        "VLPL4fGSI1pDJn5x6p9_2N14k9A44eB26Zp8",
        "VLPL4fGSI1pDJn4fGzC1bO8vO8S0B5D9l_x2"
    )

    val staticMoodTags: List<MoodTag> = emptyList()

    val staticCategories: List<Category> = listOf(
        Category("pop_indo", "Pop Indonesia", "Lagu Pop Indonesia", "music_note", "#00B0FF", "#0D47A1"),
        Category("pop_global", "Pop", "Pop Music Hits", "music_note", "#00E5BF", "#0D47A1"),
        Category("rock", "Rock", "Lagu Rock", "electric_bolt", "#00E5BF", "#263238"),
        Category("jazz", "Jazz", "Lagu Jazz", "nightlife", "#A4C5FF", "#121519"),
        Category("dangdut", "Dangdut", "Lagu Dangdut", "whatshot", "#7B3EDB", "#004D40"),
        Category("indie", "Indie", "Lagu Indie Indonesia", "headphones", "#7C4DFF", "#1A237E"),
        Category("rnb_soul", "R&B & Soul", "Lagu R&B", "nightlife", "#B47BFF", "#1A1A1E"),
        Category("hiphop", "Hip-Hop", "Lagu Hip Hop", "bolt", "#E24B00", "#263238"),
        Category("akustik", "Akustik", "Lagu Akustik", "library_music", "#00A513", "#004D40"),
        Category("metal", "Metal", "Lagu Metal", "electric_bolt", "#8C8C8C", "#263238"),
        Category("electronic", "Electronic / Dance", "Electronic Dance Music", "bolt", "#FFD600", "#1A1A1E"),
        Category("classical", "Klasik", "Classical Music Essentials", "graphic_eq", "#E0E0E0", "#121519")
    )
}
