package com.asm.taken.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
import com.asm.taken.vm.LoginVM

@Composable
fun MainNavigation(
    innerPadding: PaddingValues,
    snackBarHostState: SnackbarHostState
) {
    val navigationController = rememberNavController()
    NavHost(
        navController = navigationController,
        startDestination = Login.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        navigationLogin(
            navController = navigationController,
            snackBarHostState = snackBarHostState
        )
    }
}

fun NavGraphBuilder.navigationLogin(
    navController: NavHostController,
    snackBarHostState: SnackbarHostState
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
                    signInWithGoogle = { },
                    navigateToPhoneNumberScreen = {
                        navController.navigate(AuthenticationPhone.route)
                    }
                )
            }
        }
        composable(route = AuthenticationPhone.route) { navBackStackEntry ->
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(Login.route)
            }
            val loginVM = hiltViewModel<LoginVM>(parentEntry)
            BackgroundLogin {
                LaunchedEffect(true) {
                    loginVM.getCountriesInfo()
                }
                PhoneAuthPage(
                    loginVM,
                    snackBarHostState = snackBarHostState
                )
            }
        }
    }
}
