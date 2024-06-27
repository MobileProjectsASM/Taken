package com.asm.taken.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.asm.taken.model.CreateAccount
import com.asm.taken.model.Login
import com.asm.taken.model.MainPage
import com.asm.taken.model.SignInGamer
import com.asm.taken.ui.page.CreateAccountPage
import com.asm.taken.ui.page.LoginPage
import com.asm.taken.ui.theme.TakenTheme
import com.asm.taken.utils.AuthenticationUiClient
import com.asm.taken.utils.ResourceResolver
import com.asm.taken.vm.LoginVM
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val loginVM: LoginVM by viewModels()

    @Inject
    lateinit var resourceResolver: ResourceResolver

    @Inject
    lateinit var authenticationUiClient: AuthenticationUiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TakenTheme {
                val navigationController = rememberNavController()
                NavHost(navController = navigationController, startDestination = Login.route) {
                    composable(Login.route) {
                        LoginPage(
                            loginVM,
                            resourceResolver,
                            navigationController,
                            signInWithGoogle = {
                                lifecycleScope.launch {
                                    val signInResult = authenticationUiClient.signInWithGoogle()
                                    loginVM.loginUser(signInResult)
                                }
                            },
                            signInWithPhoneNumber = { phoneNumber ->
                                authenticationUiClient.signInWithPhoneNumber(
                                    this@MainActivity,
                                    lifecycleScope,
                                    phoneNumber,
                                ) {
                                    loginVM.loginUser(it)
                                }
                            }
                        )
                    }
                    composable(CreateAccount.route) {
                        CreateAccountPage()
                    }
                    composable(SignInGamer.route) {

                    }
                    composable(MainPage.route) {

                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TakenTheme {
        Greeting("Android")
    }
}