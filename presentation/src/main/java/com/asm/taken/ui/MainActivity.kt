package com.asm.taken.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.asm.taken.R
import com.asm.taken.model.SessionUiState
import com.asm.taken.ui.navigation.Authentication
import com.asm.taken.ui.navigation.CreateGamer
import com.asm.taken.ui.navigation.Login
import com.asm.taken.ui.navigation.MainNavigation
import com.asm.taken.ui.navigation.MainPage
import com.asm.taken.ui.navigation.Route
import com.asm.taken.ui.theme.TakenTheme
import com.asm.taken.utils.AuthenticationClient
import com.asm.taken.utils.MessageResolver
import com.asm.taken.vm.SessionVM
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authenticationClient: AuthenticationClient

    @Inject
    lateinit var messageResolver: MessageResolver

    private val sessionVM: SessionVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        var keepSplashScreen = true
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }
        launchOverStarted(sessionVM.sessionState::collect) { sessionState ->
            when (sessionState) {
                is SessionUiState.Fail -> {
                    keepSplashScreen = true
                    Snackbar.make(window.decorView, getText(R.string.err_get_session_data), Snackbar.LENGTH_SHORT).show()
                }
                SessionUiState.Loading -> keepSplashScreen = true
                else -> {
                    keepSplashScreen = false
                    val initRoute: Pair<Route, Any?> = if (sessionState is SessionUiState.UserRegister) Pair(MainPage, sessionState.gamerId)
                    else if (sessionState is SessionUiState.UnregisterUser) Pair(CreateGamer, sessionState.userData)
                    else Pair(Authentication, null)
                    setContent {
                        TakenTheme {
                            Surface {
                                PuzzleScaffold(
                                    initRoute = initRoute,
                                    authenticationClient = authenticationClient,
                                    messageResolver = messageResolver
                                )
                            }
                        }
                    }
                }
            }
        }
        sessionVM.isThereSessionActive()
        enableEdgeToEdge()
    }
}

@Composable
fun PuzzleScaffold(
    initRoute: Pair<Route, Any?>,
    authenticationClient: AuthenticationClient,
    messageResolver: MessageResolver
) {
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { innerPadding ->
        MainNavigation(
            initRoute = initRoute,
            innerPadding = innerPadding,
            snackBarHostState = snackBarHostState,
            authenticationClient = authenticationClient,
            messageResolver = messageResolver
        )
    }
}

fun <T> MainActivity.launchOverStarted(collect: suspend (FlowCollector<T>) -> Nothing, collector: FlowCollector<T>) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            collect(collector)
        }
    }
}