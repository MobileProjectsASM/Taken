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
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asm.domain.entities.Result
import com.asm.domain.errors.GeneralError
import com.asm.taken.R
import com.asm.taken.model.InputEmailError
import com.asm.taken.model.InputPasswordError
import com.asm.taken.model.InputState
import com.asm.taken.model.LoginFormCreateAccountUiState
import com.asm.taken.model.LoginUiState
import com.asm.taken.ui.CircularProgressDialog
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultOutlinedTextFieldLI
import com.asm.taken.ui.DialogError
import com.asm.taken.ui.PasswordOutlinedTextField
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.ui.SnackbarError
import com.asm.taken.utils.AuthenticationClient
import com.asm.taken.utils.getErrorEmail
import com.asm.taken.utils.getErrorPassword
import com.asm.taken.vm.LoginVM
import kotlinx.coroutines.launch

@Composable
fun CreateAccountPage(
    loginVM: LoginVM,
    authenticationClient: AuthenticationClient,
    snackBarHostState: SnackbarHostState,
    popBackStack: () -> Unit
) {
    BackHandler {
        loginVM.cleanLoginFormCreateAccount()
        popBackStack()
    }
    CreateAccountSection(
        loginVM = loginVM,
        authenticationClient = authenticationClient,
    )
    SessionSection(
        loginVM = loginVM,
        snackBarHostState = snackBarHostState,
        popBackStack = popBackStack
    )
}

@Composable
fun SessionSection(
    loginVM: LoginVM,
    snackBarHostState: SnackbarHostState,
    popBackStack: () -> Unit
) {
    val loginUiState: LoginUiState by loginVM.loginUiState.collectAsStateWithLifecycle()
    when (val state = loginUiState) {
        LoginUiState.AccountCreated -> LaunchedEffect(true) {
            popBackStack()
            loginVM.cleanLoginFormCreateAccount()
            loginVM.resetLoginUiState()
        }

        is LoginUiState.Error -> when (state.generalError) {
            is GeneralError.ClientError -> DialogError(
                title = stringResource(R.string.txt_ttl_client_error),
                image = painterResource(R.drawable.ic_warning),
                message = stringResource(R.string.err_client),
                onDismissDialog = loginVM::resetLoginUiState
            )

            GeneralError.ConnectionError -> DialogError(
                title = stringResource(R.string.txt_ttl_unexpected_error),
                image = painterResource(R.drawable.ic_warning),
                message = stringResource(R.string.err_server_connection),
                onDismissDialog = loginVM::resetLoginUiState
            )

            GeneralError.NetworkError -> SnackbarError(
                snackBarHostState = snackBarHostState,
                actionLabel = stringResource(R.string.txt_label_retry),
                duration = SnackbarDuration.Long,
                message = stringResource(R.string.err_network_connection),
                onDismiss = loginVM::resetLoginUiState
            )

            is GeneralError.ServerError -> DialogError(
                title = stringResource(R.string.txt_ttl_service_error),
                image = painterResource(R.drawable.ic_error),
                message = stringResource(R.string.err_server),
                onDismissDialog = loginVM::resetLoginUiState
            )

            GeneralError.Unknown -> SnackbarError(
                snackBarHostState = snackBarHostState,
                message = stringResource(R.string.err_auth),
                withDismissAction = true,
                onDismiss = loginVM::resetLoginUiState
            )
        }

        LoginUiState.Loading -> CircularProgressDialog()
        else -> return
    }
}

@Composable
fun CreateAccountSection(
    loginVM: LoginVM,
    authenticationClient: AuthenticationClient
) {

    val loginFormCreateAccountState: LoginFormCreateAccountUiState by loginVM.loginFormCreateAccountState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()

    PanelFormCreateAccount(
        loginFormCreateAccountState = loginFormCreateAccountState,
        validateFormCreateAccount = { email, password, passwordRepeat ->
            loginVM.validateFormCreateAccount(email, password, passwordRepeat)
        },
        createAccount = { email, password ->
            coroutineScope.launch {
                loginVM.updateLoginState(LoginUiState.Loading)
                val createAccountResult = authenticationClient.createAccount(email, password)
                val loginState = when (createAccountResult) {
                    is Result.Successful<Unit> -> LoginUiState.AccountCreated
                    is Result.Unsuccessful<GeneralError> -> LoginUiState.Error(createAccountResult.error)
                }
                loginVM.updateLoginState(loginState)
            }
        }
    )
}

@Composable
fun PanelFormCreateAccount(
    loginFormCreateAccountState: LoginFormCreateAccountUiState,
    validateFormCreateAccount: (String, String, String) -> Unit,
    createAccount: (String, String) -> Unit
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
                    loginFormCreateAccountState = loginFormCreateAccountState,
                    validateFormCreateAccount = validateFormCreateAccount,
                    createAccount = createAccount
                )
            }
        }
        Box(modifier = Modifier.height(250.dp))
    }
}

@Composable
fun FormCreateAccount(
    loginFormCreateAccountState: LoginFormCreateAccountUiState,
    validateFormCreateAccount: (String, String, String) -> Unit,
    createAccount: (String, String) -> Unit
) {
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
            errors = loginFormCreateAccountState.emailUiState.state.let { emailState ->
                when (emailState) {
                    is InputState.Error -> emailState.errors.map { getErrorEmail(it) }
                    InputState.Init, InputState.Success -> listOf()
                }
            }
        ) {
            validateFormCreateAccount(
                it,
                loginFormCreateAccountState.passwordUiState.value,
                loginFormCreateAccountState.passwordRepeatUiState.value
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        PasswordOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            label = R.string.txt_label_password,
            password = loginFormCreateAccountState.passwordUiState.value,
            leadingIcon = Icons.Default.Lock,
            errors = loginFormCreateAccountState.passwordUiState.state.let { passwordState ->
                when (passwordState) {
                    is InputState.Error -> passwordState.errors.map { getErrorPassword(it) }
                    InputState.Init, InputState.Success -> listOf()
                }
            }
        ) {
            validateFormCreateAccount(
                loginFormCreateAccountState.emailUiState.value,
                it,
                loginFormCreateAccountState.passwordRepeatUiState.value
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        PasswordOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            label = R.string.txt_label_password_repeat,
            password = loginFormCreateAccountState.passwordRepeatUiState.value,
            leadingIcon = Icons.Default.Lock,
            errors = loginFormCreateAccountState.passwordRepeatUiState.state.let { passwordRepeatState ->
                when (passwordRepeatState) {
                    is InputState.Error -> passwordRepeatState.errors.map {
                        stringResource(R.string.err_password_is_not_same)
                    }

                    InputState.Init, InputState.Success -> listOf()
                }
            }
        ) {
            validateFormCreateAccount(
                loginFormCreateAccountState.emailUiState.value,
                loginFormCreateAccountState.passwordUiState.value,
                it
            )
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
                    createAccount(
                        loginFormCreateAccountState.emailUiState.value,
                        loginFormCreateAccountState.passwordUiState.value
                    )
                }
            )
        }
    }
}