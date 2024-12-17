package com.asm.taken.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asm.domain.entities.Result
import com.asm.domain.entities.asFailure
import com.asm.domain.entities.asSuccessful
import com.asm.domain.errors.GamerFailure
import com.asm.domain.use_cases.GetCountriesInfoUC
import com.asm.domain.use_cases.GetGamerUC
import com.asm.taken.mappers.PhoneCodeMapper
import com.asm.taken.model.AuthError
import com.asm.taken.model.CountriesUiState
import com.asm.taken.model.InputOtpError
import com.asm.taken.model.InputPasswordError
import com.asm.taken.model.InputPhoneCodeError
import com.asm.taken.model.InputPhoneNumberError
import com.asm.taken.model.InputState
import com.asm.taken.model.InputUiState
import com.asm.taken.model.InputUserIdError
import com.asm.taken.model.LoginFormPhoneUiState
import com.asm.taken.model.LoginFormUiState
import com.asm.taken.model.LoginError
import com.asm.taken.model.LoginUiState
import com.asm.taken.utils.AuthResult
import com.asm.taken.utils.SendOtpError
import com.asm.taken.utils.SendOtpResult
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
    private val _loginFormUiState = MutableStateFlow(LoginFormUiState(userIdUiState = InputUiState(), passwordUiState = InputUiState()))
    private val _countriesUiState: MutableStateFlow<CountriesUiState> = MutableStateFlow(CountriesUiState.Loading)
    private val _loginFormPhoneUiState: MutableStateFlow<LoginFormPhoneUiState> = MutableStateFlow(
        LoginFormPhoneUiState(phoneCodeUiState = InputUiState(), phoneNumberUiState = InputUiState())
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
    val loginUiState: StateFlow<LoginUiState?> = _loginUiState
    val otpFormUiState: StateFlow<InputUiState<InputOtpError>> = _otpFormUiState

    //endregion

    //region LoginForm
    fun validateLoginForm(userId: String, password: String) {
        val errorsUserId = validateUserId(userId)
        val errorsPassword = validatePassword(password)
        _loginFormUiState.update {
            val userIdUiState = errorsUserId.run {
                if (isEmpty()) InputUiState(userId, InputState.Success)
                else InputUiState(userId, InputState.Error(errorsUserId))
            }
            val passwordUiState = errorsPassword.run {
                if (isEmpty()) InputUiState(password, InputState.Success)
                else InputUiState(password, InputState.Error(errorsPassword))
            }
            it.copy(
                userIdUiState = userIdUiState,
                passwordUiState = passwordUiState,
            )
        }
    }

    fun getCountriesInfo() {
        viewModelScope.launch {
            val countriesState: CountriesUiState = when (val countriesResult = getCountriesInfoUC.execute(Unit)) {
                is Result.Failure -> CountriesUiState.Failure("Error to get codes")
                is Result.Successful -> {
                    val phoneCodes = countriesResult.data.map(phoneCodeMapper::getPhoneCode)
                    CountriesUiState.Successful(phoneCodes)
                }
            }
            _countriesUiState.update { countriesState }
        }
    }

    fun updateLoginUiState(sendOtpResult: SendOtpResult) {
        viewModelScope.launch {
            val loginUiState = when (sendOtpResult) {
                is SendOtpResult.AuthenticatedWithPhone -> verifyGamerExists(sendOtpResult.userData)
                is SendOtpResult.Failure -> LoginUiState.Failure(sendOtpErrorToLoginError(sendOtpResult.sendOtpError))
                SendOtpResult.Loading -> LoginUiState.Loading
                is SendOtpResult.SentOtp -> LoginUiState.SentOtp(sendOtpResult.verificationId)
            }
            _loginUiState.update { loginUiState }
        }
    }

    fun updateLoginUiState(authResult: AuthResult) {
        viewModelScope.launch {
            val loginWithPhoneUiState = when (authResult) {
                is AuthResult.Successful -> verifyGamerExists(authResult.userData)
                is AuthResult.Failure -> LoginUiState.Failure(authErrorToLoginError(authResult.authError))
                AuthResult.Loading -> LoginUiState.Loading
            }
            _loginUiState.update { loginWithPhoneUiState }
        }
    }

    fun resetLoginUiState() {
        _loginUiState.update { null }
    }

    private fun validateUserId(userId: String): List<InputUserIdError> {
        val errors = mutableListOf<InputUserIdError>()
        if (userId.isEmpty()) errors.add(InputUserIdError.EMPTY)
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
        val errorsPhoneCode = validatePhoneCode(phoneCode)
        val errorsPhoneNumber = validatePhoneNumber(phoneNumber)
        _loginFormPhoneUiState.update {
            val phoneCodeUiState = errorsPhoneCode.run {
                if (isEmpty()) InputUiState(phoneCode, InputState.Success)
                else InputUiState(phoneCode, InputState.Error(errorsPhoneCode))
            }
            val phoneNumberUiState = errorsPhoneNumber.run {
                if (isEmpty()) InputUiState(phoneNumber, InputState.Success)
                else InputUiState(phoneNumber, InputState.Error(errorsPhoneNumber))
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
        if (gamerResult.isSuccessful) {
            val gamer = gamerResult.asSuccessful().data
            return LoginUiState.RegisteredUser(gamer.gamerId)
        }
        val failure = gamerResult.asFailure().failure
        if (failure is GamerFailure.GamerNotExists) {
            return LoginUiState.UnregisteredUser(userData.userId)
        }
        return LoginUiState.Failure(LoginError.VERIFY_GAMER_EXISTS)
    }

    private fun sendOtpErrorToLoginError(sendOtpError: SendOtpError): LoginError = when (sendOtpError) {
        SendOtpError.SEND_OTP_ERROR -> LoginError.SEND_OTP_ERROR
        SendOtpError.AUTH_ERROR -> LoginError.AUTH_ERROR
        SendOtpError.UNKNOWN_ERROR -> LoginError.UNKNOWN_ERROR
    }

    private fun authErrorToLoginError(authError: AuthError): LoginError = when (authError) {
        AuthError.AUTH_ERROR -> LoginError.AUTH_ERROR
        AuthError.UNKNOWN_ERROR -> LoginError.UNKNOWN_ERROR
    }
    //endregion

   /*

    fun validateSentCodeForm(codeSent: String?) {
        val sentCodeState = validateSentCode(codeSent)
        _sentCodeFormSTF.update {
            it.copy(
                sentCodeState = sentCodeState
            )
        }
    }
    fun resetSignInState() {
        _signInSTF.value = null
    }

    fun resetSendPhoneForm() {
        _sendPhoneFormSTF.value = SendPhoneFormState()
    }*/

}