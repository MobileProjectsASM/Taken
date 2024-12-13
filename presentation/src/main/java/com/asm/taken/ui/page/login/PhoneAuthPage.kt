package com.asm.taken.ui.page.login

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.asm.taken.R
import com.asm.taken.model.CountriesUiState
import com.asm.taken.model.CountryUiState
import com.asm.taken.model.InputState
import com.asm.taken.model.SendOtpResult
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultOutlinedTextFieldLI
import com.asm.taken.ui.DefaultText
import com.asm.taken.ui.ProgressDialog
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.ui.SimpleOutlinedTextField
import com.asm.taken.ui.puzzleFontFamily
import com.asm.taken.utils.MessageResolver
import com.asm.taken.vm.LoginVM

@Composable
fun PhoneAuthPage(
    loginVM: LoginVM,
    navController: NavHostController,
    snackBarHostState: SnackbarHostState,
    onSentPhone: (String, String) -> Unit
) {
    val countriesUiState: CountriesUiState by loginVM.countriesUiState.collectAsStateWithLifecycle()
    val sendOtpResultUiState: SendOtpResult? by loginVM.sendOtpResultUiState.collectAsStateWithLifecycle()

    when (countriesUiState) {
        is CountriesUiState.Failure -> ErrorCountries(
            message = (countriesUiState as CountriesUiState.Failure).errorMessage,
            snackBarHostState = snackBarHostState,
            loginVM = loginVM,
            onSentPhone = onSentPhone
        )
        CountriesUiState.Loading -> CircularProgressDialog()
        is CountriesUiState.Successful -> PanelAuthPhone(
            countriesUiState = (countriesUiState as CountriesUiState.Successful).countriesInfo,
            loginVM = loginVM,
            onSentPhone = onSentPhone
        )
    }
    OtpDialog(loginVM = loginVM)
    /*SendOtpView(
        loginVM = loginVM,
        navController = navController,
        sendOtpResultUiState = sendOtpResultUiState,
        snackBarHostState = snackBarHostState
    )*/
}

@Composable
fun ErrorCountries(
    message: String,
    snackBarHostState: SnackbarHostState,
    loginVM: LoginVM,
    onSentPhone: (String, String) -> Unit
) {
    LaunchedEffect(true) {
        snackBarHostState.showSnackbar(message, withDismissAction = true)
    }
    PanelAuthPhone(
        countriesUiState = null,
        loginVM = loginVM,
        onSentPhone = onSentPhone
    )
}

@Composable
fun PanelAuthPhone(
    countriesUiState: List<CountryUiState>?,
    loginVM: LoginVM,
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
    onSentPhone: (String, String) -> Unit
) {
    val loginPhoneFormState by loginVM.loginFormPhoneUiState.collectAsStateWithLifecycle()
    val phoneCodeErrors: List<String> = when (val phoneCodeUiState = loginPhoneFormState.phoneCodeUiState.state) {
        is InputState.Error -> phoneCodeUiState.errors.map { stringResource(MessageResolver.getErrorPhoneCode(it)) }
        InputState.Init, InputState.Success -> listOf()
    }
    val phoneNumberErrors: List<String> = when (val phoneNumberUiState = loginPhoneFormState.phoneNumberUiState.state) {
        is InputState.Error -> phoneNumberUiState.errors.map { stringResource(MessageResolver.getErrorPhoneNumber(it)) }
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
            cdLeadingIcon = R.string.txt_cd_li_phone_number,
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
            cdLeadingIcon = R.string.txt_cd_li_code,
            errors = phoneCodeErrors
        ) {
            loginVM.validatePhoneNumberForm(it, phoneNumberValue)
        }
    } else {
        var showChosePhoneCodeDialog by rememberSaveable {
            mutableStateOf(false)
        }

        ChoosePhoneCodeDialog(showDialog = showChosePhoneCodeDialog, countriesUiState = countriesUiState) {
            showChosePhoneCodeDialog = false
            loginVM.validatePhoneNumberForm(it, phoneNumberValue)
        }
        DefaultOutlinedTextFieldLI(
            modifier = modifier,
            value = codeValue,
            label = R.string.txt_label_code,
            leadingIcon = Icons.Default.Pin,
            cdLeadingIcon = R.string.txt_cd_li_code,
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
    showDialog: Boolean,
    countriesUiState: List<CountryUiState>,
    onPhoneCodeSelected: (String) -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = {
            onPhoneCodeSelected("")
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
            contentDescription = stringResource(id = R.string.txt_cd_country_flag),
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
fun SendOtpView(
    loginVM: LoginVM,
    navController: NavHostController,
    sendOtpResultUiState: SendOtpResult?,
    snackBarHostState: SnackbarHostState
) {
    if (sendOtpResultUiState != null) {
        when (sendOtpResultUiState) {
            is SendOtpResult.AutomaticAuthWithPhone -> {
                LaunchedEffect(true) {
                    snackBarHostState.showSnackbar("User authenticate", withDismissAction = true)
                }
            }
            is SendOtpResult.Failure -> {
                val message = stringResource(id = MessageResolver.getErrorSendOtp(sendOtpResultUiState.sendOtpError))
                LaunchedEffect(true) {
                    snackBarHostState.showSnackbar(message, withDismissAction = true)
                }
            }
            SendOtpResult.Loading -> CircularProgressDialog()
            is SendOtpResult.SentOtp -> OtpDialog(
                loginVM = loginVM
            )
        }
    }
}

@Composable
fun CircularProgressDialog() {
    ProgressDialog {
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 10.dp
        )
    }
}

@Composable
fun OtpDialog(loginVM: LoginVM) {
    val otpFormUiState by loginVM.otpFormUiState.collectAsStateWithLifecycle()
    val otpErrors: List<String> = when (val otpFormState = otpFormUiState.state) {
        is InputState.Error -> otpFormState.errors.map { stringResource(MessageResolver.getErrorVerifyOtp(it)) }
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
                        onClick = { loginVM.updateSendOtpResult(null) }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Cancel,
                            contentDescription = stringResource(R.string.txt_cd_error_input_icon),
                            tint = Color.Red
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    fontSize = dimensionResource(
                        id = R.dimen.title_text_size
                    ).value.sp,
                    text = stringResource(id = R.string.txt_ttl_choose_phone_code),
                    fontFamily = puzzleFontFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                OtpInput(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    heightOtpInput = 56.dp,
                    widthOtpInput = 32.dp,
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
                        text = stringResource(id = R.string.txt_btn_send),
                        enable = otpFormUiState.state is InputState.Success,
                    ) {

                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun OtpInput(
    modifier: Modifier,
    heightOtpInput: Dp,
    widthOtpInput: Dp,
    errors: List<String> = listOf(),
    onChange: (String) -> Unit
) {
    var one by rememberSaveable {
        mutableStateOf("")
    }
    var second by rememberSaveable {
        mutableStateOf("")
    }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally)
        ) {
            SimpleOutlinedTextField(
                modifier = Modifier
                    .height(heightOtpInput)
                    .width(widthOtpInput),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                value = one
            ) {
                one = it
                onChange("$one$second")
            }
            SimpleOutlinedTextField(
                modifier = Modifier
                    .height(heightOtpInput)
                    .width(widthOtpInput),
                value = second
            ) {
                second = it
            }
        }
        if (errors.isNotEmpty()) {
            Spacer(modifier = Modifier.height(5.dp))
            errors.forEach { error ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(
                        modifier = Modifier.size(size = 16.dp),
                        imageVector = Icons.Outlined.Cancel,
                        contentDescription = stringResource(R.string.txt_cd_error_input_icon),
                        tint = OutlinedTextFieldDefaults.colors().errorIndicatorColor
                    )
                    DefaultText(
                        error,
                        color = OutlinedTextFieldDefaults.colors().errorIndicatorColor,
                        fontSize = R.dimen.small_text_size
                    )
                }
            }
        }
    }
}

