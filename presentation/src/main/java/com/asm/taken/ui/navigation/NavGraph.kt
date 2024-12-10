package com.asm.taken.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.asm.taken.ui.page.LoginPage
import com.asm.taken.vm.LoginVM

@Composable
fun MainNavigation() {
    val navigationController = rememberNavController()
    NavHost(navController = navigationController, startDestination = Login.route) {
        navigationAuthentication(navController = navigationController)
    }
}

fun NavGraphBuilder.navigationAuthentication(navController: NavHostController) {
    navigation(
        startDestination = Authentication.route,
        route = Login.route
    ) {
        composable(route = Authentication.route) { navBackStackEntry ->
            val parentEntry = remember(navBackStackEntry) {
                navController.getBackStackEntry(Login.route)
            }
            val loginVM = hiltViewModel<LoginVM>(parentEntry)
            LoginPage(
                loginVM,
                signInWithGoogle = {
//                                lifecycleScope.launch {
//                                    val authResult = authenticationUiClient.signInWithGoogle()
//                                    loginVM.loginUser(authResult)
//                                }
                },
                signInWithPhoneNumber = { phoneNumber ->
//                                authenticationUiClient.automaticSignInPhoneNumber(
//                                    this@MainActivity,
//                                    lifecycleScope,
//                                    phoneNumber,
//                                ) {
//                                    //loginVM.loginUser(it)
//                                }
                },
                validatePhoneCode = { verificationId, phoneCode ->
//                                lifecycleScope.launch {
//                                    val authResult = authenticationUiClient.manualSignInPhoneNumber(
//                                        verificationId,
//                                        phoneCode
//                                    )
//                                    //loginVM.loginUser(authResult)
//                                }
                }
            )
        }
        composable(route = AuthenticationPhone.route) {

        }
    }
}