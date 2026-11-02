package com.example.p5.ui.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.p5.domain.model.UpdateInfo
import com.example.p5.domain.repository.UpdateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class UpdateUiState(
    val isChecking: Boolean = false,
    val updateInfo: UpdateInfo? = null,
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val downloadedApkFile: File? = null,
    val showDialog: Boolean = false,
    val userMessage: String? = null
)

class UpdateViewModel(
    private val updateRepository: UpdateRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UpdateUiState())
    val uiState: StateFlow<UpdateUiState> = _uiState.asStateFlow()

    val currentVersionName: String
        get() = updateRepository.currentVersionName

    init {
        // Automatically check for update on app startup (silent)
        checkForUpdates(isManualCheck = false)
    }

    fun checkForUpdates(isManualCheck: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isChecking = true) }
            try {
                val latest = updateRepository.checkLatestRelease()
                if (latest != null) {
                    _uiState.update {
                        it.copy(
                            isChecking = false,
                            updateInfo = latest,
                            showDialog = true
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isChecking = false,
                            userMessage = if (isManualCheck) "Aplikasi sudah dalam versi terbaru ($currentVersionName)" else null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isChecking = false,
                        userMessage = if (isManualCheck) "Gagal memeriksa pembaruan: ${e.localizedMessage ?: "Koneksi bermasalah"}" else null
                    )
                }
            }
        }
    }

    fun startDownload() {
        val info = _uiState.value.updateInfo ?: return
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isDownloading = true,
                    downloadProgress = 0f
                )
            }
            try {
                val apkFile = updateRepository.downloadApk(info.downloadUrl) { progress ->
                    _uiState.update { it.copy(downloadProgress = progress) }
                }
                _uiState.update {
                    it.copy(
                        isDownloading = false,
                        downloadedApkFile = apkFile
                    )
                }
                // Automatically prompt user to install the downloaded APK
                installDownloadedApk()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isDownloading = false,
                        userMessage = "Gagal mengunduh pembaruan: ${e.localizedMessage ?: "Error"}"
                    )
                }
            }
        }
    }

    fun installDownloadedApk() {
        val file = _uiState.value.downloadedApkFile ?: return
        try {
            updateRepository.installApk(file)
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    userMessage = "Gagal membuka installer: ${e.localizedMessage ?: "Error"}"
                )
            }
        }
    }

    fun dismissDialog() {
        if (!_uiState.value.isDownloading) {
            _uiState.update { it.copy(showDialog = false) }
        }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }
}

