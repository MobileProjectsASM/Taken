package com.asm.taken.ui.page.main_menu

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asm.domain.errors.GeneralError
import com.asm.taken.R
import com.asm.taken.model.SessionState
import com.asm.taken.ui.CircularProgressDialog
import com.asm.taken.ui.DialogError
import com.asm.taken.ui.SnackbarError
import com.asm.taken.vm.MainVM

@Composable
fun MainMenuPage(
    gamerId: String,
    snackbarHostState: SnackbarHostState,
    mainVM: MainVM
) {
    LaunchedEffect(true) { mainVM.getDataGamer(gamerId) }
    val sessionState: SessionState by mainVM.sessionState.collectAsStateWithLifecycle()
    when (val state = sessionState) {
        is SessionState.Authenticated -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "Welcome ${state.gamer.gamerNickName}")
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
            GeneralError.NetworkError -> SnackbarError(
                snackBarHostState = snackbarHostState,
                message = stringResource(R.string.err_network_connection),
                actionLabel = stringResource(R.string.txt_label_retry),
                onDismiss = {},
                onActionPerformed = { mainVM.getDataGamer(gamerId) }
            )

            is GeneralError.ServerError -> DialogError(
                title = stringResource(R.string.txt_ttl_service_error),
                image = painterResource(R.drawable.ic_error),
                message = stringResource(R.string.err_server),
                onDismissDialog = { },
                onClickAction = { }
            )

            GeneralError.Unknown -> TODO()
        }

        SessionState.Loading -> CircularProgressDialog()
        SessionState.NoAuthenticated -> TODO()
    }
}