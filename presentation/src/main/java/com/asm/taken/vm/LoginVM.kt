package com.asm.taken.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asm.domain.entities.Result
import com.asm.domain.use_cases.GetCountriesInfoUC
import com.asm.domain.use_cases.GetGamerUC
import com.asm.taken.mappers.PhoneCodeMapper
import com.asm.taken.model.CountriesUiState
import com.asm.taken.model.CountryUiState
import com.asm.taken.model.InputPasswordError
import com.asm.taken.model.InputPhoneCodeError
import com.asm.taken.model.InputPhoneNumberError
import com.asm.taken.model.InputState
import com.asm.taken.model.InputUiState
import com.asm.taken.model.InputUserIdError
import com.asm.taken.model.LoginFormPhoneUiState
import com.asm.taken.model.LoginFormUiState
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
    private val phoneNumberPattern = Regex("^[0-9]{10}$")
    private val phoneCodePattern = Regex("^[0-9]{1,3}$")
    private val validateCodePattern = Regex("^[0-9]{6}$")

    //region MutableStateFlows
    private val _loginFormUiState = MutableStateFlow(LoginFormUiState(userIdUiState = InputUiState(), passwordUiState = InputUiState()))
    private val _countriesUiState: MutableStateFlow<CountriesUiState> = MutableStateFlow(CountriesUiState.Loading)
    private val _loginFormPhoneUiState: MutableStateFlow<LoginFormPhoneUiState> = MutableStateFlow(
        LoginFormPhoneUiState(phoneCodeUiState = InputUiState(), phoneNumberUiState = InputUiState())
    )

    //endregion

    //region StateFlows
    val loginFormUiState: StateFlow<LoginFormUiState> = _loginFormUiState
    val countriesUiState: StateFlow<CountriesUiState> = _countriesUiState
    val loginFormPhoneUiState: StateFlow<LoginFormPhoneUiState> = _loginFormPhoneUiState

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

    private fun validatePhoneCode(phoneCode: String): List<InputPhoneCodeError> {
        val errors = mutableListOf<InputPhoneCodeError>()
        if (phoneCode.isEmpty()) errors.add(InputPhoneCodeError.EMPTY)
        if (phoneCode.count() > 4) errors.add(InputPhoneCodeError.LESS_THAN_4_DIGITS)
        if (!phoneCode.matches("^[0-9]+\$".toRegex())) errors.add(InputPhoneCodeError.ONLY_INT_NUMBERS)
        return errors
    }

    private fun validatePhoneNumber(phoneNumber: String): List<InputPhoneNumberError> {
        val errors = mutableListOf<InputPhoneNumberError>()
        if (phoneNumber.isEmpty()) errors.add(InputPhoneNumberError.EMPTY)
        if (!phoneNumber.matches("^[0-9]+\$".toRegex())) errors.add(InputPhoneNumberError.ONLY_INT_NUMBERS)
        return errors
    }
    //endregion

   /* //region MutableStateFlows
    private val _loginFormSTF = MutableStateFlow(LoginFormState())
    private val _sendPhoneFormSTF = MutableStateFlow<SendPhoneFormState>(SendPhoneFormState.Loading)
    private val _countriesInfoSTF = MutableStateFlow<GetCountriesInfoState>(GetCountriesInfoState.Loading)
    private val _sentCodeFormSTF = MutableStateFlow(SentCodeFormState())
    private val _signInSTF = MutableStateFlow<SignInState?>(null)
    //endregion

    //region StateFlows
    val loginFormSTF: StateFlow<LoginFormState> = _loginFormSTF
    val sendPhoneFormSTF: StateFlow<SendPhoneFormState> = _sendPhoneFormSTF
    val countriesInfoSTF: StateFlow<GetCountriesInfoState> = _countriesInfoSTF
    val sentCodeFormSTF: StateFlow<SentCodeFormState> = _sentCodeFormSTF
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

    fun validatePhoneNumberForm(phoneCode: String?, phoneNumber: String?) {
        val phoneCodeState = validatePhoneCode(phoneCode)
        val phoneNumberUiState = validatePhoneNumber(phoneNumber)
        _sendPhoneFormSTF.update {
            it.copy(
                phoneCodeState = phoneCodeState,
                phoneNumberState = phoneNumberUiState
            )
        }
    }

    fun validateSentCodeForm(codeSent: String?) {
        val sentCodeState = validateSentCode(codeSent)
        _sentCodeFormSTF.update {
            it.copy(
                sentCodeState = sentCodeState
            )
        }
    }

    fun getCountriesInfo() {
        viewModelScope.launch {
            val getCountriesInfoState: GetCountriesInfoState = when (val countriesResult = getCountriesInfoUC.execute(Unit)) {
                is Result.Failure -> GetCountriesInfoState.Failure("Error to get codes")
                is Result.Successful -> {
                    val phoneCodes = countriesResult.data.map(phoneCodeMapper::getPhoneCode)
                    GetCountriesInfoState.Successful(phoneCodes)
                }
            }
            _countriesInfoSTF.update { getCountriesInfoState }
        }
    }

    fun loginUser(authResult: AuthResult) {
        viewModelScope.launch {
            val signInState: SignInState = when (authResult) {
                is AuthResult.Failure -> SignInState.SignInFail(SignInError.AUTH_ERROR)
                is AuthResult.PhoneCodeSent -> SignInState.PhoneCodeSent(authResult.verificationId)
                is AuthResult.Successful -> handleSuccessful(authResult)
            }
            _signInSTF.update { signInState }
        }
    }

    private suspend fun handleSuccessful(successful: AuthResult.Successful): SignInState {
        val userId = successful.data.userId
        val gamerResult = getGamerUC.execute(userId)
        if (gamerResult.isSuccessful) {
            val gamer = gamerResult.asSuccessful().data
            return SignInState.RegisteredUser(gamer.gamerId)
        }
        val failure = gamerResult.asFailure().failure
        if (failure is GamerFailure.GamerNotExists) {
            return SignInState.UnregisteredUser(userId)
        }
        return SignInState.SignInFail(SignInError.REGISTER_ERROR)
    }

    fun resetSignInState() {
        _signInSTF.value = null
    }

    fun resetSendPhoneForm() {
        _sendPhoneFormSTF.value = SendPhoneFormState()
    }

    private fun validatePhoneNumber(phoneNumber: String?): PhoneNumberState {
        if (phoneNumber == null) return PhoneNumberState.Init
        if (phoneNumber.isEmpty()) return PhoneNumberState.IsEmpty
        if (!phoneNumber.matches(phoneNumberPattern)) return PhoneNumberState.IsInvalid(phoneNumber)
        return PhoneNumberState.IsValid(phoneNumber)
    }

    private fun validatePhoneCode(phoneCode: String?): PhoneCodeState {
        if (phoneCode == null) return PhoneCodeState.Init
        if (phoneCode.isEmpty()) return PhoneCodeState.IsEmpty
        if (!phoneCode.matches(phoneCodePattern)) return PhoneCodeState.IsInvalid(phoneCode)
        return PhoneCodeState.IsValid(phoneCode)
    }

    private fun validateSentCode(code: String?): SentCodeState {
        if (code == null) return SentCodeState.Init
        if (code.isEmpty()) return SentCodeState.IsEmpty
        if (!code.matches(validateCodePattern)) return SentCodeState.IsInvalid(code)
        return SentCodeState.IsInvalid(code)
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
    }*/

}