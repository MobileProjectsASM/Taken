package com.asm.taken.ui.page.login

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.asm.domain.errors.GeneralFailure
import com.asm.taken.R
import com.asm.taken.model.CountriesUiState
import com.asm.taken.model.CountryUiState
import com.asm.taken.model.InputState
import com.asm.taken.model.LoginUiState
import com.asm.taken.ui.CircularProgressDialog
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultOutlinedTextFieldLI
import com.asm.taken.ui.DefaultText
import com.asm.taken.ui.OtpMultiple
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.ui.puzzleFontFamily
import com.asm.taken.utils.AuthResult
import com.asm.taken.utils.AuthenticationClient
import com.asm.taken.utils.MessageResolver
import com.asm.taken.utils.UserData
import com.asm.taken.vm.LoginVM
import kotlinx.coroutines.launch

@Composable
fun PhoneAuthPage(
    loginVM: LoginVM,
    authenticationClient: AuthenticationClient,
    messageResolver: MessageResolver,
    snackBarHostState: SnackbarHostState,
    onSentPhone: (String, String) -> Unit,
    popBackStack: () -> Unit,
    onNavigateToMainPage: (String) -> Unit,
    onNavigateToCreateGamer: (String, String?) -> Unit
) {
    val countriesUiState: CountriesUiState by loginVM.countriesUiState.collectAsStateWithLifecycle()
    val loginUiState: LoginUiState by loginVM.loginUiState.collectAsStateWithLifecycle()

    BackHandler {
        loginVM.cleanLoginPhoneForm()
        popBackStack()
    }
    AuthWithPhone(
        loginVM = loginVM,
        messageResolver = messageResolver,
        countriesUiState = countriesUiState,
        snackBarHostState = snackBarHostState,
        onSentPhone = onSentPhone
    )
    SessionSection(
        loginVM = loginVM,
        authenticationClient = authenticationClient,
        messageResolver = messageResolver,
        loginUiState = loginUiState,
        snackBarHostState = snackBarHostState,
        onNavigateToMainPage = onNavigateToMainPage,
        onNavigateToCreateGamer = { userData ->
            onNavigateToCreateGamer(userData.userId, userData.profilePictureUrl)
        }
    )
}

@Composable
fun AuthWithPhone(
    loginVM: LoginVM,
    messageResolver: MessageResolver,
    countriesUiState: CountriesUiState,
    snackBarHostState: SnackbarHostState,
    onSentPhone: (String, String) -> Unit
) {
    when (countriesUiState) {
        is CountriesUiState.Failure -> ErrorCountries(
            generalFailure = countriesUiState.generalFailure,
            snackBarHostState = snackBarHostState,
            loginVM = loginVM,
            messageResolver = messageResolver,
            onSentPhone = onSentPhone
        )
        CountriesUiState.Loading -> CircularProgressDialog()
        is CountriesUiState.Successful -> PanelAuthPhone(
            countriesUiState = countriesUiState.countriesInfo,
            loginVM = loginVM,
            messageResolver = messageResolver,
            onSentPhone = onSentPhone
        )
    }
}

@Composable
fun ErrorCountries(
    generalFailure: GeneralFailure,
    snackBarHostState: SnackbarHostState,
    loginVM: LoginVM,
    messageResolver: MessageResolver,
    onSentPhone: (String, String) -> Unit
) {
    LaunchedEffect(true) {
        when (generalFailure) {
            is GeneralFailure.ServerError -> {
                snackBarHostState.showSnackbar("${generalFailure.code}: ${generalFailure.description}", withDismissAction = true)
            }

            GeneralFailure.NetworkConnection -> {
                val actionPerformed = snackBarHostState.showSnackbar(
                    message = messageResolver.getMessage(R.string.err_network_connection),
                    actionLabel = messageResolver.getMessage(R.string.txt_label_retry),
                    duration = SnackbarDuration.Long
                )
                if (actionPerformed == SnackbarResult.ActionPerformed) loginVM.getCountriesInfo()
            }
            GeneralFailure.Unknown -> {
                snackBarHostState.showSnackbar(messageResolver.getMessage(R.string.err_get_countries), withDismissAction = true)
            }
        }
    }
    PanelAuthPhone(
        countriesUiState = null,
        loginVM = loginVM,
        messageResolver = messageResolver,
        onSentPhone = onSentPhone
    )
}

@Composable
fun PanelAuthPhone(
    countriesUiState: List<CountryUiState>?,
    loginVM: LoginVM,
    messageResolver: MessageResolver,
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
                    loginVM = loginVM,
                    messageResolver = messageResolver,
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
    loginVM: LoginVM,
    messageResolver: MessageResolver,
    onSentPhone: (String, String) -> Unit
) {
    val loginPhoneFormState by loginVM.loginFormPhoneUiState.collectAsStateWithLifecycle()
    val phoneCodeErrors: List<String> = when (val phoneCodeUiState = loginPhoneFormState.phoneCodeUiState.state) {
        is InputState.Error -> phoneCodeUiState.errors.map { messageResolver.getErrorPhoneCode(it) }
        InputState.Init, InputState.Success -> listOf()
    }
    val phoneNumberErrors: List<String> = when (val phoneNumberUiState = loginPhoneFormState.phoneNumberUiState.state) {
        is InputState.Error -> phoneNumberUiState.errors.map { messageResolver.getErrorPhoneNumber(it) }
        InputState.Init, InputState.Success -> listOf()
    }
    Column {
        PhoneCodeInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            countriesUiState = countriesUiState,
            codeValue = loginPhoneFormState.phoneCodeUiState.value,
            phoneNumberValue = loginPhoneFormState.phoneNumberUiState.value,
            phoneCodeErrors = phoneCodeErrors,
            loginVM = loginVM
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
            errors = phoneNumberErrors
        ) {
            loginVM.validatePhoneNumberForm(loginPhoneFormState.phoneCodeUiState.value, it)
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
    phoneNumberValue: String,
    phoneCodeErrors: List<String>,
    loginVM: LoginVM
) {
    if (countriesUiState == null) {
        DefaultOutlinedTextFieldLI(
            modifier = modifier,
            value = codeValue,
            label = R.string.txt_label_code,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = Icons.Default.Pin,
            cdLeadingIcon = null,
            errors = phoneCodeErrors
        ) {
            loginVM.validatePhoneNumberForm(it, phoneNumberValue)
        }
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
                loginVM.validatePhoneNumberForm(it, phoneNumberValue)
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
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = dimensionResource(
                        id = R.dimen.title_text_size
                    ).value.sp,
                    text = stringResource(id = R.string.txt_ttl_choose_phone_code),
                    fontFamily = puzzleFontFamily,
                    fontWeight = FontWeight.Bold,
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
        AsyncImage(
            modifier = Modifier.size(50.dp),
            model = countryUiState.flag,
            contentDescription = null,
            placeholder = painterResource(id = R.drawable.american_flag),
            error = painterResource(id = R.drawable.american_flag)
        )
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
    messageResolver: MessageResolver,
    loginUiState: LoginUiState,
    snackBarHostState: SnackbarHostState,
    onNavigateToMainPage: (String) -> Unit,
    onNavigateToCreateGamer: (UserData) -> Unit
) {
    val scope = rememberCoroutineScope()
    when (loginUiState) {
        LoginUiState.Loading -> CircularProgressDialog()
        is LoginUiState.RegisteredUser -> LaunchedEffect(true) {
            onNavigateToMainPage(loginUiState.gamerId)
        }
        is LoginUiState.UnregisteredUser -> LaunchedEffect(true) {
            onNavigateToCreateGamer(loginUiState.userData)
        }
        is LoginUiState.Failure -> {
            val message = messageResolver.getErrorLogin(loginUiState.loginFailure)
            LaunchedEffect(true) {
                val snackBarResult = snackBarHostState.showSnackbar(message, withDismissAction = true)
                if (snackBarResult == SnackbarResult.Dismissed) loginVM.resetLoginUiState()
            }
        }
        is LoginUiState.SentOtp -> OtpDialog(
            loginVM = loginVM,
            phoneNumber = loginUiState.phoneNumber,
            messageResolver = messageResolver,
        ) { otp ->
            scope.launch {
                loginVM.updateLoginUiState(AuthResult.Loading)
                val authResult = authenticationClient.verifyOtp(loginUiState.verificationId, otp)
                loginVM.updateLoginUiState(authResult)
            }
        }
        else -> return
    }
}

@Composable
fun OtpDialog(
    loginVM: LoginVM,
    messageResolver: MessageResolver,
    phoneNumber: String,
    onVerifyOtp: (String) -> Unit
) {
    val otpFormUiState by loginVM.otpFormUiState.collectAsStateWithLifecycle()
    val otpErrors: List<String> = when (val otpFormState = otpFormUiState.state) {
        is InputState.Error -> otpFormState.errors.map { messageResolver.getErrorVerifyOtp(it) }
        InputState.Init, InputState.Success -> listOf()
    }

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
                            .size(size = 32.dp)
                            .padding(top = 10.dp, end = 10.dp),
                        onClick = { loginVM.resetLoginUiState() }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Cancel,
                            contentDescription = null,
                            tint = Color.Red
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    fontSize = dimensionResource(
                        id = R.dimen.title_text_size
                    ).value.sp,
                    text = stringResource(id = R.string.txt_ttl_enter_otp),
                    fontFamily = puzzleFontFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                DefaultText(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    text = "${stringResource(id = R.string.txt_inf_enter_otp)} ${phoneNumber.takeLast(4)}",
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                OtpMultiple(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    size = 40.dp,
                    numberInputs = 6,
                    errors = otpErrors
                ) {
                    loginVM.validateOtpForm(it)
                }
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DefaultButton(
                        text = stringResource(id = R.string.txt_btn_verify),
                        enable = otpFormUiState.state is InputState.Success,
                    ) {
                        onVerifyOtp(otpFormUiState.value)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
