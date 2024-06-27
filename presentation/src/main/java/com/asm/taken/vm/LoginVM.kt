package com.asm.taken.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asm.domain.entities.asFailure
import com.asm.domain.entities.asSuccessful
import com.asm.domain.errors.GamerError
import com.asm.domain.use_cases.GetGamerUC
import com.asm.taken.model.FormUiState
import com.asm.taken.model.PasswordUiState
import com.asm.taken.model.SignInError
import com.asm.taken.model.SignInResult
import com.asm.taken.model.SignInState
import com.asm.taken.model.UserIdUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    private val getGamerUC: GetGamerUC
) : ViewModel() {

    //region MutableStateFlows
    private val _formUiStateSTF = MutableStateFlow(FormUiState())
    private val _signInUiStateSTF = MutableStateFlow<SignInState?>(null)
    //endregion

    //region StateFlows
    val formUiStateSTF: StateFlow<FormUiState> = _formUiStateSTF
    val signInUiStateSTF: StateFlow<SignInState?> = _signInUiStateSTF
    //endregion

    fun updateDataLogin(userId: String?, password: String?) {
        val userIdUiState = validateUserId(userId)
        val passwordUiState = validatePassword(password)
        _formUiStateSTF.update {
            it.copy(
                userIdUiState = userIdUiState,
                passwordUiState = passwordUiState,
            )
        }
    }

    fun loginUser(signInResult: SignInResult) {
        viewModelScope.launch {
            if (signInResult.data == null) {
                _signInUiStateSTF.update { SignInState.SignInFail(SignInError.AUTH_ERROR) }
                return@launch
            }
            val userId = signInResult.data.userId
            val gamerResult = getGamerUC.execute(userId)
            if (gamerResult.isSuccessful) {
                val gamer = gamerResult.asSuccessful().data
                _signInUiStateSTF.update { SignInState.RegisteredUser(gamer.gamerId) }
                return@launch
            }
            val failure = gamerResult.asFailure().failure
            if (failure is GamerError.GamerNotExists) {
                _signInUiStateSTF.update { SignInState.UnregisteredUser(userId) }
                return@launch
            }
            _signInUiStateSTF.update { SignInState.SignInFail(SignInError.REGISTER_ERROR) }
            return@launch
        }
    }

    fun resetSignInState() {
        _signInUiStateSTF.value = null
    }

    private fun validateUserId(userId: String?): UserIdUiState {
        if (userId == null) return UserIdUiState.Init
        if (userId.isEmpty()) return UserIdUiState.IsEmpty
        return UserIdUiState.IsValid(userId)
    }

    private fun validatePassword(password: String?): PasswordUiState {
        if (password == null) return PasswordUiState.Init
        if (password.isEmpty()) return PasswordUiState.IsEmpty
        if (!password.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$"))) return PasswordUiState.IsInvalid(
            password
        )
        return PasswordUiState.IsValid(password)
    }

}