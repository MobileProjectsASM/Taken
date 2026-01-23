package com.asm.taken.ui.page.main_menu

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.rounded.Logout
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.asm.domain.entities.Gamer
import com.asm.domain.errors.GeneralError
import com.asm.taken.R
import com.asm.taken.model.CommonProcessState
import com.asm.taken.model.MainMenuState
import com.asm.taken.model.MainMenuUIState
import com.asm.taken.model.MenuProcessType
import com.asm.taken.ui.CircularProgressDialog
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultIconButton
import com.asm.taken.ui.DefaultText
import com.asm.taken.ui.ImageDialog
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.ui.SnackBarError
import com.asm.taken.vm.MainVM
import kotlinx.coroutines.launch

@Composable
fun MainMenuPage(
    gamerId: String,
    snackBarHostState: SnackbarHostState,
    mainVM: MainVM,
    navigateToAuthPage: () -> Unit,
    navigateToEditGamer: () -> Unit,
    navigateToLevelsPage: (gamerId: String) -> Unit,
    navigateToShowRanking: () -> Unit,
    navigateToShowHelp: () -> Unit
) {
    val mainMenuUIState: MainMenuUIState by mainVM.mainMenuState.collectAsStateWithLifecycle()

    LaunchedEffect(true) { mainVM.getMainMenuData(gamerId) }

    MainSection(
        mainMenuUIState = mainMenuUIState,
        snackBarHostState = snackBarHostState,
        retryGetMainMenuData = { mainVM.getMainMenuData(gamerId) },
        navigateToAuthPage = navigateToAuthPage,
        navigateToEditGamerPage = navigateToEditGamer,
        navigateToLevelsPage = { navigateToLevelsPage(gamerId) },
        navigateToShowHelpPage = navigateToShowHelp,
        navigateToShowRankingPage = navigateToShowRanking,
        createNewGame = mainVM::createNewGame,
        closeSession = mainVM::closeSession,
        resetProcess = mainVM::resetProcess
    )
}

@Composable
fun MainSection(
    mainMenuUIState: MainMenuUIState,
    snackBarHostState: SnackbarHostState,
    navigateToAuthPage: () -> Unit,
    navigateToEditGamerPage: () -> Unit,
    navigateToLevelsPage: () -> Unit,
    navigateToShowRankingPage: () -> Unit,
    navigateToShowHelpPage: () -> Unit,
    retryGetMainMenuData: () -> Unit,
    createNewGame: () -> Unit,
    closeSession: () -> Unit,
    resetProcess: () -> Unit
) {
    when (mainMenuUIState) {
        is MainMenuUIState.DataMenuLoaded -> MenuSection(
            mainMenuState = mainMenuUIState.mainMenuState,
            snackBarHostState = snackBarHostState,
            closeSession = closeSession,
            createNewGame = createNewGame,
            navigateToAuthPage = navigateToAuthPage,
            navigateToEditGamerPage = navigateToEditGamerPage,
            navigateToLevelsPage = navigateToLevelsPage,
            navigateToShowRankingPage = navigateToShowRankingPage,
            navigateToShowHelpPage = navigateToShowHelpPage,
            resetProcess = resetProcess
        )
        is MainMenuUIState.Failure -> ErrorComponentMainMenu(
            mainMenuUIState.error,
            retryProcess = retryGetMainMenuData,
            closeSession = closeSession
        )
        MainMenuUIState.Loading -> CircularProgressDialog()
        MainMenuUIState.SessionClosed -> LaunchedEffect(true) {
            navigateToAuthPage()
        }
    }
}

@Composable
fun MenuSection(
    mainMenuState: MainMenuState,
    snackBarHostState: SnackbarHostState,
    closeSession: () -> Unit,
    createNewGame: () -> Unit,
    navigateToAuthPage: () -> Unit,
    navigateToEditGamerPage: () -> Unit,
    navigateToLevelsPage: () -> Unit,
    navigateToShowRankingPage: () -> Unit,
    navigateToShowHelpPage: () -> Unit,
    resetProcess: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        var showConfirmDialog: Boolean by rememberSaveable { mutableStateOf(false) }
        if (showConfirmDialog) {
            ImageDialog(
                title = stringResource(R.string.txt_ttl_warning),
                image = painterResource(R.drawable.ic_warning),
                message = stringResource(R.string.txt_label_confirm_delete_message),
                onClose = { showConfirmDialog = false },
                onDismissRequest = { showConfirmDialog = false }
            ) {
                DefaultButton(
                    text = stringResource(id = R.string.txt_btn_confirm_delete_button),
                    onClickButton = {
                        showConfirmDialog = false
                        createNewGame()
                    }
                )
            }
        }
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            PanelGamerProfile(
                snackBarHostState = snackBarHostState,
                errorMessageImageUrlNotFound = stringResource(R.string.txt_label_image_url_not_found),
                gamer = mainMenuState.gamer,
                closeSession = closeSession
            )
            PanelMenu(
                itHasProgress = mainMenuState.itHasProgress,
                createNewGame = {
                    if (mainMenuState.itHasProgress) showConfirmDialog = true
                    else navigateToLevelsPage()
                },
                onClickContinue = navigateToLevelsPage,
                onClickEditGamer = navigateToEditGamerPage,
                onClickShowRanking = navigateToShowRankingPage,
                onClickShowHelp = navigateToShowHelpPage
            )
        }
        ProcessSection(
            menuProcessType = mainMenuState.menuProcessType,
            snackBarHostState = snackBarHostState,
            navigateToAuthPage = navigateToAuthPage,
            navigateToLevelsPage = navigateToLevelsPage,
            retryCreateNewGame = createNewGame,
            retryCloseSession = closeSession,
            resetProcess = resetProcess
        )
    }
}

@Composable
fun ProcessSection(
    menuProcessType: MenuProcessType,
    snackBarHostState: SnackbarHostState,
    navigateToAuthPage: () -> Unit,
    navigateToLevelsPage: () -> Unit,
    retryCreateNewGame: () -> Unit,
    retryCloseSession: () -> Unit,
    resetProcess: () -> Unit
) {
    when (menuProcessType) {
        is MenuProcessType.CreateNewGameProcess -> when (val processState =
            menuProcessType.process) {
            is CommonProcessState.Failure -> ErrorComponentProcess(
                snackBarHostState = snackBarHostState,
                generalError = processState.error,
                retryProcess = retryCreateNewGame,
                onCloseDialog = resetProcess,
                onDismissedSnackBar = resetProcess
            )

            CommonProcessState.Loading -> CircularProgressDialog()
            is CommonProcessState.Success<Unit> -> LaunchedEffect(true) { navigateToLevelsPage() }
            CommonProcessState.Idle -> return
        }

        is MenuProcessType.SessionCloseProcess -> when (val processState =
            menuProcessType.process) {
            is CommonProcessState.Failure -> ErrorComponentProcess(
                snackBarHostState = snackBarHostState,
                generalError = processState.error,
                retryProcess = retryCloseSession,
                onCloseDialog = resetProcess,
                onDismissedSnackBar = resetProcess
            )
            CommonProcessState.Loading -> CircularProgressDialog()
            is CommonProcessState.Success<Unit> -> LaunchedEffect(true) { navigateToAuthPage() }
            CommonProcessState.Idle -> return
        }

        MenuProcessType.Idle -> return
    }
}

@Composable
fun PanelGamerProfile(
    snackBarHostState: SnackbarHostState,
    errorMessageImageUrlNotFound: String,
    gamer: Gamer,
    closeSession: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, top = 20.dp, end = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
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
                modifier = Modifier.padding(bottom = 10.dp, start = 10.dp, end = 10.dp),
                text = stringResource(R.string.txt_ttl_welcome)
            )
            AsyncImage(
                modifier = Modifier
                    .size(size = 128.dp)
                    .clip(CircleShape)
                    .border(width = 2.dp, color = Color.Black, shape = CircleShape),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.gamer),
                model = gamer.gamerImage,
                onError = {
                    coroutineScope.launch {
                        snackBarHostState.showSnackbar(
                            errorMessageImageUrlNotFound,
                            withDismissAction = true,
                            duration = SnackbarDuration.Long
                        )
                    }
                },
                contentDescription = null
            )
            Spacer(Modifier.height(10.dp))
            DefaultText(
                modifier = Modifier.padding(bottom = 10.dp, start = 10.dp, end = 10.dp),
                text = gamer.gamerAge.toString()
            )
            Row(
                modifier = Modifier.padding(bottom = 10.dp, start = 10.dp, end = 10.dp)
            ) {
                DefaultText(
                    text = gamer.gamerNickName
                )
                gamer.gamerCountryFlag?.also {
                    DefaultText(
                        modifier = Modifier.padding(start = 10.dp),
                        text = it
                    )
                }
            }
        }
    }
}

@Composable
fun PanelMenu(
    itHasProgress: Boolean,
    createNewGame: () -> Unit,
    onClickContinue: () -> Unit,
    onClickEditGamer: () -> Unit,
    onClickShowRanking: () -> Unit,
    onClickShowHelp: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DefaultButton(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp),
                text = stringResource(id = R.string.txt_btn_new_game),
                onClickButton = createNewGame
            )
            if (itHasProgress) {
                DefaultButton(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp),
                    text = stringResource(id = R.string.txt_btn_continue_game),
                    onClickButton = onClickContinue
                )
            }
            DefaultButton(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp),
                text = stringResource(id = R.string.txt_btn_edit_gamer),
                onClickButton = onClickEditGamer
            )
            DefaultButton(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp),
                text = stringResource(id = R.string.txt_btn_show_ranking),
                onClickButton = onClickShowRanking
            )
            DefaultButton(
                modifier = Modifier.padding(10.dp),
                text = stringResource(id = R.string.txt_btn_help),
                onClickButton = onClickShowHelp
            )
        }
    }
}

@Composable
fun ErrorComponentMainMenu(
    generalError: GeneralError,
    retryProcess: () -> Unit,
    closeSession: () -> Unit
) {
    when (generalError) {
        is GeneralError.ClientError -> ImageDialog(
            title = stringResource(R.string.txt_ttl_client_error),
            image = painterResource(R.drawable.ic_warning),
            message = stringResource(R.string.err_client),
            retryProcess = retryProcess,
            closeSession = closeSession
        )

        GeneralError.ConnectionError -> ImageDialog(
            title = stringResource(R.string.txt_ttl_service_error),
            image = painterResource(R.drawable.ic_sin_internet),
            message = stringResource(R.string.err_server),
            retryProcess = retryProcess,
            closeSession = closeSession
        )

        is GeneralError.ServerError -> ImageDialog(
            title = stringResource(R.string.txt_ttl_service_error),
            image = painterResource(R.drawable.ic_error),
            message = stringResource(R.string.err_server),
            retryProcess = retryProcess,
            closeSession = closeSession
        )

        GeneralError.Unknown -> ImageDialog(
            title = stringResource(R.string.txt_ttl_unexpected_error),
            image = painterResource(R.drawable.ic_cancelar),
            message = stringResource(R.string.err_process_data),
            closeSession = closeSession,
        )
    }
}

@Composable
fun ErrorComponentProcess(
    snackBarHostState: SnackbarHostState,
    generalError: GeneralError,
    retryProcess: () -> Unit,
    onCloseDialog: () -> Unit,
    onDismissedSnackBar: () -> Unit
) {
    when (generalError) {
        is GeneralError.ClientError -> ImageDialog(
            title = stringResource(R.string.txt_ttl_client_error),
            image = painterResource(R.drawable.ic_warning),
            message = stringResource(R.string.err_client),
            retryProcess = retryProcess,
            onClose = onCloseDialog
        )

        GeneralError.ConnectionError -> ImageDialog(
            title = stringResource(R.string.txt_ttl_service_error),
            image = painterResource(R.drawable.ic_sin_internet),
            message = stringResource(R.string.err_connection),
            retryProcess = retryProcess,
            onClose = onCloseDialog
        )

        is GeneralError.ServerError -> ImageDialog(
            title = stringResource(R.string.txt_ttl_service_error),
            image = painterResource(R.drawable.ic_error),
            message = stringResource(R.string.err_server),
            retryProcess = retryProcess,
            onClose = onCloseDialog
        )

        GeneralError.Unknown -> SnackBarError(
            snackBarHostState = snackBarHostState,
            message = stringResource(R.string.err_auth),
            withDismissAction = true,
            onDismissed = onDismissedSnackBar
        )
    }
}

@Composable
fun ImageDialog(
    title: String,
    image: Painter,
    message: String,
    retryProcess: (() -> Unit)? = null,
    closeSession: () -> Unit
) {
    ImageDialog(
        title = title,
        image = image,
        message = message
    ) {
        retryProcess?.also {
            DefaultIconButton(
                text = stringResource(id = R.string.txt_label_retry),
                imageVector = Icons.Filled.Replay,
                onClickButton = retryProcess
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        DefaultIconButton(
            text = stringResource(id = R.string.txt_btn_logout),
            imageVector = Icons.AutoMirrored.Filled.Logout,
            onClickButton = closeSession
        )
    }
}

@Composable
fun ImageDialog(
    title: String,
    image: Painter,
    message: String,
    retryProcess: () -> Unit,
    onClose: () -> Unit
) {
    ImageDialog(
        title = title,
        image = image,
        message = message,
        onClose = onClose
    ) {
        DefaultIconButton(
            text = stringResource(id = R.string.txt_label_retry),
            imageVector = Icons.Filled.Replay,
            onClickButton = retryProcess
        )
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewMainMenu() {
    val snackBarHostState = remember { SnackbarHostState() }

    //val mainMenuUIState: CommonProcessState<MainMenuState> = CommonProcessState.Loading
    val gamer = Gamer(
        gamerId = "abcd-efgh",
        gamerNickName = "Arturo",
        gamerAge = 28,
        gamerCountry = "Mexico",
        gamerCountryFlag = "\uD83C\uDDF2\uD83C\uDDFD",
        gamerImage = "https://images.unsplash.com/photo-1575936123452-b67c3203c357?fm=jpg&q=60&w=3000&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8aW1hZ2V8ZW58MHx8MHx8fDA%3D"
    )
    val mainMenuUIState: MainMenuUIState = MainMenuUIState.DataMenuLoaded(
        mainMenuState = MainMenuState(
            gamer = gamer,
            itHasProgress = false,
            /*menuProcessType = MenuProcessType.CreateNewGameProcess(CommonProcessState.Failure(
                error = GeneralError.ConnectionError
            ))*/
        )
    )
    /*val mainMenuUIState: CommonProcessState<MainMenuState> = CommonProcessState.Failure(
        error = GeneralError.ClientError()
    )*/

    MainSection(
        mainMenuUIState = mainMenuUIState,
        snackBarHostState = snackBarHostState,
        navigateToAuthPage = { },
        navigateToEditGamerPage = { },
        navigateToLevelsPage = { },
        retryGetMainMenuData = { },
        navigateToShowRankingPage = { },
        navigateToShowHelpPage = { },
        createNewGame = { },
        closeSession = { },
        resetProcess = { }
    )
}