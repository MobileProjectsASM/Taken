package com.asm.taken.ui.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.asm.taken.ui.page.main_menu.BackgroundMainSection
import com.asm.taken.ui.page.main_menu.EditGamerPage
import com.asm.taken.ui.page.main_menu.MainMenuPage
import com.asm.taken.utils.AuthenticationClient
import com.asm.taken.utils.AuthenticationProviders
import com.asm.taken.vm.CreateGamerVM
import com.asm.taken.vm.EditGamerVM
import com.asm.taken.vm.LoginVM
import com.asm.taken.vm.LoginVM2
import com.asm.taken.vm.MainVM
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.firebase.auth.FirebaseAuth

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
        composable<Authentication> { navBackStackEntry ->

            val loginManager = LoginManager.getInstance()
            val callbackManager = CallbackManager.Factory.create()
            val metaLauncher = rememberLauncherForActivityResult(loginManager.createLogInActivityResultContract(callbackManager)) {}
            val authProviders = AuthenticationProviders(
                context = LocalContext.current,
                loginManager = loginManager,
                callbackManager = callbackManager,
                metaAuthLauncher = metaLauncher
            )

            val loginVM2 = hiltViewModel<LoginVM2>(navBackStackEntry)

            BackgroundLogin {
                MainAuthPage(
                    authProvider = authProviders,
                    loginVM2 = loginVM2,
                    snackBarHostState = snackBarHostState,
                    onNavigateToCreateAccount = {
                        navigationController.navigate(CreateAccount)
                    },
                    onNavigateToAuthWithPhone = {
                        navigationController.navigate(AuthenticationPhone)
                    },
                    onNavigateToMainPage = {
                        navigationController.navigate(MainPage(gamerId = it)) {
                            popUpTo(Login::class) {
                                inclusive = true
                            }
                        }
                    },
                    onNavigateToCreateGamer = {
                        navigationController.navigate(
                            CreateGamer(
                                id = it.userId,
                                image = it.profilePictureUrl
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
        /*navigationLogin(
            navController = navigationController,
            snackBarHostState = snackBarHostState,
            authenticationClient = authenticationClient
        )*/
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
            BackgroundMainSection {
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
                    },
                    navigateToEditGamer = {
                        navigationController.navigate(EditGamer(gamerId = gamerId))
                    }
                )
            }
        }
        composable<EditGamer> { navBackStackEntry ->
            val gamerId = navBackStackEntry.arguments?.getString("gamerId") ?: "default"
            val editGamerVM = hiltViewModel<EditGamerVM>()
            BackgroundMainSection {
                EditGamerPage(
                    gamerId = gamerId,
                    authenticationClient = authenticationClient,
                    snackBarHostState = snackBarHostState,
                    editGamerVM = editGamerVM,
                    navigateToMainMenu = navigationController::popBackStack
                )
            }
        }
    }
}
