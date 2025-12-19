package com.asm.taken.ui.page.main_menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.asm.domain.entities.Gamer
import com.asm.domain.errors.GeneralError
import com.asm.taken.R
import com.asm.taken.model.SessionState
import com.asm.taken.ui.CircularProgressDialog
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultText
import com.asm.taken.ui.DialogError
import com.asm.taken.ui.SnackBarError
import com.asm.taken.vm.MainVM
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

@Composable
fun MainMenuPage(
    gamerId: String,
    snackBarHostState: SnackbarHostState,
    mainVM: MainVM
) {
    LaunchedEffect(true) { mainVM.getMainDataGamer(gamerId) }
    val sessionState: SessionState by mainVM.sessionState.collectAsStateWithLifecycle()
    when (val state = sessionState) {
        is SessionState.Authenticated -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            ContentMainMenu(
                authenticatedGamer = state,
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

        is SessionState.Fail -> when (state.error) {
            is GeneralError.ClientError -> DialogError(
                title = stringResource(R.string.txt_ttl_client_error),
                image = painterResource(R.drawable.ic_warning),
                message = stringResource(R.string.err_client),
                onDismissDialog = {},
                onClickAction = { }
            )
            GeneralError.ConnectionError -> TODO()
            GeneralError.NetworkError -> SnackBarError(
                snackBarHostState = snackBarHostState,
                message = stringResource(R.string.err_network_connection),
                actionLabel = stringResource(R.string.txt_label_retry),
                onDismiss = {},
                onActionPerformed = { mainVM.getMainDataGamer(gamerId) }
            )

            is GeneralError.ServerError -> DialogError(
                title = stringResource(R.string.txt_ttl_service_error),
                image = painterResource(R.drawable.ic_error),
                message = stringResource(R.string.err_server),
                onDismissDialog = { },
                onClickAction = { }
            )

            GeneralError.Unknown -> SnackBarError(
                snackBarHostState = snackBarHostState,
                message = stringResource(R.string.err_auth),
                withDismissAction = true,
                onDismiss = {}//loginVM::resetLoginUiState
            )
        }

        SessionState.Loading -> CircularProgressDialog()
        SessionState.NoAuthenticated -> TODO()
    }
}

@Composable
fun ContentMainMenu(
    authenticatedGamer: SessionState.Authenticated,
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
            gamer = authenticatedGamer.gamer,
            onEdit = onEdit,
            onCloseSession = onCloseSession
        )
        PanelMenu(
            itHasProgress = authenticatedGamer.itHasProgress,
            onCreateNewGame = onCreateNewGame,
            onContinueGame = onContinueGame,
            onEditGamer = onEditGamer,
            onShowRanking = onShowRanking,
            onShowHelp = onShowHelp
        )
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
                Image(
                    modifier = Modifier
                        .size(32.dp)
                        .fillMaxWidth()
                        .clickable { onEdit(gamer.gamerId) },
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = null
                )
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
    val authenticatedGamer = SessionState.Authenticated(
        gamer = gamer,
        itHasProgress = true
    )
    ContentMainMenu(
        authenticatedGamer = authenticatedGamer,
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