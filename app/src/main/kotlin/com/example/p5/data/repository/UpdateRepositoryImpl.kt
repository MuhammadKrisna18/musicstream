package com.example.p5.data.repository

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.p5.BuildConfig
import com.example.p5.domain.model.UpdateInfo
import com.example.p5.domain.repository.UpdateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream

class UpdateRepositoryImpl(
    private val context: Context,
    private val okHttpClient: OkHttpClient,
    private val repoOwnerAndName: String = "MuhammadKrisna18/musicstream"
) : UpdateRepository {

    override val currentVersionName: String
        get() = BuildConfig.VERSION_NAME

    override suspend fun checkLatestRelease(): UpdateInfo? = withContext(Dispatchers.IO) {
        val url = "https://api.github.com/repos/$repoOwnerAndName/releases/latest"
        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/vnd.github.v3+json")
            .header("User-Agent", "MusicStream-Android")
            .build()

        try {
            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                // If 404, no release exists yet
                return@withContext null
            }

            val body = response.body?.string() ?: return@withContext null
            val json = JSONObject(body)

            val tagName = json.optString("tag_name", "").trim()
            val releaseNotes = json.optString("body", "").trim()
            val assets = json.optJSONArray("assets")

            if (tagName.isEmpty()) return@withContext null

            if (isNewerVersion(currentVersionName, tagName)) {
                if (assets != null) {
                    for (i in 0 until assets.length()) {
                        val asset = assets.getJSONObject(i)
                        val name = asset.optString("name", "")
                        val downloadUrl = asset.optString("browser_download_url", "")
                        val size = asset.optLong("size", 0L)

                        if (name.endsWith(".apk", ignoreCase = true) && downloadUrl.isNotEmpty()) {
                            return@withContext UpdateInfo(
                                latestVersion = tagName,
                                releaseNotes = releaseNotes,
                                downloadUrl = downloadUrl,
                                apkFileName = name,
                                apkSize = size
                            )
                        }
                    }
                }
            }

            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun downloadApk(downloadUrl: String, onProgress: (Float) -> Unit): File =
        withContext(Dispatchers.IO) {
            val request = Request.Builder()
                .url(downloadUrl)
                .header("User-Agent", "MusicStream-Android")
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                throw IllegalStateException("Gagal mengunduh APK: HTTP ${response.code}")
            }

            val responseBody = response.body ?: throw IllegalStateException("Response body kosong")
            val totalBytes = responseBody.contentLength()

            // Save APK in app external files downloads folder so package manager can read it
            val downloadDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.cacheDir
            if (!downloadDir.exists()) {
                downloadDir.mkdirs()
            }
            val apkFile = File(downloadDir, "musicstream_update.apk")
            if (apkFile.exists()) {
                apkFile.delete()
            }

            responseBody.byteStream().use { inputStream ->
                FileOutputStream(apkFile).use { outputStream ->
                    val buffer = ByteArray(8 * 1024)
                    var bytesRead: Int
                    var totalRead = 0L

                    while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                        outputStream.write(buffer, 0, bytesRead)
                        totalRead += bytesRead
                        if (totalBytes > 0) {
                            val progress = totalRead.toFloat() / totalBytes
                            onProgress(progress.coerceIn(0f, 1f))
                        }
                    }
                    outputStream.flush()
                }
            }

            apkFile
        }

    override fun installApk(apkFile: File) {
        val authority = "${context.packageName}.fileprovider"
        val apkUri = FileProvider.getUriForFile(context, authority, apkFile)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(intent)
    }

    private fun isNewerVersion(current: String, latest: String): Boolean {
        val currentTokens = current.trimStart('v', 'V').split(".", "-")
        val latestTokens = latest.trimStart('v', 'V').split(".", "-")

        val currentNums = currentTokens.mapNotNull { it.toIntOrNull() }
        val latestNums = latestTokens.mapNotNull { it.toIntOrNull() }

        val maxLen = maxOf(currentNums.size, latestNums.size)
        for (i in 0 until maxLen) {
            val c = currentNums.getOrElse(i) { 0 }
            val l = latestNums.getOrElse(i) { 0 }
            if (l > c) return true
            if (l < c) return false
        }
        return false
    }
}

