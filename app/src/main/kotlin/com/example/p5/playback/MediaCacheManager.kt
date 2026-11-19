package com.example.p5.playback

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.FileDataSource
import androidx.media3.datasource.cache.CacheDataSink
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.CacheKeyFactory
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@OptIn(UnstableApi::class)
object MediaCacheManager {
    private const val MAX_CACHE_SIZE_BYTES = 500L * 1024L * 1024L
    private const val CACHE_FRAGMENT_SIZE = 5L * 1024L * 1024L

    @Volatile
    private var simpleCache: SimpleCache? = null

    @Synchronized
    fun getCache(context: Context): SimpleCache {
        return simpleCache ?: run {
            val cacheDir = File(context.cacheDir, "media_stream_cache")
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            val evictor = LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE_BYTES)
            val databaseProvider = StandaloneDatabaseProvider(context.applicationContext)
            SimpleCache(cacheDir, evictor, databaseProvider).also {
                simpleCache = it
            }
        }
    }

    fun createCacheDataSourceFactory(context: Context): DataSource.Factory {
        val cache = getCache(context.applicationContext)

        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36")
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15_000)
            .setReadTimeoutMs(30_000)

        val upstreamFactory = DefaultDataSource.Factory(context.applicationContext, httpDataSourceFactory)
        val cacheReadFactory = FileDataSource.Factory()
        val cacheWriteFactory = CacheDataSink.Factory()
            .setCache(cache)
            .setFragmentSize(CACHE_FRAGMENT_SIZE)

        val cacheKeyFactory = CacheKeyFactory { dataSpec ->
            dataSpec.key ?: dataSpec.uri.toString()
        }

        return CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(upstreamFactory)
            .setCacheReadDataSourceFactory(cacheReadFactory)
            .setCacheWriteDataSinkFactory(cacheWriteFactory)
            .setCacheKeyFactory(cacheKeyFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    }

    fun isSongCached(context: Context, songId: String): Boolean {
        return try {
            val cache = getCache(context.applicationContext)
            cache.getCachedSpans(songId).isNotEmpty()
        } catch (_: Exception) {
            false
        }
    }

    fun getCachedBytesForSong(context: Context, songId: String): Long {
        return try {
            val cache = getCache(context.applicationContext)
            var totalBytes = 0L
            for (span in cache.getCachedSpans(songId)) {
                totalBytes += span.length
            }
            totalBytes
        } catch (_: Exception) {
            0L
        }
    }

    fun removeSongFromCache(context: Context, songId: String) {
        try {
            val cache = getCache(context.applicationContext)
            cache.removeResource(songId)
        } catch (_: Exception) {
        }
    }

    fun getCachedSizeBytes(context: Context): Long {
        return try {
            getCache(context.applicationContext).cacheSpace
        } catch (_: Exception) {
            0L
        }
    }

    fun clearCache(context: Context) {
        try {
            val cache = getCache(context.applicationContext)
            for (key in cache.keys) {
                cache.removeResource(key)
            }
        } catch (_: Exception) {
        }
    }
}
