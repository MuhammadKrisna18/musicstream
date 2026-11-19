package com.example.p5.domain.model

enum class HomeSectionStyle {
    STANDARD_CARDS,
    QUICK_PICKS_GRID,
    RANKED_CHARTS
}

data class HomeSection(
    val id: String,
    val title: String,
    val subtitle: String?,
    val query: String?,
    val tracks: List<Song>,
    val style: HomeSectionStyle = HomeSectionStyle.STANDARD_CARDS
)

data class HomeFeed(
    val sections: List<HomeSection>,
    val moodTags: List<MoodTag>,
    val categories: List<Category>,
    val playlists: List<Playlist> = emptyList()
)
