package com.asm.taken.ui.page.login

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.Card
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.asm.taken.R
import com.asm.taken.model.InputState
import com.asm.taken.model.LoginFormCreateAccountUiState
import com.asm.taken.model.LoginUiState
import com.asm.taken.ui.CircularProgressDialog
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultOutlinedTextFieldLI
import com.asm.taken.ui.PasswordOutlinedTextField
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.utils.AuthenticationClient
import com.asm.taken.utils.MessageResolver
import com.asm.taken.utils.SignUpResult
import com.asm.taken.vm.LoginVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CreateAccountPage(
    loginVM: LoginVM,
    authenticationClient: AuthenticationClient,
    navController: NavController,
    messageResolver: MessageResolver,
    snackBarHostState: SnackbarHostState
) {
    val coroutineScope = rememberCoroutineScope()

    BackHandler {
        loginVM.cleanLoginFormCreateAccount()
        navController.popBackStack()
    }
    PanelCreateAccount(
        loginVM = loginVM,
        authenticationClient = authenticationClient,
        messageResolver = messageResolver,
        coroutineScope = coroutineScope
    )
    LoginState(
        loginVM = loginVM,
        navController = navController,
        messageResolver = messageResolver,
        snackBarHostState = snackBarHostState
    )
}

@Composable
fun LoginState(
    loginVM: LoginVM,
    navController: NavController,
    messageResolver: MessageResolver,
    snackBarHostState: SnackbarHostState
) {
    val loginUiState: LoginUiState? by loginVM.loginUiState.collectAsStateWithLifecycle()
    if (loginUiState == null || loginUiState is LoginUiState.SentOtp
        || loginUiState is LoginUiState.RegisteredUser
        || loginUiState is LoginUiState.UnregisteredUser) return
    when (loginUiState) {
        LoginUiState.AccountCreated -> LaunchedEffect(true) {
            navController.popBackStack()
            loginVM.cleanLoginFormCreateAccount()
            loginVM.resetLoginUiState()
        }
        is LoginUiState.Failure -> {
            val message = messageResolver.getErrorLogin((loginUiState as LoginUiState.Failure).loginFailure)
            LaunchedEffect(true) {
                val snackBarResult = snackBarHostState.showSnackbar(message, withDismissAction = true)
                if (snackBarResult == SnackbarResult.Dismissed) loginVM.resetLoginUiState()
            }
        }
        else-> CircularProgressDialog()
    }
}

@Composable
fun PanelCreateAccount(
    loginVM: LoginVM,
    authenticationClient: AuthenticationClient,
    messageResolver: MessageResolver,
    coroutineScope: CoroutineScope
) {
    Column(
       modifier = Modifier
           .fillMaxWidth()
           .verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.height(250.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 15.dp, horizontal = 10.dp)
            ) {
                PuzzleGeneralTitle(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.txt_ttl_form_create_account)
                )
                Spacer(modifier = Modifier.height(50.dp))
                FormCreateAccount(
                    loginVM = loginVM,
                    messageResolver = messageResolver
                ) { email, password ->
                    coroutineScope.launch {
                        loginVM.updateLoginUiState(SignUpResult.Loading)
                        val signUpResult = authenticationClient.createAccount(email, password)
                        loginVM.updateLoginUiState(signUpResult)
                    }
                }
            }
        }
        Box(modifier = Modifier.height(250.dp))
    }
}

@Composable
fun FormCreateAccount(
    loginVM: LoginVM,
    messageResolver: MessageResolver,
    createAccount: (String, String) -> Unit
) {
    val loginFormCreateAccountState: LoginFormCreateAccountUiState by loginVM.loginFormCreateAccountState.collectAsStateWithLifecycle()
    val emailErrors: List<String> = when (val emailUiState = loginFormCreateAccountState.emailUiState.state) {
        is InputState.Error -> emailUiState.errors.map { messageResolver.getErrorEmail(it) }
        InputState.Init, InputState.Success -> listOf()
    }
    val passwordErrors: List<String> = when (val passwordUiState = loginFormCreateAccountState.passwordUiState.state) {
        is InputState.Error -> passwordUiState.errors.map { messageResolver.getErrorPassword(it) }
        InputState.Init, InputState.Success -> listOf()
    }
    val passwordRepeatErrors: List<String> = when (val passwordRepeatUiState = loginFormCreateAccountState.passwordRepeatUiState.state) {
        is InputState.Error -> passwordRepeatUiState.errors.map { messageResolver.getErrorPasswordRepeat(it) }
        InputState.Init, InputState.Success -> listOf()
    }
    Column {
        DefaultOutlinedTextFieldLI(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            value = loginFormCreateAccountState.emailUiState.value,
            label = R.string.txt_label_email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = Icons.Default.Mail,
            cdLeadingIcon = null,
            errors = emailErrors
        ) {
            loginVM.validateFormCreateAccount(it, loginFormCreateAccountState.passwordUiState.value, loginFormCreateAccountState.passwordRepeatUiState.value)
        }
        Spacer(modifier = Modifier.height(10.dp))
        PasswordOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            label = R.string.txt_label_password,
            password = loginFormCreateAccountState.passwordUiState.value,
            leadingIcon = Icons.Default.Lock,
            errors = passwordErrors
        ) {
            loginVM.validateFormCreateAccount(loginFormCreateAccountState.emailUiState.value, it, loginFormCreateAccountState.passwordRepeatUiState.value)
        }
        Spacer(modifier = Modifier.height(10.dp))
        PasswordOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            label = R.string.txt_label_password_repeat,
            password = loginFormCreateAccountState.passwordRepeatUiState.value,
            leadingIcon = Icons.Default.Lock,
            errors = passwordRepeatErrors
        ) {
            loginVM.validateFormCreateAccount(loginFormCreateAccountState.emailUiState.value, loginFormCreateAccountState.passwordUiState.value, it)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DefaultButton(
                text = stringResource(id = R.string.txt_btn_create_account),
                enable = loginFormCreateAccountState.emailUiState.state is InputState.Success
                        && loginFormCreateAccountState.passwordUiState.state is InputState.Success
                        && loginFormCreateAccountState.passwordRepeatUiState.state is InputState.Success,
                onClickButton = {
                    createAccount(loginFormCreateAccountState.emailUiState.value, loginFormCreateAccountState.passwordUiState.value)
                }
            )
        }
    }
}