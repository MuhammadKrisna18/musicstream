package com.example.p5.data.remote.innertube

import com.example.p5.data.config.MusicConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicBoolean
import java.util.regex.Pattern

class InnerTubeSessionManager(
    private val okHttpClient: OkHttpClient
) {
    private var cachedVisitorData: String = MusicConfig.DEFAULT_VISITOR_DATA
    private var cachedSts: Int = MusicConfig.DEFAULT_STS
    private var lastSessionFetchTime: Long = 0L
    private val isFetchingSession = AtomicBoolean(false)

    fun refreshSessionDataIfNeeded() {
        val now = System.currentTimeMillis()
        if (now - lastSessionFetchTime < 3600000L) return
        if (!isFetchingSession.compareAndSet(false, true)) return

        try {
            val request = Request.Builder()
                .url(MusicConfig.YOUTUBE_BASE_ORIGIN)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Safari/537.36")
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val html = response.body?.string() ?: ""
                val visitorMatcher = Pattern.compile("\"VISITOR_DATA\":\"([^\"]+)\"").matcher(html)
                if (visitorMatcher.find()) {
                    cachedVisitorData = visitorMatcher.group(1) ?: cachedVisitorData
                }

                val stsMatcher = Pattern.compile("\"STS\":([0-9]+)").matcher(html)
                if (stsMatcher.find()) {
                    val foundSts = stsMatcher.group(1)?.toIntOrNull()
                    if (foundSts != null && foundSts > 0) {
                        cachedSts = foundSts
                    }
                }
                lastSessionFetchTime = now
            }
        } catch (_: Exception) {
        } finally {
            isFetchingSession.set(false)
        }
    }

    fun buildSearchPayload(query: String): JSONObject {
        refreshSessionDataIfNeeded()
        return JSONObject().apply {
            put("context", JSONObject().apply {
                put("client", JSONObject().apply {
                    put("clientName", "WEB_REMIX")
                    put("clientVersion", "1.20240801.01.00")
                    put("hl", "id")
                    put("gl", "ID")
                    put("visitorData", cachedVisitorData)
                })
            })
            put("query", query)
        }
    }

    fun buildPlayerPayload(videoId: String): JSONObject {
        refreshSessionDataIfNeeded()
        return JSONObject().apply {
            put("context", JSONObject().apply {
                put("client", JSONObject().apply {
                    put("clientName", "ANDROID")
                    put("clientVersion", "19.29.35")
                    put("androidSdkVersion", 34)
                    put("hl", "en")
                    put("gl", "US")
                })
                put("playback", JSONObject().apply {
                    put("audioConfig", JSONObject().apply {
                        put("audioQuality", "AUDIO_QUALITY_HIGH")
                    })
                })
            })
            put("videoId", videoId)
            put("contentCheckOk", true)
            put("racyCheckOk", true)
            put("playbackContext", JSONObject().apply {
                put("contentPlaybackContext", JSONObject().apply {
                    put("signatureTimestamp", cachedSts)
                })
            })
        }
    }

    fun buildBrowsePayload(browseId: String, params: String? = null): JSONObject {
        return JSONObject().apply {
            put("context", JSONObject().apply {
                put("client", JSONObject().apply {
                    put("clientName", "WEB_REMIX")
                    put("clientVersion", "1.20240801.01.00")
                    put("hl", "id")
                    put("gl", "ID")
                })
            })
            put("browseId", browseId)
            if (!params.isNullOrBlank()) {
                put("params", params)
            }
        }
    }
}
