package com.example.p5.ui.settings

import com.example.p5.domain.model.AppThemeId
import com.example.p5.domain.model.UpdateInfo
import com.example.p5.domain.repository.ThemeRepository
import com.example.p5.domain.repository.UpdateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private class FakeUpdateRepository(
        override val currentVersionName: String = "2.9.1"
    ) : UpdateRepository {
        override suspend fun checkLatestRelease(): UpdateInfo? = null
        override suspend fun downloadApk(downloadUrl: String, onProgress: (Float) -> Unit): File = File("dummy.apk")
        override fun installApk(apkFile: File) {}
    }

    private class FakeThemeRepository(
        initialTheme: AppThemeId = AppThemeId.DEFAULT_DARK
    ) : ThemeRepository {
        val themeFlow = MutableStateFlow(initialTheme)
        override val currentTheme: StateFlow<AppThemeId> = themeFlow.asStateFlow()
        override suspend fun setTheme(themeId: AppThemeId) {
            themeFlow.value = themeId
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_loadsCurrentVersionAndTheme() {
        val fakeRepo = FakeUpdateRepository(currentVersionName = "2.9.1")
        val fakeThemeRepo = FakeThemeRepository(initialTheme = AppThemeId.DEFAULT_DARK)
        val viewModel = SettingsViewModel(
            updateRepository = fakeRepo,
            themeRepository = fakeThemeRepo,
            ioDispatcher = testDispatcher
        )

        assertEquals("2.9.1", viewModel.uiState.value.currentVersion)
        assertEquals(AppThemeId.DEFAULT_DARK, viewModel.uiState.value.currentTheme)
        assertEquals(0L, viewModel.uiState.value.cacheSizeBytes)
        assertEquals("0 B", viewModel.uiState.value.formattedCacheSize)
        assertNull(viewModel.uiState.value.userMessage)
    }

    @Test
    fun selectTheme_updatesCurrentThemeInState() = runTest {
        val fakeRepo = FakeUpdateRepository()
        val fakeThemeRepo = FakeThemeRepository()
        val viewModel = SettingsViewModel(
            updateRepository = fakeRepo,
            themeRepository = fakeThemeRepo,
            ioDispatcher = testDispatcher
        )

        assertEquals(AppThemeId.DEFAULT_DARK, viewModel.uiState.value.currentTheme)

        viewModel.selectTheme(AppThemeId.ANIME_CYBERPUNK)

        assertEquals(AppThemeId.ANIME_CYBERPUNK, viewModel.uiState.value.currentTheme)
        assertEquals(AppThemeId.ANIME_CYBERPUNK, fakeThemeRepo.currentTheme.value)
    }

    @Test
    fun updateCacheSize_updatesStateAndFormattedSizeAccurately() {
        val fakeRepo = FakeUpdateRepository()
        val fakeThemeRepo = FakeThemeRepository()
        val viewModel = SettingsViewModel(
            updateRepository = fakeRepo,
            themeRepository = fakeThemeRepo,
            ioDispatcher = testDispatcher
        )

        viewModel.updateCacheSize(1024L * 512L)
        assertEquals(524288L, viewModel.uiState.value.cacheSizeBytes)
        assertEquals("512.0 KB", viewModel.uiState.value.formattedCacheSize)

        viewModel.updateCacheSize(1024L * 1024L * 15L)
        assertEquals("15.0 MB", viewModel.uiState.value.formattedCacheSize)
    }

    @Test
    fun clearUserMessage_clearsActiveMessage() {
        val fakeRepo = FakeUpdateRepository()
        val fakeThemeRepo = FakeThemeRepository()
        val viewModel = SettingsViewModel(
            updateRepository = fakeRepo,
            themeRepository = fakeThemeRepo,
            ioDispatcher = testDispatcher
        )

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
