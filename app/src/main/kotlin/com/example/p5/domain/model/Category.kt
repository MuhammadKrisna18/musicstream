package com.example.p5.domain.model

data class Category(
    val id: String,
    val title: String,
    val query: String,
    val icon: String,
    val startColor: String,
    val endColor: String
)

data class MoodTag(
    val id: String,
    val label: String,
    val query: String
)

data class HomeCategories(
    val moodTags: List<MoodTag>,
    val categories: List<Category>
)
