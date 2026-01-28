package com.asm.taken.ui.page.login

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asm.domain.entities.AuthUser
import com.asm.domain.entities.CountryInfo
import com.asm.taken.R
import com.asm.taken.model.AuthPhoneProcessState
import com.asm.taken.model.CountriesState
import com.asm.taken.model.InputOtpError
import com.asm.taken.model.InputPhoneCodeError
import com.asm.taken.model.InputPhoneNumberError
import com.asm.taken.model.InputState
import com.asm.taken.model.PhoneAuthFormState
import com.asm.taken.model.OtpFormState
import com.asm.taken.model.PhoneAuthUIState
import com.asm.taken.ui.CircularProgressDialog
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultOutlinedTextFieldLI
import com.asm.taken.ui.DefaultText
import com.asm.taken.ui.ErrorProcessComponent
import com.asm.taken.ui.OtpMultiple
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.utils.AuthenticationProviders
import com.asm.taken.vm.AuthPhoneVM
import kotlinx.coroutines.launch

@Composable
fun PhoneAuthPage(
    authenticationProviders: AuthenticationProviders,
    authPhoneVM: AuthPhoneVM,
    snackBarHostState: SnackbarHostState,
    popBackStack: () -> Unit,
    onNavigateToMainPage: (String) -> Unit,
    onNavigateToCreateGamer: (String, String?) -> Unit
) {
    val croutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val phoneAuthUIState: PhoneAuthUIState by authPhoneVM.phoneAuthUIState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        authPhoneVM.getCountriesInfo()
    }
    BackHandler {
        authPhoneVM.cleanPhoneForm()
        popBackStack()
    }
    AuthWithPhone(
        phoneAuthFormState = phoneAuthUIState.phoneAuthFormState,
        snackBarHostState = snackBarHostState,
        validateForm = authPhoneVM::validatePhoneNumberForm,
        phoneNumberSent = { code, phoneNumber ->
            croutineScope.launch {
                authPhoneVM.updateToLoading()
                val result = authenticationProviders.authWithPhoneNumber(
                    activity = context as Activity,
                    phoneNumber = "+$code$phoneNumber"
                )
                when (result) {
                    is AuthenticationProviders.AuthWithPhone.Authenticated -> authPhoneVM.verifyGamerExists(result.authUser.userId)
                    is AuthenticationProviders.AuthWithPhone.Error -> authPhoneVM.updateToError(
                        generalError = result.generalError
                    )
                    is AuthenticationProviders.AuthWithPhone.OtpSend -> authPhoneVM.updateToSentOtp(
                        verificationId = result.verificationId,
                        phoneNumber = phoneNumber
                    )
                }
            }
        },
        retryGetDataForm = authPhoneVM::getCountriesInfo,
        resetProcess = authPhoneVM::resetDataProcess
    )
    SessionSection(
        authPhoneProcessState = authPhoneVM.phoneAuthUIState.value.phoneAuthProcessState,
        snackBarHostState = snackBarHostState,
        onNavigateToMainPage = onNavigateToMainPage,
        onNavigateToCreateGamer = { authUser ->
            onNavigateToCreateGamer(authUser.userId, authUser.profilePictureUrl)
        },
        validateForm = authPhoneVM::validateOtpForm,
        verifyOtp = authPhoneVM::verifyOtp,
        retryProcessAuth = {

        },
        resetProcess = authPhoneVM::resetAuthProcessState,
    )
}

@Composable
fun AuthWithPhone(
    phoneAuthFormState: PhoneAuthFormState,
    snackBarHostState: SnackbarHostState,
    validateForm: (String, String) -> Unit,
    phoneNumberSent: (String, String) -> Unit,
    retryGetDataForm: () -> Unit,
    resetProcess: () -> Unit
) {
    PanelFormPhoneNumber(
        phoneAuthFormState = phoneAuthFormState,
        validateForm = validateForm,
        onSentPhone = phoneNumberSent
    )
    when (val currentState = phoneAuthFormState.dataFormProcess) {
        is CountriesState.Error -> ErrorProcessComponent(
            generalError = currentState.generalError,
            snackBarHostState = snackBarHostState,
            retryProcess = retryGetDataForm,
            resetProcess = resetProcess
        )
        CountriesState.Loading -> CircularProgressDialog()
        else -> return
    }
}

@Composable
fun PanelFormPhoneNumber(
    phoneAuthFormState: PhoneAuthFormState,
    validateForm: (String, String) -> Unit,
    onSentPhone: (String, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.height(250.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 15.dp, horizontal = 10.dp)
            ) {
                PuzzleGeneralTitle(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.txt_ttl_login_with_phone_number)
                )
                Spacer(modifier = Modifier.height(50.dp))
                FormPhoneNumber(
                    phoneAuthFormState = phoneAuthFormState,
                    validateForm = validateForm,
                    onSentPhone = onSentPhone
                )
            }
        }
        Box(modifier = Modifier.height(250.dp))
    }
}

@Composable
fun FormPhoneNumber(
    phoneAuthFormState: PhoneAuthFormState,
    validateForm: (String, String) -> Unit,
    onSentPhone: (String, String) -> Unit
) {
    Column {
        PhoneCodeInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            countries = (phoneAuthFormState.dataFormProcess as? CountriesState.CountriesLoaded)?.countriesInfo,
            codeValue = phoneAuthFormState.phoneCodeUiState.value,
            phoneCodeErrors = phoneAuthFormState.phoneCodeUiState.state.let { phoneCodeState ->
                when (phoneCodeState) {
                    is InputState.Error -> phoneCodeState.errors.map { getErrorPhoneCode(it) }

                    InputState.Idle, InputState.Success -> listOf()
                }
            },
            onChageCode = {
                validateForm(it, phoneAuthFormState.phoneNumberUiState.value)
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        DefaultOutlinedTextFieldLI(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            value = phoneAuthFormState.phoneNumberUiState.value,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = R.string.txt_label_pn,
            leadingIcon = Icons.Default.Phone,
            cdLeadingIcon = null,
            errors = phoneAuthFormState.phoneNumberUiState.state.let { phoneNumberState ->
                when (phoneNumberState) {
                    is InputState.Error -> phoneNumberState.errors.map { getErrorPhoneNumber(it) }

                    InputState.Idle, InputState.Success -> listOf()
                }
            }
        ) {
            validateForm(phoneAuthFormState.phoneCodeUiState.value, it)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DefaultButton(
                text = stringResource(id = R.string.txt_btn_send),
                enable = phoneAuthFormState.phoneNumberUiState.state is InputState.Success && phoneAuthFormState.phoneCodeUiState.state is InputState.Success,
            ) {
                onSentPhone(
                    phoneAuthFormState.phoneCodeUiState.value,
                    phoneAuthFormState.phoneNumberUiState.value
                )
            }
        }
    }
}

@Composable
fun PhoneCodeInput(
    modifier: Modifier = Modifier,
    countries: List<CountryInfo>?,
    codeValue: String,
    phoneCodeErrors: List<String>,
    onChageCode: (String) -> Unit
) {
    if (countries == null) {
        DefaultOutlinedTextFieldLI(
            modifier = modifier,
            value = codeValue,
            label = R.string.txt_label_code,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = Icons.Default.Pin,
            cdLeadingIcon = null,
            errors = phoneCodeErrors,
            onValueChange = onChageCode
        )
    } else {
        var showChosePhoneCodeDialog by rememberSaveable {
            mutableStateOf(false)
        }

        if (showChosePhoneCodeDialog) {
            ChoosePhoneCodeDialog(
                codeValue = codeValue,
                countries = countries
            ) {
                showChosePhoneCodeDialog = false
                onChageCode(it)
            }
        }
        DefaultOutlinedTextFieldLI(
            modifier = modifier,
            value = codeValue,
            label = R.string.txt_label_code,
            leadingIcon = Icons.Default.Pin,
            cdLeadingIcon = null,
            readOnly = true,
            errors = phoneCodeErrors,
            onClickable = {
                showChosePhoneCodeDialog = true
            }
        )
    }
}

@Composable
fun ChoosePhoneCodeDialog(
    codeValue: String,
    countries: List<CountryInfo>,
    onPhoneCodeSelected: (String) -> Unit
) {
    Dialog(onDismissRequest = {
        onPhoneCodeSelected(codeValue)
    }) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .height(400.dp)
            ) {
                PuzzleGeneralTitle(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.txt_ttl_choose_phone_code),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn {
                    items(countries) {
                        ItemCountry(country = it) { countryUiState ->
                            onPhoneCodeSelected(countryUiState.phoneCode)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCountry(country: CountryInfo, onClick: (CountryInfo) -> Unit) {
    Row(
        modifier = Modifier
            .clickable { onClick(country) }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.padding(15.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = country.flag,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        DefaultText(
            modifier = Modifier.fillMaxWidth(),
            text = country.name
        )
    }
}

@Composable
fun SessionSection(
    authPhoneProcessState: AuthPhoneProcessState,
    snackBarHostState: SnackbarHostState,
    onNavigateToMainPage: (String) -> Unit,
    onNavigateToCreateGamer: (AuthUser) -> Unit,
    validateForm: (String) -> Unit,
    verifyOtp: (String, String) -> Unit,
    retryProcessAuth: () -> Unit,
    resetProcess: () -> Unit
) {
    when (authPhoneProcessState) {
        is AuthPhoneProcessState.Error -> ErrorProcessComponent(
            generalError = authPhoneProcessState.generalError,
            snackBarHostState = snackBarHostState,
            retryProcess = retryProcessAuth,
            resetProcess = resetProcess
        )
        AuthPhoneProcessState.Loading -> CircularProgressDialog()
        is AuthPhoneProcessState.SentOtp -> OtpDialog(
            otpFormState = authPhoneProcessState.otpFormState,
            onCloseDialog = resetProcess,
            phoneNumber = authPhoneProcessState.phoneNumber,
            validateForm = validateForm,
            verifyOtp = {
                verifyOtp(authPhoneProcessState.verificationId, it)
            }
        )
        is AuthPhoneProcessState.RegisteredUser -> LaunchedEffect(true) {
            onNavigateToMainPage(authPhoneProcessState.gamerId)
        }
        is AuthPhoneProcessState.UnregisteredUser -> LaunchedEffect(true) {
            onNavigateToCreateGamer(authPhoneProcessState.authUser)
        }

        AuthPhoneProcessState.Idle -> return
    }
}

@Composable
fun OtpDialog(
    otpFormState: OtpFormState,
    phoneNumber: String,
    onCloseDialog: () -> Unit,
    validateForm: (String) -> Unit,
    verifyOtp: (String) -> Unit
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    Spacer(
                        modifier = Modifier.weight(weight = 1f)
                    )
                    IconButton(
                        modifier = Modifier
                            .background(color = Color.Red, shape = CircleShape)
                            .size(24.dp),
                        onClick = onCloseDialog
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                PuzzleGeneralTitle(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = stringResource(id = R.string.txt_ttl_enter_otp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                DefaultText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = "${stringResource(id = R.string.txt_inf_enter_otp)} ${
                        phoneNumber.takeLast(
                            4
                        )
                    }",
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                OtpMultiple(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    size = 40.dp,
                    numberInputs = 6,
                    errors = otpFormState.otpInputState.state.let { otpFormState ->
                        when (otpFormState) {
                            is InputState.Error -> otpFormState.errors.map { getErrorVerifyOtp(it) }
                            InputState.Idle, InputState.Success -> listOf()
                        }
                    },
                    onChange = validateForm
                )
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DefaultButton(
                        text = stringResource(id = R.string.txt_btn_verify),
                        enable = otpFormState.otpInputState.state is InputState.Success,
                    ) {
                        verifyOtp(otpFormState.otpInputState.value)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun getErrorPhoneCode(error: InputPhoneCodeError): String = when (error) {
    InputPhoneCodeError.EMPTY -> stringResource(R.string.err_empty_field)
    InputPhoneCodeError.LESS_THAN_4_DIGITS -> stringResource(R.string.err_less_than_4_digits)
    InputPhoneCodeError.ONLY_INT_NUMBERS -> stringResource(R.string.err_only_int_numbers)
}

@Composable
fun getErrorPhoneNumber(error: InputPhoneNumberError): String = when (error) {
    InputPhoneNumberError.EMPTY -> stringResource(R.string.err_empty_field)
    InputPhoneNumberError.ONLY_INT_NUMBERS -> stringResource(R.string.err_only_int_numbers)
}

@Composable
fun getErrorVerifyOtp(error: InputOtpError): String = when (error) {
    InputOtpError.EMPTY -> stringResource(R.string.err_otp_empty)
    InputOtpError.BE_6_DIGITS -> stringResource(R.string.err_otp_be_6_digits)
    InputOtpError.ONLY_INT_NUMBERS -> stringResource(R.string.err_only_int_numbers)
}
