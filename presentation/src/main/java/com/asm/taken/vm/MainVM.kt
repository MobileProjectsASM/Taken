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
import com.asm.taken.model.GamerState
import com.asm.taken.model.MainMenuState
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

    private val _gamerState = MutableStateFlow<GamerState>(GamerState.Loading)
    private val _mainMenuState = MutableStateFlow<MainMenuState?>(null)


    val gamerState: StateFlow<GamerState> = _gamerState
    val mainMenuState: StateFlow<MainMenuState?> = _mainMenuState

    fun getMainDataGamer(gamerId: String) {
        viewModelScope.launch {
            _gamerState.update { GamerState.Loading }
            val gamerData = async { getGamerUC.execute(gamerId) }
            val gamesData = async { hasThereBeenAnyProgressUC.execute(gamerId) }

            val resultGamerData = gamerData.await()
            val isThereProgressResult = gamesData.await()

            val gamerState: GamerState = when {
                resultGamerData is Result.Unsuccessful<GeneralError> -> GamerState.Fail(resultGamerData.error)
                isThereProgressResult is Result.Unsuccessful<GeneralError> -> GamerState.Fail(isThereProgressResult.error)
                else -> {
                    val gamer = resultGamerData.asSuccessful().data
                    val isThereProgress = isThereProgressResult.asSuccessful().data
                    gamer?.let {
                        GamerState.Successful(it, isThereProgress)
                    } ?: GamerState.Fail(GeneralError.ServerError()).also {
                        Log.e(TAG, "The user is authenticated but the associated gamer not exits")
                    }
                }
            }
            _gamerState.update { gamerState }
        }
    }

    fun closeSession() {
        viewModelScope.launch {
            _mainMenuState.update { MainMenuState.Loading }
            val mainMenuState = when (val closeSessionResult = closeSessionUC.execute(Unit)) {
                is Result.Successful<Unit> -> MainMenuState.SessionClosed
                is Result.Unsuccessful<GeneralError> -> MainMenuState.Fail(closeSessionResult.error)
            }
            _mainMenuState.update { mainMenuState }
        }
    }
}