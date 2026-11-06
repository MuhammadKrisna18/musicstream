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

    val staticMoodTags: List<MoodTag> = listOf(
        MoodTag("top_hits", "🔥 Top Hits", "Lagu Trending"),
        MoodTag("santai", "☕ Santai", "Lagu Santai"),
        MoodTag("olah_raga", "⚡ Olah Raga", "Lagu Workout"),
        MoodTag("rock", "🎸 Rock", "Lagu Rock"),
        MoodTag("akustik", "🌙 Akustik", "Lagu Akustik"),
        MoodTag("pesta", "🎉 Pesta", "Lagu Pesta"),
        MoodTag("fokus", "🎧 Fokus", "Lagu Fokus")
    )

    val staticCategories: List<Category> = listOf(
        Category("top_hits", "Top Hits", "Lagu Trending", "whatshot", "#00E5BF", "#004D40"),
        Category("santai", "Santai & Rileks", "Lagu Santai", "nightlife", "#00BFA5", "#121519"),
        Category("olah_raga", "Olah Raga & Energi", "Lagu Workout", "bolt", "#1DE9B6", "#004D40"),
        Category("pop_vibes", "Populer Indonesia", "Lagu Pop Indonesia", "music_note", "#00B0FF", "#0D47A1"),
        Category("indie_wave", "Indie & Senja", "Lagu Indie Indonesia", "headphones", "#7C4DFF", "#1A237E"),
        Category("rock_metal", "Rock & Metal", "Lagu Rock", "electric_bolt", "#00E5BF", "#263238"),
        Category("fokus_kerja", "Fokus & Belajar", "Lagu Fokus Lofi", "headphones", "#64FFDA", "#263238"),
        Category("romantis", "Romantis & Cinta", "Lagu Romantis", "favorite", "#26A69A", "#004D40")
    )
}

