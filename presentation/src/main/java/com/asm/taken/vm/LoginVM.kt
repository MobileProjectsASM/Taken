package com.asm.taken.vm

import androidx.lifecycle.ViewModel
import com.asm.taken.model.LoginData
import com.asm.taken.model.TextState
import com.asm.taken.utils.ResourceResolver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    val resourceResolver: ResourceResolver
): ViewModel() {

    init {

    }

    //region MutableStateFlows
    private val _loginDataSTF = MutableStateFlow(LoginData())
    //endregion

    //region StateFlows
    val loginDataSTF: StateFlow<LoginData> = _loginDataSTF
    //endregion

    fun updateDataLogin(userId: String?, password: String?) {
        val userMessage = validateUserId(userId)
        val passwordMessage = validatePassword(password)
        _loginDataSTF.update {
            it.copy(
                userId = userId,
                password = password,
                userIdMessage = when(userMessage) {
                    TextState.Init, TextState.Valid -> null
                    is TextState.Error -> userMessage.message
                },
                passwordMessage = when(passwordMessage) {
                    TextState.Init, TextState.Valid -> null
                    is TextState.Error -> passwordMessage.message
                },
                btnLoginEnable = userMessage is TextState.Valid && passwordMessage is TextState.Valid
            )
        }
    }

    fun updateIsPasswordVisible (isPasswordVisible: Boolean) {
        _loginDataSTF.update {
            it.copy(isPasswordVisible = isPasswordVisible)
        }
    }

    private fun validateUserId(userId: String?): TextState {
        if (userId == null) return TextState.Init
        if (userId.isEmpty()) return TextState.Error("Data empty")
        return TextState.Valid
    }

    private fun validatePassword(password: String?): TextState {
        if (password == null) return TextState.Init
        if (password.isEmpty()) return TextState.Error("Data empty")
        if (!password.matches(Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$"))) return TextState.Error("No cumple patron")
        return TextState.Valid

    }
}