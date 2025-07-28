package com.asm.taken.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asm.domain.entities.Result
import com.asm.domain.entities.Session
import com.asm.domain.errors.GeneralFailure
import com.asm.domain.use_cases.CloseSessionUC
import com.asm.domain.use_cases.GetSessionUC
import com.asm.taken.model.InitRouteUiState
import com.asm.taken.model.SessionError
import com.asm.taken.model.CloseSessionUiState
import com.asm.taken.ui.navigation.CreateGamer
import com.asm.taken.ui.navigation.Login
import com.asm.taken.ui.navigation.MainPage
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

    private val _initRouteState: MutableStateFlow<InitRouteUiState> = MutableStateFlow(InitRouteUiState.Loading)

    val initRouteState: StateFlow<InitRouteUiState> = _initRouteState
    //endregion

    fun getInitRoute() {
        viewModelScope.launch {
            val initRouteState = when (val resultSession = getSessionUC.execute()) {
                is Result.Successful<Session?> -> when (val data = resultSession.data) {
                    is Session.UserRegister -> InitRouteUiState.Success(MainPage(data.gamerId))//SessionUiState.UserRegister(data.gamerId)
                    is Session.UserUnregister -> InitRouteUiState.Success(CreateGamer(
                        id = data.userId,
                        image = data.userImage
                    ))
                    null -> InitRouteUiState.Success(Login)
                }
                is Result.Unsuccessful<GeneralFailure> -> InitRouteUiState.Fail(when (resultSession.failure) {
                    GeneralFailure.NetworkConnection -> SessionError.NETWORK_CONNECTION
                    is GeneralFailure.ServerError -> SessionError.SERVER_ERROR
                    GeneralFailure.Unknown -> SessionError.UNKNOWN
                })
            }
            _initRouteState.update {
                initRouteState
            }
        }
    }
}