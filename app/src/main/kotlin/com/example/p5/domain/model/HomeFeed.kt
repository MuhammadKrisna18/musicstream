package com.example.p5.domain.model

data class HomeSection(
    val id: String,
    val title: String,
    val subtitle: String?,
    val query: String?,
    val tracks: List<Song>
)

data class HomeFeed(
    val sections: List<HomeSection>,
    val moodTags: List<MoodTag>,
    val categories: List<Category>
)
