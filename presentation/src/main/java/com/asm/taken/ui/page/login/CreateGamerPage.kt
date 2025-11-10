package com.asm.taken.ui.page.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Attribution
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.asm.taken.R
import com.asm.taken.model.CountriesUiState
import com.asm.taken.model.CountryUiState
import com.asm.taken.model.NavigationState
import com.asm.taken.model.ImageSelected
import com.asm.taken.model.InputAgeError
import com.asm.taken.model.InputAliasError
import com.asm.taken.model.InputCountryError
import com.asm.taken.model.InputImageError
import com.asm.taken.model.InputState
import com.asm.taken.model.LoginCreateGamerFormUiState
import com.asm.taken.model.CloseSessionUiState
import com.asm.taken.ui.CircularProgressDialog
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultOutlinedTextFieldLI
import com.asm.taken.ui.DefaultText
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.ui.navigation.CreateGamer
import com.asm.taken.ui.puzzleFontFamily
import com.asm.taken.utils.AuthenticationClient
import com.asm.taken.utils.MessageResolver
import com.asm.taken.vm.EditGamerVM

@Composable
fun CreateGamerPage(
    createGamerInfo: CreateGamer,
    editGamerVM: EditGamerVM,
    authenticationClient: AuthenticationClient,
    messageResolver: MessageResolver,
    snackBarHostState: SnackbarHostState,
    onNavigateToAuthentication: () -> Unit,
    onNavigateToHome: (String) -> Unit
) {
    LaunchedEffect(true) {
        editGamerVM.getCountriesInfo()
    }
    val countriesUiState: CountriesUiState by editGamerVM.countriesUiState.collectAsStateWithLifecycle()
    CreateGamerSection(
        createGamerInfo = createGamerInfo,
        authenticationClient = authenticationClient,
        editGamerVM = editGamerVM,
        snackBarHostState = snackBarHostState,
        countriesUiState = countriesUiState,
        messageResolver = messageResolver,
        onNavigateToHome = onNavigateToHome,
        onNavigateToAuthentication = onNavigateToAuthentication
    )
}

@Composable
fun SessionSection(
    editGamerVM: EditGamerVM,
    authenticationClient: AuthenticationClient,
    closeSessionUiState: CloseSessionUiState?,
    snackBarHostState: SnackbarHostState,
    messageResolver: MessageResolver,
    onNavigateToAuthentication: () -> Unit,
    onNavigateToHome: (String) -> Unit
) {
    when (closeSessionUiState) {
        CloseSessionUiState.Loading -> CircularProgressDialog()
        is CloseSessionUiState.Fail -> LaunchedEffect(true) {
            val message = ""//messageResolver.getErrorSession(closeSessionUiState.error)
            val snackBarResult = snackBarHostState.showSnackbar(message, withDismissAction = true)
            //if (snackBarResult == SnackbarResult.Dismissed) sessionVM.
        }
        CloseSessionUiState.Logout -> LaunchedEffect(true) {
            onNavigateToAuthentication()
        }
        null -> return
    }
}

@Composable
fun CreateGamerSection(
    createGamerInfo: CreateGamer,
    authenticationClient: AuthenticationClient,
    editGamerVM: EditGamerVM,
    snackBarHostState: SnackbarHostState,
    countriesUiState: CountriesUiState,
    messageResolver: MessageResolver,
    onNavigateToHome: (String) -> Unit,
    onNavigateToAuthentication: () -> Unit
) {
    when (countriesUiState) {
        is CountriesUiState.Failure -> ErrorCountries(
            createGamerInfo = createGamerInfo,
            authenticationClient = authenticationClient,
            editGamerVM = editGamerVM,
            snackBarHostState = snackBarHostState,
            messageResolver = messageResolver,
            onNavigateToHome = onNavigateToHome,
            onNavigateToAuthentication = onNavigateToAuthentication
        )

        CountriesUiState.Loading -> CircularProgressDialog()

        is CountriesUiState.Successful -> PanelCreateGamer(
            createGamerInfo = createGamerInfo,
            authenticationClient = authenticationClient,
            editGamerVM = editGamerVM,
            countriesUiState = countriesUiState.countriesInfo,
            messageResolver = messageResolver,
            onNavigateToHome = onNavigateToHome,
            onNavigateToAuthentication = onNavigateToAuthentication
        )
    }
}

@Composable
fun ErrorCountries(
    createGamerInfo: CreateGamer,
    authenticationClient: AuthenticationClient,
    editGamerVM: EditGamerVM,
    snackBarHostState: SnackbarHostState,
    messageResolver: MessageResolver,
    onNavigateToHome: (String) -> Unit,
    onNavigateToAuthentication: () -> Unit
) {
    LaunchedEffect(true) {
        snackBarHostState.showSnackbar(messageResolver.getMessage(R.string.err_get_countries))
    }
    PanelCreateGamer(
        createGamerInfo = createGamerInfo,
        authenticationClient = authenticationClient,
        editGamerVM = editGamerVM,
        countriesUiState = null,
        messageResolver = messageResolver,
        onNavigateToHome = onNavigateToHome,
        onNavigateToAuthentication = onNavigateToAuthentication
    )
}

@Composable
fun PanelCreateGamer(
    createGamerInfo: CreateGamer,
    authenticationClient: AuthenticationClient,
    editGamerVM: EditGamerVM,
    countriesUiState: List<CountryUiState>?,
    messageResolver: MessageResolver,
    onNavigateToHome: (String) -> Unit,
    onNavigateToAuthentication: () -> Unit
) {
    MainCreateGamer(
        createGamerInfo = createGamerInfo,
        authenticationClient = authenticationClient,
        editGamerVM = editGamerVM,
        countriesUiState = countriesUiState,
        messageResolver = messageResolver
    )
    NavigationSection(
        editGamerVM = editGamerVM,
        messageResolver = messageResolver,
        onNavigateToHome = onNavigateToHome,
        onNavigateToAuthentication = onNavigateToAuthentication,
    )
}

@Composable
fun NavigationSection(
    editGamerVM: EditGamerVM,
    messageResolver: MessageResolver,
    onNavigateToHome: (String) -> Unit,
    onNavigateToAuthentication: () -> Unit
) {
    val navigationState: NavigationState? by editGamerVM.navigationState.collectAsStateWithLifecycle()
    when (val state = navigationState) {
        is NavigationState.Failure -> LaunchedEffect(true) {

        }
        is NavigationState.GamerCreated -> LaunchedEffect(true) {
            onNavigateToHome(state.gamerId)
        }
        NavigationState.SessionClosed -> LaunchedEffect(true) {
            onNavigateToAuthentication()
        }
        NavigationState.Loading -> CircularProgressDialog()
        null -> return
    }
}

@Composable
fun MainCreateGamer(
    createGamerInfo: CreateGamer,
    authenticationClient: AuthenticationClient,
    editGamerVM: EditGamerVM,
    countriesUiState: List<CountryUiState>?,
    messageResolver: MessageResolver
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
                    .fillMaxWidth()
                    .padding(vertical = 15.dp, horizontal = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        modifier = Modifier
                            .size(38.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Red,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(7.dp),
                        onClick = {
                            editGamerVM.closeSession(authenticationClient::signOut)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = stringResource(R.string.txt_cd_btn_logout),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                PuzzleGeneralTitle(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.txt_ttl_form_create_gamer)
                )
                Spacer(modifier = Modifier.height(50.dp))
                FormCreateGamer(
                    createGamerInfo = createGamerInfo,
                    editGamerVM = editGamerVM,
                    countriesUiState = countriesUiState,
                    messageResolver = messageResolver,
                )
            }
        }
        Box(modifier = Modifier.height(250.dp))
    }
}

@Composable
fun FormCreateGamer(
    createGamerInfo: CreateGamer,
    editGamerVM: EditGamerVM,
    countriesUiState: List<CountryUiState>?,
    messageResolver: MessageResolver
) {
    val loginCreateGamerFormState: LoginCreateGamerFormUiState by editGamerVM.loginCreateGamerFormState.collectAsStateWithLifecycle()
    var showChangeProfileImageDialog: Boolean by rememberSaveable { mutableStateOf(false) }
    val aliasErrors: List<String> = when (val aliasUiState = loginCreateGamerFormState.aliasUiState.state) {
        is InputState.Error<InputAliasError> -> aliasUiState.errors.map { messageResolver.getErrorAlias(it) }
        InputState.Init, InputState.Success -> listOf()
    }
    val ageErrors: List<String> = when (val ageUiState = loginCreateGamerFormState.ageUiState.state) {
        is InputState.Error<InputAgeError> -> ageUiState.errors.map { messageResolver.getErrorAge(it) }
        InputState.Init, InputState.Success -> listOf()
    }
    val countryErrors: List<String> = when (val countryState = loginCreateGamerFormState.countryUiState.state) {
        is InputState.Error<InputCountryError> -> countryState.errors.map { messageResolver.getErrorCountry(it) }
        InputState.Init, InputState.Success -> listOf()
    }
    val imageErrors: List<String> = when (val imageSelectedState = loginCreateGamerFormState.imageSelected.state) {
        is InputState.Error<InputImageError> -> imageSelectedState.errors.map { messageResolver.getErrorImage(it) }
        InputState.Init, InputState.Success -> listOf()
    }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            editGamerVM.validateCreateGamerForm(
                alias = loginCreateGamerFormState.aliasUiState.value,
                age = loginCreateGamerFormState.aliasUiState.value,
                country = loginCreateGamerFormState.countryUiState.value,
                imageSelected = ImageSelected.Gallery(uri)
            )
        }
    }
    if (showChangeProfileImageDialog) {
        ChangeProfileImageDialog(
            createGamerInfo = createGamerInfo,
            messageResolver = messageResolver
        ) { optionChosen ->
            showChangeProfileImageDialog = false
            when (optionChosen) {
                OptionChosen.Default -> editGamerVM.validateCreateGamerForm(
                    alias = loginCreateGamerFormState.aliasUiState.value,
                    age = loginCreateGamerFormState.ageUiState.value,
                    country = loginCreateGamerFormState.countryUiState.value,
                    imageSelected = ImageSelected.Default
                )
                OptionChosen.Gallery -> launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                is OptionChosen.SocialNetwork -> editGamerVM.validateCreateGamerForm(
                    alias = loginCreateGamerFormState.aliasUiState.value,
                    age = loginCreateGamerFormState.aliasUiState.value,
                    country = loginCreateGamerFormState.countryUiState.value,
                    imageSelected = ImageSelected.SocialNetwork(optionChosen.urlImage)
                )
            }
        }
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        InputSelectImage(
            imageSelected = loginCreateGamerFormState.imageSelected.value
        ) {
            showChangeProfileImageDialog = true
        }
        Spacer(modifier = Modifier.height(10.dp))
        DefaultOutlinedTextFieldLI(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            value = loginCreateGamerFormState.aliasUiState.value,
            label = R.string.txt_label_alias,
            leadingIcon = Icons.Default.Person,
            cdLeadingIcon = null,
            errors = aliasErrors
        ) { newAlias ->
            editGamerVM.validateCreateGamerForm(
                alias = newAlias,
                age = loginCreateGamerFormState.ageUiState.value,
                country = loginCreateGamerFormState.countryUiState.value,
                imageSelected = loginCreateGamerFormState.imageSelected.value
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        DefaultOutlinedTextFieldLI(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = loginCreateGamerFormState.ageUiState.value,
            label = R.string.txt_label_age,
            leadingIcon = Icons.Default.Attribution,
            cdLeadingIcon = null,
            errors = ageErrors
        ) { newAge ->
            editGamerVM.validateCreateGamerForm(
                alias = loginCreateGamerFormState.aliasUiState.value,
                age = newAge,
                country = loginCreateGamerFormState.countryUiState.value,
                imageSelected = loginCreateGamerFormState.imageSelected.value
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        CountryInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            countriesUiState = countriesUiState,
            value = loginCreateGamerFormState.countryUiState.value,
            countryErrors = countryErrors
        ) { newCountry ->
            editGamerVM.validateCreateGamerForm(
                alias = loginCreateGamerFormState.aliasUiState.value,
                age = loginCreateGamerFormState.ageUiState.value,
                country = newCountry,
                imageSelected = loginCreateGamerFormState.imageSelected.value
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DefaultButton(
                text = stringResource(id = R.string.txt_btn_create_gamer),
                enable = loginCreateGamerFormState.ageUiState.state is InputState.Success && loginCreateGamerFormState.ageUiState.state is InputState.Success && loginCreateGamerFormState.countryUiState.state is InputState.Success,
            ) {
                editGamerVM.createGamer(createGamerInfo.id, loginCreateGamerFormState.aliasUiState.value, loginCreateGamerFormState.ageUiState.value.toInt(), loginCreateGamerFormState.countryUiState.value, loginCreateGamerFormState.imageSelected.value)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun CountryInput(
    modifier: Modifier = Modifier,
    countriesUiState: List<CountryUiState>?,
    value: String,
    countryErrors: List<String>,
    onCountryChange: (String) -> Unit
) {
    if (countriesUiState == null) {
        DefaultOutlinedTextFieldLI(
            modifier = modifier,
            value = value,
            label = R.string.txt_label_country,
            leadingIcon = Icons.Default.Public,
            cdLeadingIcon = null,
            errors = countryErrors,
            onValueChange = onCountryChange
        )
    } else {
        var showChoseCountryDialog by rememberSaveable {
            mutableStateOf(false)
        }

        if (showChoseCountryDialog) {
            ChooseCountryDialog(
                country = value,
                countriesUiState = countriesUiState
            ) {
                showChoseCountryDialog = false
                onCountryChange(it)
            }
        }
        DefaultOutlinedTextFieldLI(
            modifier = modifier,
            value = value,
            label = R.string.txt_label_country,
            leadingIcon = Icons.Default.Public,
            cdLeadingIcon = null,
            readOnly = true,
            errors = countryErrors,
            onClickable = {
                showChoseCountryDialog = true
            }
        )
    }
}

@Composable
fun ChooseCountryDialog(
    country: String,
    countriesUiState: List<CountryUiState>,
    onCountrySelected: (String) -> Unit
) {
    Dialog(
        onDismissRequest = { onCountrySelected(country) }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
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
                    text = stringResource(id = R.string.txt_ttl_choose_country),
                    fontFamily = puzzleFontFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn {
                    items(countriesUiState) {
                        ItemCountry(countryUiState = it) { countryUiState ->
                            onCountrySelected(countryUiState.country)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InputSelectImage(
    imageSelected: ImageSelected,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(128.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .size(size = 128.dp)
                .clip(CircleShape)
                .border(width = 2.dp, color = Color.Black, shape = CircleShape),
            contentScale = ContentScale.Crop,
            model = when (imageSelected) {
                ImageSelected.Default -> R.drawable.gamer
                is ImageSelected.Gallery -> imageSelected.uri
                is ImageSelected.SocialNetwork -> imageSelected.urlImage
            },
            contentDescription = null
        )
        Button(
            modifier = Modifier
                .size(38.dp)
                .align(Alignment.BottomEnd),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(7.dp),
            onClick = onClick
        ) {
            Icon(
                imageVector = Icons.Rounded.PhotoCamera,
                contentDescription = stringResource(R.string.txt_cd_btn_choose_image),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ChangeProfileImageDialog(
    createGamerInfo: CreateGamer,
    messageResolver: MessageResolver,
    onOptionSelected: (OptionChosen) -> Unit
) {
    Dialog(
        onDismissRequest = { onOptionSelected(OptionChosen.Default) }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 20.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = dimensionResource(
                        id = R.dimen.title_text_size
                    ).value.sp,
                    text = messageResolver.getMessage(R.string.txt_ttl_choose_option)
                )
                Spacer(modifier = Modifier.height(30.dp))
                OptionItem(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onOptionSelected(OptionChosen.Default) }
                ) {
                    DefaultText(
                        text = messageResolver.getMessage(R.string.txt_label_default_image)
                    )
                }
                OptionItem(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onOptionSelected(OptionChosen.Gallery) }
                ) {
                    DefaultText(
                        text = messageResolver.getMessage(R.string.txt_label_gallery)
                    )
                }
                if (createGamerInfo.image != null) {
                    OptionItem(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onOptionSelected(OptionChosen.SocialNetwork(createGamerInfo.image)) }
                    ) {
                        DefaultText(
                            text = messageResolver.getMessage(R.string.txt_label_social_network_image)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OptionItem(
    modifier: Modifier,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        content = content
    )
}

sealed class OptionChosen {
    data object Default: OptionChosen()
    data object Gallery: OptionChosen()
    data class SocialNetwork(val urlImage: String): OptionChosen()
}