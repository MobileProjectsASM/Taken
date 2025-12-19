package com.asm.taken.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asm.domain.entities.AuthUser
import com.asm.domain.entities.Result
import com.asm.domain.entities.Session
import com.asm.domain.errors.GeneralError
import com.asm.domain.use_cases.GamerExistsUC
import com.asm.domain.use_cases.GetCountriesInfoUC
import com.asm.domain.use_cases.SaveSessionUC
import com.asm.taken.mappers.CountryMapper
import com.asm.taken.model.CountriesUiState
import com.asm.taken.model.InputEmailError
import com.asm.taken.model.InputOtpError
import com.asm.taken.model.InputPasswordError
import com.asm.taken.model.InputPhoneCodeError
import com.asm.taken.model.InputPhoneNumberError
import com.asm.taken.model.InputRepeatValueError
import com.asm.taken.model.InputState
import com.asm.taken.model.InputUiState
import com.asm.taken.model.LoginFormCreateAccountUiState
import com.asm.taken.model.LoginFormPhoneUiState
import com.asm.taken.model.LoginFormUiState
import com.asm.taken.model.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    private val gamerExistsUC: GamerExistsUC,
    private val getCountriesInfoUC: GetCountriesInfoUC,
    private val saveSessionUC: SaveSessionUC,
    private val countryMapper: CountryMapper
) : ViewModel() {

    companion object {
        const val TAG = "LoginVW"
    }

    //region MutableStateFlows
    private val _loginFormUiState = MutableStateFlow(
        LoginFormUiState(
            emailUiState = InputUiState(""),
            passwordUiState = InputUiState("")
        )
    )
    private val _countriesUiState: MutableStateFlow<CountriesUiState?> =
        MutableStateFlow(null)
    private val _loginFormPhoneUiState: MutableStateFlow<LoginFormPhoneUiState> = MutableStateFlow(
        LoginFormPhoneUiState(
            phoneCodeUiState = InputUiState(""),
            phoneNumberUiState = InputUiState("")
        )
    )
    private val _loginFormCreateAccountUiState: MutableStateFlow<LoginFormCreateAccountUiState> =
        MutableStateFlow(
            LoginFormCreateAccountUiState(
                emailUiState = InputUiState(""),
                passwordUiState = InputUiState(""),
                passwordRepeatUiState = InputUiState("")
            )
        )
    private val _loginUiState: MutableStateFlow<LoginUiState> =
        MutableStateFlow(LoginUiState.Logout)
    private val _otpFormUiState: MutableStateFlow<InputUiState<String, InputOtpError>> =
        MutableStateFlow(
            InputUiState("")
        )

    //endregion

    //region StateFlows

    val loginFormUiState: StateFlow<LoginFormUiState> = _loginFormUiState
    val countriesUiState: StateFlow<CountriesUiState?> = _countriesUiState
    val loginFormPhoneUiState: StateFlow<LoginFormPhoneUiState> = _loginFormPhoneUiState
    val loginFormCreateAccountState: StateFlow<LoginFormCreateAccountUiState> =
        _loginFormCreateAccountUiState
    val loginUiState: StateFlow<LoginUiState> = _loginUiState
    val otpFormUiState: StateFlow<InputUiState<String, InputOtpError>> = _otpFormUiState

    //endregion

    override fun onCleared() {
        Log.i(TAG, "onCleared")
        super.onCleared()
    }

    //region LoginForm
    fun validateLoginForm(email: String, password: String) {
        val emailErrors = validateEmail(email)
        val passwordErrors = validatePasswordEmpty(password)
        _loginFormUiState.update {
            val emailUiState = emailErrors.run {
                if (isEmpty()) InputUiState(email, InputState.Success)
                else InputUiState(email, InputState.Error(emailErrors))
            }
            val passwordUiState = passwordErrors.run {
                if (isEmpty()) InputUiState(password, InputState.Success)
                else InputUiState(password, InputState.Error(passwordErrors))
            }
            it.copy(
                emailUiState = emailUiState,
                passwordUiState = passwordUiState,
            )
        }
    }

    fun getCountriesInfo() {
        viewModelScope.launch {
            _countriesUiState.update { CountriesUiState.Loading }
            val countriesState: CountriesUiState =
                when (val countriesResult = getCountriesInfoUC.execute(Unit)) {
                    is Result.Unsuccessful -> CountriesUiState.Failure(countriesResult.error)
                    is Result.Successful -> {
                        val phoneCodes = countriesResult.data.map(countryMapper::toCountryUiState)
                        CountriesUiState.Successful(phoneCodes)
                    }
                }
            _countriesUiState.update { countriesState }
        }
    }

    fun updateLoginState(authResult: Result<AuthUser, GeneralError>) {
        viewModelScope.launch {
            val loginState = when (authResult) {
                is Result.Successful<AuthUser> -> updateSession(authResult.data)
                is Result.Unsuccessful<GeneralError> -> LoginUiState.Error(authResult.error)
            }
            _loginUiState.update { loginState }
        }
    }

    fun updateLoginState(loginUiState: LoginUiState) {
        _loginUiState.update { loginUiState }
    }

    fun resetLoginUiState() {
        _loginUiState.update { LoginUiState.Logout }
    }

    fun resetCountriesUiState() {
        _countriesUiState.update { null }
    }

    fun cleanLoginPhoneForm() {
        _loginFormPhoneUiState.update {
            it.copy(
                phoneCodeUiState = InputUiState(""),
                phoneNumberUiState = InputUiState("")
            )
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

    private fun validatePasswordEmpty(password: String): List<InputPasswordError> {
        val errors = mutableListOf<InputPasswordError>()
        if (password.isEmpty()) errors.add(InputPasswordError.EMPTY)
        return errors
    }
    //endregion

    //region LoginPhoneForm
    fun validatePhoneNumberForm(phoneCode: String, phoneNumber: String) {
        val phoneCodeErrors = validatePhoneCode(phoneCode)
        val phoneNumberErrors = validatePhoneNumber(phoneNumber)
        _loginFormPhoneUiState.update {
            val phoneCodeUiState = phoneCodeErrors.run {
                if (isEmpty()) InputUiState(phoneCode, InputState.Success)
                else InputUiState(phoneCode, InputState.Error(phoneCodeErrors))
            }
            val phoneNumberUiState = phoneNumberErrors.run {
                if (isEmpty()) InputUiState(phoneNumber, InputState.Success)
                else InputUiState(phoneNumber, InputState.Error(phoneNumberErrors))
            }
            it.copy(
                phoneCodeUiState = phoneCodeUiState,
                phoneNumberUiState = phoneNumberUiState
            )
        }
    }

    fun validateOtpForm(otp: String) {
        val errorsOtp = validateOtp(otp)
        _otpFormUiState.update {
            it.copy(
                value = otp,
                state = if (errorsOtp.isEmpty()) InputState.Success else InputState.Error(errorsOtp)
            )
        }
    }

    private fun validatePhoneCode(phoneCode: String): List<InputPhoneCodeError> {
        val errors = mutableListOf<InputPhoneCodeError>()
        if (phoneCode.isEmpty()) errors.add(InputPhoneCodeError.EMPTY)
        if (phoneCode.count() > 3) errors.add(InputPhoneCodeError.LESS_THAN_4_DIGITS)
        if (!phoneCode.matches("^[0-9]+\$".toRegex())) errors.add(InputPhoneCodeError.ONLY_INT_NUMBERS)
        return errors
    }

    private fun validatePhoneNumber(phoneNumber: String): List<InputPhoneNumberError> {
        val errors = mutableListOf<InputPhoneNumberError>()
        if (phoneNumber.isEmpty()) errors.add(InputPhoneNumberError.EMPTY)
        if (!phoneNumber.matches("^[0-9]+\$".toRegex())) errors.add(InputPhoneNumberError.ONLY_INT_NUMBERS)
        return errors
    }

    private fun validateOtp(otp: String): List<InputOtpError> {
        val errors = mutableListOf<InputOtpError>()
        if (otp.isEmpty()) errors.add(InputOtpError.EMPTY)
        if (otp.count() != 6) errors.add(InputOtpError.BE_6_DIGITS)
        if (!otp.matches("^[0-9]+\$".toRegex())) errors.add(InputOtpError.ONLY_INT_NUMBERS)
        return errors
    }

    private suspend fun updateSession(authUser: AuthUser): LoginUiState {
        return when (val gamerExistsResult = gamerExistsUC.execute(authUser.userId)) {
            is Result.Successful<Boolean> -> {
                val session = when (gamerExistsResult.data) {
                    true -> Session.UserRegister(authUser.userId)
                    false -> authUser.run { Session.UserUnregister(userId, profilePictureUrl) }
                }
                when (val saveSessionResult = saveSessionUC.execute(session)) {
                    is Result.Successful<Unit> -> when (gamerExistsResult.data) {
                        true -> LoginUiState.RegisteredUser(authUser.userId)
                        false -> LoginUiState.UnregisteredUser(authUser)
                    }

                    is Result.Unsuccessful<GeneralError> -> LoginUiState.Error(saveSessionResult.error)
                }
            }

            is Result.Unsuccessful<GeneralError> -> LoginUiState.Error(gamerExistsResult.error)
        }
    }
    //endregion

    //region LoginFormCreateAccount
    fun validateFormCreateAccount(
        email: String,
        password: String,
        passwordRepeat: String
    ) {
        val emailErrors = validateEmail(email)
        val passwordErrors = validatePassword(password)
        val passwordRepeatErrors = validatePasswordRepeat(password, passwordRepeat)
        _loginFormCreateAccountUiState.update {
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
            it.copy(
                emailUiState = emailUiState,
                passwordUiState = passwordUiState,
                passwordRepeatUiState = passwordRepeatUiState
            )
        }
    }

    fun cleanLoginFormCreateAccount() {
        _loginFormCreateAccountUiState.update {
            it.copy(
                emailUiState = InputUiState(""),
                passwordUiState = InputUiState(""),
                passwordRepeatUiState = InputUiState("")
            )
        }
    }

    private fun validatePasswordRepeat(
        password: String,
        passwordRepeat: String
    ): List<InputRepeatValueError> {
        val errors = mutableListOf<InputRepeatValueError>()
        if (password != passwordRepeat) errors.add(InputRepeatValueError.IS_NOT_SAME_VALUE)
        return errors
    }

    //endregion
}