package com.asm.taken.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
import com.asm.taken.ui.page.main_menu.BackgroundMainMenu
import com.asm.taken.ui.page.main_menu.MainMenuPage
import com.asm.taken.utils.AuthenticationClient
import com.asm.taken.vm.CreateGamerVM
import com.asm.taken.vm.LoginVM
import com.asm.taken.vm.MainVM

@Composable
fun MainNavigation(
    initRoute: Route,
    innerPadding: PaddingValues,
    snackBarHostState: SnackbarHostState,
    authenticationClient: AuthenticationClient
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
            authenticationClient = authenticationClient
        )
        composable<CreateGamer> { navBackStackEntry ->
            val createGamer: CreateGamer = navBackStackEntry.toRoute()
            val createGamerVM = hiltViewModel<CreateGamerVM>(navBackStackEntry)
            BackgroundLogin {
                CreateGamerPage(
                    createGamerInfo = createGamer,
                    createGamerVM = createGamerVM,
                    authenticationClient = authenticationClient,
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
            authenticationClient = authenticationClient,
            snackBarHostState = snackBarHostState,
            navigationController = navigationController
        )
    }
}

fun NavGraphBuilder.navigationLogin(
    navController: NavHostController,
    snackBarHostState: SnackbarHostState,
    authenticationClient: AuthenticationClient
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
                          navController.navigate(
                            CreateGamer(
                                id = userId,
                                image = imageUrl
                            )
                        ) {
                            popUpTo(Login::class) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
        composable<AuthenticationPhone> { navBackStackEntry ->
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(Login)
            }
            val loginVM = hiltViewModel<LoginVM>(parentEntry)
            BackgroundLogin {
                PhoneAuthPage(
                    loginVM = loginVM,
                    authenticationClient = authenticationClient,
                    snackBarHostState = snackBarHostState,
                    popBackStack = navController::popBackStack,
                    onNavigateToMainPage = {
                        navController.navigate(MainPage(gamerId = it)) {
                            popUpTo(Login::class) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToCreateGamer = { userId, imageUrl ->
                        navController.navigate(
                            CreateGamer(
                                id = userId,
                                image = imageUrl
                            )
                        ) {
                            popUpTo(Login::class) {
                                inclusive = true
                            }
                        }
                    }
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
                    snackBarHostState = snackBarHostState,
                    popBackStack = navController::popBackStack
                )
            }
        }
    }
}

fun NavGraphBuilder.navigationMainPage(
    authenticationClient: AuthenticationClient,
    snackBarHostState: SnackbarHostState,
    navigationController: NavHostController
) {
    navigation<MainPage>(startDestination = Home) {
        composable<Home> { navBackStackEntry ->
            val gamerId = navBackStackEntry.arguments?.getString("gamerId") ?: "default"
            val parentEntry = remember(navBackStackEntry) {
                navigationController.getBackStackEntry(MainPage::class)
            }
            val mainVM = hiltViewModel<MainVM>(parentEntry)
            BackgroundMainMenu {
                MainMenuPage(
                    gamerId = gamerId,
                    snackBarHostState = snackBarHostState,
                    mainVM = mainVM,
                    authenticationClient = authenticationClient,
                    onNavigateToAuthentication = {
                        navigationController.navigate(Login) {
                            popUpTo(MainPage::class) {
                                inclusive = true
                            }
                        }
                    }
                )
            }
        }
    }
}
