package com.asm.taken.ui.page

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.asm.taken.R
import com.asm.taken.model.CountriesInfoState
import com.asm.taken.model.CountryInfoState
import com.asm.taken.model.CreateAccount
import com.asm.taken.model.LoginFormUiState
import com.asm.taken.model.PasswordUiState
import com.asm.taken.model.PhoneCodeState
import com.asm.taken.model.PhoneNumberState
import com.asm.taken.model.SendPhoneFormUiState
import com.asm.taken.model.SignInError
import com.asm.taken.model.SignInState
import com.asm.taken.model.UserIdUiState
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultImageButton
import com.asm.taken.ui.DefaultOutlinedTextFieldLI
import com.asm.taken.ui.DefaultText
import com.asm.taken.ui.DefaultTextButton
import com.asm.taken.ui.PasswordOutlinedTextField
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.ui.puzzleFontFamily
import com.asm.taken.utils.ResourceResolver
import com.asm.taken.vm.LoginVM


@Composable
fun LoginPage(
    loginVM: LoginVM,
    resourceResolver: ResourceResolver,
    navController: NavHostController,
    signInWithGoogle: () -> Unit,
    signInWithPhoneNumber: (String) -> Unit,
    validatePhoneCode: (String, String) -> Unit
) {
    val signInUiState by loginVM.signInSTF.collectAsStateWithLifecycle()
    var showDialog: Boolean? by rememberSaveable { mutableStateOf(null) }
    val context = LocalContext.current
    LaunchedEffect(key1 = signInUiState) {
        if (signInUiState == null) return@LaunchedEffect
        when (signInUiState!!) {
            is SignInState.RegisteredUser -> {

            }
            is SignInState.UnregisteredUser -> {
                navController.navigate(CreateAccount.route)
            }
            is SignInState.PhoneCodeSent -> {

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
    }
    if (showDialog != null && showDialog!!) {
        FormPhoneDialog(
            loginVM = loginVM,
            resourceResolver = resourceResolver,
            title = stringResource(id = R.string.txt_ttl_form_phone_dialog),
            onDismissRequest = {
                showDialog = false
                loginVM.resetSendPhoneForm()
            },
        ) {
            showDialog = false
            loginVM.resetSendPhoneForm()
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val configuration = LocalConfiguration.current
        val radius = configuration.screenWidthDp
        Box(
            modifier = Modifier
                .height(500.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = (radius * 0.8).dp))
                .background(color = colorResource(id = R.color.purple_200).copy(alpha = 0.4f))
        )
        Box(
            modifier = Modifier
                .height(500.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = radius.dp))
                .background(color = colorResource(id = R.color.purple_200))
        )
        Box(
            modifier = Modifier
                .height(270.dp)
                .fillMaxWidth()
        ) {
            Image(
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.Center),
                painter = painterResource(id = R.drawable.puzzle),
                contentDescription = stringResource(
                    id = R.string.txt_cd_icon_app
                )
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                fontSize = dimensionResource(id = R.dimen.app_name_size).value.sp,
                text = stringResource(id = R.string.app_name),
                fontFamily = puzzleFontFamily,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.height(250.dp))
            PanelLogin(loginVM, resourceResolver)
            PanelSocialMedia(
                signInWithGoogle = signInWithGoogle,
                signInWithPhoneNumber = {
                    showDialog = true
                    loginVM.getCountriesInfo()
                }
            )
            Box(modifier = Modifier.height(250.dp))
        }
    }
}

@Composable
fun PanelLogin(loginVM: LoginVM, resourceResolver: ResourceResolver) {
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
            FormLogin(loginVM = loginVM, resourceResolver = resourceResolver)
        }
    }
}

@Composable
fun FormLogin(loginVM: LoginVM, resourceResolver: ResourceResolver) {
    val loginFormUiState: LoginFormUiState by loginVM.loginFormSTF.collectAsStateWithLifecycle()
    val userId = loginFormUiState.userIdUiState.value ?: "";
    val userIdErrorMessage =  getUserIdErrorMessage(resourceResolver, loginFormUiState.userIdUiState)
    val password = loginFormUiState.passwordUiState.value ?: ""
    val passwordErrorMessage = getPasswordErrorMessage(resourceResolver, loginFormUiState.passwordUiState)
    val isBtnLoginEnable = loginFormUiState.userIdUiState is UserIdUiState.IsValid && loginFormUiState.passwordUiState is PasswordUiState.IsValid
    FormLoginContent(
        userId = userId,
        userIdErrorMessage = userIdErrorMessage,
        password = password,
        passwordErrorMessage = passwordErrorMessage,
        isBtnLoginEnable = isBtnLoginEnable,
        onUserIdChange = {
            loginVM.validateLoginForm(it, loginFormUiState.passwordUiState.value)
        },
        onPasswordChange =  {
            loginVM.validateLoginForm(loginFormUiState.userIdUiState.value, it)
        }
    ) {

    }
}

@Composable
fun FormLoginContent(
    userId: String,
    userIdErrorMessage: String? = null,
    password: String,
    passwordErrorMessage: String? = null,
    isBtnLoginEnable: Boolean,
    onUserIdChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    Column {
        DefaultOutlinedTextFieldLI(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            value = userId,
            label = R.string.txt_label_user_id_login,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = Icons.Default.Person,
            cdLeadingIcon = R.string.txt_cd_li_user_id,
            errorMessage = userIdErrorMessage,
            onValueChange = onUserIdChange
        )
        Spacer(modifier = Modifier.height(10.dp))
        PasswordOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            label = R.string.txt_label_password,
            password = password,
            leadingIcon = Icons.Default.Lock,
            passwordErrorMessage = passwordErrorMessage,
            onPasswordChange = onPasswordChange
        )
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DefaultButton(
                text = stringResource(id = R.string.txt_btn_login),
                enable = isBtnLoginEnable,
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
                text = stringResource(id = R.string.txt_btn_with_email)
            ) {

            }
        }
    }
}

@Composable
fun FormPhoneDialog(
    loginVM: LoginVM,
    resourceResolver: ResourceResolver,
    title: String,
    onDismissRequest: () -> Unit,
    sendPhone: (String) -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 20.dp
                )
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = dimensionResource(
                        id = R.dimen.title_text_size
                    ).value.sp,
                    text = title,
                    fontFamily = puzzleFontFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                ContentPhoneDialog(
                    loginVM = loginVM,
                    resourceResolver = resourceResolver,
                    sendPhone = sendPhone
                )
            }
        }
    }
}

@Composable
fun ContentPhoneDialog(
    loginVM: LoginVM,
    resourceResolver: ResourceResolver,
    sendPhone: (String) -> Unit
) {
    val commonModifier = Modifier.height(310.dp)
    val countriesInfoState by loginVM.countriesInfoSTF.collectAsStateWithLifecycle()
    when (countriesInfoState) {
        is CountriesInfoState.Failure, is CountriesInfoState.Successful -> Box(
            modifier = commonModifier.padding(top = 30.dp)
        ) {
            var countriesInfo: List<CountryInfoState> = listOf()
            if (countriesInfoState is CountriesInfoState.Failure) {
                val errorMessage = (countriesInfoState as CountriesInfoState.Failure).errorMessage
                Toast.makeText(LocalContext.current, errorMessage, Toast.LENGTH_SHORT).show()
            } else {
                countriesInfo = (countriesInfoState as CountriesInfoState.Successful).countriesInfo
            }
            FormPhoneNumber(
                loginVM = loginVM,
                countriesInfo = countriesInfo,
                resourceResolver = resourceResolver,
                sendPhone = sendPhone
            )
        }
        CountriesInfoState.Loading -> Box(
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
}

@Composable
fun FormPhoneNumber(
    loginVM: LoginVM,
    countriesInfo: List<CountryInfoState>,
    resourceResolver: ResourceResolver,
    sendPhone: (String) -> Unit
) {
    val sendPhoneFormState: SendPhoneFormUiState by loginVM.sendPhoneFormSTF.collectAsStateWithLifecycle()
    FormPhoneNumberContent(
        loginVM = loginVM,
        resourceResolver = resourceResolver,
        phoneNumberState = sendPhoneFormState.phoneNumberState,
        phoneCodeState = sendPhoneFormState.phoneCodeState,
        countriesInfo = countriesInfo,
        sendPhone = sendPhone
    )
}

@Composable
fun FormPhoneNumberContent(
    loginVM: LoginVM,
    resourceResolver: ResourceResolver,
    phoneNumberState: PhoneNumberState,
    phoneCodeState: PhoneCodeState,
    countriesInfo: List<CountryInfoState> = listOf(),
    sendPhone: (String) -> Unit
) {
    val phoneNumberErrorMessage = getPhoneNumberErrorMessage(resourceResolver, phoneNumberState)
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (countriesInfo.isNotEmpty()) {

            AutocompleteCountriesContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                label = R.string.txt_label_country,
                leadingIcon = Icons.Filled.Place,
                cdLeadingIcon = R.string.txt_cd_li_country,
                countriesInfo = countriesInfo,
                errorMessage = when (phoneCodeState) {
                    PhoneCodeState.Init, is PhoneCodeState.IsValid  -> null
                    PhoneCodeState.IsEmpty -> stringResource(id = R.string.err_choose_country)
                    is PhoneCodeState.IsInvalid -> stringResource(id = R.string.err_choose_country)
                },
            ) {
                loginVM.validatePhoneNumberForm(it?.phoneCode ?: "", phoneNumberState.value)
            }
        } else {
            DefaultOutlinedTextFieldLI(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                value = phoneCodeState.value ?: "",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                label = R.string.txt_label_code,
                leadingIcon = Icons.Default.Add,
                cdLeadingIcon = R.string.txt_cd_li_phone_number,
                errorMessage = when (phoneCodeState) {
                    PhoneCodeState.Init, is PhoneCodeState.IsValid  -> null
                    PhoneCodeState.IsEmpty -> stringResource(id = R.string.err_empty_field)
                    is PhoneCodeState.IsInvalid -> stringResource(id = R.string.err_does_not_meet_pattern_phone_code)
                },
            ) {
                loginVM.validatePhoneNumberForm(it, phoneNumberState.value)
            }
        }
        Spacer(modifier = Modifier.height(30.dp))
        DefaultOutlinedTextFieldLI(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            value = phoneNumberState.value ?: "",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            label = R.string.txt_label_pn,
            leadingIcon = Icons.Default.PhoneAndroid,
            cdLeadingIcon = R.string.txt_cd_li_phone_number,
            errorMessage = phoneNumberErrorMessage,
        ) {
            loginVM.validatePhoneNumberForm(phoneCodeState.value, it)
        }
        Spacer(modifier = Modifier.height(30.dp))
        DefaultButton(
            text = stringResource(R.string.txt_btn_verify_pn),
            enable = phoneNumberState is PhoneNumberState.IsValid && phoneCodeState is PhoneCodeState.IsValid,
        ) {
            sendPhone(phoneNumberState.value ?: "")
        }
    }
}

@Composable
fun AutocompleteCountriesContent(
    modifier: Modifier = Modifier,
    @StringRes label: Int,
    leadingIcon: ImageVector,
    @StringRes cdLeadingIcon: Int,
    errorMessage: String? = null,
    countriesInfo: List<CountryInfoState>,
    onCountrySelected: (CountryInfoState?) -> Unit
) {
    var expanded: Boolean by rememberSaveable { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    var country: String by rememberSaveable { mutableStateOf("") }
    var countryFlag: String by rememberSaveable { mutableStateOf("") }

    Column {
        DefaultOutlinedTextFieldLI(
            modifier = modifier.onGloballyPositioned { coordinates ->
                textFieldSize = coordinates.size.toSize()
            },
            value = country,
            label = label,
            leadingIcon = leadingIcon,
            cdLeadingIcon = cdLeadingIcon,
            errorMessage = errorMessage,
            trailingIcon = {
                if (countryFlag.isEmpty()) return@DefaultOutlinedTextFieldLI
                AsyncImage(
                    modifier = Modifier
                        .width(24.dp)
                        .height(24.dp),
                    model = countryFlag,
                    contentDescription = stringResource(id = R.string.txt_cd_country_flag),
                )
            }
        ) {
            expanded = it.isNotBlank()
            country = it
            countryFlag = ""
            onCountrySelected(null)
        }
        AnimatedVisibility(visible = expanded) {
            Card(
                modifier = Modifier
                    .width(textFieldSize.width.dp)
                    .padding(start = 10.dp, end = 10.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 130.dp),
                ) {
                    if (countriesInfo.isEmpty()) return@LazyColumn
                    items(
                        countriesInfo.filter {
                            it.country.lowercase().contains(country.lowercase())
                        }.sortedBy { it.country }
                    ) { countryInfo ->
                        ItemCountryInfo(
                            countryInfo = countryInfo
                        ) {
                            expanded = false
                            country = countryInfo.country
                            countryFlag = countryInfo.flag
                            onCountrySelected(countryInfo)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCountryInfo(
    countryInfo: CountryInfoState,
    onSelect: (CountryInfoState) -> Unit
) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .clickable {
            onSelect(countryInfo)
        }) {
        Row(
            modifier = Modifier.padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            AsyncImage(
                modifier = Modifier
                    .width(32.dp)
                    .height(32.dp),
                model = countryInfo.flag,
                contentDescription = stringResource(id = R.string.txt_cd_country_flag),
            )
            Spacer(modifier = Modifier.width(10.dp))
            DefaultText(
                modifier = Modifier.fillMaxWidth(),
                text = countryInfo.country
            )
        }
    }
}

fun getUserIdErrorMessage(resourceResolver: ResourceResolver, userIdUiState: UserIdUiState): String? = when (userIdUiState) {
    UserIdUiState.Init, is UserIdUiState.IsValid -> null
    UserIdUiState.IsEmpty -> resourceResolver.getString(R.string.err_empty_field)
}

fun getPasswordErrorMessage(resourceResolver: ResourceResolver, passwordUiState: PasswordUiState): String? = when (passwordUiState) {
    PasswordUiState.Init, is PasswordUiState.IsValid -> null
    PasswordUiState.IsEmpty -> resourceResolver.getString(R.string.err_empty_field)
    is PasswordUiState.IsInvalid -> resourceResolver.getString(R.string.err_does_not_meet_pattern_password)
}

fun getPhoneNumberErrorMessage(resourceResolver: ResourceResolver, phoneNumberState: PhoneNumberState): String? = when (phoneNumberState) {
    PhoneNumberState.Init, is PhoneNumberState.IsValid -> null
    PhoneNumberState.IsEmpty -> resourceResolver.getString(R.string.err_empty_field)
    is PhoneNumberState.IsInvalid -> resourceResolver.getString(R.string.err_does_not_meet_pattern_phone)
}
