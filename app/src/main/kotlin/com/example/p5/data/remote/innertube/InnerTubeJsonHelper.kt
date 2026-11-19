package com.example.p5.data.remote.innertube

import com.example.p5.data.config.MusicConfig
import com.example.p5.domain.model.PlaylistType
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

object InnerTubeJsonHelper {

    private val durationRegex = Regex("""^\d{1,2}:\d{2}(?::\d{2})?$""")

    fun isDurationFormat(text: String): Boolean {
        return text.matches(durationRegex)
    }

    fun isViewCountText(text: String): Boolean {
        return text.contains("ditonton", ignoreCase = true) ||
            text.contains("views", ignoreCase = true) ||
            text.contains("diputar", ignoreCase = true) ||
            text.contains("plays", ignoreCase = true)
    }

    fun isArtistChannelId(browseId: String?): Boolean {
        return !browseId.isNullOrBlank() &&
            (browseId.startsWith("UC") || browseId.startsWith("FE") || browseId.startsWith("MP"))
    }

    fun isSearchPlaylistBrowseId(browseId: String): Boolean {
        return browseId.startsWith("VL") ||
            browseId.startsWith("PL") ||
            browseId.startsWith("RD") ||
            browseId.startsWith("MPRE")
    }

    fun isBrowsePlaylistId(browseId: String): Boolean {
        return browseId.startsWith("VL") ||
            browseId.startsWith("PL") ||
            browseId.startsWith("RDEM")
    }

    fun extractFirstRunText(parent: JSONObject?, key: String): String {
        return parent?.optJSONObject(key)
            ?.optJSONArray("runs")
            ?.optJSONObject(0)
            ?.optString("text")
            .orEmpty()
    }

    fun extractSimpleOrFirstRunText(node: JSONObject?): String {
        if (node == null) return ""
        val simple = node.optString("simpleText")
        if (simple.isNotEmpty()) return simple
        return node.optJSONArray("runs")?.optJSONObject(0)?.optString("text").orEmpty()
    }

    fun extractLastThumbnailUrl(thumbs: JSONArray?, fallbackVideoId: String? = null): String {
        val lastUrl = if (thumbs != null && thumbs.length() > 0) {
            thumbs.optJSONObject(thumbs.length() - 1)?.optString("url")
        } else {
            null
        }
        if (!lastUrl.isNullOrEmpty()) return lastUrl
        return if (!fallbackVideoId.isNullOrEmpty()) {
            MusicConfig.THUMBNAIL_HQ_TEMPLATE.format(fallbackVideoId)
        } else {
            ""
        }
    }

    fun extractMusicThumbnailUrl(
        parent: JSONObject?,
        rendererKey: String,
        fallbackVideoId: String? = null
    ): String {
        val thumbs = parent?.optJSONObject(rendererKey)
            ?.optJSONObject("musicThumbnailRenderer")
            ?.optJSONObject("thumbnail")
            ?.optJSONArray("thumbnails")
        return extractLastThumbnailUrl(thumbs, fallbackVideoId)
    }

    fun inferPlaylistType(title: String): PlaylistType {
        return when {
            title.contains("mix", ignoreCase = true) || title.contains("radio", ignoreCase = true) -> PlaylistType.RANDOM
            title.contains("pop", ignoreCase = true) ||
                title.contains("rock", ignoreCase = true) ||
                title.contains("indie", ignoreCase = true) ||
                title.contains("jazz", ignoreCase = true) ||
                title.contains("dangdut", ignoreCase = true) ||
                title.contains("koplo", ignoreCase = true) -> PlaylistType.GENRE
            else -> PlaylistType.ARTIST
        }
    }

    fun formatStripeColorHex(stripeColor: Long, defaultColor: String = "#00E5BF"): String {
        return if (stripeColor != 0L) {
            String.format(Locale.ROOT, "#%06X", 0xFFFFFF and stripeColor.toInt())
        } else {
            defaultColor
        }
    }
}
