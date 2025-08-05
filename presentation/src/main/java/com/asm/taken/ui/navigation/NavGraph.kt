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
import androidx.navigation.toRoute
import com.asm.taken.ui.page.login.BackgroundLogin
import com.asm.taken.ui.page.login.CreateAccountPage
import com.asm.taken.ui.page.login.CreateGamerPage
import com.asm.taken.ui.page.login.MainAuthPage
import com.asm.taken.ui.page.login.PhoneAuthPage
import com.asm.taken.utils.AuthenticationClient
import com.asm.taken.utils.MessageResolver
import com.asm.taken.vm.EditGamerVM
import com.asm.taken.vm.LoginVM

@Composable
fun MainNavigation(
    initRoute: Route,
    innerPadding: PaddingValues,
    snackBarHostState: SnackbarHostState,
    authenticationClient: AuthenticationClient,
    messageResolver: MessageResolver
) {
    val navigationController = rememberNavController()

    NavHost(
        navController = navigationController,
        startDestination = initRoute,
        modifier = Modifier.padding(innerPadding)
    ) {
        navigationLogin(
            navController = navigationController,
            snackBarHostState = snackBarHostState,
            authenticationClient = authenticationClient,
            messageResolver = messageResolver
        )
        composable<CreateGamer> { navBackStackEntry ->
            val createGamer: CreateGamer = navBackStackEntry.toRoute()
            val editGamerVM = hiltViewModel<EditGamerVM>(navBackStackEntry)
            BackgroundLogin {
                CreateGamerPage(
                    createGamerInfo = createGamer,
                    editGamerVM = editGamerVM,
                    authenticationClient = authenticationClient,
                    messageResolver = messageResolver,
                    snackBarHostState = snackBarHostState,
                    onNavigateToAuthentication = {
                        navigationController.navigate(Login) {
                            popUpTo(CreateGamer::class) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToHome = { gamerId ->
                        navigationController.navigate(MainPage(gamerId = gamerId)) {
                            popUpTo(CreateGamer::class) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
        navigationMainPage(
            navController = navigationController
        )
    }
}

fun NavGraphBuilder.navigationLogin(
    navController: NavHostController,
    snackBarHostState: SnackbarHostState,
    authenticationClient: AuthenticationClient,
    messageResolver: MessageResolver
) {
    navigation<Login>(startDestination = Authentication) {
        composable<Authentication> { navBackStackEntry ->
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(Login)
            }
            val loginVM = hiltViewModel<LoginVM>(parentEntry)
            BackgroundLogin {
                MainAuthPage(
                    loginVM = loginVM,
                    authenticationClient = authenticationClient,
                    snackBarHostState = snackBarHostState,
                    messageResolver = messageResolver,
                    onNavigateToCreateAccount = {
                        navController.navigate(CreateAccount)
                    },
                    onNavigateToAuthWithPhone = {
                        navController.navigate(AuthenticationPhone)
                    },
                    onNavigateToMainPage = {
                        navController.navigate(MainPage(gamerId = it)) {
                            popUpTo(Login::class) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToCreateGamer = { userId, imageUrl ->
                        navController.navigate(CreateGamer(
                            id = userId,
                            image = imageUrl
                        )) {
                            popUpTo(Login::class) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
        composable<AuthenticationPhone> { navBackStackEntry ->
            val coroutineScope = rememberCoroutineScope()
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(Login)
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
                    onNavigateToCreateGamer = { userId, imageUrl ->
                        navController.navigate(CreateGamer(
                            id = userId,
                            image = imageUrl
                        )) {
                            popUpTo(Login::class) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToMainPage = {
                        navController.navigate(MainPage(gamerId = it)) {
                            popUpTo(Login::class) {
                                inclusive = true
                            }
                        }
                    },
                    popBackStack = navController::popBackStack
                )
            }
        }
        composable<CreateAccount> { navBackStackEntry ->
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(Login)
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
    }
}

fun NavGraphBuilder.navigationMainPage(
    navController: NavHostController
) {
    navigation<MainPage>(startDestination = Home) {
        composable<Home> { navBackStackEntry ->
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(MainPage::class)
            }
            val gamerId = parentEntry.toRoute<MainPage>().gamerId
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Welcome $gamerId")
            }
        }
    }
}
