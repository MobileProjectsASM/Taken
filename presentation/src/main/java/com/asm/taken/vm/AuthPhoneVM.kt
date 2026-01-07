package com.asm.taken.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asm.domain.entities.AuthUser
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.domain.use_cases.GamerExistsUC
import com.asm.domain.use_cases.GetCountriesInfoUC
import com.asm.domain.use_cases.SignInUserUC
import com.asm.taken.model.AuthPhoneProcessState
import com.asm.taken.model.CountriesState
import com.asm.taken.model.InputOtpError
import com.asm.taken.model.InputPhoneCodeError
import com.asm.taken.model.InputPhoneNumberError
import com.asm.taken.model.InputState
import com.asm.taken.model.InputUiState
import com.asm.taken.model.OtpFormState
import com.asm.taken.model.PhoneAuthUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthPhoneVM @Inject constructor(
    private val signInUserUC: SignInUserUC,
    private val getCountriesInfoUC: GetCountriesInfoUC,
    private val gamerExistsUC: GamerExistsUC
) : ViewModel() {

    companion object {
        const val TAG = "auth_phone_number_vm"
    }

    private val _phoneAuthUIState: MutableStateFlow<PhoneAuthUIState> = MutableStateFlow(
        PhoneAuthUIState()
    )

    val phoneAuthUIState: StateFlow<PhoneAuthUIState> = _phoneAuthUIState

    fun resetDataProcess() {
        val currentFormState = _phoneAuthUIState.value.phoneAuthFormState
        _phoneAuthUIState.update {
            it.copy(
                phoneAuthFormState = currentFormState.copy(dataFormProcess = CountriesState.Idle)
            )
        }
    }

    fun resetAuthProcessState() {
        _phoneAuthUIState.update {
            it.copy(
                phoneAuthProcessState = AuthPhoneProcessState.Idle
            )
        }
    }

    fun updateToSentOtp(verificationId: String, phoneNumber: String) {
        _phoneAuthUIState.update {
            it.copy(
                phoneAuthProcessState = AuthPhoneProcessState.SentOtp(
                    verificationId = verificationId,
                    phoneNumber = phoneNumber
                )
            )
        }
    }

    fun updateToError(generalError: GeneralError) {
        _phoneAuthUIState.update {
            it.copy(
                phoneAuthProcessState = AuthPhoneProcessState.Error(generalError)
            )
        }
    }

    fun updateToLoading() {
        _phoneAuthUIState.update {
            it.copy(
                phoneAuthProcessState = AuthPhoneProcessState.Loading
            )
        }
    }

    fun cleanPhoneForm() {
        val currentForm = _phoneAuthUIState.value.phoneAuthFormState
        _phoneAuthUIState.update {
            it.copy(
                phoneAuthFormState = currentForm.copy(
                    phoneNumberUiState = InputUiState(""),
                    phoneCodeUiState = InputUiState("")
                )
            )
        }
    }

    fun getCountriesInfo() {
        viewModelScope.launch {
            val currentFormState = _phoneAuthUIState.value.phoneAuthFormState
            val phoneAuthFormState =
                currentFormState.copy(dataFormProcess = CountriesState.Loading)
            _phoneAuthUIState.update { it.copy(phoneAuthFormState = phoneAuthFormState) }
            val processState = when (val countriesResult = getCountriesInfoUC.execute(Unit)) {
                is Result.Unsuccessful -> currentFormState.copy(
                    dataFormProcess = CountriesState.Error(countriesResult.error)
                )

                is Result.Successful -> currentFormState.copy(
                    dataFormProcess = CountriesState.CountriesLoaded(countriesResult.data)
                )
            }
            _phoneAuthUIState.update { it.copy(phoneAuthFormState = processState) }
        }
    }

    fun validatePhoneNumberForm(phoneCode: String, phoneNumber: String) {
        val currentFormState = _phoneAuthUIState.value.phoneAuthFormState
        val phoneCodeErrors = validatePhoneCode(phoneCode)
        val phoneNumberErrors = validatePhoneNumber(phoneNumber)
        val phoneCodeUiState = phoneCodeErrors.run {
            if (isEmpty()) InputUiState(phoneCode, InputState.Success)
            else InputUiState(phoneCode, InputState.Error(phoneCodeErrors))
        }
        val phoneNumberUiState = phoneNumberErrors.run {
            if (isEmpty()) InputUiState(phoneNumber, InputState.Success)
            else InputUiState(phoneNumber, InputState.Error(phoneNumberErrors))
        }
        _phoneAuthUIState.update {
            it.copy(
                phoneAuthFormState = currentFormState.copy(
                    phoneCodeUiState = phoneCodeUiState,
                    phoneNumberUiState = phoneNumberUiState
                )
            )
        }
    }

    fun verifyOtp(sessionId: String, otp: String) {
        viewModelScope.launch {
            val processState = AuthPhoneProcessState.Loading
            _phoneAuthUIState.update { it.copy(phoneAuthProcessState = processState) }
            val credentials = SignInUserUC.CredentialType.OTP(sessionId, otp)
            val authProcessType = when (val authResult = signInUserUC.execute(credentials)) {
                is Result.Successful<SignInUserUC.User> -> {
                    when (val data = authResult.data) {
                        is SignInUserUC.User.RegisteredUser -> AuthPhoneProcessState.RegisteredUser(
                            data.gamerId
                        )

                        is SignInUserUC.User.UnregisteredUser -> AuthPhoneProcessState.UnregisteredUser(
                            data.authUser
                        )
                    }
                }

                is Result.Unsuccessful<GeneralError> -> AuthPhoneProcessState.Error(authResult.error)
            }
            _phoneAuthUIState.update { it.copy(phoneAuthProcessState = authProcessType) }
        }
    }

    //Exceptional case
    fun verifyGamerExists(userId: String) {
        viewModelScope.launch {
            _phoneAuthUIState.update {
                it.copy(phoneAuthProcessState = AuthPhoneProcessState.Loading)
            }
            val processState = when (val result = gamerExistsUC.execute(userId)) {
                is Result.Successful<Boolean> -> if (result.data) AuthPhoneProcessState.RegisteredUser(userId)
                else AuthPhoneProcessState.UnregisteredUser(AuthUser(userId, null))
                is Result.Unsuccessful<GeneralError> -> AuthPhoneProcessState.Error(result.error)
            }
            _phoneAuthUIState.update {
                it.copy(phoneAuthProcessState = processState)
            }
        }
    }

    fun validateOtpForm(otp: String) {
        val currentProcessState = _phoneAuthUIState.value.phoneAuthProcessState
        if (currentProcessState is AuthPhoneProcessState.SentOtp) {
            val errorsOtp = validateOtp(otp)
            val otpInputState = InputUiState(
                value = otp,
                state = if (errorsOtp.isEmpty()) InputState.Success else InputState.Error(errorsOtp)
            )
            _phoneAuthUIState.update {
                it.copy(
                    phoneAuthProcessState = currentProcessState.copy(
                        otpFormState = OtpFormState(
                            otpInputState = otpInputState
                        )
                    )
                )
            }
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
}