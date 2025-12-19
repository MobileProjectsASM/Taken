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
import androidx.compose.material3.SnackbarDuration
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asm.domain.entities.AuthUser
import com.asm.domain.errors.GeneralError
import com.asm.taken.R
import com.asm.taken.model.CountriesUiState
import com.asm.taken.model.CountryUiState
import com.asm.taken.model.InputOtpError
import com.asm.taken.model.InputPhoneCodeError
import com.asm.taken.model.InputPhoneNumberError
import com.asm.taken.model.InputState
import com.asm.taken.model.InputUiState
import com.asm.taken.model.LoginFormPhoneUiState
import com.asm.taken.model.LoginUiState
import com.asm.taken.ui.CircularProgressDialog
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultOutlinedTextFieldLI
import com.asm.taken.ui.DefaultText
import com.asm.taken.ui.DialogError
import com.asm.taken.ui.OtpMultiple
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.ui.SnackbarError
import com.asm.taken.utils.AuthenticationClient
import com.asm.taken.vm.LoginVM
import kotlinx.coroutines.launch

@Composable
fun PhoneAuthPage(
    loginVM: LoginVM,
    authenticationClient: AuthenticationClient,
    snackBarHostState: SnackbarHostState,
    popBackStack: () -> Unit,
    onNavigateToMainPage: (String) -> Unit,
    onNavigateToCreateGamer: (String, String?) -> Unit
) {
    LaunchedEffect(true) {
        loginVM.getCountriesInfo()
    }
    BackHandler {
        loginVM.cleanLoginPhoneForm()
        popBackStack()
    }
    AuthWithPhone(
        loginVM = loginVM,
        authenticationClient = authenticationClient,
        snackBarHostState = snackBarHostState
    )
    SessionSection(
        loginVM = loginVM,
        authenticationClient = authenticationClient,
        snackBarHostState = snackBarHostState,
        onNavigateToMainPage = onNavigateToMainPage,
        onNavigateToCreateGamer = { authUser ->
            onNavigateToCreateGamer(authUser.userId, authUser.profilePictureUrl)
        }
    )
}

@Composable
fun AuthWithPhone(
    loginVM: LoginVM,
    authenticationClient: AuthenticationClient,
    snackBarHostState: SnackbarHostState
) {
    val countriesUiState: CountriesUiState by loginVM.countriesUiState.collectAsStateWithLifecycle()
    val loginPhoneFormState by loginVM.loginFormPhoneUiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    PanelFormPhoneNumber(
        countriesUiState = (countriesUiState as? CountriesUiState.Successful)?.countriesInfo,
        loginPhoneFormState = loginPhoneFormState,
        validateForm = loginVM::validatePhoneNumberForm,
        onSentPhone = { code, phoneNumber ->
            authenticationClient.authWithPhoneNumber(
                context as Activity,
                coroutineScope = coroutineScope,
                phoneNumber = "+$code$phoneNumber",
                onPhoneLoginLoading = { loginVM.updateLoginState(LoginUiState.Loading) },
                onOtpSend = { verificationId, _ ->
                    loginVM.updateLoginState(
                        LoginUiState.SentOtp(verificationId, phoneNumber)
                    )
                },
                onAuthResult = loginVM::updateLoginState
            )
        }
    )
    when (val currentState = countriesUiState) {
        is CountriesUiState.Failure -> ErrorCountries(
            generalError = currentState.generalFailure,
            snackBarHostState = snackBarHostState,
            onDissmissDialog = loginVM::resetLoginUiState,
            onClickActionDialog = loginVM::getCountriesInfo,
            onDismissSnackBar = loginVM::resetLoginUiState,
        )

        CountriesUiState.Loading -> CircularProgressDialog()
        is CountriesUiState.Successful -> return
    }
}

@Composable
fun ErrorCountries(
    generalError: GeneralError,
    snackBarHostState: SnackbarHostState,
    onDissmissDialog: () -> Unit,
    onClickActionDialog: () -> Unit,
    onDismissSnackBar: () -> Unit,
) {
    when (generalError) {
        is GeneralError.ClientError -> DialogError(
            title = stringResource(R.string.txt_ttl_client_error),
            image = painterResource(R.drawable.ic_warning),
            message = stringResource(R.string.err_client),
            onDismissDialog = onDissmissDialog
        )

        GeneralError.NetworkError -> SnackbarError(
            snackBarHostState = snackBarHostState,
            actionLabel = stringResource(R.string.txt_label_retry),
            duration = SnackbarDuration.Long,
            message = stringResource(R.string.err_network_connection),
            onDismiss = onDismissSnackBar,
            onActionPerformed = onClickActionDialog
        )

        is GeneralError.ServerError -> DialogError(
            title = stringResource(R.string.txt_ttl_service_error),
            image = painterResource(R.drawable.ic_error),
            message = stringResource(R.string.err_server),
            onDismissDialog = onDissmissDialog
        )

        GeneralError.Unknown -> SnackbarError(
            snackBarHostState = snackBarHostState,
            message = stringResource(R.string.err_get_countries),
            withDismissAction = true,
            onDismiss = onDismissSnackBar
        )

        GeneralError.ConnectionError -> DialogError(
            title = stringResource(R.string.txt_ttl_unexpected_error),
            image = painterResource(R.drawable.ic_warning),
            message = stringResource(R.string.err_server_connection),
            onDismissDialog = onDissmissDialog,
            onClickAction = onClickActionDialog
        )
    }
}

@Composable
fun PanelFormPhoneNumber(
    countriesUiState: List<CountryUiState>?,
    loginPhoneFormState: LoginFormPhoneUiState,
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
                    countriesUiState = countriesUiState,
                    loginPhoneFormState = loginPhoneFormState,
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
    countriesUiState: List<CountryUiState>?,
    loginPhoneFormState: LoginFormPhoneUiState,
    validateForm: (String, String) -> Unit,
    onSentPhone: (String, String) -> Unit
) {
    Column {
        PhoneCodeInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            countriesUiState = countriesUiState,
            codeValue = loginPhoneFormState.phoneCodeUiState.value,
            phoneCodeErrors = loginPhoneFormState.phoneCodeUiState.state.let { phoneCodeState ->
                when (phoneCodeState) {
                    is InputState.Error -> phoneCodeState.errors.map { getErrorPhoneCode(it) }

                    InputState.Init, InputState.Success -> listOf()
                }
            },
            onChageCode = {
                validateForm(it, loginPhoneFormState.phoneNumberUiState.value)
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        DefaultOutlinedTextFieldLI(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            value = loginPhoneFormState.phoneNumberUiState.value,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = R.string.txt_label_pn,
            leadingIcon = Icons.Default.Phone,
            cdLeadingIcon = null,
            errors = loginPhoneFormState.phoneNumberUiState.state.let { phoneNumberState ->
                when (phoneNumberState) {
                    is InputState.Error -> phoneNumberState.errors.map { getErrorPhoneNumber(it) }

                    InputState.Init, InputState.Success -> listOf()
                }
            }
        ) {
            validateForm(loginPhoneFormState.phoneCodeUiState.value, it)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DefaultButton(
                text = stringResource(id = R.string.txt_btn_send),
                enable = loginPhoneFormState.phoneNumberUiState.state is InputState.Success && loginPhoneFormState.phoneCodeUiState.state is InputState.Success,
            ) {
                onSentPhone(
                    loginPhoneFormState.phoneCodeUiState.value,
                    loginPhoneFormState.phoneNumberUiState.value
                )
            }
        }
    }
}

@Composable
fun PhoneCodeInput(
    modifier: Modifier = Modifier,
    countriesUiState: List<CountryUiState>?,
    codeValue: String,
    phoneCodeErrors: List<String>,
    onChageCode: (String) -> Unit
) {
    if (countriesUiState == null) {
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
                countriesUiState = countriesUiState
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
    countriesUiState: List<CountryUiState>,
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
                    items(countriesUiState) {
                        ItemCountry(countryUiState = it) { countryUiState ->
                            onPhoneCodeSelected(countryUiState.phoneCode)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCountry(countryUiState: CountryUiState, onClick: (CountryUiState) -> Unit) {
    Row(
        modifier = Modifier
            .clickable { onClick(countryUiState) }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.padding(15.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = countryUiState.flag,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        DefaultText(
            modifier = Modifier.fillMaxWidth(),
            text = countryUiState.country
        )
    }
}

@Composable
fun SessionSection(
    loginVM: LoginVM,
    authenticationClient: AuthenticationClient,
    snackBarHostState: SnackbarHostState,
    onNavigateToMainPage: (String) -> Unit,
    onNavigateToCreateGamer: (AuthUser) -> Unit
) {
    val loginUiState: LoginUiState by loginVM.loginUiState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    when (val loginState = loginUiState) {
        LoginUiState.Loading -> CircularProgressDialog()
        is LoginUiState.RegisteredUser -> LaunchedEffect(true) {
            onNavigateToMainPage(loginState.gamerId)
        }

        is LoginUiState.UnregisteredUser -> LaunchedEffect(true) {
            onNavigateToCreateGamer(loginState.authUser)
        }

        is LoginUiState.Error -> when (loginState.generalError) {
            is GeneralError.ClientError -> DialogError(
                title = stringResource(R.string.txt_ttl_client_error),
                image = painterResource(R.drawable.ic_warning),
                message = stringResource(R.string.err_client),
                onDismissDialog = { loginVM.resetLoginUiState() }
            )

            GeneralError.ConnectionError -> DialogError(
                title = stringResource(R.string.txt_ttl_unexpected_error),
                image = painterResource(R.drawable.ic_warning),
                message = stringResource(R.string.err_server_connection),
                onDismissDialog = loginVM::resetLoginUiState
            )

            GeneralError.NetworkError -> SnackbarError(
                snackBarHostState = snackBarHostState,
                actionLabel = stringResource(R.string.txt_label_retry),
                duration = SnackbarDuration.Long,
                message = stringResource(R.string.err_network_connection),
                onDismiss = loginVM::resetLoginUiState
            )

            is GeneralError.ServerError -> DialogError(
                title = stringResource(R.string.txt_ttl_service_error),
                image = painterResource(R.drawable.ic_error),
                message = stringResource(R.string.err_server),
                onDismissDialog = loginVM::resetLoginUiState
            )

            GeneralError.Unknown -> SnackbarError(
                snackBarHostState = snackBarHostState,
                message = stringResource(R.string.err_auth),
                withDismissAction = true,
                onDismiss = loginVM::resetLoginUiState
            )
        }

        is LoginUiState.SentOtp -> {
            val otpFormState by loginVM.otpFormUiState.collectAsStateWithLifecycle()

            OtpDialog(
                otpFormState = otpFormState,
                onCloseDialog = loginVM::resetLoginUiState,
                phoneNumber = loginState.phoneNumber,
                validateForm = loginVM::validateOtpForm
            ) { otp ->
                scope.launch {
                    loginVM.updateLoginState(LoginUiState.Loading)
                    val authResult = authenticationClient.verifyOtp(loginState.verificationId, otp)
                    loginVM.updateLoginState(authResult)
                }
            }
        }

        else -> return
    }
}

@Composable
fun OtpDialog(
    otpFormState: InputUiState<String, InputOtpError>,
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
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
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
                    errors = otpFormState.state.let { otpFormState ->
                        when (otpFormState) {
                            is InputState.Error -> otpFormState.errors.map { getErrorVerifyOtp(it) }
                            InputState.Init, InputState.Success -> listOf()
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
                        enable = otpFormState.state is InputState.Success,
                    ) {
                        verifyOtp(otpFormState.value)
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
