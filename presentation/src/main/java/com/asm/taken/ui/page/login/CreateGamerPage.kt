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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
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
import coil.compose.AsyncImagePainter
import com.asm.domain.entities.CountryInfo
import com.asm.taken.R
import com.asm.taken.model.CountriesState
import com.asm.taken.model.CountryData
import com.asm.taken.model.CreateGamerProcessState
import com.asm.taken.model.CreateGamerUIState
import com.asm.taken.model.CreateGamerFormState
import com.asm.taken.model.InputAgeError
import com.asm.taken.model.InputAliasError
import com.asm.taken.model.InputCountryError
import com.asm.taken.model.InputImageError
import com.asm.taken.model.InputState
import com.asm.taken.ui.CircularProgressDialog
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultOutlinedTextFieldLI
import com.asm.taken.ui.DefaultText
import com.asm.taken.ui.ErrorProcessComponent
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.ui.navigation.CreateGamer
import com.asm.taken.ui.puzzleFontFamily
import com.asm.taken.utils.getErrorAge
import com.asm.taken.utils.getErrorAlias
import com.asm.taken.utils.getErrorCountry
import com.asm.taken.utils.getErrorImage
import com.asm.taken.vm.CreateGamerVM
import kotlinx.coroutines.launch

@Composable
fun CreateGamerPage(
    createGamerInfo: CreateGamer,
    createGamerVM: CreateGamerVM,
    snackBarHostState: SnackbarHostState,
    onNavigateToAuthentication: () -> Unit,
    onNavigateToHome: (String) -> Unit
) {
    val createGamerUIState: CreateGamerUIState by createGamerVM.createGamerUIState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        createGamerVM.getCountriesInfo()
    }

    CreateGamerSection(
        createGamerFormState = createGamerUIState.createGamerFormState,
        socialNetworkImage = createGamerInfo.image,
        gamerId = createGamerInfo.id,
        snackBarHostState = snackBarHostState,
        validateFormCreateGamer = createGamerVM::validateCreateGamerForm,
        saveGamer = createGamerVM::createGamer,
        closeSession = createGamerVM::closeSession,
        resetProcess = createGamerVM::resetCountriesState,
        retryGetCountries = createGamerVM::getCountriesInfo
    )
    NavigationSection(
        createGamerProcessState = createGamerUIState.createGamerProcessState,
        snackBarHostState = snackBarHostState,
        onNavigateToHome = onNavigateToHome,
        onNavigateToAuthentication = onNavigateToAuthentication,
        retryCreateGamer = {

        },
        resetProcess = createGamerVM::resetProcessState
    )
}

@Composable
fun CreateGamerSection(
    createGamerFormState: CreateGamerFormState,
    socialNetworkImage: String?,
    gamerId: String,
    snackBarHostState: SnackbarHostState,
    validateFormCreateGamer: (String, String, CountryData, String?) -> Unit,
    saveGamer: (String, String, Int, String, String?, String?) -> Unit,
    closeSession: () -> Unit,
    resetProcess: () -> Unit,
    retryGetCountries: () -> Unit
) {
    PanelFormCreateGamer(
        snackBarHostState = snackBarHostState,
        labelButtonSaveGamer = stringResource(R.string.txt_btn_create_gamer),
        socialNetworkImage = socialNetworkImage,
        createGamerFormState = createGamerFormState,
        countries = (createGamerFormState.countriesState as? CountriesState.CountriesLoaded)?.countriesInfo,
        validateFormCreateGamer = validateFormCreateGamer,
        saveGamer = {
            saveGamer(
                gamerId,
                createGamerFormState.aliasUiState.value,
                createGamerFormState.ageUiState.value.toInt(),
                createGamerFormState.countryUiState.value.name,
                createGamerFormState.countryUiState.value.flag,
                createGamerFormState.imageURI.value
            )
        },
        closeSession = closeSession
    )
    when (val countriesState = createGamerFormState.countriesState) {
        is CountriesState.Error -> ErrorProcessComponent(
            generalError = countriesState.generalError,
            snackBarHostState = snackBarHostState,
            resetProcess = resetProcess,
            retryProcess = retryGetCountries
        )

        CountriesState.Loading -> CircularProgressDialog()
        else -> return
    }
}

@Composable
fun NavigationSection(
    createGamerProcessState: CreateGamerProcessState,
    snackBarHostState: SnackbarHostState,
    onNavigateToHome: (String) -> Unit,
    onNavigateToAuthentication: () -> Unit,
    retryCreateGamer: () -> Unit,
    resetProcess: () -> Unit
) {
    when (createGamerProcessState) {
        is CreateGamerProcessState.Failure -> ErrorProcessComponent(
            generalError = createGamerProcessState.error,
            snackBarHostState = snackBarHostState,
            retryProcess = retryCreateGamer,
            resetProcess = resetProcess
        )
        is CreateGamerProcessState.GamerCreated -> LaunchedEffect(true) {
            onNavigateToHome(createGamerProcessState.gamerId)
        }
        CreateGamerProcessState.Loading -> CircularProgressDialog()
        CreateGamerProcessState.SessionClosed -> LaunchedEffect(true) {
            onNavigateToAuthentication()
        }
        CreateGamerProcessState.Idle -> return
    }
}

@Composable
fun PanelFormCreateGamer(
    snackBarHostState: SnackbarHostState,
    labelButtonSaveGamer: String,
    socialNetworkImage: String?,
    createGamerFormState: CreateGamerFormState,
    countries: List<CountryInfo>?,
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
                    snackBarHostState = snackBarHostState,
                    labelButtonSaveGamer = labelButtonSaveGamer,
                    socialNetworkImage = socialNetworkImage,
                    errorImageUrlNotFound = stringResource(R.string.txt_label_image_url_not_found),
                    createGamerFormState = createGamerFormState,
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
    snackBarHostState: SnackbarHostState,
    errorImageUrlNotFound: String,
    labelButtonSaveGamer: String,
    socialNetworkImage: String?,
    createGamerFormState: CreateGamerFormState,
    countriesUiState: List<CountryInfo>?,
    validateFormCreateGamer: (String, String, CountryData, String?) -> Unit,
    enableActionButton: Boolean = createGamerFormState.aliasUiState.state is InputState.Success && createGamerFormState.ageUiState.state is InputState.Success && createGamerFormState.countryUiState.state is InputState.Success,
    saveGamer: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var showChangeProfileImageDialog: Boolean by rememberSaveable { mutableStateOf(false) }
    when (val imageSelectedState = createGamerFormState.imageURI.state) {
        is InputState.Error<InputImageError> -> imageSelectedState.errors.map { getErrorImage(it) }
        InputState.Idle, InputState.Success -> listOf()
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            validateFormCreateGamer(
                createGamerFormState.aliasUiState.value,
                createGamerFormState.ageUiState.value,
                createGamerFormState.countryUiState.value,
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
                    createGamerFormState.aliasUiState.value,
                    createGamerFormState.ageUiState.value,
                    createGamerFormState.countryUiState.value,
                    null
                )

                OptionChosen.Gallery -> launcher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )

                is OptionChosen.SocialNetwork -> validateFormCreateGamer(
                    createGamerFormState.aliasUiState.value,
                    createGamerFormState.ageUiState.value,
                    createGamerFormState.countryUiState.value,
                    optionChosen.urlImage
                )

                null -> return@ChangeProfileImageDialog
            }
        }
    }
    Column(
        modifier = Modifier.padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InputSelectImage(
            imageURI = createGamerFormState.imageURI.value,
            error = painterResource(R.drawable.gamer),
            onError = {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar(
                        message = errorImageUrlNotFound,
                        withDismissAction = true,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        ) {
            showChangeProfileImageDialog = true
        }
        Spacer(modifier = Modifier.height(10.dp))
        DefaultOutlinedTextFieldLI(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            value = createGamerFormState.aliasUiState.value,
            label = R.string.txt_label_alias,
            leadingIcon = Icons.Default.Person,
            cdLeadingIcon = null,
            errors = createGamerFormState.aliasUiState.state.let { aliasState ->
                when (aliasState) {
                    is InputState.Error<InputAliasError> -> aliasState.errors.map { getErrorAlias(it) }
                    InputState.Idle, InputState.Success -> listOf()
                }
            }
        ) { newAlias ->
            validateFormCreateGamer(
                newAlias,
                createGamerFormState.ageUiState.value,
                createGamerFormState.countryUiState.value,
                createGamerFormState.imageURI.value
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        DefaultOutlinedTextFieldLI(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = createGamerFormState.ageUiState.value,
            label = R.string.txt_label_age,
            leadingIcon = Icons.Default.Attribution,
            cdLeadingIcon = null,
            errors = createGamerFormState.ageUiState.state.let { ageState ->
                when (ageState) {
                    is InputState.Error<InputAgeError> -> ageState.errors.map { getErrorAge(it) }
                    InputState.Idle, InputState.Success -> listOf()
                }
            }
        ) { newAge ->
            validateFormCreateGamer(
                createGamerFormState.aliasUiState.value,
                newAge,
                createGamerFormState.countryUiState.value,
                createGamerFormState.imageURI.value
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        CountryInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            countriesUiState = countriesUiState,
            value = createGamerFormState.countryUiState.value,
            countryErrors = createGamerFormState.countryUiState.state.let { countryState ->
                when (countryState) {
                    is InputState.Error<InputCountryError> -> countryState.errors.map {
                        getErrorCountry(it)
                    }

                    InputState.Idle, InputState.Success -> listOf()
                }
            }
        ) {
            validateFormCreateGamer(
                createGamerFormState.aliasUiState.value,
                createGamerFormState.ageUiState.value,
                it,
                createGamerFormState.imageURI.value
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
    countriesUiState: List<CountryInfo>?,
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
    countriesUiState: List<CountryInfo>,
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
    error: Painter? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
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
            error = error,
            onError = onError,
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
    onOptionSelected: (OptionChosen?) -> Unit
) {
    Dialog(
        onDismissRequest = { onOptionSelected(null) }) {
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