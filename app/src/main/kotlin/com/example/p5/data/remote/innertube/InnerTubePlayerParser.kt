package com.example.p5.data.remote.innertube

import com.example.p5.data.config.MusicConfig
import com.example.p5.domain.model.AudioStream
import org.json.JSONArray
import org.json.JSONObject

class InnerTubePlayerParser {

    fun parsePlayerResponse(videoId: String, jsonString: String): AudioStream {
        val root = JSONObject(jsonString)
        val playabilityStatus = root.optJSONObject("playabilityStatus")
        val status = playabilityStatus?.optString("status").orEmpty()
        if (status != "OK") {
            val reason = playabilityStatus?.optString("reason") ?: "Stream unavailable ($status)"
            throw IllegalStateException(reason)
        }

        val streamingData = root.optJSONObject("streamingData") ?: throw IllegalStateException("No streamingData found")
        val adaptiveFormats = streamingData.optJSONArray("adaptiveFormats") ?: JSONArray()

        var bestAudioUrl: String? = null
        var bestBitrate: Double? = null
        var bestFormat = "m4a"

        for (i in 0 until adaptiveFormats.length()) {
            val formatObj = adaptiveFormats.getJSONObject(i)
            val mimeType = formatObj.optString("mimeType", "")
            if (!mimeType.contains("audio")) continue

            val url = formatObj.optString("url")
            if (url.isNullOrEmpty()) continue

            val itag = formatObj.optInt("itag", 0)
            val bitrate = formatObj.optDouble("bitrate", 0.0)

            if (itag == 140) {
                bestAudioUrl = url
                bestBitrate = bitrate
                bestFormat = "m4a"
                break
            } else if (bestAudioUrl == null || bitrate > (bestBitrate ?: 0.0)) {
                bestAudioUrl = url
                bestBitrate = bitrate
                bestFormat = if (mimeType.contains("mp4")) "m4a" else "webm"
            }
        }

        val finalUrl = bestAudioUrl ?: throw IllegalStateException("No playable direct audio stream found")

        val videoDetails = root.optJSONObject("videoDetails")
        val title = videoDetails?.optString("title") ?: "Unknown Title"
        val author = videoDetails?.optString("author") ?: "YouTube Music"
        val lengthSeconds = videoDetails?.optString("lengthSeconds")?.toIntOrNull() ?: 0
        val thumbUrl = MusicConfig.THUMBNAIL_HQ_TEMPLATE.format(videoId)

        return AudioStream(
            id = videoId,
            title = title,
            artist = author,
            streamUrl = finalUrl,
            durationSeconds = lengthSeconds,
            durationFormatted = InnerTubeFormatter.formatSeconds(lengthSeconds),
            thumbnailUrl = thumbUrl,
            format = bestFormat,
            bitrate = bestBitrate
        )
    }
}
