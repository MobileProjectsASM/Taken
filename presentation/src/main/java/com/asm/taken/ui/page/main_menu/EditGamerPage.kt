package com.asm.taken.ui.page.main_menu

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Attribution
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Gamer
import com.asm.domain.errors.GeneralError
import com.asm.taken.R
import com.asm.taken.model.CommonProcessState
import com.asm.taken.model.CountryData
import com.asm.taken.model.EditGamerProcessType
import com.asm.taken.model.EditGamerUIState
import com.asm.taken.model.InputAgeError
import com.asm.taken.model.InputAliasError
import com.asm.taken.model.InputCountryError
import com.asm.taken.model.InputImageError
import com.asm.taken.model.InputState
import com.asm.taken.model.InputUiState
import com.asm.taken.model.MetaDataEditForm
import com.asm.taken.ui.CircularProgressDialog
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultIconButton
import com.asm.taken.ui.DefaultOutlinedTextFieldLI
import com.asm.taken.ui.ErrorComponent
import com.asm.taken.ui.ImageDialog
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.ui.SnackBarError
import com.asm.taken.ui.page.login.ChangeProfileImageDialog
import com.asm.taken.ui.page.login.CountryInput
import com.asm.taken.ui.page.login.InputSelectImage
import com.asm.taken.ui.page.login.OptionChosen
import com.asm.taken.utils.getErrorAge
import com.asm.taken.utils.getErrorAlias
import com.asm.taken.utils.getErrorCountry
import com.asm.taken.utils.getErrorImage
import com.asm.taken.vm.EditGamerVM
import kotlinx.coroutines.launch

@Composable
fun EditGamerPage(
    gamerId: String,
    snackBarHostState: SnackbarHostState,
    editGamerVM: EditGamerVM,
    navigateToMainMenu: () -> Unit
) {
    val editGamerUIState: EditGamerUIState by editGamerVM.editGamerUIState.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        editGamerVM.getGamerData(
            gamerId = gamerId
        )
    }

    EditGamerSection(
        editGamerFormState = editGamerUIState,
        gamerId = gamerId,
        snackBarHostState = snackBarHostState,
        saveGamer = editGamerVM::saveGamer,
        validateEditGamerForm = editGamerVM::validateEditGamerForm,
        deleteGamer = {

        },
        navigateToMainMenu = navigateToMainMenu,
        retryGetMetaData = { editGamerVM.getGamerData(gamerId = gamerId) },
        resetGamerProcessState = editGamerVM::resetEditGamerProcessState,
        onBack = navigateToMainMenu
    )
}

@Composable
fun EditGamerSection(
    editGamerFormState: EditGamerUIState,
    gamerId: String,
    snackBarHostState: SnackbarHostState,
    saveGamer: (String, String, Int, String, String?, String?) -> Unit,
    validateEditGamerForm: (String, String, CountryData, String?) -> Unit,
    deleteGamer: () -> Unit,
    navigateToMainMenu: () -> Unit,
    retryGetMetaData: () -> Unit,
    resetGamerProcessState: () -> Unit,
    onBack: () -> Unit
) {
    when (editGamerFormState) {
        is EditGamerUIState.Failure -> ErrorComponent(
            generalError = editGamerFormState.error,
            retryProcess = retryGetMetaData,
            onBack = onBack
        )

        EditGamerUIState.Loading -> CircularProgressDialog()
        is EditGamerUIState.Success -> PanelFormEditGamer(
            metaDataEditForm = editGamerFormState.metaDataEditForm,
            snackBarHostState = snackBarHostState,
            currentGamer = editGamerFormState.metaDataEditForm.gamer,
            defaultImageUrl = editGamerFormState.metaDataEditForm.defaultImageUrl,
            labelButtonSaveGamer = stringResource(R.string.txt_btn_save_changes),
            socialNetworkImage = editGamerFormState.metaDataEditForm.socialNetworkImage,
            countries = editGamerFormState.metaDataEditForm.countries,
            validateFormCreateGamer = validateEditGamerForm,
            saveGamer = {
                saveGamer(
                    gamerId,
                    editGamerFormState.metaDataEditForm.aliasUiState.value,
                    editGamerFormState.metaDataEditForm.ageUiState.value.toInt(),
                    editGamerFormState.metaDataEditForm.countryUiState.value.name,
                    editGamerFormState.metaDataEditForm.countryUiState.value.flag,
                    editGamerFormState.metaDataEditForm.imageURI.value
                )
            },
            deleteGamer = deleteGamer,
            navigateToMainMenu = navigateToMainMenu,
            onBack = navigateToMainMenu,
            resetGamerProcessState = resetGamerProcessState
        )

        CommonProcessState.Idle -> return
    }
}

@Composable
fun ResultOperationsSection(
    editGamerProcessType: EditGamerProcessType,
    snackBarHostState: SnackbarHostState,
    navigateToMainMenu: () -> Unit,
    retryUpdateGamerProcess: () -> Unit,
    retryDeleteGamerProcess: () -> Unit,
    resetEditGamerProcess: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    when (editGamerProcessType) {
        is EditGamerProcessType.DeleteGamerState -> when (val processState =
            editGamerProcessType.processState) {
            is CommonProcessState.Failure -> ErrorProcessComponent(
                snackBarHostState = snackBarHostState,
                generalError = processState.error,
                retryProcess = retryDeleteGamerProcess,
                resetProcess = resetEditGamerProcess
            )

            CommonProcessState.Loading -> CircularProgressDialog()
            is CommonProcessState.Success<Unit> -> TODO()
            CommonProcessState.Idle -> return
        }

        is EditGamerProcessType.UpdateGamerState -> when (val processState =
            editGamerProcessType.processState) {
            is CommonProcessState.Failure -> ErrorProcessComponent(
                snackBarHostState = snackBarHostState,
                generalError = processState.error,
                retryProcess = retryUpdateGamerProcess,
                resetProcess = resetEditGamerProcess
            )

            CommonProcessState.Loading -> CircularProgressDialog()
            is CommonProcessState.Success<Unit> -> {
                ImageDialog(
                    title = stringResource(R.string.txt_ttl_success_operation),
                    image = painterResource(R.drawable.ic_success),
                    message = stringResource(R.string.txt_label_gamer_updated)
                ) {
                    DefaultButton(
                        text = stringResource(R.string.txt_btn_accept),
                        onClickButton = {
                            resetEditGamerProcess()
                            coroutineScope.launch {
                                navigateToMainMenu()
                            }
                        }
                    )
                }
            }

            CommonProcessState.Idle -> return
        }

        EditGamerProcessType.Idle -> return
    }
}

@Composable
fun PanelFormEditGamer(
    metaDataEditForm: MetaDataEditForm,
    snackBarHostState: SnackbarHostState,
    currentGamer: Gamer? = null,
    defaultImageUrl: String? = null,
    labelButtonSaveGamer: String,
    socialNetworkImage: String?,
    countries: List<CountryInfo>?,
    validateFormCreateGamer: (String, String, CountryData, String?) -> Unit,
    saveGamer: () -> Unit,
    deleteGamer: () -> Unit,
    onBack: () -> Unit,
    navigateToMainMenu: () -> Unit,
    resetGamerProcessState: () -> Unit
) {
    Box {
        var showConfirmationDialog by rememberSaveable { mutableStateOf(false) }
        if (showConfirmationDialog) {
            ImageDialog(
                title = stringResource(R.string.txt_ttl_warning),
                message = stringResource(R.string.txt_label_confirm_delete_message),
                image = painterResource(R.drawable.ic_warning),
                onClose = {
                    showConfirmationDialog = false
                },
                onDismissRequest = {
                    showConfirmationDialog = false
                }
            ) {
                DefaultButton(
                    text = stringResource(id = R.string.txt_btn_confirm_delete_button),
                    onClickButton = {
                        showConfirmationDialog = false
                        deleteGamer()
                    }
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        IconButton(
                            modifier = Modifier
                                .background(color = Color.Red, shape = CircleShape)
                                .size(32.dp),
                            onClick = onBack
                        ) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                    }
                    PuzzleGeneralTitle(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.txt_ttl_form_edit_gamer)
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                    FormEditGamer(
                        snackBarHostState = snackBarHostState,
                        labelButtonSaveGamer = labelButtonSaveGamer,
                        errorImageUrlNotFound = stringResource(R.string.txt_label_image_url_not_found),
                        socialNetworkImage = socialNetworkImage,
                        countriesUiState = countries,
                        validateFormCreateGamer = validateFormCreateGamer,
                        metaDataEditForm = metaDataEditForm,
                        enableActionButton = metaDataEditForm.aliasUiState.state is InputState.Success && metaDataEditForm.ageUiState.state is InputState.Success && metaDataEditForm.countryUiState.state is InputState.Success && currentGamer?.let {
                            it.gamerNickName != metaDataEditForm.aliasUiState.value
                                    || it.gamerAge.toString() != metaDataEditForm.ageUiState.value
                                    || it.gamerCountry != metaDataEditForm.countryUiState.value.name
                                    || (it.gamerImage == defaultImageUrl
                                    && metaDataEditForm.imageURI.value != null
                                    && metaDataEditForm.imageURI.value != defaultImageUrl)
                                    || (it.gamerImage != defaultImageUrl && metaDataEditForm.imageURI.value != it.gamerImage)
                        } ?: false,
                        saveGamer = saveGamer
                    )
                    DefaultButton(
                        modifier = Modifier.padding(bottom = 10.dp),
                        text = stringResource(R.string.txt_btn_delete_gamer),
                        color = colorResource(R.color.red),
                        onClickButton = {
                            showConfirmationDialog = true
                        }
                    )
                }
            }
        }
        ResultOperationsSection(
            editGamerProcessType = metaDataEditForm.editGamerProcessType,
            snackBarHostState = snackBarHostState,
            navigateToMainMenu = navigateToMainMenu,
            retryUpdateGamerProcess = saveGamer,
            retryDeleteGamerProcess = deleteGamer,
            resetEditGamerProcess = resetGamerProcessState
        )
    }
}

@Composable
fun FormEditGamer(
    snackBarHostState: SnackbarHostState,
    errorImageUrlNotFound: String,
    labelButtonSaveGamer: String,
    socialNetworkImage: String?,
    metaDataEditForm: MetaDataEditForm,
    countriesUiState: List<CountryInfo>?,
    validateFormCreateGamer: (String, String, CountryData, String?) -> Unit,
    enableActionButton: Boolean = metaDataEditForm.aliasUiState.state is InputState.Success && metaDataEditForm.ageUiState.state is InputState.Success && metaDataEditForm.countryUiState.state is InputState.Success,
    saveGamer: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var showChangeProfileImageDialog: Boolean by rememberSaveable { mutableStateOf(false) }
    when (val imageSelectedState = metaDataEditForm.imageURI.state) {
        is InputState.Error<InputImageError> -> imageSelectedState.errors.map { getErrorImage(it) }
        InputState.Idle, InputState.Success -> listOf()
    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            validateFormCreateGamer(
                metaDataEditForm.aliasUiState.value,
                metaDataEditForm.ageUiState.value,
                metaDataEditForm.countryUiState.value,
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
                    metaDataEditForm.aliasUiState.value,
                    metaDataEditForm.ageUiState.value,
                    metaDataEditForm.countryUiState.value,
                    null
                )

                OptionChosen.Gallery -> launcher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )

                is OptionChosen.SocialNetwork -> validateFormCreateGamer(
                    metaDataEditForm.aliasUiState.value,
                    metaDataEditForm.ageUiState.value,
                    metaDataEditForm.countryUiState.value,
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
            imageURI = metaDataEditForm.imageURI.value,
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
            value = metaDataEditForm.aliasUiState.value,
            label = R.string.txt_label_alias,
            leadingIcon = Icons.Default.Person,
            cdLeadingIcon = null,
            errors = metaDataEditForm.aliasUiState.state.let { aliasState ->
                when (aliasState) {
                    is InputState.Error<InputAliasError> -> aliasState.errors.map { getErrorAlias(it) }
                    InputState.Idle, InputState.Success -> listOf()
                }
            }
        ) { newAlias ->
            validateFormCreateGamer(
                newAlias,
                metaDataEditForm.ageUiState.value,
                metaDataEditForm.countryUiState.value,
                metaDataEditForm.imageURI.value
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        DefaultOutlinedTextFieldLI(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            value = metaDataEditForm.ageUiState.value,
            label = R.string.txt_label_age,
            leadingIcon = Icons.Default.Attribution,
            cdLeadingIcon = null,
            errors = metaDataEditForm.ageUiState.state.let { ageState ->
                when (ageState) {
                    is InputState.Error<InputAgeError> -> ageState.errors.map { getErrorAge(it) }
                    InputState.Idle, InputState.Success -> listOf()
                }
            }
        ) { newAge ->
            validateFormCreateGamer(
                metaDataEditForm.aliasUiState.value,
                newAge,
                metaDataEditForm.countryUiState.value,
                metaDataEditForm.imageURI.value
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        CountryInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            countriesUiState = countriesUiState,
            value = metaDataEditForm.countryUiState.value,
            countryErrors = metaDataEditForm.countryUiState.state.let { countryState ->
                when (countryState) {
                    is InputState.Error<InputCountryError> -> countryState.errors.map {
                        getErrorCountry(it)
                    }

                    InputState.Idle, InputState.Success -> listOf()
                }
            }
        ) {
            validateFormCreateGamer(
                metaDataEditForm.aliasUiState.value,
                metaDataEditForm.ageUiState.value,
                it,
                metaDataEditForm.imageURI.value
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
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun ErrorComponent(
    generalError: GeneralError,
    retryProcess: () -> Unit,
    onBack: () -> Unit
) {
    when (generalError) {
        is GeneralError.ClientError -> DialogError(
            title = stringResource(R.string.txt_ttl_client_error),
            image = painterResource(R.drawable.ic_warning),
            message = stringResource(R.string.err_client),
            onAction = retryProcess,
            onBack = onBack
        )

        GeneralError.ConnectionError -> DialogError(
            title = stringResource(R.string.txt_ttl_client_error),
            image = painterResource(R.drawable.ic_sin_internet),
            message = stringResource(R.string.err_connection),
            onAction = retryProcess,
            onBack = onBack
        )

        is GeneralError.ServerError -> DialogError(
            title = stringResource(R.string.txt_ttl_service_error),
            image = painterResource(R.drawable.ic_error),
            message = stringResource(R.string.err_server),
            onAction = retryProcess,
            onBack = onBack
        )

        GeneralError.Unknown -> DialogError(
            title = stringResource(R.string.txt_ttl_unexpected_error),
            image = painterResource(R.drawable.ic_warning),
            message = stringResource(R.string.err_unknown),
            onAction = retryProcess,
            onBack = onBack
        )
    }
}

@Composable
fun ErrorProcessComponent(
    snackBarHostState: SnackbarHostState,
    generalError: GeneralError,
    retryProcess: () -> Unit,
    resetProcess: () -> Unit
) {
    when (generalError) {
        is GeneralError.ClientError -> DialogError(
            title = stringResource(R.string.txt_ttl_client_error),
            image = painterResource(R.drawable.ic_warning),
            message = stringResource(R.string.err_client),
            onDismissRequest = resetProcess,
            onClose = resetProcess,
            onAction = retryProcess
        )

        GeneralError.ConnectionError -> DialogError(
            title = stringResource(R.string.txt_ttl_client_error),
            image = painterResource(R.drawable.ic_sin_internet),
            message = stringResource(R.string.err_connection),
            onDismissRequest = resetProcess,
            onClose = resetProcess,
            onAction = retryProcess
        )

        is GeneralError.ServerError -> DialogError(
            title = stringResource(R.string.txt_ttl_service_error),
            image = painterResource(R.drawable.ic_error),
            message = stringResource(R.string.err_server),
            onDismissRequest = resetProcess,
            onClose = resetProcess,
            onAction = retryProcess
        )

        GeneralError.Unknown -> SnackBarError(
            snackBarHostState = snackBarHostState,
            message = stringResource(R.string.err_auth),
            withDismissAction = true,
            onDismissed = resetProcess
        )
    }
}

@Composable
fun DialogError(
    title: String,
    image: Painter,
    message: String,
    onBack: () -> Unit,
    onAction: () -> Unit
) {
    ImageDialog(
        title = title,
        image = image,
        message = message,
        onBack = onBack
    ) {
        DefaultIconButton(
            text = stringResource(id = R.string.txt_label_retry),
            imageVector = Icons.Filled.Replay,
            onClickButton = onAction
        )
    }
}

@Composable
fun DialogError(
    title: String,
    image: Painter,
    message: String,
    onDismissRequest: (() -> Unit)? = null,
    onClose: () -> Unit,
    onAction: () -> Unit
) {
    ImageDialog(
        title = title,
        image = image,
        message = message,
        onDismissRequest = onDismissRequest,
        onClose = onClose
    ) {
        DefaultIconButton(
            text = stringResource(id = R.string.txt_label_retry),
            imageVector = Icons.Filled.Replay,
            onClickButton = onAction
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun EditGamerPagePreview() {
    val snackBarHostState = remember { SnackbarHostState() }

    val editForm = MetaDataEditForm(
        imageURI = InputUiState(""),
        aliasUiState = InputUiState(""),
        ageUiState = InputUiState(""),
        countryUiState = InputUiState(CountryData()),
        countries = listOf(),
        gamer = Gamer(
            gamerId = "",
            gamerNickName = "",
            gamerAge = 20,
            gamerCountry = "",
            gamerCountryFlag = "",
            gamerImage = ""
        ),
        defaultImageUrl = "",
        socialNetworkImage = "",
        editGamerProcessType = EditGamerProcessType.DeleteGamerState(
            processState = CommonProcessState.Success(
                Unit
            )
        )
    )

    EditGamerSection(
        editGamerFormState = EditGamerUIState.Success(metaDataEditForm = editForm),
        gamerId = "abcd-efgh",
        snackBarHostState = snackBarHostState,
        saveGamer = { a, b, c, d, e, f ->

        },
        validateEditGamerForm = { a, b, c, d ->

        },
        deleteGamer = {

        },
        navigateToMainMenu = {

        },
        retryGetMetaData = { },
        resetGamerProcessState = {

        },
        onBack = {}
    )
}