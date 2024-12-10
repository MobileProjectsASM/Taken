//package com.asm.taken.ui.navigation
//
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import com.asm.taken.ui.page.CreateAccountPage
//import com.asm.taken.ui.page.LoginPage
//
//@Composable
//fun NavGraph(navController: NavHostController) {
//    NavHost(navController = navController, startDestination = Login.route) {
//        composable(Login.route) {
//            LoginPage(
//                loginVM,
//                resourceResolver,
//                navigationController,
//                signInWithGoogle = {
////                                lifecycleScope.launch {
////                                    val authResult = authenticationUiClient.signInWithGoogle()
////                                    loginVM.loginUser(authResult)
////                                }
//                },
//                signInWithPhoneNumber = { phoneNumber ->
////                                authenticationUiClient.automaticSignInPhoneNumber(
////                                    this@MainActivity,
////                                    lifecycleScope,
////                                    phoneNumber,
////                                ) {
////                                    //loginVM.loginUser(it)
////                                }
//                },
//                validatePhoneCode = { verificationId, phoneCode ->
////                                lifecycleScope.launch {
////                                    val authResult = authenticationUiClient.manualSignInPhoneNumber(
////                                        verificationId,
////                                        phoneCode
////                                    )
////                                    //loginVM.loginUser(authResult)
////                                }
//                }
//            )
//        }
//        composable(CreateAccount.route) {
//            CreateAccountPage()
//        }
//        composable(SignInGamer.route) {
//
//        }
//        composable(MainPage.route) {
//
//        }
//    }
//}