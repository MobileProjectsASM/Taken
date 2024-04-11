package com.asm.taken.vm

import androidx.lifecycle.ViewModel
import com.asm.taken.model.FormUiState
import com.asm.taken.model.PasswordUiState
import com.asm.taken.model.UserIdUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor() : ViewModel() {

    //region MutableStateFlows
    private val _formUiStateSTF = MutableStateFlow(FormUiState())
    //endregion

    //region StateFlows
    val formUiStateSTF: StateFlow<FormUiState> = _formUiStateSTF
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

    private fun validateUserId(userId: String?): UserIdUiState {
        if (userId == null) return UserIdUiState.Init
        if (userId.isEmpty()) return UserIdUiState.IsEmpty
        return UserIdUiState.IsValid(userId)
    }

    private fun validatePassword(password: String?): PasswordUiState {
        if (password == null) return PasswordUiState.Init
        if (password.isEmpty()) return PasswordUiState.IsEmpty
        if (!password.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$"))) return PasswordUiState.IsInvalid(password)
        return PasswordUiState.IsValid(password)
    }
}