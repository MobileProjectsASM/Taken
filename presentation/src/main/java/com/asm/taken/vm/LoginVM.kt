package com.asm.taken.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@HiltViewModel
class LoginVM @Inject constructor(): ViewModel() {

    //region MutableStateFlows
    private val _userIdSTF = MutableStateFlow("")
    private val _passwordSTF = MutableStateFlow("")
    private val _isPasswordVisibleSTF = MutableStateFlow(false)
    private val _isBtnLoginEnableSTF = MutableStateFlow(false)
    //endregion

    //region StateFlows
    val userIdSTFlow: StateFlow<String> = _userIdSTF
    val passwordSTFlow: StateFlow<String> = _passwordSTF
    val isPasswordVisibleSTF: StateFlow<Boolean> = _isPasswordVisibleSTF
    val isBtnLoginEnableSTF: StateFlow<Boolean> = _isBtnLoginEnableSTF
    //endregion

    fun updateDataLogin(userId: String, password: String) {
        _userIdSTF.value = userId
        _passwordSTF.value = password
        _isBtnLoginEnableSTF.value = userId.isNotEmpty() && password.isNotEmpty()
    }

    fun updateIsPasswordVisible (isPasswordVisible: Boolean) {
        _isPasswordVisibleSTF.value = isPasswordVisible
    }
}