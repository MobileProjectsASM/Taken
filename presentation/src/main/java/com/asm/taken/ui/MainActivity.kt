package com.asm.taken.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.asm.taken.ui.navigation.MainNavigation
import com.asm.taken.ui.theme.TakenTheme
import com.asm.taken.utils.AuthenticationUiClient
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authenticationUiClient: AuthenticationUiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TakenTheme {
                Surface {
                    PuzzleScaffold(
                        authenticationUiClient = authenticationUiClient
                    )
                }
            }
        }
    }
}

@Composable
fun PuzzleScaffold(authenticationUiClient: AuthenticationUiClient) {
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        MainNavigation(
            innerPadding,
            snackBarHostState = snackBarHostState,
            authenticationUiClient = authenticationUiClient
        )
    }
}