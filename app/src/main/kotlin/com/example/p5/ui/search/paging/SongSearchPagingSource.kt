package com.example.p5.ui.search.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.p5.domain.model.Song
import com.example.p5.domain.usecase.SearchMusicUseCase

class SongSearchPagingSource(
    private val searchMusicUseCase: SearchMusicUseCase,
    private val query: String
) : PagingSource<Int, Song>() {

    private val seenSongIds = mutableSetOf<String>()

    override fun getRefreshKey(state: PagingState<Int, Song>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Song> {
        val page = params.key ?: 1
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            return LoadResult.Page(
                data = emptyList(),
                prevKey = null,
                nextKey = null
            )
        }

        val searchQuery = when (page) {
            1 -> trimmed
            2 -> "$trimmed official"
            3 -> "$trimmed audio"
            4 -> "$trimmed live"
            5 -> "$trimmed acoustic"
            else -> "$trimmed remix"
        }

        return try {
            val limit = params.loadSize.coerceAtLeast(15)
            val result = searchMusicUseCase(searchQuery, limit)
            result.fold(
                onSuccess = { songs ->
                    val uniqueSongs = songs.filter { seenSongIds.add(it.id) }
                    LoadResult.Page(
                        data = uniqueSongs,
                        prevKey = if (page == 1) null else page - 1,
                        nextKey = if (songs.isEmpty() || page >= 6) null else page + 1
                    )
                },
                onFailure = { throwable ->
                    LoadResult.Error(throwable)
                }
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
