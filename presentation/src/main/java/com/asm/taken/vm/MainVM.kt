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

    private val _mainMenuState = MutableStateFlow<CommonProcessState<MainMenuState>>(
        CommonProcessState.Loading
    )

    val mainMenuState: StateFlow<CommonProcessState<MainMenuState>> = _mainMenuState

    fun getMainMenuData(gamerId: String) {
        viewModelScope.launch {
            _mainMenuState.update { CommonProcessState.Loading }

            val gamerData = async { getGamerUC.execute(gamerId) }
            val gamesData = async { hasThereBeenAnyProgressUC.execute(gamerId) }

            val resultGamerData = gamerData.await()
            val isThereProgressResult = gamesData.await()
            val mainMenuState: CommonProcessState<MainMenuState> = when {
                resultGamerData is Result.Unsuccessful<GeneralError> -> CommonProcessState.Failure(
                    resultGamerData.error
                )

                isThereProgressResult is Result.Unsuccessful<GeneralError> -> CommonProcessState.Failure(
                    isThereProgressResult.error
                )

                else -> {
                    val gamer = resultGamerData.asSuccessful().data
                    val isThereProgress = isThereProgressResult.asSuccessful().data
                    gamer?.let {
                        CommonProcessState.Success(
                            MainMenuState(
                                gamer = gamer,
                                itHasProgress = isThereProgress
                            )
                        )
                    } ?: CommonProcessState.Failure(GeneralError.ServerError()).also {
                        Log.e(TAG, "The user is authenticated but the associated gamer not exits")
                    }
                }
            }
            _mainMenuState.update { mainMenuState }
        }
    }

    fun closeSession() {
        viewModelScope.launch {
            val currentState = _mainMenuState.value
            if (currentState is CommonProcessState.Success) {
                _mainMenuState.update {
                    CommonProcessState.Success(
                        currentState.data.copy(
                            menuProcessType = MenuProcessType.SessionCloseProcess(CommonProcessState.Loading)
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
                    CommonProcessState.Success(
                        currentState.data.copy(
                            menuProcessType = MenuProcessType.SessionCloseProcess(sessionClosedState)
                        )
                    )
                }
            }
        }
    }

    fun resetProcess() {
        val mainMenuState = _mainMenuState.value
        if (mainMenuState is CommonProcessState.Success) {
            _mainMenuState.update {
                CommonProcessState.Success(mainMenuState.data.copy(menuProcessType = MenuProcessType.Idle))
            }
        }
    }

    fun createNewGame() {

    }
}