package com.asm.taken.ui.page.login

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asm.domain.entities.AuthUser
import com.asm.taken.R
import com.asm.taken.model.InputState
import com.asm.taken.model.LoginFailure
import com.asm.taken.model.LoginFormUiState
import com.asm.taken.model.LoginUiState
import com.asm.taken.ui.CircularProgressDialog
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultImageButton
import com.asm.taken.ui.DefaultOutlinedTextFieldLI
import com.asm.taken.ui.DefaultText
import com.asm.taken.ui.DefaultTextButton
import com.asm.taken.ui.PasswordOutlinedTextField
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.utils.AuthResult
import com.asm.taken.utils.AuthenticationClient
import com.asm.taken.utils.ResourceResolver
import com.asm.taken.vm.LoginVM
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainAuthPage(
    loginVM: LoginVM,
    authenticationClient: AuthenticationClient,
    resourceResolver: ResourceResolver,
    snackBarHostState: SnackbarHostState,
    onNavigateToCreateAccount: () -> Unit,
    onNavigateToAuthWithPhone: () -> Unit,
    onNavigateToMainPage: (String) -> Unit,
    onNavigateToCreateGamer: (String, String?) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    AuthenticationSection(
        loginVM = loginVM,
        resourceResolver = resourceResolver,
        coroutineScope = coroutineScope,
        authenticationClient = authenticationClient,
        onNavigateToCreateAccount = onNavigateToCreateAccount,
        onNavigateToAuthWithPhone = onNavigateToAuthWithPhone
    )
    SessionSection(
        loginVM = loginVM,
        resourceResolver = resourceResolver,
        snackBarHostState = snackBarHostState,
        onNavigateToMainPage = onNavigateToMainPage,
        onNavigateToCreateGamer = {
            onNavigateToCreateGamer(it.userId, it.profilePictureUrl)
        }
    )
}

@Composable
fun AuthenticationSection(
    loginVM: LoginVM,
    resourceResolver: ResourceResolver,
    coroutineScope: CoroutineScope,
    authenticationClient: AuthenticationClient,
    onNavigateToCreateAccount: () -> Unit,
    onNavigateToAuthWithPhone: () -> Unit,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.height(250.dp))
        PanelLogin(
            loginVM = loginVM,
            authenticationClient = authenticationClient,
            coroutineScope = coroutineScope,
            resourceResolver = resourceResolver
        )
        PanelSocialMedia(
            signInWithGoogle = {
                coroutineScope.launch {
                    loginVM.updateLoginState(LoginUiState.Loading)
                    val authResult = authenticationClient.signInWithGoogle(context)
                    loginVM.updateLoginState(authResult)
                }
            },
            signInWithPhoneNumber = onNavigateToAuthWithPhone,
            signInWithFacebook = {
                authenticationClient.signInWithFacebook(
                    activityResultRegistryOwner = context as ComponentActivity,
                    coroutineScope = coroutineScope,
                    onFacebookLoginLoading = { loginVM.updateLoginState(LoginUiState.Loading) },
                    onAuthResult = loginVM::updateLoginState
                )
            },
            createAccount = onNavigateToCreateAccount
        )
        Box(modifier = Modifier.height(250.dp))
    }
}

@Composable
fun PanelLogin(
    loginVM: LoginVM,
    authenticationClient: AuthenticationClient,
    coroutineScope: CoroutineScope,
    resourceResolver: ResourceResolver
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp, horizontal = 10.dp)
        ) {
            PuzzleGeneralTitle(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.txt_ttl_login_dialog)
            )
            DefaultText(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.txt_inf_login_dialog),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(50.dp))
            FormLogin(
                loginVM = loginVM,
                resourceResolver = resourceResolver
            ) { email, password ->
                coroutineScope.launch {
                    loginVM.updateLoginState(LoginUiState.Loading)
                    val authResult = authenticationClient.signInWithEmailAndPassword(email, password)
                    loginVM.updateLoginState(authResult)
                }
            }
        }
    }
}

@Composable
fun SessionSection(
    loginVM: LoginVM,
    resourceResolver: ResourceResolver,
    snackBarHostState: SnackbarHostState,
    onNavigateToMainPage: (gamerId: String) -> Unit,
    onNavigateToCreateGamer: (AuthUser) -> Unit
) {
    val loginUiState: LoginUiState by loginVM.loginUiState.collectAsStateWithLifecycle()
    when (val state = loginUiState) {
        is LoginUiState.Failure -> when (val loginFailure = state.loginFailure) {
            is LoginFailure.AuthFailure -> TODO()
            LoginFailure.LogoutFailure -> TODO()
            is LoginFailure.RegisterFailure -> TODO()
            is LoginFailure.SendOtpFailure -> TODO()
            is LoginFailure.SignUpFailure -> TODO()
        }
        /*val message = messageResolver.getErrorLogin(state.loginFailure)
        LaunchedEffect(true) {
            val snackBarResult = snackBarHostState.showSnackbar(message, withDismissAction = true)
            if (snackBarResult == SnackbarResult.Dismissed) loginVM.resetLoginUiState()
        }*/
        is LoginUiState.Loading -> CircularProgressDialog()
        is LoginUiState.RegisteredUser -> LaunchedEffect(true) {
            onNavigateToMainPage(state.gamerId)
        }

        is LoginUiState.UnregisteredUser -> LaunchedEffect(true) {
            onNavigateToCreateGamer(state.authUser)
        }

        else -> return
    }
}

@Composable
fun FormLogin(
    loginVM: LoginVM,
    resourceResolver: ResourceResolver,
    signInWithEmailAndPassword: (String, String) -> Unit,
) {
    val loginFormState: LoginFormUiState by loginVM.loginFormUiState.collectAsStateWithLifecycle()
    val emailErrors: List<String> = when (val emailUiState = loginFormState.emailUiState.state) {
        is InputState.Error -> emailUiState.errors.map { resourceResolver.getErrorEmail(it) }
        InputState.Init -> listOf()
        InputState.Success -> listOf()
    }
    val passwordErrors: List<String> =
        when (val passwordUiState = loginFormState.passwordUiState.state) {
            is InputState.Error -> passwordUiState.errors.map { resourceResolver.getErrorPassword(it) }
            InputState.Init -> listOf()
            InputState.Success -> listOf()
        }
    Column {
        DefaultOutlinedTextFieldLI(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            value = loginFormState.emailUiState.value,
            label = R.string.txt_label_email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = Icons.Default.Mail,
            cdLeadingIcon = null,
            errors = emailErrors
        ) {
            loginVM.validateLoginForm(it, loginFormState.passwordUiState.value)
        }
        Spacer(modifier = Modifier.height(10.dp))
        PasswordOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            label = R.string.txt_label_password,
            password = loginFormState.passwordUiState.value,
            leadingIcon = Icons.Default.Lock,
            errors = passwordErrors,
        ) {
            loginVM.validateLoginForm(loginFormState.emailUiState.value, it)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DefaultButton(
                text = stringResource(id = R.string.txt_btn_login),
                enable = loginFormState.emailUiState.state is InputState.Success && loginFormState.passwordUiState.state is InputState.Success,
                onClickButton = {
                    signInWithEmailAndPassword(
                        loginFormState.emailUiState.value,
                        loginFormState.passwordUiState.value
                    )
                }
            )
        }
    }
}

@Composable
fun PanelSocialMedia(
    signInWithGoogle: () -> Unit,
    signInWithPhoneNumber: () -> Unit,
    signInWithFacebook: () -> Unit,
    createAccount: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PuzzleGeneralTitle(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.txt_inf_sign_in)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 15.dp, horizontal = 10.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                DefaultImageButton(
                    imageSize = 40.dp,
                    iconButton = R.drawable.facebook,
                    cdIconButton = null,
                    onClickButton = signInWithFacebook
                )
                DefaultImageButton(
                    imageSize = 40.dp,
                    iconButton = R.drawable.google,
                    cdIconButton = null,
                    onClickButton = signInWithGoogle
                )
                DefaultImageButton(
                    imageSize = 40.dp,
                    iconButton = R.drawable.phone,
                    cdIconButton = null,
                    onClickButton = signInWithPhoneNumber
                )
            }
            DefaultTextButton(
                text = stringResource(id = R.string.txt_btn_create_account),
                onClickButton = createAccount
            )
        }
    }
}