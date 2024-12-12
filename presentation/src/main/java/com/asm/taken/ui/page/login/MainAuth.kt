package com.asm.taken.ui.page.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.asm.taken.R
import com.asm.taken.model.InputState
import com.asm.taken.model.LoginFormUiState
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultImageButton
import com.asm.taken.ui.DefaultOutlinedTextFieldLI
import com.asm.taken.ui.DefaultText
import com.asm.taken.ui.DefaultTextButton
import com.asm.taken.ui.PasswordOutlinedTextField
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.ui.navigation.AuthenticationPhone
import com.asm.taken.utils.MessageResolver
import com.asm.taken.vm.LoginVM

@Composable
fun MainAuthPage(
    loginVM: LoginVM,
    navController: NavHostController,
    signInWithGoogle: () -> Unit
) {
    /*LaunchedEffect(key1 = signInUiState) {
        if (signInUiState == null) return@LaunchedEffect
        when (signInUiState!!) {
            is SignInState.RegisteredUser -> {

            }
            is SignInState.UnregisteredUser -> {
                navController.navigate(CreateAccount.route)
            }
            is SignInState.PhoneCodeSent -> {
                val verificationId = (signInUiState as SignInState.PhoneCodeSent).verificationId

            }
            is SignInState.SignInFail -> {
                val signInFail = signInUiState as SignInState.SignInFail
                when (signInFail.signInError) {
                    SignInError.AUTH_ERROR -> Toast.makeText(context, "Authentication error", Toast.LENGTH_SHORT).show()
                    SignInError.REGISTER_ERROR -> Toast.makeText(context, "Register error", Toast.LENGTH_SHORT).show()
                }
            }
        }
        loginVM.resetSignInState()
    }*/

    /*if (showDialog) {
        FormPhoneDialog(
            loginVM = loginVM,
            resourceResolver = resourceResolver,
            title = stringResource(id = R.string.txt_ttl_form_phone_dialog),
            onDismissRequest = {
                showDialog = false
                loginVM.resetSendPhoneForm()
            },
        ) {
            signInWithPhoneNumber(it)
            showDialog = false
            loginVM.resetSendPhoneForm()
        }
    }*/
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.height(250.dp))
        PanelLogin(loginVM)
        PanelSocialMedia(
            signInWithGoogle = signInWithGoogle,
        ) {
            navController.navigate(AuthenticationPhone.route)
        }
        Box(modifier = Modifier.height(250.dp))
    }
}

@Composable
fun PanelLogin(loginVM: LoginVM) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp, horizontal = 10.dp)
        ) {
            PuzzleGeneralTitle(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.txt_ttl_login_dialog)
            )
            DefaultText(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.txt_inf_login_dialog),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(50.dp))
            FormLogin(
                loginVM = loginVM,
            ) {

            }
        }
    }
}

@Composable
fun FormLogin(
    loginVM: LoginVM,
    onSubmit: () -> Unit,
) {
    val loginFormState: LoginFormUiState by loginVM.loginFormUiState.collectAsStateWithLifecycle()
    val userIdErrors: List<String> = when (val userIdUiState = loginFormState.userIdUiState.state) {
        is InputState.Error -> userIdUiState.errors.map { stringResource(MessageResolver.getErrorUserId(it)) }
        InputState.Init -> listOf()
        InputState.Success -> listOf()
    }
    val passwordErrors: List<String> = when (val passwordUiState = loginFormState.passwordUiState.state) {
        is InputState.Error -> passwordUiState.errors.map { stringResource(MessageResolver.getErrorPassword(it)) }
        InputState.Init -> listOf()
        InputState.Success -> listOf()
    }
    Column {
        DefaultOutlinedTextFieldLI(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            value = loginFormState.userIdUiState.value,
            label = R.string.txt_label_user_id_login,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = Icons.Default.Person,
            cdLeadingIcon = R.string.txt_cd_li_user_id,
            errors = userIdErrors
        ) {
            loginVM.validateLoginForm(it, loginFormState.passwordUiState.value)
        }
        Spacer(modifier = Modifier.height(10.dp))
        PasswordOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            label = R.string.txt_label_password,
            password = loginFormState.passwordUiState.value,
            leadingIcon = Icons.Default.Lock,
            errors = passwordErrors,
        ) {
            loginVM.validateLoginForm(loginFormState.userIdUiState.value, it)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DefaultButton(
                text = stringResource(id = R.string.txt_btn_login),
                enable = loginFormState.userIdUiState.state is InputState.Success && loginFormState.passwordUiState.state is InputState.Success,
                onClickButton = onSubmit
            )
        }
    }
}

@Composable
fun PanelSocialMedia(
    signInWithGoogle: () -> Unit,
    signInWithPhoneNumber: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PuzzleGeneralTitle(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.txt_inf_sign_in)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                DefaultImageButton(
                    imageSize = 40.dp,
                    iconButton = R.drawable.facebook,
                    cdIconButton = R.string.txt_cd_icon_button
                )
                DefaultImageButton(
                    imageSize = 40.dp,
                    iconButton = R.drawable.google,
                    cdIconButton = R.string.txt_cd_icon_button,
                    onClickButton = signInWithGoogle
                )
                DefaultImageButton(
                    imageSize = 40.dp,
                    iconButton = R.drawable.phone,
                    cdIconButton = R.string.txt_cd_icon_button,
                    onClickButton = signInWithPhoneNumber
                )
            }
            DefaultTextButton(
                text = stringResource(id = R.string.txt_btn_create_account)
            ) {

            }
        }
    }
}


//@Composable
//fun FormPhoneDialog(
//    loginVM: LoginVM,
//    resourceResolver: ResourceResolver,
//    title: String,
//    onDismissRequest: () -> Unit,
//    sendPhone: (String) -> Unit
//) {
//    LaunchedEffect(true) {
//        loginVM.getCountriesInfo()
//    }
//
//    Dialog(onDismissRequest = onDismissRequest) {
//        Card(modifier = Modifier.fillMaxWidth()) {
//            Column(
//                modifier = Modifier.padding(
//                    horizontal = 16.dp,
//                    vertical = 20.dp
//                )
//            ) {
//                Text(
//                    modifier = Modifier.fillMaxWidth(),
//                    fontSize = dimensionResource(
//                        id = R.dimen.title_text_size
//                    ).value.sp,
//                    text = title,
//                    fontFamily = puzzleFontFamily,
//                    fontWeight = FontWeight.Bold,
//                    textAlign = TextAlign.Center
//                )
//                ContentPhoneDialog(
//                    loginVM = loginVM,
//                    resourceResolver = resourceResolver,
//                    sendPhone = sendPhone
//                )
//            }
//        }
//    }
//}

//@Composable
//fun ContentPhoneDialog(
//    loginVM: LoginVM,
//    resourceResolver: ResourceResolver,
//    sendPhone: (String) -> Unit
//) {
//    val commonModifier = Modifier.height(310.dp)
//    val sendPhoneFormState by loginVM.sendPhoneFormSTF.collectAsStateWithLifecycle()
//    if (sendPhoneFormState is SendPhoneFormState.Loading) return Box(
//        modifier = commonModifier.fillMaxSize(),
//        contentAlignment = Alignment.Center
//    ) {
//        CircularProgressIndicator(
//            modifier = Modifier.width(64.dp),
//            color = MaterialTheme.colorScheme.secondary,
//            trackColor = MaterialTheme.colorScheme.surfaceVariant,
//        )
//    }
//    return Box(
//        modifier = commonModifier.padding(top = 30.dp)
//    ) {
//        if (sendPhoneFormState is SendPhoneFormState.ErrorForm) {
//            val errorPhoneData = (sendPhoneFormState as SendPhoneFormState.ErrorForm).errorPhoneFormData
//            val errorMessage = getErrorMessage(LocalContext.current, errorPhoneData)
//            Toast.makeText(LocalContext.current, errorMessage, Toast.LENGTH_SHORT).show()
//        }
//        val countriesInfo: List<CountryInfoState> = if (sendPhoneFormState is SendPhoneFormState.BuildForm) {
//            (sendPhoneFormState as SendPhoneFormState.BuildForm).countries
//        } else listOf()
//        FormPhoneNumber(
//            loginVM = loginVM,
//            countriesInfo = countriesInfo,
//            resourceResolver = resourceResolver,
//            sendPhone = sendPhone
//        )
//    }
//}

//fun getErrorMessage(context: Context, errorPhoneData: SendPhoneFormState.ErrorPhoneFormData): String = when (errorPhoneData) {
//    SendPhoneFormState.ErrorPhoneFormData.GET_COUNTRIES -> context.getString(R.string.err_get_countries)
//}

//@Composable
//fun FormPhoneNumber(
//    loginVM: LoginVM,
//    countriesInfo: List<CountryInfoState>,
//    resourceResolver: ResourceResolver,
//    sendPhone: (String) -> Unit
//) {
//    val sendPhoneFormState: SendPhoneFormState by loginVM.sendPhoneFormSTF.collectAsStateWithLifecycle()
//    FormPhoneNumberContent(
//        loginVM = loginVM,
//        resourceResolver = resourceResolver,
//        phoneNumberState = sendPhoneFormState.phoneNumberState,
//        phoneCodeState = sendPhoneFormState.phoneCodeState,
//        countriesInfo = countriesInfo,
//        sendPhone = sendPhone
//    )
//}

//@Composable
//fun FormPhoneNumberContent(
//    loginVM: LoginVM,
//    resourceResolver: ResourceResolver,
//    phoneNumberState: PhoneNumberState,
//    phoneCodeState: PhoneCodeState,
//    countriesInfo: List<CountryInfoState> = listOf(),
//    sendPhone: (String) -> Unit
//) {
//    val phoneNumberErrorMessage = getPhoneNumberErrorMessage(resourceResolver, phoneNumberState)
//    val scrollState = rememberScrollState()
//    Column(
//        modifier = Modifier.verticalScroll(scrollState),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        if (countriesInfo.isNotEmpty()) {
//            AutocompleteCountriesContent(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 10.dp),
//                label = R.string.txt_label_country,
//                leadingIcon = Icons.Filled.Place,
//                cdLeadingIcon = R.string.txt_cd_li_country,
//                countriesInfo = countriesInfo,
//                errorMessage = when (phoneCodeState) {
//                    PhoneCodeState.Init, is PhoneCodeState.IsValid  -> null
//                    PhoneCodeState.IsEmpty -> stringResource(id = R.string.err_choose_country)
//                    is PhoneCodeState.IsInvalid -> stringResource(id = R.string.err_choose_country)
//                },
//            ) {
//                loginVM.validatePhoneNumberForm(it?.phoneCode ?: "", phoneNumberState.value)
//            }
//        } else {
//            DefaultOutlinedTextFieldLI(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 10.dp),
//                value = phoneCodeState.value ?: "",
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//                label = R.string.txt_label_code,
//                leadingIcon = Icons.Default.Add,
//                cdLeadingIcon = R.string.txt_cd_li_code,
//                errorMessage = when (phoneCodeState) {
//                    PhoneCodeState.Init, is PhoneCodeState.IsValid  -> null
//                    PhoneCodeState.IsEmpty -> stringResource(id = R.string.err_empty_field)
//                    is PhoneCodeState.IsInvalid -> stringResource(id = R.string.err_does_not_meet_pattern_phone_code)
//                },
//            ) {
//                loginVM.validatePhoneNumberForm(it, phoneNumberState.value)
//            }
//        }
//        Spacer(modifier = Modifier.height(30.dp))
//        DefaultOutlinedTextFieldLI(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 10.dp),
//            value = phoneNumberState.value ?: "",
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
//            label = R.string.txt_label_pn,
//            leadingIcon = Icons.Default.PhoneAndroid,
//            cdLeadingIcon = R.string.txt_cd_li_phone_number,
//            errorMessage = phoneNumberErrorMessage,
//        ) {
//            loginVM.validatePhoneNumberForm(phoneCodeState.value, it)
//        }
//        TextField(
//            value = "",
//            onValueChange =  {
//            },
//            form = "",
//        )
//        Spacer(modifier = Modifier.height(30.dp))
//        DefaultButton(
//            text = stringResource(R.string.txt_btn_send),
//            enable = phoneNumberState is PhoneNumberState.IsValid && phoneCodeState is PhoneCodeState.IsValid,
//        ) {
//            val completeNumber = "+${phoneCodeState.value}${phoneNumberState.value}"
//            sendPhone(completeNumber)
//        }
//    }
//}

//@Composable
//fun AutocompleteCountriesContent(
//    modifier: Modifier = Modifier,
//    @StringRes label: Int,
//    leadingIcon: ImageVector,
//    @StringRes cdLeadingIcon: Int,
//    errorMessage: String? = null,
//    countriesInfo: List<CountryUiState>,
//    onCountrySelected: (CountryUiState?) -> Unit
//) {
//    var expanded: Boolean by rememberSaveable { mutableStateOf(false) }
//    var textFieldSize by remember { mutableStateOf(Size.Zero) }
//    var country: String by rememberSaveable { mutableStateOf("") }
//    var countryFlag: String by rememberSaveable { mutableStateOf("") }
//
//    Column {
//        DefaultOutlinedTextFieldLI(
//            modifier = modifier.onGloballyPositioned { coordinates ->
//                textFieldSize = coordinates.size.toSize()
//            },
//            value = country,
//            label = label,
//            leadingIcon = leadingIcon,
//            cdLeadingIcon = cdLeadingIcon,
//            errorMessage = errorMessage,
//            trailingIcon = {
//                if (countryFlag.isEmpty()) return@DefaultOutlinedTextFieldLI
//                AsyncImage(
//                    modifier = Modifier
//                        .width(24.dp)
//                        .height(24.dp),
//                    model = countryFlag,
//                    contentDescription = stringResource(id = R.string.txt_cd_country_flag),
//                )
//            }
//        ) {
//            expanded = it.isNotBlank()
//            country = it
//            countryFlag = ""
//            onCountrySelected(null)
//        }
//        AnimatedVisibility(visible = expanded) {
//            Card(
//                modifier = Modifier
//                    .width(textFieldSize.width.dp)
//                    .padding(start = 10.dp, end = 10.dp),
//                shape = RoundedCornerShape(10.dp)
//            ) {
//                LazyColumn(
//                    modifier = Modifier.heightIn(max = 130.dp),
//                ) {
//                    if (countriesInfo.isEmpty()) return@LazyColumn
//                    items(
//                        countriesInfo.filter {
//                            it.country.lowercase().contains(country.lowercase())
//                        }.sortedBy { it.country }
//                    ) { countryInfo ->
//                        ItemCountryInfo(
//                            countryInfo = countryInfo
//                        ) {
//                            expanded = false
//                            country = countryInfo.country
//                            countryFlag = countryInfo.flag
//                            onCountrySelected(countryInfo)
//                        }
//                    }
//                }
//            }
//        }
//    }
//}

//@Composable
//fun ItemCountryInfo(
//    countryInfo: CountryUiState,
//    onSelect: (CountryUiState) -> Unit
//) {
//    Box(modifier = Modifier
//        .fillMaxWidth()
//        .clickable {
//            onSelect(countryInfo)
//        }) {
//        Row(
//            modifier = Modifier.padding(vertical = 10.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Spacer(modifier = Modifier.width(8.dp))
//            AsyncImage(
//                modifier = Modifier
//                    .width(32.dp)
//                    .height(32.dp),
//                model = countryInfo.flag,
//                contentDescription = stringResource(id = R.string.txt_cd_country_flag),
//            )
//            Spacer(modifier = Modifier.width(10.dp))
//            DefaultText(
//                modifier = Modifier.fillMaxWidth(),
//                text = countryInfo.country
//            )
//        }
//    }
//}

/*@Composable
fun FormValidateSentCodeDialog(
    loginVM: LoginVM,
    isLoading: Boolean,
    onSendCode: (String) -> Unit
) {
    val commonModifier = Modifier.height(310.dp)
    if (isLoading) {
        Box(
            modifier = commonModifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp),
                color = MaterialTheme.colorScheme.secondary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
    when (isLoading) {
        is GetCountriesInfoState.Failure, is GetCountriesInfoState.Successful -> Box(
            modifier = commonModifier.padding(top = 30.dp)
        ) {
            var countriesInfo: List<CountryInfoState> = listOf()
            if (countriesInfoState is GetCountriesInfoState.Failure) {
                val errorMessage = (countriesInfoState as GetCountriesInfoState.Failure).errorMessage
                Toast.makeText(LocalContext.current, errorMessage, Toast.LENGTH_SHORT).show()
            } else {
                countriesInfo = (countriesInfoState as GetCountriesInfoState.Successful).countriesInfo
            }
            FormPhoneNumber(
                loginVM = loginVM,
                countriesInfo = countriesInfo,
                resourceResolver = resourceResolver,
                sendPhone = sendPhone
            )
        }
        GetCountriesInfoState.Loading ->
    }
}*/

//@Composable
//fun FormValidateSentCode(
//    loginVM: LoginVM,
//    onCodeChange: (String) -> Unit,
//    onSendCode: (String) -> Unit,
//) {
//    val sentCodeFormState: SentCodeFormState by loginVM.sentCodeFormSTF.collectAsStateWithLifecycle()
//    FormValidateSentCodeContent(
//        sentCodeFormState = sentCodeFormState,
//        onCodeChange = onCodeChange,
//        onSendCode = onSendCode
//    )
//}
//
//@Composable
//fun FormValidateSentCodeContent(
//    sentCodeFormState: SentCodeFormState,
//    onCodeChange: (String) -> Unit,
//    onSendCode: (String) -> Unit
//) {
//    Column(
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        DefaultOutlinedTextFieldLI(
//            value = sentCodeFormState.sentCodeState.value ?: "",
//            label = R.string.txt_label_code,
//            leadingIcon = Icons.Default.Pin,
//            cdLeadingIcon = R.string.txt_cd_li_code,
//            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
//            errorMessage = when (sentCodeFormState.sentCodeState) {
//                SentCodeState.Init, is SentCodeState.IsValid -> null
//                SentCodeState.IsEmpty -> stringResource(id = R.string.err_empty_field)
//                is SentCodeState.IsInvalid -> stringResource(id = R.string.err_does_not_meet_pattern_phone_code)
//            },
//            onValueChange = onCodeChange
//        )
//        Spacer(modifier = Modifier.height(30.dp))
//        DefaultButton(
//            enable = sentCodeFormState.sentCodeState is SentCodeState.IsValid,
//            text = stringResource(id = R.string.txt_btn_verify)
//        ) {
//            onSendCode(sentCodeFormState.sentCodeState.value ?: "")
//        }
//    }
//}
