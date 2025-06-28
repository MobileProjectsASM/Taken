package com.asm.taken.ui.navigation

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
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
import com.asm.taken.ui.page.login.CreateAccountPage
import com.asm.taken.ui.page.login.CreateGamerPage
import com.asm.taken.ui.page.login.MainAuthPage
import com.asm.taken.ui.page.login.PhoneAuthPage
import com.asm.taken.utils.AuthenticationClient
import com.asm.taken.utils.MessageResolver
import com.asm.taken.vm.LoginVM

@Composable
fun MainNavigation(
    initRoute: Pair<Route, Any?>,
    innerPadding: PaddingValues,
    snackBarHostState: SnackbarHostState,
    authenticationClient: AuthenticationClient,
    messageResolver: MessageResolver
) {
    val navigationController = rememberNavController()

    val mainDestination: String = if (initRoute.first == Authentication || initRoute.first == CreateGamer) Login.route
    else MainPage.createRoute(initRoute.second?.toString() ?: "")

    val secondaryDestination: String = when (initRoute.first) {
        Authentication -> Authentication.route
        CreateGamer -> CreateGamer.route
        else -> Authentication.route
    }

    NavHost(
        navController = navigationController,
        startDestination = mainDestination,
        modifier = Modifier.padding(innerPadding)
    ) {
        navigationLogin(
            initRoute = secondaryDestination,
            navController = navigationController,
            snackBarHostState = snackBarHostState,
            authenticationClient = authenticationClient,
            messageResolver = messageResolver
        )
        navigationMainPage(
            navController = navigationController
        )
    }
}

fun NavGraphBuilder.navigationLogin(
    initRoute: String,
    navController: NavHostController,
    snackBarHostState: SnackbarHostState,
    authenticationClient: AuthenticationClient,
    messageResolver: MessageResolver
) {
    navigation(
        startDestination = initRoute,
        route = Login.route
    ) {
        composable(route = Authentication.route) { navBackStackEntry ->
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(Login.route)
            }
            val loginVM = hiltViewModel<LoginVM>(parentEntry)
            BackgroundLogin {
                MainAuthPage(
                    loginVM = loginVM,
                    authenticationClient = authenticationClient,
                    snackBarHostState = snackBarHostState,
                    messageResolver = messageResolver,
                    onNavigateToCreateAccount = {
                        navController.navigate(CreateAccount.route)
                    },
                    onNavigateToAuthWithPhone = {
                        navController.navigate(AuthenticationPhone.route)
                    },
                    onNavigateToMainPage = {
                        navController.navigate(MainPage.createRoute(it))
                    },
                    onNavigateToCreateGamer = {
                        navController.navigate(CreateGamer.route) {
                            popUpTo(Login.route) { inclusive = false }
                        }
                    }
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
                    loginVM = loginVM,
                    authenticationClient = authenticationClient,
                    messageResolver = messageResolver,
                    snackBarHostState = snackBarHostState,
                    onSentPhone = { code, phoneNumber ->
                        authenticationClient.authWithPhoneNumber(
                            context as Activity,
                            coroutineScope = coroutineScope,
                            phoneNumber = "+$code$phoneNumber",
                            onOtpSend = loginVM::updateLoginUiState,
                            onAuthResult = loginVM::updateLoginUiState
                        )
                    },
                    onNavigateToCreateGamer = {
                        navController.navigate(CreateGamer.route) {
                            popUpTo(Login.route) { inclusive = false }
                        }
                    },
                    onNavigateToMainPage = {
                        navController.navigate(MainPage.createRoute(it))
                    },
                    popBackStack = navController::popBackStack
                )
            }
        }
        composable(route = CreateAccount.route) { navBackStackEntry ->
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(Login.route)
            }
            val loginVM = hiltViewModel<LoginVM>(parentEntry)
            BackgroundLogin {
                CreateAccountPage(
                    loginVM = loginVM,
                    authenticationClient = authenticationClient,
                    messageResolver = messageResolver,
                    snackBarHostState = snackBarHostState,
                    popBackStack = navController::popBackStack
                )
            }
        }
        composable(route = CreateGamer.route) { navBackStackEntry ->
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(Login.route)
            }
            val loginVM = hiltViewModel<LoginVM>(parentEntry)
            BackgroundLogin {
                LaunchedEffect(true) {
                    loginVM.getCountriesInfo()
                }
                CreateGamerPage(
                    loginVM = loginVM,
                    authenticationClient = authenticationClient,
                    messageResolver = messageResolver,
                    snackBarHostState = snackBarHostState,
                    onNavigateToAuthentication = {
                        navController.navigate(Authentication.route) {
                            popUpTo(Login.route) { inclusive = false }
                        }
                    }
                )
            }
        }
    }
}

fun NavGraphBuilder.navigationMainPage(
    navController: NavHostController
) {
    navigation(
        startDestination = Home.route,
        route = MainPage.route
    ) {
        composable(
            route = Home.route
        ) { navBackStackEntry ->
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(MainPage.route)
            }
            val gamerId = parentEntry.arguments?.getString(MainPage.gamerIdArg) ?: ""
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Welcome $gamerId")
            }
        }
    }
}
