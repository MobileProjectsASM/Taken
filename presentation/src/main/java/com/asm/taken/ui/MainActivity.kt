package com.asm.taken.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.asm.taken.model.Login
import com.asm.taken.model.SignInState
import com.asm.taken.ui.page.LoginPage
import com.asm.taken.ui.theme.TakenTheme
import com.asm.taken.utils.GoogleAuthUiClient
import com.asm.taken.utils.ResourceResolver
import com.asm.taken.vm.LoginVM
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val loginVM: LoginVM by viewModels()

    @Inject
    lateinit var resourceResolver: ResourceResolver

    @Inject
    lateinit var googleAuthUiClient: GoogleAuthUiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TakenTheme {
                val navigationController = rememberNavController()
                NavHost(navController = navigationController, startDestination = Login.route) {
                    composable(Login.route) {
                        val launcherActivity = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartIntentSenderForResult()
                        ) { activityResult ->
                            if (activityResult.resultCode == RESULT_OK) lifecycleScope.launch {
                                val intent = activityResult.data ?: return@launch
                                val signInResult = googleAuthUiClient.signInWithIntent(intent)
                                loginVM.signInWithGoogle(signInResult)
                            }
                        }

                        LoginPage(
                            loginVM,
                            resourceResolver,
                            navigationController,
                            signInWithGoogle = {
                                lifecycleScope.launch {
                                    val intentSender =
                                        googleAuthUiClient.getSignInIntent() ?: return@launch
                                    val signInIntent =
                                        IntentSenderRequest.Builder(intentSender).build()
                                    launcherActivity.launch(signInIntent)
                                }
                            }
                        )
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