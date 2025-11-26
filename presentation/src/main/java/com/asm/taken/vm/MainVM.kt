package com.asm.taken.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asm.domain.entities.Result
import com.asm.domain.entities.asSuccessful
import com.asm.domain.errors.GeneralError
import com.asm.domain.use_cases.CloseSessionUC
import com.asm.domain.use_cases.GetGamerUC
import com.asm.domain.use_cases.HasThereBeenAnyProgressUC
import com.asm.taken.model.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainVM @Inject constructor(
    private val closeSessionUC: CloseSessionUC,
    private val getGamerUC: GetGamerUC,
    private val hasThereBeenAnyProgressUC: HasThereBeenAnyProgressUC
) : ViewModel() {

    companion object {
        const val TAG = "MainMenuVM"
    }

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)


    val sessionState: StateFlow<SessionState> = _sessionState

    fun getMainDataGamer(gamerId: String) {
        viewModelScope.launch {
            _sessionState.update { SessionState.Loading }
            val gamerData = async { getGamerUC.execute(gamerId) }
            val gamesData = async { hasThereBeenAnyProgressUC.execute(gamerId) }

            val resultGamerData = gamerData.await()
            val isThereProgressResult = gamesData.await()

            val sessionState: SessionState = when {
                resultGamerData is Result.Unsuccessful<GeneralError> -> SessionState.Fail(resultGamerData.error)
                isThereProgressResult is Result.Unsuccessful<GeneralError> -> SessionState.Fail(isThereProgressResult.error)
                else -> {
                    val gamer = resultGamerData.asSuccessful().data
                    val isThereProgress = isThereProgressResult.asSuccessful().data
                    gamer?.let {
                        SessionState.Authenticated(it, isThereProgress)
                    } ?: SessionState.Fail(GeneralError.ServerError()).also {
                        Log.e(TAG, "The user is authenticated but the associated gamer not exits")
                    }
                }
            }
            _sessionState.update { sessionState }
        }
    }
}