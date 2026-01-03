package com.asm.taken.ui.page.login

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.filled.Attribution
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.asm.domain.errors.GeneralError
import com.asm.taken.R
import com.asm.taken.model.CountriesUiState
import com.asm.taken.model.Country
import com.asm.taken.model.CountryData
import com.asm.taken.model.EditGamerFormUiState
import com.asm.taken.model.InputAgeError
import com.asm.taken.model.InputAliasError
import com.asm.taken.model.InputCountryError
import com.asm.taken.model.InputImageError
import com.asm.taken.model.InputState
import com.asm.taken.model.NavigationState
import com.asm.taken.ui.CircularProgressDialog
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultOutlinedTextFieldLI
import com.asm.taken.ui.DefaultText
import com.asm.taken.ui.DialogError
import com.asm.taken.ui.ErrorCountries
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.ui.SnackBarError
import com.asm.taken.ui.navigation.CreateGamer
import com.asm.taken.ui.puzzleFontFamily
import com.asm.taken.utils.AuthenticationClient
import com.asm.taken.utils.getErrorAge
import com.asm.taken.utils.getErrorAlias
import com.asm.taken.utils.getErrorCountry
import com.asm.taken.utils.getErrorImage
import com.asm.taken.vm.CreateGamerVM

@Composable
fun CreateGamerPage(
    createGamerInfo: CreateGamer,
    createGamerVM: CreateGamerVM,
    authenticationClient: AuthenticationClient,
    snackBarHostState: SnackbarHostState,
    onNavigateToAuthentication: () -> Unit,
    onNavigateToHome: (String) -> Unit
) {
    CreateGamerSection(
        socialNetworkImage = createGamerInfo.image,
        gamerId = createGamerInfo.id,
        authenticationClient = authenticationClient,
        createGamerVM = createGamerVM,
        snackBarHostState = snackBarHostState
    )
    NavigationSection(
        snackBarHostState = snackBarHostState,
        createGamerVM = createGamerVM,
        onNavigateToHome = onNavigateToHome,
        onNavigateToAuthentication = onNavigateToAuthentication,
    )
}

@Composable
fun CreateGamerSection(
    socialNetworkImage: String?,
    gamerId: String,
    authenticationClient: AuthenticationClient,
    createGamerVM: CreateGamerVM,
    snackBarHostState: SnackbarHostState,
) {
    val editGamerFormState: EditGamerFormUiState by createGamerVM.editGamerFormState.collectAsStateWithLifecycle()
    val countriesUiState: CountriesUiState? by createGamerVM.countriesUiState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        createGamerVM.getCountriesInfo()
    }

    PanelFormCreateGamer(
        labelButtonSaveGamer = stringResource(R.string.txt_btn_create_gamer),
        socialNetworkImage = socialNetworkImage,
        editGamerFormState = editGamerFormState,
        countries = (countriesUiState as? CountriesUiState.Successful)?.countriesInfo,
        validateFormCreateGamer = createGamerVM::validateCreateGamerForm,
        saveGamer = {
            createGamerVM.createGamer(
                gamerId,
                editGamerFormState.aliasUiState.value,
                editGamerFormState.ageUiState.value.toInt(),
                editGamerFormState.countryUiState.value.name,
                editGamerFormState.countryUiState.value.flag,
                editGamerFormState.imageURI.value
            )
        },
        closeSession = {
            createGamerVM.closeSession(authenticationClient::signOut)
        }
    )
    when (val countriesState = countriesUiState) {
        is CountriesUiState.Failure -> ErrorCountries(
            generalError = countriesState.generalFailure,
            snackBarHostState = snackBarHostState,
            resetState = createGamerVM::resetCountriesState,
            retryProcess = createGamerVM::getCountriesInfo
        )

        CountriesUiState.Loading -> CircularProgressDialog()
        else -> return
    }
}

@Composable
fun NavigationSection(
    snackBarHostState: SnackbarHostState,
    createGamerVM: CreateGamerVM,
    onNavigateToHome: (String) -> Unit,
    onNavigateToAuthentication: () -> Unit
) {
    val navigationState: NavigationState? by createGamerVM.navigationState.collectAsStateWithLifecycle()

    when (val state = navigationState) {
        is NavigationState.Failure -> when (state.error) {
            is GeneralError.ClientError -> DialogError(
                title = stringResource(R.string.txt_ttl_client_error),
                image = painterResource(R.drawable.ic_warning),
                message = stringResource(R.string.err_client),
                onDismissedDialog = createGamerVM::resetNavigationState
            )

            GeneralError.ConnectionError -> DialogError(
                title = stringResource(R.string.txt_ttl_unexpected_error),
                image = painterResource(R.drawable.ic_warning),
                message = stringResource(R.string.err_server_connection),
                onDismissedDialog = createGamerVM::resetNavigationState
            )

            GeneralError.NetworkError -> SnackBarError(
                snackBarHostState = snackBarHostState,
                actionLabel = stringResource(R.string.txt_label_retry),
                duration = SnackbarDuration.Long,
                message = stringResource(R.string.err_network_connection),
                onDismissed = createGamerVM::resetNavigationState
            )

            is GeneralError.ServerError -> DialogError(
                title = stringResource(R.string.txt_ttl_service_error),
                image = painterResource(R.drawable.ic_error),
                message = stringResource(R.string.err_server),
                onDismissedDialog = createGamerVM::resetNavigationState
            )

            GeneralError.Unknown -> SnackBarError(
                snackBarHostState = snackBarHostState,
                message = stringResource(R.string.err_auth),
                withDismissAction = true,
                onDismissed = createGamerVM::resetNavigationState
            )
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
fun PanelFormCreateGamer(
    labelButtonSaveGamer: String,
    socialNetworkImage: String?,
    editGamerFormState: EditGamerFormUiState,
    countries: List<Country>?,
    validateFormCreateGamer: (String, String, CountryData, String?) -> Unit,
    saveGamer: () -> Unit,
    closeSession: () -> Unit
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
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        modifier = Modifier
                            .background(color = Color.Red, shape = CircleShape)
                            .size(32.dp),
                        onClick = closeSession
                    ) {
                        Icon(
                            modifier = Modifier.size(20.dp),
                            imageVector = Icons.AutoMirrored.Rounded.Logout,
                            contentDescription = null,
                            tint = Color.White
                        )
                    }
                }
                PuzzleGeneralTitle(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.txt_ttl_form_create_gamer)
                )
                Spacer(modifier = Modifier.height(50.dp))
                FormEditGamer(
                    labelButtonSaveGamer = labelButtonSaveGamer,
                    socialNetworkImage = socialNetworkImage,
                    editGamerFormState = editGamerFormState,
                    countriesUiState = countries,
                    validateFormCreateGamer = validateFormCreateGamer,
                    saveGamer = saveGamer
                )
            }
        }
        Box(modifier = Modifier.height(250.dp))
    }
}

@Composable
fun FormEditGamer(
    labelButtonSaveGamer: String,
    socialNetworkImage: String?,
    editGamerFormState: EditGamerFormUiState,
    countriesUiState: List<Country>?,
    validateFormCreateGamer: (String, String, CountryData, String?) -> Unit,
    enableActionButton: Boolean = editGamerFormState.aliasUiState.state is InputState.Success && editGamerFormState.ageUiState.state is InputState.Success && editGamerFormState.countryUiState.state is InputState.Success,
    saveGamer: () -> Unit
) {
    var showChangeProfileImageDialog: Boolean by rememberSaveable { mutableStateOf(false) }
    when (val imageSelectedState = editGamerFormState.imageURI.state) {
        is InputState.Error<InputImageError> -> imageSelectedState.errors.map { getErrorImage(it) }
        InputState.Idle, InputState.Success -> listOf()
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            validateFormCreateGamer(
                editGamerFormState.aliasUiState.value,
                editGamerFormState.ageUiState.value,
                editGamerFormState.countryUiState.value,
                uri.toString()
            )
        }
    }
    if (showChangeProfileImageDialog) {
        ChangeProfileImageDialog(
            socialNetworkImage = socialNetworkImage
        ) { optionChosen ->
            showChangeProfileImageDialog = false
            when (optionChosen) {
                OptionChosen.Default -> validateFormCreateGamer(
                    editGamerFormState.aliasUiState.value,
                    editGamerFormState.ageUiState.value,
                    editGamerFormState.countryUiState.value,
                    null
                )

                OptionChosen.Gallery -> launcher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )

                is OptionChosen.SocialNetwork -> validateFormCreateGamer(
                    editGamerFormState.aliasUiState.value,
                    editGamerFormState.ageUiState.value,
                    editGamerFormState.countryUiState.value,
                    optionChosen.urlImage
                )
            }
        }
    }
    Column(
        modifier = Modifier.padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InputSelectImage(
            imageURI = editGamerFormState.imageURI.value
        ) {
            showChangeProfileImageDialog = true
        }
        Spacer(modifier = Modifier.height(10.dp))
        DefaultOutlinedTextFieldLI(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            value = editGamerFormState.aliasUiState.value,
            label = R.string.txt_label_alias,
            leadingIcon = Icons.Default.Person,
            cdLeadingIcon = null,
            errors = editGamerFormState.aliasUiState.state.let { aliasState ->
                when (aliasState) {
                    is InputState.Error<InputAliasError> -> aliasState.errors.map { getErrorAlias(it) }
                    InputState.Idle, InputState.Success -> listOf()
                }
            }
        ) { newAlias ->
            validateFormCreateGamer(
                newAlias,
                editGamerFormState.ageUiState.value,
                editGamerFormState.countryUiState.value,
                editGamerFormState.imageURI.value
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        DefaultOutlinedTextFieldLI(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = editGamerFormState.ageUiState.value,
            label = R.string.txt_label_age,
            leadingIcon = Icons.Default.Attribution,
            cdLeadingIcon = null,
            errors = editGamerFormState.ageUiState.state.let { ageState ->
                when (ageState) {
                    is InputState.Error<InputAgeError> -> ageState.errors.map { getErrorAge(it) }
                    InputState.Idle, InputState.Success -> listOf()
                }
            }
        ) { newAge ->
            validateFormCreateGamer(
                editGamerFormState.aliasUiState.value,
                newAge,
                editGamerFormState.countryUiState.value,
                editGamerFormState.imageURI.value
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        CountryInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            countriesUiState = countriesUiState,
            value = editGamerFormState.countryUiState.value,
            countryErrors = editGamerFormState.countryUiState.state.let { countryState ->
                when (countryState) {
                    is InputState.Error<InputCountryError> -> countryState.errors.map {
                        getErrorCountry(it)
                    }

                    InputState.Idle, InputState.Success -> listOf()
                }
            }
        ) {
            validateFormCreateGamer(
                editGamerFormState.aliasUiState.value,
                editGamerFormState.ageUiState.value,
                it,
                editGamerFormState.imageURI.value
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DefaultButton(
                text = labelButtonSaveGamer,
                enable = enableActionButton,
                onClickButton = saveGamer
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun CountryInput(
    modifier: Modifier = Modifier,
    countriesUiState: List<Country>?,
    value: CountryData,
    countryErrors: List<String>,
    onCountryChange: (CountryData) -> Unit
) {
    if (countriesUiState == null) {
        DefaultOutlinedTextFieldLI(
            modifier = modifier,
            value = value.name,
            label = R.string.txt_label_country,
            leadingIcon = Icons.Default.Public,
            cdLeadingIcon = null,
            errors = countryErrors,
        ) { country ->
            onCountryChange(CountryData(country, null))
        }
    } else {
        var showChoseCountryDialog by rememberSaveable {
            mutableStateOf(false)
        }

        if (showChoseCountryDialog) {
            ChooseCountryDialog(
                country = value.name, flag = value.flag, countriesUiState = countriesUiState
            ) { country, flag ->
                showChoseCountryDialog = false
                onCountryChange(CountryData(country, flag))
            }
        }
        DefaultOutlinedTextFieldLI(
            modifier = modifier,
            value = value.name,
            label = R.string.txt_label_country,
            leadingIcon = Icons.Default.Public,
            cdLeadingIcon = null,
            readOnly = true,
            errors = countryErrors,
            onClickable = {
                showChoseCountryDialog = true
            })
    }
}

@Composable
fun ChooseCountryDialog(
    country: String,
    flag: String?,
    countriesUiState: List<Country>,
    onCountrySelected: (String, String?) -> Unit
) {
    Dialog(
        onDismissRequest = { onCountrySelected(country, flag) }) {
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
                        ItemCountry(country = it) { countryUiState ->
                            onCountrySelected(countryUiState.name, countryUiState.flag)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InputSelectImage(
    imageURI: String?,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier.size(128.dp)
    ) {
        AsyncImage(
            modifier = Modifier
                .size(size = 128.dp)
                .clip(CircleShape)
                .border(width = 2.dp, color = Color.Black, shape = CircleShape),
            contentScale = ContentScale.Crop,
            model = when {
                imageURI == null -> R.drawable.gamer
                else -> imageURI
            },
            contentDescription = null
        )
        Button(
            modifier = Modifier
                .size(38.dp)
                .align(Alignment.BottomEnd),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black, contentColor = Color.White
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
    socialNetworkImage: String?,
    onOptionSelected: (OptionChosen) -> Unit
) {
    Dialog(
        onDismissRequest = { onOptionSelected(OptionChosen.Default) }) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(vertical = 20.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = dimensionResource(
                        id = R.dimen.title_text_size
                    ).value.sp,
                    text = stringResource(R.string.txt_ttl_choose_option)
                )
                Spacer(modifier = Modifier.height(30.dp))
                OptionItem(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onOptionSelected(OptionChosen.Default) }) {
                    DefaultText(
                        text = stringResource(R.string.txt_label_default_image)
                    )
                }
                OptionItem(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onOptionSelected(OptionChosen.Gallery) }) {
                    DefaultText(
                        text = stringResource(R.string.txt_label_gallery)
                    )
                }
                if (socialNetworkImage != null) {
                    OptionItem(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onOptionSelected(OptionChosen.SocialNetwork(socialNetworkImage)) }) {
                        DefaultText(
                            text = stringResource(R.string.txt_label_social_network_image)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OptionItem(
    modifier: Modifier, onClick: () -> Unit, content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        content = content)
}

sealed class OptionChosen {
    data object Default : OptionChosen()
    data object Gallery : OptionChosen()
    data class SocialNetwork(val urlImage: String) : OptionChosen()
}