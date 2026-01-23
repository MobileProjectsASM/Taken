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
import com.asm.taken.model.CommonProcessState
import com.asm.taken.model.MainMenuState
import com.asm.taken.model.MainMenuUIState
import com.asm.taken.model.MenuProcessType
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
        const val TAG = "main_menu_view_model"
    }

    private val _mainMenuState = MutableStateFlow<MainMenuUIState>(
        MainMenuUIState.Loading
    )

    val mainMenuState: StateFlow<MainMenuUIState> = _mainMenuState

    fun getMainMenuData(gamerId: String) {
        viewModelScope.launch {
            _mainMenuState.update { MainMenuUIState.Loading }

            val gamerData = async { getGamerUC.execute(gamerId) }
            val gamesData = async { hasThereBeenAnyProgressUC.execute(gamerId) }

            val resultGamerData = gamerData.await()
            val isThereProgressResult = gamesData.await()
            val mainMenuState: MainMenuUIState = when {
                resultGamerData is Result.Unsuccessful<GeneralError> -> MainMenuUIState.Failure(
                    resultGamerData.error
                )

                isThereProgressResult is Result.Unsuccessful<GeneralError> -> MainMenuUIState.Failure(
                    isThereProgressResult.error
                )

                else -> {
                    val gamer = resultGamerData.asSuccessful().data
                    val isThereProgress = isThereProgressResult.asSuccessful().data
                    gamer?.let {
                        MainMenuUIState.DataMenuLoaded(
                            MainMenuState(
                                gamer = gamer,
                                itHasProgress = isThereProgress
                            )
                        )
                    } ?: MainMenuUIState.Failure(GeneralError.ServerError()).also {
                        Log.e(TAG, "The user is authenticated but the associated gamer not exits")
                    }
                }
            }
            _mainMenuState.update { mainMenuState }
        }
    }

    fun closeSession() {
        when (val currentState = _mainMenuState.value) {
            is MainMenuUIState.DataMenuLoaded -> {
                viewModelScope.launch {
                    _mainMenuState.update {
                        MainMenuUIState.DataMenuLoaded(
                            currentState.mainMenuState.copy(
                                menuProcessType = MenuProcessType.SessionCloseProcess(
                                    CommonProcessState.Loading
                                )
                            )
                        )
                    }
                    val sessionClosedState =
                        when (val closeSessionResult = closeSessionUC.execute(Unit)) {
                            is Result.Successful<Unit> -> CommonProcessState.Success(Unit)
                            is Result.Unsuccessful<GeneralError> -> CommonProcessState.Failure(
                                closeSessionResult.error
                            )
                        }

                    _mainMenuState.update {
                        MainMenuUIState.DataMenuLoaded(
                            currentState.mainMenuState.copy(
                                menuProcessType = MenuProcessType.SessionCloseProcess(
                                    sessionClosedState
                                )
                            )
                        )
                    }
                }
            }

            is MainMenuUIState.Failure -> {
                viewModelScope.launch {
                    _mainMenuState.update { MainMenuUIState.Loading }
                    val sessionClosedState =
                        when (val closeSessionResult = closeSessionUC.execute(Unit)) {
                            is Result.Successful<Unit> -> MainMenuUIState.SessionClosed
                            is Result.Unsuccessful<GeneralError> -> MainMenuUIState.Failure(
                                closeSessionResult.error
                            )
                        }
                    _mainMenuState.update { sessionClosedState }
                }
            }

            else -> return
        }
    }

    fun resetProcess() {
        val currentState = _mainMenuState.value
        if (currentState is MainMenuUIState.DataMenuLoaded) {
            _mainMenuState.update {
                MainMenuUIState.DataMenuLoaded(currentState.mainMenuState.copy(menuProcessType = MenuProcessType.Idle))
            }
        }
    }

    fun createNewGame() {

    }
}