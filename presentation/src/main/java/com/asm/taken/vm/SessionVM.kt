package com.asm.taken.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asm.domain.entities.Result
import com.asm.domain.entities.Session
import com.asm.domain.errors.GeneralError
import com.asm.domain.use_cases.GetSessionUC
import com.asm.taken.model.InitRouteUiState
import com.asm.taken.ui.navigation.Authentication
import com.asm.taken.ui.navigation.CreateGamer
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
) : ViewModel() {

    //region session states

    private val _initRouteState: MutableStateFlow<InitRouteUiState> =
        MutableStateFlow(InitRouteUiState.Loading)

    val initRouteState: StateFlow<InitRouteUiState> = _initRouteState
    //endregion

    fun getInitRoute() {
        viewModelScope.launch {
            val initRouteState = when (val resultSession = getSessionUC.execute()) {
                is Result.Successful<Session?> -> when (val session = resultSession.data) {
                    is Session.UserRegister -> InitRouteUiState.Success(MainPage(session.gamerId))
                    is Session.UserUnregister -> session.run { CreateGamer(userId, userImage) }
                        .let {
                            InitRouteUiState.Success(it)
                        }

                    null -> InitRouteUiState.Success(Authentication)
                }

                is Result.Unsuccessful<GeneralError> -> InitRouteUiState.Fail(resultSession.error)
            }
            _initRouteState.update {
                initRouteState
            }
        }
    }
}