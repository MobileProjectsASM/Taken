package com.asm.taken.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.asm.taken.ui.navigation.MainNavigation
import com.asm.taken.ui.theme.TakenTheme
import com.asm.taken.utils.AuthenticationClient
import com.asm.taken.utils.MessageResolver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    @Inject
    lateinit var authenticationClient: AuthenticationClient

    @Inject
    lateinit var messageResolver: MessageResolver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TakenTheme {
                Surface {
                    PuzzleScaffold(
                        authenticationClient = authenticationClient,
                        messageResolver = messageResolver
                    )
                }
            }
        }
    }
}

@Composable
fun PuzzleScaffold(
    authenticationClient: AuthenticationClient,
    messageResolver: MessageResolver
) {
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        MainNavigation(
            innerPadding,
            snackBarHostState = snackBarHostState,
            authenticationClient = authenticationClient,
            messageResolver = messageResolver
        )
    }
}