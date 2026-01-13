package com.asm.taken.ui.page.main_menu

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Gamer
import com.asm.taken.R
import com.asm.taken.model.CommonProcessState
import com.asm.taken.model.CountryData
import com.asm.taken.model.EditGamerFormState
import com.asm.taken.model.EditGamerProcessType
import com.asm.taken.model.EditGamerUIState
import com.asm.taken.model.InputAgeError
import com.asm.taken.model.InputAliasError
import com.asm.taken.model.InputCountryError
import com.asm.taken.model.InputImageError
import com.asm.taken.model.InputState
import com.asm.taken.model.MetaDataEditForm
import com.asm.taken.ui.CircularProgressDialog
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultOutlinedTextFieldLI
import com.asm.taken.ui.ErrorComponent
import com.asm.taken.ui.ImageDialog
import com.asm.taken.ui.PuzzleGeneralTitle
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
        editGamerFormState = editGamerUIState.editGamerFormState,
        gamerId = gamerId,
        snackBarHostState = snackBarHostState,
        saveGamer = editGamerVM::saveGamer,
        validateEditGamerForm = editGamerVM::validateEditGamerForm,
        deleteGamer = {

        },
        navigateToMainMenu = navigateToMainMenu,
        retryGetMetaData = { editGamerVM.getGamerData(gamerId = gamerId) },
        resetGetMetaDataProcess = editGamerVM::resetMetaDataProcessState
    )
    ResultOperationsSection(
        editGamerProcessType = editGamerUIState.editGamerProcessType,
        snackBarHostState = snackBarHostState,
        navigateToMainMenu = navigateToMainMenu,
        retryUpdateGamerProcess = {

        },
        retryDeleteGamerProcess = {

        },
        resetEditGamerProcess = editGamerVM::resetEditGamerProcessState
    )
}

@Composable
fun EditGamerSection(
    editGamerFormState: EditGamerFormState,
    gamerId: String,
    snackBarHostState: SnackbarHostState,
    saveGamer: (String, String, Int, String, String?, String?) -> Unit,
    validateEditGamerForm: (String, String, CountryData, String?) -> Unit,
    deleteGamer: () -> Unit,
    navigateToMainMenu: () -> Unit,
    retryGetMetaData: () -> Unit,
    resetGetMetaDataProcess: () -> Unit
) {
    PanelFormEditGamer(
        editGamerFormState = editGamerFormState,
        snackBarHostState = snackBarHostState,
        currentGamer = (editGamerFormState.metaDataFormState as? CommonProcessState.Success)?.data?.gamer,
        defaultImageUrl = (editGamerFormState.metaDataFormState as? CommonProcessState.Success)?.data?.defaultImageUrl,
        labelButtonSaveGamer = stringResource(R.string.txt_btn_save_changes),
        socialNetworkImage = (editGamerFormState.metaDataFormState as? CommonProcessState.Success)?.data?.socialNetworkImage,
        countries = (editGamerFormState.metaDataFormState as? CommonProcessState.Success)?.data?.countries,
        validateFormCreateGamer = validateEditGamerForm,
        saveGamer = {
            saveGamer(
                gamerId,
                editGamerFormState.aliasUiState.value,
                editGamerFormState.ageUiState.value.toInt(),
                editGamerFormState.countryUiState.value.name,
                editGamerFormState.countryUiState.value.flag,
                editGamerFormState.imageURI.value
            )
        },
        deleteGamer = deleteGamer,
        onBack = navigateToMainMenu
    )

    when (val state = editGamerFormState.metaDataFormState) {
        is CommonProcessState.Failure -> ErrorComponent(
            generalError = state.error,
            snackBarHostState = snackBarHostState,
            retryProcess = retryGetMetaData,
            resetProcessState = resetGetMetaDataProcess
        )

        CommonProcessState.Loading -> CircularProgressDialog()
        CommonProcessState.Idle, is CommonProcessState.Success<MetaDataEditForm> -> return
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
    when (editGamerProcessType) {
        is EditGamerProcessType.DeleteGamerState -> when (val processState = editGamerProcessType.processState) {
            is CommonProcessState.Failure -> ErrorComponent(
                generalError = processState.error,
                snackBarHostState = snackBarHostState,
                retryProcess = retryDeleteGamerProcess,
                resetProcessState = resetEditGamerProcess
            )
            CommonProcessState.Loading -> CircularProgressDialog()
            is CommonProcessState.Success<Unit> -> TODO()
            CommonProcessState.Idle -> return
        }
        is EditGamerProcessType.UpdateGamerState -> when (val processState = editGamerProcessType.processState) {
            is CommonProcessState.Failure -> ErrorComponent(
                generalError = processState.error,
                snackBarHostState = snackBarHostState,
                retryProcess = retryUpdateGamerProcess,
                resetProcessState = resetEditGamerProcess
            )
            CommonProcessState.Loading -> CircularProgressDialog()
            is CommonProcessState.Success<Unit> -> ImageDialog(
                title = stringResource(R.string.txt_ttl_success_operation),
                image = painterResource(R.drawable.ic_success),
                message = stringResource(R.string.txt_label_gamer_updated)
            ) {
                DefaultButton(
                    text = stringResource(R.string.txt_btn_accept),
                    onClickButton = {
                        resetEditGamerProcess()
                        //navigateToMainMenu()
                    }
                )
            }
            CommonProcessState.Idle -> return
        }
        EditGamerProcessType.Idle -> return
    }
}

@Composable
fun PanelFormEditGamer(
    editGamerFormState: EditGamerFormState,
    snackBarHostState: SnackbarHostState,
    currentGamer: Gamer? = null,
    defaultImageUrl: String? = null,
    labelButtonSaveGamer: String,
    socialNetworkImage: String?,
    countries: List<CountryInfo>?,
    validateFormCreateGamer: (String, String, CountryData, String?) -> Unit,
    saveGamer: () -> Unit,
    deleteGamer: () -> Unit,
    onBack: () -> Unit
) {
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
                    editGamerFormState = editGamerFormState,
                    enableActionButton = editGamerFormState.aliasUiState.state is InputState.Success && editGamerFormState.ageUiState.state is InputState.Success && editGamerFormState.countryUiState.state is InputState.Success && currentGamer?.let {
                        it.gamerNickName != editGamerFormState.aliasUiState.value
                                || it.gamerAge.toString() != editGamerFormState.ageUiState.value
                                || it.gamerCountry != editGamerFormState.countryUiState.value.name
                                || (it.gamerImage == defaultImageUrl
                                && editGamerFormState.imageURI.value != null
                                && editGamerFormState.imageURI.value != defaultImageUrl)
                                || (it.gamerImage != defaultImageUrl && editGamerFormState.imageURI.value != it.gamerImage)
                    } ?: false,
                    saveGamer = saveGamer
                )
                DefaultButton(
                    text = stringResource(R.string.txt_btn_delete_gamer),
                    color = colorResource(R.color.input_error_color),
                    onClickButton = deleteGamer
                )
            }
        }
    }
}

@Composable
fun FormEditGamer(
    snackBarHostState: SnackbarHostState,
    errorImageUrlNotFound: String,
    labelButtonSaveGamer: String,
    socialNetworkImage: String?,
    editGamerFormState: EditGamerFormState,
    countriesUiState: List<CountryInfo>?,
    validateFormCreateGamer: (String, String, CountryData, String?) -> Unit,
    enableActionButton: Boolean = editGamerFormState.aliasUiState.state is InputState.Success && editGamerFormState.ageUiState.state is InputState.Success && editGamerFormState.countryUiState.state is InputState.Success,
    saveGamer: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
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

                null -> return@ChangeProfileImageDialog
            }
        }
    }
    Column(
        modifier = Modifier.padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InputSelectImage(
            imageURI = editGamerFormState.imageURI.value,
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

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun EditGamerPagePreview() {

}