package com.asm.taken.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asm.domain.entities.Gamer
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.use_cases.CloseSessionUC
import com.asm.domain.use_cases.GetGamerUC
import com.asm.taken.model.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val closeSessionUC: CloseSessionUC,
    private val getGamerUC: GetGamerUC
) : ViewModel() {

    companion object {
        const val TAG = "MainMenuVM"
    }

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)


    val sessionState: StateFlow<SessionState> = _sessionState

    fun getDataGamer(gamerId: String) {
        viewModelScope.launch {
            _sessionState.update { SessionState.Loading }
            val sessionState: SessionState = when (val resultUC = getGamerUC.execute(gamerId)) {
                is Result.Successful<Gamer?> -> resultUC.data?.let {
                    SessionState.Authenticated(it)
                } ?: SessionState.Fail(GeneralError.ServerError()).also {
                    Log.e(TAG, "The user is authenticated but the associated gamer not exits")
                }

                is Result.Unsuccessful<GeneralError> -> SessionState.Fail(resultUC.error)
            }
            _sessionState.update { sessionState }
        }
    }
}