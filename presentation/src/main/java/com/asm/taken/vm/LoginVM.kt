package com.asm.taken.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asm.domain.entities.asFailure
import com.asm.domain.entities.asSuccessful
import com.asm.domain.errors.GamerError
import com.asm.domain.use_cases.GetGamerUC
import com.asm.taken.model.LoginFormUiState
import com.asm.taken.model.PasswordUiState
import com.asm.taken.model.SignInError
import com.asm.taken.model.AuthResult
import com.asm.taken.model.PhoneNumberUiState
import com.asm.taken.model.SignInState
import com.asm.taken.model.UserIdUiState
import com.asm.taken.model.SendPhoneFormUiState
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
    private val passwordPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$")
    private val phoneNumberPattern = Regex("^[0-9]{10}\$")

    //region MutableStateFlows
    private val _loginFormSTF = MutableStateFlow(LoginFormUiState())
    private val _sendPhoneFormSTF = MutableStateFlow(SendPhoneFormUiState())
    private val _signInSTF = MutableStateFlow<SignInState?>(null)
    //endregion

    //region StateFlows
    val loginFormSTF: StateFlow<LoginFormUiState> = _loginFormSTF
    val sendPhoneFormSTF: StateFlow<SendPhoneFormUiState> = _sendPhoneFormSTF
    val signInSTF: StateFlow<SignInState?> = _signInSTF
    //endregion

    fun validateLoginForm(userId: String?, password: String?) {
        val userIdUiState = validateUserId(userId)
        val passwordUiState = validatePassword(password)
        _loginFormSTF.update {
            it.copy(
                userIdUiState = userIdUiState,
                passwordUiState = passwordUiState,
            )
        }
    }

    fun validatePhoneNumberFrom(phoneNumber: String?) {
        val phoneNumberUiState = validatePhoneNumber(phoneNumber)
        _sendPhoneFormSTF.update {
            it.copy(
                phoneNumberUiState = phoneNumberUiState
            )
        }
    }

    fun loginUser(authResult: AuthResult) {
        viewModelScope.launch {
            when (authResult) {
                is AuthResult.Error -> _signInSTF.update { SignInState.SignInFail(SignInError.AUTH_ERROR) }
                is AuthResult.PhoneCodeSent -> _signInSTF.update { SignInState.PhoneCodeSent(authResult.verificationId) }
                is AuthResult.Successful -> {
                    val userId = authResult.data.userId
                    val gamerResult = getGamerUC.execute(userId)
                    if (gamerResult.isSuccessful) {
                        val gamer = gamerResult.asSuccessful().data
                        _signInSTF.update { SignInState.RegisteredUser(gamer.gamerId) }
                        return@launch
                    }
                    val failure = gamerResult.asFailure().failure
                    if (failure is GamerError.GamerNotExists) {
                        _signInSTF.update { SignInState.UnregisteredUser(userId) }
                        return@launch
                    }
                    _signInSTF.update { SignInState.SignInFail(SignInError.REGISTER_ERROR) }
                }
            }
        }
    }

    fun resetSignInState() {
        _signInSTF.value = null
    }

    fun resetSendPhoneForm() {
        _sendPhoneFormSTF.value = SendPhoneFormUiState()
    }

    private fun validatePhoneNumber(phoneNumber: String?): PhoneNumberUiState {
        if (phoneNumber == null) return PhoneNumberUiState.Init
        if (phoneNumber.isEmpty()) return PhoneNumberUiState.IsEmpty
        if (!phoneNumber.matches(phoneNumberPattern)) return PhoneNumberUiState.IsInvalid(phoneNumber)
        return PhoneNumberUiState.IsValid(phoneNumber)
    }

    private fun validateUserId(userId: String?): UserIdUiState {
        if (userId == null) return UserIdUiState.Init
        if (userId.isEmpty()) return UserIdUiState.IsEmpty
        return UserIdUiState.IsValid(userId)
    }

    private fun validatePassword(password: String?): PasswordUiState {
        if (password == null) return PasswordUiState.Init
        if (password.isEmpty()) return PasswordUiState.IsEmpty
        if (!password.matches(passwordPattern)) return PasswordUiState.IsInvalid(password)
        return PasswordUiState.IsValid(password)
    }

}