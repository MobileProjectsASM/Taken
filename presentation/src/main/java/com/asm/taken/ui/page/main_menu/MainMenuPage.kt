package com.asm.taken.ui.page.main_menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.SnackbarHostState
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
import com.asm.taken.model.GamerState
import com.asm.taken.model.MainMenuState
import com.asm.taken.ui.CircularProgressDialog
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultText
import com.asm.taken.ui.ImageDialog
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.ui.SnackBarError
import com.asm.taken.utils.AuthenticationClient
import com.asm.taken.vm.MainVM

@Composable
fun MainMenuPage(
    gamerId: String,
    snackBarHostState: SnackbarHostState,
    mainVM: MainVM,
    authenticationClient: AuthenticationClient,
    onNavigateToAuthentication: () -> Unit
) {
    LaunchedEffect(true) { mainVM.getMainDataGamer(gamerId) }
    MainSection(
        gamerId = gamerId,
        mainVM = mainVM,
        authenticationClient = authenticationClient,
        snackBarHostState = snackBarHostState
    )
    NavigateSection(
        snackBarHostState = snackBarHostState,
        mainVM = mainVM,
        onNavigateToAuthentication = onNavigateToAuthentication
    )
}

@Composable
fun MainSection(
    gamerId: String,
    snackBarHostState: SnackbarHostState,
    mainVM: MainVM,
    authenticationClient: AuthenticationClient
) {
    val gamerState: GamerState by mainVM.gamerState.collectAsStateWithLifecycle()
    when (val state = gamerState) {
        is GamerState.Successful -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ContentMainMenu(
                successfulGamer = state,
                onEdit = {},
                onCloseSession = {
                    mainVM.closeSession(authenticationClient::signOut)
                },
                onCreateNewGame = {

                },
                onContinueGame = {

                },
                onEditGamer = {

                },
                onShowRanking = {

                },
                onShowHelp = {

                }
            )
        }

        is GamerState.Fail -> when (state.error) {
            is GeneralError.ClientError -> TODO()
            GeneralError.ConnectionError -> TODO()
            GeneralError.NetworkError -> DialogError(
                title = stringResource(R.string.txt_ttl_client_error),
                image = painterResource(R.drawable.ic_warning),
                message = stringResource(R.string.err_network_connection),
                retryProcess = {
                    mainVM.getMainDataGamer(gamerId)
                },
                logOut = {
                    mainVM.closeSession(authenticationClient::signOut)
                },
                onDismissDialog = {

                }
            )

            is GeneralError.ServerError -> DialogError(
                title = stringResource(R.string.txt_ttl_service_error),
                image = painterResource(R.drawable.ic_error),
                message = stringResource(R.string.err_server),
                retryProcess = {

                },
                logOut = {

                },
                onDismissDialog = {

                }
            )

            GeneralError.Unknown -> SnackBarError(
                snackBarHostState = snackBarHostState,
                message = stringResource(R.string.err_auth),
                withDismissAction = true,
                onDismiss = {}
            )
        }

        GamerState.Loading -> CircularProgressDialog()
    }
}

@Composable
fun ContentMainMenu(
    successfulGamer: GamerState.Successful,
    onEdit: (String) -> Unit,
    onCloseSession: () -> Unit,
    onCreateNewGame: (Boolean) -> Unit,
    onContinueGame: () -> Unit,
    onEditGamer: () -> Unit,
    onShowRanking: () -> Unit,
    onShowHelp: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        PanelGamerProfile(
            gamer = successfulGamer.gamer,
            onEdit = onEdit,
            onCloseSession = onCloseSession
        )
        PanelMenu(
            itHasProgress = successfulGamer.itHasProgress,
            onCreateNewGame = onCreateNewGame,
            onContinueGame = onContinueGame,
            onEditGamer = onEditGamer,
            onShowRanking = onShowRanking,
            onShowHelp = onShowHelp
        )
    }
}

@Composable
fun NavigateSection(
    snackBarHostState: SnackbarHostState,
    mainVM: MainVM,
    onNavigateToAuthentication: () -> Unit
) {
    val mainMenuState: MainMenuState? by mainVM.mainMenuState.collectAsStateWithLifecycle()
    when (val state = mainMenuState) {
        is MainMenuState.Fail -> when (state.error) {
            is GeneralError.ClientError -> TODO()
            GeneralError.ConnectionError -> TODO()
            GeneralError.NetworkError -> TODO()

            is GeneralError.ServerError -> TODO()

            GeneralError.Unknown -> TODO()
        }

        MainMenuState.Loading -> CircularProgressDialog()
        MainMenuState.SessionClosed -> LaunchedEffect(true) {
            onNavigateToAuthentication()
        }

        null -> return
    }
}

@Composable
fun PanelGamerProfile(
    gamer: Gamer,
    onEdit: (String) -> Unit,
    onCloseSession: () -> Unit,
) {
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
                    .padding(10.dp)
            ) {
                Spacer(
                    modifier = Modifier.weight(weight = 1f)
                )
                Image(
                    modifier = Modifier
                        .size(32.dp)
                        .fillMaxWidth()
                        .clickable(onClick = onCloseSession),
                    painter = painterResource(R.drawable.ic_logout),
                    contentDescription = null
                )
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
                model = gamer.gamerImage,
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
    onCreateNewGame: (Boolean) -> Unit,
    onContinueGame: () -> Unit,
    onEditGamer: () -> Unit,
    onShowRanking: () -> Unit,
    onShowHelp: () -> Unit
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
                onClickButton = { onCreateNewGame(itHasProgress) }
            )
            if (itHasProgress) {
                DefaultButton(
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp),
                    text = stringResource(id = R.string.txt_btn_continue_game),
                    onClickButton = onContinueGame
                )
            }
            DefaultButton(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp),
                text = stringResource(id = R.string.txt_btn_edit_gamer),
                onClickButton = onEditGamer
            )
            DefaultButton(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 10.dp),
                text = stringResource(id = R.string.txt_btn_show_ranking),
                onClickButton = onShowRanking
            )
            DefaultButton(
                modifier = Modifier.padding(10.dp),
                text = stringResource(id = R.string.txt_btn_help),
                onClickButton = onShowHelp
            )
        }
    }
}

@Composable
fun DialogError(
    title: String,
    image: Painter,
    message: String,
    retryProcess: () -> Unit,
    logOut: () -> Unit,
    onDismissDialog: () -> Unit
) {
    var showErrorDialog by rememberSaveable { mutableStateOf(true) }
    if (showErrorDialog) {
        ImageDialog(
            title = title,
            image = image,
            message = message,
            onDismissRequest = {

            }
        ) {
            DefaultButton(
                text = stringResource(id = R.string.txt_label_retry),
                onClickButton = {
                    retryProcess()
                    showErrorDialog = false
                    onDismissDialog()
                }
            )
            DefaultButton(
                text = stringResource(id = R.string.txt_btn_logout),
                onClickButton = {
                    logOut()
                    showErrorDialog = false
                    onDismissDialog()
                }
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewMainMenu() {
    val gamer = Gamer(
        gamerId = "abcd-efgh",
        gamerNickName = "Arturo",
        gamerAge = 28,
        gamerCountry = "Mexico",
        gamerCountryFlag = "\uD83C\uDDF2\uD83C\uDDFD",
        gamerImage = "https://firebasestorage.googleapis.com/v0/b/puzzle-16426.firebasestorage.app/o/images%2Fprofile%2Fpi_7Si8Y2UkZBNVjQLVpwLuiwuqXv93.webp?alt=media&token=4b00af8a-5e39-4064-bb18-80cd6565300d"
    )
    val successfulGamer = GamerState.Successful(
        gamer = gamer,
        itHasProgress = true
    )
    ContentMainMenu(
        successfulGamer = successfulGamer,
        onEdit = {},
        onCloseSession = {},
        onCreateNewGame = {

        },
        onContinueGame = {

        },
        onEditGamer = {

        },
        onShowRanking = {

        },
        onShowHelp = {

        }
    )
}