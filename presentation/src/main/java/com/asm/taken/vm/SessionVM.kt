package com.asm.taken.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asm.domain.entities.Result
import com.asm.domain.entities.Session
import com.asm.domain.errors.GeneralFailure
import com.asm.domain.use_cases.GetSessionUC
import com.asm.domain.use_cases.SaveSessionUC
import com.asm.taken.model.SessionUiState
import com.asm.taken.utils.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionVM @Inject constructor(
    private val getSessionUC: GetSessionUC
): ViewModel() {

    //region session states

    private val _sessionState: MutableStateFlow<SessionUiState> = MutableStateFlow(SessionUiState.Loading)

    val sessionState: StateFlow<SessionUiState> = _sessionState
    //endregion

    fun isThereSessionActive() {
        viewModelScope.launch {
            val sessionUiState = when (val resultSession = getSessionUC.execute()) {
                is Result.Successful<Session?> -> when (val data = resultSession.data) {
                    is Session.UserRegister -> SessionUiState.UserRegister(data.gamerId)
                    is Session.UserUnregister -> SessionUiState.UnregisterUser(UserData(data.userId, data.userImage))
                    null -> SessionUiState.Logout
                }
                is Result.Unsuccessful<GeneralFailure> -> SessionUiState.Fail(resultSession.failure)
            }
            _sessionState.update {
                sessionUiState
            }
        }
    }
}