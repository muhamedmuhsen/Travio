package com.example.feature.code

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class CodeViewModel @Inject constructor(
    //private val codeUseCase:CodeUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(CodeState())
    val state = _state.asStateFlow()

    private val _event = Channel<CodeEvent>()
    val event = _event.receiveAsFlow()


    fun sendEvent(event: CodeEvent) {
        viewModelScope.launch {
            _event.send(event)
        }
    }

    fun onBackClicked() {
        sendEvent(CodeEvent.OnBackClicked)
    }

    fun onContinueClicked() {
        /*TODO: validate the codes*/
        /*TODO: if the 6 digits is n't empty and correct, -> navigate to to reset password screen*/
        if (_state.value.code.all { it != "0" }) {
            /*TODO: check if it is equal the code i got from the backend*/

        } else {
            _state.value = _state.value.copy(isCodeError = true)
            sendEvent(CodeEvent.ShowError("Please fill all the filled"))
        }

    }


}