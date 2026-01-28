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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.asm.taken.R
import com.asm.taken.model.InputState
import com.asm.taken.model.CreateAccountFormState
import com.asm.taken.model.CreateAccountProcessState
import com.asm.taken.model.CreateAccountUIState
import com.asm.taken.ui.CircularProgressDialog
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultOutlinedTextFieldLI
import com.asm.taken.ui.ErrorProcessComponent
import com.asm.taken.ui.PasswordOutlinedTextField
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.utils.getErrorEmail
import com.asm.taken.utils.getErrorPassword
import com.asm.taken.vm.CreateAccountVM

@Composable
fun CreateAccountPage(
    createAccountVM: CreateAccountVM,
    snackBarHostState: SnackbarHostState,
    popBackStack: () -> Unit
) {
    val createAccountUIState: CreateAccountUIState by createAccountVM.createAccountUIState.collectAsStateWithLifecycle()

    BackHandler {
        createAccountVM.resetFormState()
        popBackStack()
    }
    PanelFormCreateAccount(
        createAccountFormState = createAccountUIState.createAccountFormState,
        validateFormCreateAccount = createAccountVM::validateFormCreateAccount,
        createAccount = createAccountVM::createAccount
    )
    ProcessSection(
        createAccountProcessState = createAccountUIState.createAccountProcessState,
        snackBarHostState = snackBarHostState,
        accountCreated = {
            LaunchedEffect(true) {
                popBackStack()
                createAccountVM.resetFormState()
                createAccountVM.resetProcessState()
            }
        },
        retryProcess = {
            createAccountVM.createAccount(
                email = createAccountUIState.createAccountFormState.emailUiState.value,
                password = createAccountUIState.createAccountFormState.passwordUiState.value
            )
        },
        resetProcess = createAccountVM::resetProcessState
    )
}

@Composable
fun ProcessSection(
    createAccountProcessState: CreateAccountProcessState,
    snackBarHostState: SnackbarHostState,
    accountCreated: @Composable () -> Unit,
    retryProcess: () -> Unit,
    resetProcess: () -> Unit
) {
    when (createAccountProcessState) {
        CreateAccountProcessState.AccountProcessCreated -> accountCreated()
        is CreateAccountProcessState.Error -> ErrorProcessComponent(
            generalError = createAccountProcessState.generalError,
            snackBarHostState = snackBarHostState,
            retryProcess = retryProcess,
            resetProcess = resetProcess
        )
        CreateAccountProcessState.Loading -> CircularProgressDialog()
        CreateAccountProcessState.Idle -> return
    }
}

@Composable
fun PanelFormCreateAccount(
    createAccountFormState: CreateAccountFormState,
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
                    createAccountFormState = createAccountFormState,
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
    createAccountFormState: CreateAccountFormState,
    validateFormCreateAccount: (String, String, String) -> Unit,
    createAccount: (String, String) -> Unit
) {
    Column {
        DefaultOutlinedTextFieldLI(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            value = createAccountFormState.emailUiState.value,
            label = R.string.txt_label_email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            leadingIcon = Icons.Default.Mail,
            cdLeadingIcon = null,
            errors = createAccountFormState.emailUiState.state.let { emailState ->
                when (emailState) {
                    is InputState.Error -> emailState.errors.map { getErrorEmail(it) }
                    InputState.Idle, InputState.Success -> listOf()
                }
            }
        ) {
            validateFormCreateAccount(
                it,
                createAccountFormState.passwordUiState.value,
                createAccountFormState.passwordRepeatUiState.value
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        PasswordOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            label = R.string.txt_label_password,
            password = createAccountFormState.passwordUiState.value,
            leadingIcon = Icons.Default.Lock,
            errors = createAccountFormState.passwordUiState.state.let { passwordState ->
                when (passwordState) {
                    is InputState.Error -> passwordState.errors.map { getErrorPassword(it) }
                    InputState.Idle, InputState.Success -> listOf()
                }
            }
        ) {
            validateFormCreateAccount(
                createAccountFormState.emailUiState.value,
                it,
                createAccountFormState.passwordRepeatUiState.value
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        PasswordOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            label = R.string.txt_label_password_repeat,
            password = createAccountFormState.passwordRepeatUiState.value,
            leadingIcon = Icons.Default.Lock,
            errors = createAccountFormState.passwordRepeatUiState.state.let { passwordRepeatState ->
                when (passwordRepeatState) {
                    is InputState.Error -> passwordRepeatState.errors.map {
                        stringResource(R.string.err_password_is_not_same)
                    }

                    InputState.Idle, InputState.Success -> listOf()
                }
            }
        ) {
            validateFormCreateAccount(
                createAccountFormState.emailUiState.value,
                createAccountFormState.passwordUiState.value,
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
                enable = createAccountFormState.emailUiState.state is InputState.Success
                        && createAccountFormState.passwordUiState.state is InputState.Success
                        && createAccountFormState.passwordRepeatUiState.state is InputState.Success,
                onClickButton = {
                    createAccount(
                        createAccountFormState.emailUiState.value,
                        createAccountFormState.passwordUiState.value
                    )
                }
            )
        }
    }
}