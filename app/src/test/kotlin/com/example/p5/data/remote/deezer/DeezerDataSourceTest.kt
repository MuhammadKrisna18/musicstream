package com.example.p5.data.remote.deezer

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DeezerDataSourceTest {

    private val testDispatcher = StandardTestDispatcher()

    private fun createMockClient(jsonResponse: String, statusCode: Int = 200): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                Response.Builder()
                    .request(chain.request())
                    .protocol(Protocol.HTTP_1_1)
                    .code(statusCode)
                    .message(if (statusCode == 200) "OK" else "Error")
                    .body(jsonResponse.toResponseBody("application/json".toMediaType()))
                    .build()
            }
            .build()
    }

    @Test
    fun getGenres_parsesValidGenresAndFiltersExcluded() = runTest(testDispatcher) {
        val sampleJson = """
            {
                "data": [
                    { "id": 0, "name": "All", "picture_xl": "https://img/0.jpg" },
                    { "id": 132, "name": "Pop", "picture_xl": "https://img/pop.jpg" },
                    { "id": 152, "name": "Rock", "picture_xl": "https://img/rock.jpg" },
                    { "id": 95, "name": "Kids", "picture_xl": "https://img/kids.jpg" }
                ]
            }
        """.trimIndent()

        val client = createMockClient(sampleJson)
        val dataSource = DeezerDataSource(client, testDispatcher)

        val genres = dataSource.getGenres(forceRefresh = true)

        assertEquals(2, genres.size)
        assertEquals(132L, genres[0].id)
        assertEquals("Pop", genres[0].name)
        assertEquals("https://img/pop.jpg", genres[0].pictureUrl)
        assertEquals(152L, genres[1].id)
        assertEquals("Rock", genres[1].name)
    }

    @Test
    fun getGenres_onHttpError_returnsCachedOrEmpty() = runTest(testDispatcher) {
        val client = createMockClient("{}", statusCode = 500)
        val dataSource = DeezerDataSource(client, testDispatcher)

        val genres = dataSource.getGenres(forceRefresh = true)

        assertTrue(genres.isEmpty())
    }
}
