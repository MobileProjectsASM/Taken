package com.asm.taken.ui.page.login

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asm.domain.entities.AuthUser
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.taken.R
import com.asm.taken.model.AuthState
import com.asm.taken.model.AuthTypeState
import com.asm.taken.model.InputState
import com.asm.taken.model.EmailAndPasswordFormState
import com.asm.taken.model.LoginUIState
import com.asm.taken.ui.CircularProgressDialog
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultImageButton
import com.asm.taken.ui.DefaultOutlinedTextFieldLI
import com.asm.taken.ui.DefaultText
import com.asm.taken.ui.DefaultTextButton
import com.asm.taken.ui.ErrorComponent
import com.asm.taken.ui.PasswordOutlinedTextField
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.utils.AuthenticationProviders
import com.asm.taken.utils.getErrorEmail
import com.asm.taken.utils.getErrorPassword
import com.asm.taken.vm.LoginVM
import kotlinx.coroutines.launch

@Composable
fun MainAuthPage(
    authProvider: AuthenticationProviders,
    loginVM: LoginVM,
    snackBarHostState: SnackbarHostState,
    onNavigateToCreateAccount: () -> Unit,
    onNavigateToAuthWithPhone: () -> Unit,
    onNavigateToMainPage: (String) -> Unit,
    onNavigateToCreateGamer: (AuthUser) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    val loginUIState: LoginUIState by loginVM.loginUIState.collectAsStateWithLifecycle()

    AuthenticationSection(
        emailAndPasswordFormState = loginUIState.emailAndPasswordFormState,
        onNavigateToCreateAccount = onNavigateToCreateAccount,
        onNavigateToAuthWithPhone = onNavigateToAuthWithPhone,
        validateFormLogin = loginVM::validateLoginForm,
        loginWithEmailAndPassword = loginVM::signInWithEmailAndPassword,
        loginWithGoogle = {
            coroutineScope.launch {
                when (val result = authProvider.authWithGoogle()) {
                    is Result.Successful<String> -> loginVM.signInWithGoogle(result.data)
                    is Result.Unsuccessful<GeneralError> -> loginVM.updateAuthGoogleErrorState(
                        result.error
                    )
                }
            }
        },
        loginWithFacebook = {
            coroutineScope.launch {
                when (val result = authProvider.authWithFacebook()) {
                    is Result.Successful<String> -> loginVM.signInWithFacebook(result.data)
                    is Result.Unsuccessful<GeneralError> -> loginVM.updateAuthFacebookErrorState(
                        result.error
                    )
                }
            }
        }
    )
    when (val authType = loginUIState.authTypeState) {
        is AuthTypeState.EmailAndPasswordAuthType -> ProcessSection(
            authState = authType.authState,
            snackBarHostState = snackBarHostState,
            retryProcess = {
                loginVM.signInWithEmailAndPassword(
                    email = loginUIState.emailAndPasswordFormState.emailUiState.value,
                    password = loginUIState.emailAndPasswordFormState.passwordUiState.value
                )
            },
            onNavigateToMainPage = onNavigateToMainPage,
            onNavigateToCreateGamer = onNavigateToCreateGamer,
            resetProcessState = loginVM::resetProcessState
        )
        is AuthTypeState.FacebookAuthType -> ProcessSection(
            authState = authType.authState,
            snackBarHostState = snackBarHostState,
            retryProcess = {
                coroutineScope.launch {
                    when (val result = authProvider.authWithFacebook()) {
                        is Result.Successful<String> -> loginVM.signInWithFacebook(result.data)
                        is Result.Unsuccessful<GeneralError> -> loginVM.updateAuthFacebookErrorState(
                            result.error
                        )
                    }
                }
            },
            onNavigateToMainPage = onNavigateToMainPage,
            onNavigateToCreateGamer = onNavigateToCreateGamer,
            resetProcessState = loginVM::resetProcessState
        )
        is AuthTypeState.GoogleAuthType -> ProcessSection(
            authState = authType.authState,
            snackBarHostState = snackBarHostState,
            retryProcess = {
                coroutineScope.launch {
                    when (val result = authProvider.authWithGoogle()) {
                        is Result.Successful<String> -> loginVM.signInWithGoogle(result.data)
                        is Result.Unsuccessful<GeneralError> -> loginVM.updateAuthGoogleErrorState(
                            result.error
                        )
                    }
                }
            },
            onNavigateToMainPage = onNavigateToMainPage,
            onNavigateToCreateGamer = onNavigateToCreateGamer,
            resetProcessState = loginVM::resetProcessState
        )
        AuthTypeState.Idle -> return
    }
}

@Composable
fun AuthenticationSection(
    emailAndPasswordFormState: EmailAndPasswordFormState,
    onNavigateToCreateAccount: () -> Unit,
    onNavigateToAuthWithPhone: () -> Unit,
    validateFormLogin: (String, String) -> Unit,
    loginWithEmailAndPassword: (email: String, password: String) -> Unit,
    loginWithGoogle: () -> Unit,
    loginWithFacebook: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.height(250.dp))
        PanelFormLogin(
            emailAndPasswordFormState = emailAndPasswordFormState,
            validateFormLogin = validateFormLogin,
            signInWithEmailAndPassword = loginWithEmailAndPassword
        )
        PanelSocialMedia(
            signInWithGoogle = loginWithGoogle,
            signInWithPhoneNumber = onNavigateToAuthWithPhone,
            signInWithFacebook =  loginWithFacebook,
            createAccount = onNavigateToCreateAccount
        )
        Box(modifier = Modifier.height(250.dp))
    }
}

@Composable
fun PanelFormLogin(
    emailAndPasswordFormState: EmailAndPasswordFormState,
    validateFormLogin: (String, String) -> Unit,
    signInWithEmailAndPassword: (String, String) -> Unit,
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
                emailAndPasswordFormState = emailAndPasswordFormState,
                validateFormLogin = validateFormLogin,
                signInWithEmailAndPassword = signInWithEmailAndPassword
            )
        }
    }
}

@Composable
fun ProcessSection(
    authState: AuthState,
    snackBarHostState: SnackbarHostState,
    retryProcess: () -> Unit,
    resetProcessState: () -> Unit,
    onNavigateToMainPage: (gamerId: String) -> Unit,
    onNavigateToCreateGamer: (AuthUser) -> Unit
) {
    when (authState) {
        is AuthState.Error -> ErrorComponent(
            generalError = authState.generalError,
            retryProcess = retryProcess,
            snackBarHostState = snackBarHostState,
            resetProcessState = resetProcessState
        )
        AuthState.Loading -> CircularProgressDialog()
        is AuthState.RegisteredUser -> LaunchedEffect(true) {
            onNavigateToMainPage(authState.gamerId)
        }
        is AuthState.UnregisteredUser -> LaunchedEffect(true) {
            onNavigateToCreateGamer(authState.authUser)
        }
        AuthState.Idle -> return
    }
}

@Composable
fun FormLogin(
    emailAndPasswordFormState: EmailAndPasswordFormState,
    validateFormLogin: (String, String) -> Unit,
    signInWithEmailAndPassword: (String, String) -> Unit,
) {
    Column {
        DefaultOutlinedTextFieldLI(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            value = emailAndPasswordFormState.emailUiState.value,
            label = R.string.txt_label_email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = Icons.Default.Mail,
            cdLeadingIcon = null,
            errors = emailAndPasswordFormState.emailUiState.state.let {
                when (val emailUiState = emailAndPasswordFormState.emailUiState.state) {
                    is InputState.Error -> emailUiState.errors.map { getErrorEmail(it) }
                    InputState.Idle, InputState.Success -> listOf()
                }
            }
        ) {
            validateFormLogin(it, emailAndPasswordFormState.passwordUiState.value)
        }
        Spacer(modifier = Modifier.height(10.dp))
        PasswordOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            label = R.string.txt_label_password,
            password = emailAndPasswordFormState.passwordUiState.value,
            leadingIcon = Icons.Default.Lock,
            errors = emailAndPasswordFormState.passwordUiState.state.let {
                when (val passwordUiState = emailAndPasswordFormState.passwordUiState.state) {
                    is InputState.Error -> passwordUiState.errors.map { getErrorPassword(it) }
                    InputState.Idle, InputState.Success -> listOf()
                }
            },
        ) {
            validateFormLogin(emailAndPasswordFormState.emailUiState.value, it)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DefaultButton(
                text = stringResource(id = R.string.txt_btn_login),
                enable = emailAndPasswordFormState.emailUiState.state is InputState.Success && emailAndPasswordFormState.passwordUiState.state is InputState.Success,
                onClickButton = {
                    signInWithEmailAndPassword(
                        emailAndPasswordFormState.emailUiState.value,
                        emailAndPasswordFormState.passwordUiState.value
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