package com.asm.taken.vm

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(): ViewModel() {

    //region MutableStateFlows
    private val _userIdSTF = MutableStateFlow("")
    private val _passwordSTF = MutableStateFlow("")
    private val _isPasswordVisibleSTF = MutableStateFlow(false)
    //endregion

    //region StateFlows
    val userIdSTFlow: StateFlow<String> = _userIdSTF
    val passwordSTFlow: StateFlow<String> = _passwordSTF
    val isPasswordVisibleSTF: StateFlow<Boolean> = _isPasswordVisibleSTF
    //endregion


    val setUserId = { userId: String ->
        _userIdSTF.value = userId
    }

    val setPassword = { password: String ->
        _passwordSTF.value = password
    }

    val setIsPasswordVisible = { isPasswordVisible: Boolean ->
        _isPasswordVisibleSTF.value = isPasswordVisible
    }
}