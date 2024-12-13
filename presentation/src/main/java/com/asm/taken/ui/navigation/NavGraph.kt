package com.asm.taken.ui.navigation

import android.app.Activity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.asm.taken.ui.page.login.BackgroundLogin
import com.asm.taken.ui.page.login.MainAuthPage
import com.asm.taken.ui.page.login.PhoneAuthPage
import com.asm.taken.utils.AuthenticationUiClient
import com.asm.taken.vm.LoginVM

@Composable
fun MainNavigation(
    innerPadding: PaddingValues,
    snackBarHostState: SnackbarHostState,
    authenticationUiClient: AuthenticationUiClient
) {
    val navigationController = rememberNavController()
    NavHost(
        navController = navigationController,
        startDestination = Login.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        navigationLogin(
            navController = navigationController,
            snackBarHostState = snackBarHostState,
            authenticationUiClient = authenticationUiClient
        )
    }
}

fun NavGraphBuilder.navigationLogin(
    navController: NavHostController,
    snackBarHostState: SnackbarHostState,
    authenticationUiClient: AuthenticationUiClient
) {
    navigation(
        startDestination = Authentication.route,
        route = Login.route
    ) {
        composable(route = Authentication.route) { navBackStackEntry ->
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(Login.route)
            }
            val loginVM = hiltViewModel<LoginVM>(parentEntry)
            BackgroundLogin {
                MainAuthPage(
                    loginVM,
                    navController = navController,
                    signInWithGoogle = { },
                )
            }
        }
        composable(route = AuthenticationPhone.route) { navBackStackEntry ->
            val coroutineScope = rememberCoroutineScope()
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(Login.route)
            }
            val context = LocalContext.current
            val loginVM = hiltViewModel<LoginVM>(parentEntry)
            BackgroundLogin {
                LaunchedEffect(true) {
                    loginVM.getCountriesInfo()
                }
                PhoneAuthPage(
                    loginVM,
                    navController,
                    snackBarHostState = snackBarHostState,
                    onSentPhone = { code, phoneNumber ->
                        authenticationUiClient.authWithPhoneNumber(
                            context as Activity,
                            coroutineScope = coroutineScope,
                            phoneNumber = "+$code$phoneNumber",
                            onOtpSend = loginVM::updateSendOtpResult
                        )
                    }
                )
            }
        }
    }
}
