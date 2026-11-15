package com.example.p5.domain.model

data class Category(
    val id: String,
    val title: String,
    val query: String,
    val icon: String = "music_note",
    val startColor: String = "#00E5BF",
    val endColor: String = "#1A1A1E",
    val browseId: String = "",
    val params: String = ""
)

data class MoodTag(
    val id: String,
    val label: String,
    val query: String,
    val browseId: String = "",
    val params: String = ""
)

data class HomeCategories(
    val moodTags: List<MoodTag>,
    val categories: List<Category>
)
