package com.asm.taken.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.asm.taken.ui.navigation.CreateAccount
import com.asm.taken.ui.navigation.Login
import com.asm.taken.ui.navigation.MainNavigation
import com.asm.taken.ui.navigation.MainPage
import com.asm.taken.ui.navigation.SignInGamer
import com.asm.taken.ui.page.CreateAccountPage
import com.asm.taken.ui.page.LoginPage
import com.asm.taken.ui.theme.TakenTheme
import com.asm.taken.utils.AuthenticationUiClient
import com.asm.taken.utils.ResourceResolver
import com.asm.taken.vm.LoginVM
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val loginVM: LoginVM by viewModels()

    @Inject
    lateinit var authenticationUiClient: AuthenticationUiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TakenTheme {
                MainNavigation()
            }
        }
    }
}