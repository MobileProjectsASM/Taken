package com.asm.taken.ui.page.main_menu

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asm.domain.entities.Gamer
import com.asm.domain.errors.GeneralError
import com.asm.taken.R
import com.asm.taken.model.Country
import com.asm.taken.model.CountryData
import com.asm.taken.model.EditGamerFormUiState
import com.asm.taken.model.EditGamerOperationsState
import com.asm.taken.model.EditGamerState
import com.asm.taken.model.InputState
import com.asm.taken.ui.CircularProgressDialog
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.ImageDialog
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.ui.SnackBarError
import com.asm.taken.ui.page.login.FormEditGamer
import com.asm.taken.utils.AuthenticationClient
import com.asm.taken.vm.EditGamerVM

@Composable
fun EditGamerPage(
    gamerId: String,
    snackBarHostState: SnackbarHostState,
    editGamerVM: EditGamerVM,
    authenticationClient: AuthenticationClient,
    navigateToMainMenu: () -> Unit
) {
    EditGamerSection(
        gamerId = gamerId,
        editGamerVM = editGamerVM,
        navigateToMainMenu = navigateToMainMenu,
        authenticationClient = authenticationClient,
        snackBarHostState = snackBarHostState
    )
    ResultOperationsSection(
        editGamerVM = editGamerVM,
        snackBarHostState = snackBarHostState,
        navigateToMainMenu = navigateToMainMenu
    )
}

@Composable
fun EditGamerSection(
    gamerId: String,
    editGamerVM: EditGamerVM,
    authenticationClient: AuthenticationClient,
    snackBarHostState: SnackbarHostState,
    navigateToMainMenu: () -> Unit
) {
    LaunchedEffect(true) {
        editGamerVM.getGamerData(
            gamerId = gamerId,
            getCurrentUserSocialNetworkImage = authenticationClient::getCurrentUserSocialNetworkImage
        )
    }

    val gamerUIState: EditGamerState by editGamerVM.gamerState.collectAsStateWithLifecycle()
    val editGamerFormState: EditGamerFormUiState by editGamerVM.editGamerFormState.collectAsStateWithLifecycle()

    when (val gamerState = gamerUIState) {
        is EditGamerState.Failure -> when (gamerState.error) {
            is GeneralError.ClientError -> com.asm.taken.ui.DialogError(
                title = stringResource(R.string.txt_ttl_client_error),
                image = painterResource(R.drawable.ic_warning),
                message = stringResource(R.string.err_client),
                onDismissedDialog = {  }
            )

            GeneralError.ConnectionError -> com.asm.taken.ui.DialogError(
                title = stringResource(R.string.txt_ttl_unexpected_error),
                image = painterResource(R.drawable.ic_warning),
                message = stringResource(R.string.err_server_connection),
                onDismissedDialog = {  }
            )

            GeneralError.NetworkError -> SnackBarError(
                snackBarHostState = snackBarHostState,
                actionLabel = stringResource(R.string.txt_label_retry),
                duration = SnackbarDuration.Long,
                message = stringResource(R.string.err_network_connection),
                onDismissed = {  }
            )

            is GeneralError.ServerError -> com.asm.taken.ui.DialogError(
                title = stringResource(R.string.txt_ttl_service_error),
                image = painterResource(R.drawable.ic_error),
                message = stringResource(R.string.err_server),
                onDismissedDialog = {  }
            )

            GeneralError.Unknown -> SnackBarError(
                snackBarHostState = snackBarHostState,
                message = stringResource(R.string.err_process_gamer),
                withDismissAction = true,
                onDismissed = {  }
            )
        }

        EditGamerState.Loading -> CircularProgressDialog()
        is EditGamerState.Success -> {
            LaunchedEffect(true) {
                editGamerVM.validateEditGamerForm(
                    alias = gamerState.gamer.gamerNickName,
                    age = gamerState.gamer.gamerAge.toString(),
                    countryData = CountryData(
                        gamerState.gamer.gamerCountry,
                        gamerState.gamer.gamerCountryFlag
                    ),
                    imageURI = gamerState.gamer.gamerImage
                )
            }

            PanelFormEditGamer(
                snackBarHostState = snackBarHostState,
                currentGamer = gamerState.gamer,
                defaultImageUrl = gamerState.defaultImageUrl,
                editGamerFormState = editGamerFormState,
                labelButtonSaveGamer = stringResource(R.string.txt_btn_save_changes),
                socialNetworkImage = gamerState.socialNetworkImage,
                countries = gamerState.countries,
                validateFormCreateGamer = editGamerVM::validateEditGamerForm,
                saveGamer = {
                    editGamerVM.saveGamer(
                        id = gamerState.gamer.gamerId,
                        alias = editGamerFormState.aliasUiState.value,
                        age = editGamerFormState.ageUiState.value.toInt(),
                        country = editGamerFormState.countryUiState.value.name,
                        countryFlag = editGamerFormState.countryUiState.value.flag,
                        imageURI = editGamerFormState.imageURI.value
                    )
                },
                onBack = navigateToMainMenu
            )
        }
    }
}

@Composable
fun ResultOperationsSection(
    editGamerVM: EditGamerVM,
    snackBarHostState: SnackbarHostState,
    navigateToMainMenu: () -> Unit
) {
    val editGamerOperationState: EditGamerOperationsState? by editGamerVM.editGamerOperationsState.collectAsStateWithLifecycle()

    when (val state = editGamerOperationState) {
        is EditGamerOperationsState.Failure -> when (state.error) {
            is GeneralError.ClientError -> com.asm.taken.ui.DialogError(
                title = stringResource(R.string.txt_ttl_client_error),
                image = painterResource(R.drawable.ic_warning),
                message = stringResource(R.string.err_client),
                onDismissedDialog = { }
            )

            GeneralError.ConnectionError -> com.asm.taken.ui.DialogError(
                title = stringResource(R.string.txt_ttl_unexpected_error),
                image = painterResource(R.drawable.ic_warning),
                message = stringResource(R.string.err_server_connection),
                onDismissedDialog = { }
            )

            GeneralError.NetworkError -> SnackBarError(
                snackBarHostState = snackBarHostState,
                actionLabel = stringResource(R.string.txt_label_retry),
                duration = SnackbarDuration.Long,
                message = stringResource(R.string.err_network_connection),
                onDismissed = { }
            )

            is GeneralError.ServerError -> com.asm.taken.ui.DialogError(
                title = stringResource(R.string.txt_ttl_service_error),
                image = painterResource(R.drawable.ic_error),
                message = stringResource(R.string.err_server),
                onDismissedDialog = { }
            )

            GeneralError.Unknown -> SnackBarError(
                snackBarHostState = snackBarHostState,
                message = stringResource(R.string.err_process_gamer),
                withDismissAction = true,
                onDismissed = { }
            )
        }

        EditGamerOperationsState.GamerDeleted -> TODO()
        EditGamerOperationsState.GamerUpdated -> ImageDialog(
            title = stringResource(R.string.txt_ttl_success_operation),
            image = painterResource(R.drawable.ic_success),
            message = stringResource(R.string.txt_label_gamer_updated)
        ) {
            DefaultButton(
                text = stringResource(R.string.txt_btn_accept),
                onClickButton = {
                    editGamerVM.resetEditGamerOperationsState()
                    navigateToMainMenu()
                }
            )
        }

        EditGamerOperationsState.Loading -> CircularProgressDialog()
        null -> return
    }
}

@Composable
fun PanelFormEditGamer(
    snackBarHostState: SnackbarHostState,
    currentGamer: Gamer,
    defaultImageUrl: String? = null,
    labelButtonSaveGamer: String,
    socialNetworkImage: String?,
    editGamerFormState: EditGamerFormUiState,
    countries: List<Country>?,
    validateFormCreateGamer: (String, String, CountryData, String?) -> Unit,
    saveGamer: () -> Unit,
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
                    .fillMaxWidth()
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
                    editGamerFormState = editGamerFormState,
                    countriesUiState = countries,
                    validateFormCreateGamer = validateFormCreateGamer,
                    enableActionButton = editGamerFormState.aliasUiState.state is InputState.Success && editGamerFormState.ageUiState.state is InputState.Success && editGamerFormState.countryUiState.state is InputState.Success && currentGamer.let {
                        it.gamerNickName != editGamerFormState.aliasUiState.value
                                || it.gamerAge.toString() != editGamerFormState.ageUiState.value
                                || it.gamerCountry != editGamerFormState.countryUiState.value.name
                                || (it.gamerImage == defaultImageUrl
                                && editGamerFormState.imageURI.value != null
                                && editGamerFormState.imageURI.value != defaultImageUrl)
                                || (it.gamerImage != defaultImageUrl && editGamerFormState.imageURI.value != it.gamerImage)
                    },
                    saveGamer = saveGamer
                )
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun EditGamerPagePreview() {

}