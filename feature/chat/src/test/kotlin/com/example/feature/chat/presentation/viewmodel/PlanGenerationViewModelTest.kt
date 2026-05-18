package com.example.feature.chat.presentation.viewmodel

import com.example.feature.chat.domain.model.PlanGenerationState
import com.example.feature.chat.domain.model.PlanStatus
import com.example.feature.chat.domain.repository.FakeChatRepository
import com.example.feature.chat.domain.usecase.ObservePlanStatusUseCase
import com.example.feature.chat.presentation.state.PlanGenerationUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlanGenerationViewModelTest {

    private lateinit var fakeRepository: FakeChatRepository
    private lateinit var observePlanStatusUseCase: ObservePlanStatusUseCase
    private lateinit var viewModel: PlanGenerationViewModel
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeChatRepository()
        observePlanStatusUseCase = ObservePlanStatusUseCase(fakeRepository)
        viewModel = PlanGenerationViewModel(observePlanStatusUseCase)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should start with Idle state`() {
        kotlinx.coroutines.test.runTest {
            val state = viewModel.state.value
            org.junit.Assert.assertTrue(state is PlanGenerationUiState.Idle)
        }
    }

    @Test
    fun `should update state to Success when plan completes`() {
        kotlinx.coroutines.test.runTest {
            val threadId = "thread_123"
            viewModel.startObserving(threadId)
            advanceUntilIdle()
            
            fakeRepository.emitPlanStatus(PlanGenerationState(threadId, PlanStatus.COMPLETED))
            advanceUntilIdle()

            val state = viewModel.state.value
            org.junit.Assert.assertTrue(state is PlanGenerationUiState.Success)
        }
    }
}
