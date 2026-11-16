package com.example.p5.playback

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File

@OptIn(UnstableApi::class)
object MediaCacheManager {
    private const val MAX_CACHE_SIZE_BYTES = 300L * 1024L * 1024L

    @Volatile
    private var simpleCache: SimpleCache? = null

    @Synchronized
    fun getCache(context: Context): SimpleCache {
        return simpleCache ?: run {
            val cacheDir = File(context.cacheDir, "media_stream_cache")
            val evictor = LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE_BYTES)
            val databaseProvider = StandaloneDatabaseProvider(context.applicationContext)
            try {
                if (!cacheDir.exists()) {
                    cacheDir.mkdirs()
                }
                SimpleCache(cacheDir, evictor, databaseProvider).also {
                    simpleCache = it
                }
            } catch (_: Exception) {
                cacheDir.deleteRecursively()
                cacheDir.mkdirs()
                SimpleCache(cacheDir, evictor, databaseProvider).also {
                    simpleCache = it
                }
            }
        }
    }

    fun createCacheDataSourceFactory(context: Context): DataSource.Factory {
        val httpDataSourceFactory = DefaultHttpDataSource.Factory()
            .setUserAgent("com.google.android.youtube/20.10.38 (Linux; U; Android 11) gzip")
            .setAllowCrossProtocolRedirects(true)
            .setConnectTimeoutMs(15_000)
            .setReadTimeoutMs(30_000)

        val upstreamFactory = DefaultDataSource.Factory(context.applicationContext, httpDataSourceFactory)

        val cache = try {
            getCache(context.applicationContext)
        } catch (_: Exception) {
            null
        }

        return if (cache != null) {
            CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(upstreamFactory)
                .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        } else {
            upstreamFactory
        }
    }

    fun getCachedSizeBytes(context: Context): Long {
        return try {
            getCache(context).cacheSpace
        } catch (e: Exception) {
            0L
        }
    }

    fun clearCache(context: Context) {
        try {
            val cache = getCache(context)
            for (key in cache.keys) {
                cache.removeResource(key)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

