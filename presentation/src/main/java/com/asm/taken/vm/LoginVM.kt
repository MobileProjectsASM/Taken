package com.asm.taken.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asm.domain.entities.Result
import com.asm.domain.entities.asSuccessful
import com.asm.domain.errors.GamerFailure
import com.asm.domain.use_cases.GetCountriesInfoUC
import com.asm.domain.use_cases.GetGamerUC
import com.asm.taken.mappers.PhoneCodeMapper
import com.asm.taken.model.CountriesUiState
import com.asm.taken.model.InputEmailError
import com.asm.taken.model.InputOtpError
import com.asm.taken.model.InputPasswordError
import com.asm.taken.model.InputPhoneCodeError
import com.asm.taken.model.InputPhoneNumberError
import com.asm.taken.model.InputRepeatValueError
import com.asm.taken.model.InputState
import com.asm.taken.model.InputUiState
import com.asm.taken.model.LoginFailure
import com.asm.taken.model.LoginFormCreateAccountUiState
import com.asm.taken.model.LoginFormPhoneUiState
import com.asm.taken.model.LoginFormUiState
import com.asm.taken.model.LoginUiState
import com.asm.taken.utils.AuthResult
import com.asm.taken.utils.SendOtpResult
import com.asm.taken.utils.SignUpResult
import com.asm.taken.utils.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginVM @Inject constructor(
    private val getGamerUC: GetGamerUC,
    private val getCountriesInfoUC: GetCountriesInfoUC,
    private val phoneCodeMapper: PhoneCodeMapper
) : ViewModel() {

    //region MutableStateFlows
    private val _loginFormUiState = MutableStateFlow(LoginFormUiState(emailUiState = InputUiState(), passwordUiState = InputUiState()))
    private val _countriesUiState: MutableStateFlow<CountriesUiState> = MutableStateFlow(CountriesUiState.Loading)
    private val _loginFormPhoneUiState: MutableStateFlow<LoginFormPhoneUiState> = MutableStateFlow(
        LoginFormPhoneUiState(phoneCodeUiState = InputUiState(), phoneNumberUiState = InputUiState())
    )
    private val _loginFormCreateAccountUiState: MutableStateFlow<LoginFormCreateAccountUiState> = MutableStateFlow(
        LoginFormCreateAccountUiState(emailUiState = InputUiState(), passwordUiState = InputUiState(), passwordRepeatUiState = InputUiState())
    )
    private val _loginUiState: MutableStateFlow<LoginUiState?> = MutableStateFlow(null)
    private val _otpFormUiState: MutableStateFlow<InputUiState<InputOtpError>> = MutableStateFlow(
        InputUiState()
    )

    //endregion

    //region StateFlows
    val loginFormUiState: StateFlow<LoginFormUiState> = _loginFormUiState
    val countriesUiState: StateFlow<CountriesUiState> = _countriesUiState
    val loginFormPhoneUiState: StateFlow<LoginFormPhoneUiState> = _loginFormPhoneUiState
    val loginFormCreateAccountState: StateFlow<LoginFormCreateAccountUiState> = _loginFormCreateAccountUiState
    val loginUiState: StateFlow<LoginUiState?> = _loginUiState
    val otpFormUiState: StateFlow<InputUiState<InputOtpError>> = _otpFormUiState

    //endregion

    //region LoginForm
    fun validateLoginForm(email: String, password: String) {
        val emailErrors = validateEmail(email)
        val passwordErrors = validatePassword(password)
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
            val countriesState: CountriesUiState = when (val countriesResult = getCountriesInfoUC.execute(Unit)) {
                is Result.Unsuccessful -> CountriesUiState.Failure(countriesResult.failure)
                is Result.Successful -> {
                    val phoneCodes = countriesResult.data.map(phoneCodeMapper::getPhoneCode)
                    CountriesUiState.Successful(phoneCodes)
                }
            }
            _countriesUiState.update { countriesState }
        }
    }

    fun updateLoginUiState(sendOtpResult: SendOtpResult) {
        val loginUiState = when (sendOtpResult) {
            is SendOtpResult.Failure -> LoginUiState.Failure(LoginFailure.SendOtpFailure(sendOtpResult.phonesSendOtpError))
            SendOtpResult.Loading -> LoginUiState.Loading
            is SendOtpResult.SentOtp -> LoginUiState.SentOtp(sendOtpResult.verificationId, sendOtpResult.phoneNumber)
        }
        _loginUiState.update { loginUiState }
    }

    fun updateLoginUiState(authResult: AuthResult) {
        viewModelScope.launch {
            val loginWithPhoneUiState = when (authResult) {
                is AuthResult.Successful -> verifyGamerExists(authResult.userData)
                is AuthResult.Failure -> LoginUiState.Failure(LoginFailure.AuthFailure(authResult.authError))
                AuthResult.Loading -> LoginUiState.Loading
            }
            _loginUiState.update { loginWithPhoneUiState }
        }
    }

    fun updateLoginUiState(signUpResult: SignUpResult) {
        viewModelScope.launch {
            val loginState = when (signUpResult) {
                is SignUpResult.Failure -> LoginUiState.Failure(LoginFailure.SignUpFailure(signUpResult.signUpError))
                SignUpResult.Loading -> LoginUiState.Loading
                SignUpResult.Successful -> LoginUiState.AccountCreated
            }
            _loginUiState.update { loginState }
        }
    }

    fun resetLoginUiState() {
        _loginUiState.update { null }
    }

    fun cleanLoginPhoneForm() {
        _loginFormPhoneUiState.update {
            it.copy(
                phoneCodeUiState = InputUiState(),
                phoneNumberUiState = InputUiState()
            )
        }
    }

    private fun validateEmail(email: String): List<InputEmailError> {
        val errors = mutableListOf<InputEmailError>()
        if (email.isEmpty()) errors.add(InputEmailError.EMPTY)
        if (!email.contains("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$".toRegex())) errors.add(InputEmailError.EMAIL_INVALID)
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

    private suspend fun verifyGamerExists(userData: UserData): LoginUiState {
        val gamerResult = getGamerUC.execute(userData.userId)
        if (gamerResult is Result.Successful) {
            val gamer = gamerResult.asSuccessful().data
            return LoginUiState.RegisteredUser(gamer.gamerId)
        }
        return when (val failure = (gamerResult as Result.Unsuccessful).failure) {
            GamerFailure.GamerNotExists -> LoginUiState.UnregisteredUser(userData.userId)
            is GamerFailure.General -> LoginUiState.Failure(LoginFailure.RegisterFailure(failure.generalFailure))
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
            val emailUiState: InputUiState<InputEmailError> = emailErrors.run {
                when {
                    isEmpty() -> InputUiState(email, InputState.Success)
                    else -> InputUiState(email, InputState.Error(this))
                }
            }
            val passwordUiState: InputUiState<InputPasswordError> = passwordErrors.run {
                when {
                    isEmpty() -> InputUiState(password, InputState.Success)
                    else -> InputUiState(password, InputState.Error(this))
                }
            }
            val passwordRepeatUiState: InputUiState<InputRepeatValueError> = passwordRepeatErrors.run {
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
                emailUiState = InputUiState(),
                passwordUiState = InputUiState(),
                passwordRepeatUiState = InputUiState()
            )
        }
    }

    private fun validatePasswordRepeat(password: String, passwordRepeat: String): List<InputRepeatValueError> {
        val errors = mutableListOf<InputRepeatValueError>()
        if (password != passwordRepeat) errors.add(InputRepeatValueError.IS_NOT_SAME_VALUE)
        return errors
    }

    //endregion
}