package com.asm.taken.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.repositories.AuthRepository
import com.asm.domain.use_cases.SignInUserUC
import com.asm.taken.model.AuthState
import com.asm.taken.model.AuthTypeState
import com.asm.taken.model.EmailAndPasswordFormState
import com.asm.taken.model.InputEmailError
import com.asm.taken.model.InputPasswordError
import com.asm.taken.model.InputState
import com.asm.taken.model.InputUiState
import com.asm.taken.model.LoginUIState2
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginVM2 @Inject constructor(
    private val signInUserUC: SignInUserUC
) : ViewModel() {

    private lateinit var authRepository: AuthRepository
    private val _loginUIState2: MutableStateFlow<LoginUIState2> = MutableStateFlow(LoginUIState2())

    val loginUIState2: StateFlow<LoginUIState2> = _loginUIState2

    fun setAuthRepository(authRepository: AuthRepository) {
        this.authRepository = authRepository
    }

    fun resetProcessState() {
        _loginUIState2.update { it.copy(authTypeState = AuthTypeState.Idle) }
    }

    fun validateLoginForm(email: String, password: String) {
        val emailErrors = validateEmail(email)
        val passwordErrors = validatePasswordEmpty(password)
        val emailUiState = emailErrors.run {
            if (isEmpty()) InputUiState(email, InputState.Success)
            else InputUiState(email, InputState.Error(emailErrors))
        }
        val passwordUiState = passwordErrors.run {
            if (isEmpty()) InputUiState(password, InputState.Success)
            else InputUiState(password, InputState.Error(passwordErrors))
        }
        val emailAndPasswordFormState = EmailAndPasswordFormState(
            emailUiState = emailUiState,
            passwordUiState = passwordUiState
        )
        _loginUIState2.update { it.copy(emailAndPasswordFormState = emailAndPasswordFormState) }
    }

    fun signInWithEmailAndPassword(
        email: String,
        password: String
    ) {
        viewModelScope.launch {
            val authTypeState = AuthTypeState.EmailAndPasswordAuthType(AuthState.Loading)
            _loginUIState2.update { it.copy(authTypeState = authTypeState) }

            val credentials = SignInUserUC.CredentialType.EmailAndPassword(
                email = email,
                password = password,
                authRepository = authRepository
            )
            val authEmailAndPasswordState = when (val result = signInUserUC.execute(credentials)) {
                is Result.Successful<SignInUserUC.User> -> when (val user = result.data) {
                    is SignInUserUC.User.RegisteredUser -> AuthTypeState.EmailAndPasswordAuthType(
                        AuthState.RegisteredUser(user.gamerId)
                    )
                    is SignInUserUC.User.UnregisteredUser -> AuthTypeState.EmailAndPasswordAuthType(
                        AuthState.UnregisteredUser(user.authUser)
                    )
                }
                is Result.Unsuccessful<GeneralError> -> AuthTypeState.EmailAndPasswordAuthType(
                    AuthState.Error(result.error)
                )
            }
            _loginUIState2.update { it.copy(authTypeState = authEmailAndPasswordState) }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            val authTypeState = AuthTypeState.GoogleAuthType(AuthState.Loading)
            _loginUIState2.update { it.copy(authTypeState = authTypeState) }

            val credentials = SignInUserUC.CredentialType.Google(
                authRepository = authRepository
            )
            val authGoogleState = when (val result = signInUserUC.execute(credentials)) {
                is Result.Successful<SignInUserUC.User> -> when (val user = result.data) {
                    is SignInUserUC.User.RegisteredUser -> AuthTypeState.GoogleAuthType(
                        AuthState.RegisteredUser(user.gamerId)
                    )
                    is SignInUserUC.User.UnregisteredUser -> AuthTypeState.GoogleAuthType(
                        AuthState.UnregisteredUser(user.authUser)
                    )
                }
                is Result.Unsuccessful<GeneralError> -> AuthTypeState.GoogleAuthType(
                    AuthState.Error(result.error)
                )
            }
            _loginUIState2.update { it.copy(authTypeState = authGoogleState) }
        }
    }

    fun signInWithFacebook() {
        viewModelScope.launch {
            val authTypeState = AuthTypeState.FacebookAuthType(AuthState.Loading)
            _loginUIState2.update { it.copy(authTypeState = authTypeState) }

            val credentials = SignInUserUC.CredentialType.Facebook(
                authRepository = authRepository
            )
            val authFacebookState = when (val result = signInUserUC.execute(credentials)) {
                is Result.Successful<SignInUserUC.User> -> when (val user = result.data) {
                    is SignInUserUC.User.RegisteredUser -> AuthTypeState.FacebookAuthType(
                        AuthState.RegisteredUser(user.gamerId)
                    )
                    is SignInUserUC.User.UnregisteredUser -> AuthTypeState.FacebookAuthType(
                        AuthState.UnregisteredUser(user.authUser)
                    )
                }
                is Result.Unsuccessful<GeneralError> -> AuthTypeState.FacebookAuthType(
                    AuthState.Error(result.error)
                )
            }
            _loginUIState2.update { it.copy(authTypeState = authFacebookState) }
        }
    }

    private fun validateEmail(email: String): List<InputEmailError> {
        val errors = mutableListOf<InputEmailError>()
        if (email.isEmpty()) errors.add(InputEmailError.EMPTY)
        if (!email.contains("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex())) errors.add(
            InputEmailError.EMAIL_INVALID
        )
        return errors
    }

    private fun validatePasswordEmpty(password: String): List<InputPasswordError> {
        val errors = mutableListOf<InputPasswordError>()
        if (password.isEmpty()) errors.add(InputPasswordError.EMPTY)
        return errors
    }
}

