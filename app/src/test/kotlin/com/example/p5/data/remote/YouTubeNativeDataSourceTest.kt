package com.example.p5.data.remote

import com.example.p5.domain.model.Category
import com.example.p5.domain.model.HomeGenreConfig
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test

class YouTubeNativeDataSourceTest {

    private lateinit var dataSource: YouTubeNativeDataSource
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        val client = OkHttpClient()
        dataSource = YouTubeNativeDataSource(
            okHttpClient = client,
            ioDispatcher = testDispatcher
        )
    }

    @Test
    fun getCategories_returnsConfiguredEightGenresWhenCacheEmpty() {
        val homeCategories = dataSource.getCategories()
        val categories = homeCategories.categories

        assertEquals(8, categories.size)
        assertEquals(HomeGenreConfig.CATEGORIES.map { it.id }, categories.map { it.id })
        assertEquals(HomeGenreConfig.CATEGORIES.map { it.title }, categories.map { it.title })
    }

    @Test
    fun getCategories_containsAllExpectedGenreTitles() {
        val titles = dataSource.getCategories().categories.map { it.title }
        val expected = listOf(
            "Pop",
            "Indie Pop",
            "Koplo",
            "Dangdut",
            "R&B",
            "Jazz",
            "Acoustic",
            "Keroncong"
        )
        assertEquals(expected, titles)
    }

    @Test
    fun genreCategories_canBeResolvedByHomeGenreConfig() {
        val categories = dataSource.getCategories().categories
        for (category in categories) {
            val genre = HomeGenreConfig.findById(category.id)
            assertNotNull(genre)
            assertEquals(category.title, genre?.title)
        }
    }
}
