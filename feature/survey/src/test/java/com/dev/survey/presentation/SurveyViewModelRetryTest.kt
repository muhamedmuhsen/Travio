package com.dev.survey.presentation

import com.dev.survey.components.TravelCategory
import com.example.domain.model.survey.SurveyPreferencesRequest
import com.example.domain.repository.prefernces.PreferencesManager
import com.example.domain.repository.survey.SurveyPreferencesRepository
import com.example.domain.usecase.survey.SubmitSurveyPreferencesUseCase
import com.example.domain.utils.DataError
import com.example.domain.utils.Result
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SurveyViewModelRetryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun givenSubmitFailure_whenRetrySubmission_thenSecondAttemptSucceeds() = runTest {
        val repository = QueuedSurveyPreferencesRepository(
            mutableListOf(
                Result.Error(DataError.Network.ServerError),
                Result.Success(Unit)
            )
        )
        val preferencesManager = FakePreferencesManager()
        val viewModel = SurveyViewModel(
            preferencesManager = preferencesManager,
            submitSurveyPreferencesUseCase = SubmitSurveyPreferencesUseCase(repository)
        )

        viewModel.onAction(SurveyAction.ToggleCategory(0, TravelCategory.BEACHES))
        viewModel.onAction(SurveyAction.NextStep)
        viewModel.onAction(SurveyAction.ToggleCategory(1, TravelCategory.RELAXED))
        viewModel.onAction(SurveyAction.NextStep)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.submitState is SurveySubmitState.Error)

        viewModel.onAction(SurveyAction.RetrySubmission)
        advanceUntilIdle()

        assertEquals(2, repository.callCount)
        assertTrue(viewModel.uiState.value.submitState is SurveySubmitState.Success)
        assertTrue(preferencesManager.isSurveyComplete())
    }

    @Test
    fun givenSubmittingInFlight_whenDuplicateActions_thenOnlyOneRequestIsSent() = runTest {
        val repository = BlockingSurveyPreferencesRepository()
        val viewModel = SurveyViewModel(
            preferencesManager = FakePreferencesManager(),
            submitSurveyPreferencesUseCase = SubmitSurveyPreferencesUseCase(repository)
        )

        viewModel.onAction(SurveyAction.ToggleCategory(0, TravelCategory.BEACHES))
        viewModel.onAction(SurveyAction.NextStep)
        viewModel.onAction(SurveyAction.ToggleCategory(1, TravelCategory.RELAXED))
        viewModel.onAction(SurveyAction.NextStep)
        runCurrent()

        viewModel.onAction(SurveyAction.NextStep)
        viewModel.onAction(SurveyAction.RetrySubmission)
        runCurrent()

        assertEquals(1, repository.callCount)

        repository.complete(Result.Success(Unit))
        advanceUntilIdle()

        assertEquals(1, repository.callCount)
    }

    private class BlockingSurveyPreferencesRepository : SurveyPreferencesRepository {
        var callCount: Int = 0
            private set

        private var deferred: CompletableDeferred<Result<Unit, DataError>> = CompletableDeferred()

        override suspend fun submitUserPreferences(request: SurveyPreferencesRequest): Result<Unit, DataError> {
            callCount += 1
            return deferred.await()
        }

        fun complete(result: Result<Unit, DataError>) {
            deferred.complete(result)
        }
    }

    private class QueuedSurveyPreferencesRepository(
        private val queuedResults: MutableList<Result<Unit, DataError>>
    ) : SurveyPreferencesRepository {
        var callCount: Int = 0
            private set

        override suspend fun submitUserPreferences(request: SurveyPreferencesRequest): Result<Unit, DataError> {
            callCount += 1
            return if (queuedResults.isNotEmpty()) queuedResults.removeAt(0) else Result.Success(Unit)
        }
    }

    private class FakePreferencesManager : PreferencesManager {
        private val onboarding = MutableStateFlow(false)
        private val loggedIn = MutableStateFlow(false)
        private val darkMode = MutableStateFlow(false)
        private val darkModeNullable = MutableStateFlow<Boolean?>(null)
        private val chooseLanguage = MutableStateFlow(false)
        private val surveyComplete = MutableStateFlow(false)

        override suspend fun setOnboardingComplete(complete: Boolean) { onboarding.value = complete }
        override suspend fun isOnboardingComplete(): Boolean = onboarding.value
        override fun observeOnboardingComplete(): Flow<Boolean> = onboarding
        override suspend fun setLoggedIn(loggedIn: Boolean) { this.loggedIn.value = loggedIn }
        override suspend fun isLoggedIn(): Boolean = loggedIn.value
        override fun observeLoggedIn(): Flow<Boolean> = loggedIn
        override suspend fun setChooseLanguage(complete: Boolean) { chooseLanguage.value = complete }
        override fun observeChooseLanguage(): Flow<Boolean> = chooseLanguage
        override suspend fun saveDarkModePreference(isDarkMode: Boolean) {
            darkMode.value = isDarkMode
            darkModeNullable.value = isDarkMode
        }

        override suspend fun isDarkModeEnabled(): Boolean = darkMode.value
        override fun observeDarkMode(): Flow<Boolean> = darkMode
        override fun observeDarkModeNullable(): Flow<Boolean?> = darkModeNullable
        override suspend fun setSurveyComplete(complete: Boolean) { surveyComplete.value = complete }
        override suspend fun isSurveyComplete(): Boolean = surveyComplete.value
        override fun observeSurveyComplete(): Flow<Boolean> = surveyComplete
    }
}


