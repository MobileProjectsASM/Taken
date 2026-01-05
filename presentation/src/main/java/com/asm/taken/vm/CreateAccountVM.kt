package com.asm.taken.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.use_cases.CreateAccountUC
import com.asm.taken.model.CreateAccountFormState
import com.asm.taken.model.CreateAccountProcessState
import com.asm.taken.model.CreateAccountUIState
import com.asm.taken.model.InputEmailError
import com.asm.taken.model.InputPasswordError
import com.asm.taken.model.InputRepeatValueError
import com.asm.taken.model.InputState
import com.asm.taken.model.InputUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateAccountVM @Inject constructor(
    private val createAccountUC: CreateAccountUC
): ViewModel() {

    companion object {
        const val TAG = "create_account_view_model"
    }

    private val _createAccountUIState: MutableStateFlow<CreateAccountUIState> = MutableStateFlow(
        CreateAccountUIState()
    )

    val createAccountUIState: StateFlow<CreateAccountUIState> = _createAccountUIState

    fun resetProcessState() {
        _createAccountUIState.update {
            it.copy(createAccountProcessState = CreateAccountProcessState.Idle)
        }
    }

    fun resetFormState() {
        _createAccountUIState.update {
            it.copy(createAccountFormState = CreateAccountFormState())
        }
    }

    fun createAccount(email: String, password: String) {
        viewModelScope.launch {
            val createAccountProcessState = CreateAccountProcessState.Loading
            _createAccountUIState.update { it.copy(createAccountProcessState = createAccountProcessState) }
            val result = createAccountUC.execute(
                params = CreateAccountUC.CreateAccountParams(
                    email = email,
                    password = password
                )
            )
            val state = when (result) {
                is Result.Successful<Unit> -> CreateAccountProcessState.AccountProcessCreated
                is Result.Unsuccessful<GeneralError> -> CreateAccountProcessState.Error(result.error)
            }
            _createAccountUIState.update { it.copy(createAccountProcessState = state) }
        }
    }

    fun validateFormCreateAccount(
        email: String,
        password: String,
        passwordRepeat: String
    ) {
        val emailErrors = validateEmail(email)
        val passwordErrors = validatePassword(password)
        val passwordRepeatErrors = validatePasswordRepeat(password, passwordRepeat)
        val emailUiState: InputUiState<String, InputEmailError> = emailErrors.run {
            when {
                isEmpty() -> InputUiState(email, InputState.Success)
                else -> InputUiState(email, InputState.Error(this))
            }
        }
        val passwordUiState: InputUiState<String, InputPasswordError> = passwordErrors.run {
            when {
                isEmpty() -> InputUiState(password, InputState.Success)
                else -> InputUiState(password, InputState.Error(this))
            }
        }
        val passwordRepeatUiState: InputUiState<String, InputRepeatValueError> =
            passwordRepeatErrors.run {
                when {
                    isEmpty() -> InputUiState(passwordRepeat, InputState.Success)
                    else -> InputUiState(passwordRepeat, InputState.Error(this))
                }
            }
        val createAccountFormState = CreateAccountFormState(
            emailUiState = emailUiState,
            passwordUiState = passwordUiState,
            passwordRepeatUiState = passwordRepeatUiState
        )
        _createAccountUIState.update {
            it.copy(createAccountFormState = createAccountFormState)
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

    private fun validatePassword(password: String): List<InputPasswordError> {
        val errors = mutableListOf<InputPasswordError>()
        if (password.isEmpty()) errors.add(InputPasswordError.EMPTY)
        if (password.count() < 8) errors.add(InputPasswordError.LEAST_THAN_8_CHARACTERS)
        if (!password.contains("[A-Z]".toRegex())) errors.add(InputPasswordError.LEAST_ONE_UPPERCASE)
        if (!password.contains("\\d".toRegex())) errors.add(InputPasswordError.LEAST_ONE_NUMBER)
        if (!password.contains("[@$!%*?&#]".toRegex())) errors.add(InputPasswordError.LEAST_ONE_SPECIAL_CHARACTER)
        return errors
    }

    private fun validatePasswordRepeat(
        password: String,
        passwordRepeat: String
    ): List<InputRepeatValueError> {
        val errors = mutableListOf<InputRepeatValueError>()
        if (password != passwordRepeat) errors.add(InputRepeatValueError.IS_NOT_SAME_VALUE)
        return errors
    }
}