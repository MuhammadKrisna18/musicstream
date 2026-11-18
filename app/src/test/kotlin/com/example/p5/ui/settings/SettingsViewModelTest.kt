package com.example.p5.ui.settings

import com.example.p5.domain.model.UpdateInfo
import com.example.p5.domain.repository.UpdateRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private class FakeUpdateRepository(
        override val currentVersionName: String = "2.9.1"
    ) : UpdateRepository {
        override suspend fun checkLatestRelease(): UpdateInfo? = null
        override suspend fun downloadApk(downloadUrl: String, onProgress: (Float) -> Unit): File = File("dummy.apk")
        override fun installApk(apkFile: File) {}
    }

    private val testDispatcher = StandardTestDispatcher()

    @Test
    fun initialState_loadsCurrentVersionFromRepository() {
        val fakeRepo = FakeUpdateRepository(currentVersionName = "2.9.1")
        val viewModel = SettingsViewModel(updateRepository = fakeRepo, ioDispatcher = testDispatcher)

        assertEquals("2.9.1", viewModel.uiState.value.currentVersion)
        assertEquals(0L, viewModel.uiState.value.cacheSizeBytes)
        assertEquals("0 B", viewModel.uiState.value.formattedCacheSize)
        assertNull(viewModel.uiState.value.userMessage)
    }

    @Test
    fun updateCacheSize_updatesStateAndFormattedSizeAccurately() {
        val fakeRepo = FakeUpdateRepository()
        val viewModel = SettingsViewModel(updateRepository = fakeRepo, ioDispatcher = testDispatcher)

        viewModel.updateCacheSize(1024L * 512L)
        assertEquals(524288L, viewModel.uiState.value.cacheSizeBytes)
        assertEquals("512.0 KB", viewModel.uiState.value.formattedCacheSize)

        viewModel.updateCacheSize(1024L * 1024L * 15L)
        assertEquals("15.0 MB", viewModel.uiState.value.formattedCacheSize)
    }

    @Test
    fun clearUserMessage_clearsActiveMessage() {
        val fakeRepo = FakeUpdateRepository()
        val viewModel = SettingsViewModel(updateRepository = fakeRepo, ioDispatcher = testDispatcher)

        viewModel.clearUserMessage()
        assertNull(viewModel.uiState.value.userMessage)
    }

    @Test
    fun formattedCacheSize_formatsZeroAndNegativeCorrectly() {
        val stateZero = SettingsUiState(cacheSizeBytes = 0L)
        assertEquals("0 B", stateZero.formattedCacheSize)

        val stateNegative = SettingsUiState(cacheSizeBytes = -10L)
        assertEquals("0 B", stateNegative.formattedCacheSize)
    }
}
