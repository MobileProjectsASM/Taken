package com.asm.taken.ui.page.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attribution
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.asm.taken.R
import com.asm.taken.model.InputAgeError
import com.asm.taken.model.InputAliasError
import com.asm.taken.model.InputCountryError
import com.asm.taken.model.InputState
import com.asm.taken.model.LoginFormCreateGamerUiState
import com.asm.taken.ui.DefaultOutlinedTextFieldLI
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.utils.MessageResolver
import com.asm.taken.vm.LoginVM

@Composable
fun CreateGamerPage(
    loginVM: LoginVM,
    navController: NavController,
    messageResolver: MessageResolver,
    snackBarHostState: SnackbarHostState
) {
    CreateGamerSection(
        loginVM = loginVM,
        messageResolver = messageResolver
    )
}

@Composable
fun CreateGamerSection(
    loginVM: LoginVM,
    messageResolver: MessageResolver
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.height(250.dp))
        PanelCreateGamer(
            loginVM = loginVM,
            messageResolver = messageResolver
        )
        Box(modifier = Modifier.height(250.dp))
    }
}

@Composable
fun PanelCreateGamer(
    loginVM: LoginVM,
    messageResolver: MessageResolver,
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
                text = stringResource(id = R.string.txt_ttl_form_create_gamer)
            )
            Spacer(modifier = Modifier.height(50.dp))
            FormCreateGamer(
                loginVM = loginVM,
                messageResolver = messageResolver
            ) {

            }
        }
    }
}

@Composable
fun FormCreateGamer(
    loginVM: LoginVM,
    messageResolver: MessageResolver,
    createGamer: () -> Unit
) {
    val loginFormCreateGamerState: LoginFormCreateGamerUiState by loginVM.loginFormCreateGamerState.collectAsStateWithLifecycle()
    val aliasErrors: List<String> = when (val aliasUiState = loginFormCreateGamerState.aliasUiState.state) {
        is InputState.Error<InputAliasError> -> aliasUiState.errors.map { messageResolver.getErrorAlias(it) }
        InputState.Init, InputState.Success -> listOf()
    }
    val ageErrors: List<String> = when (val ageUiState = loginFormCreateGamerState.ageUiState.state) {
        is InputState.Error<InputAgeError> -> ageUiState.errors.map { messageResolver.getErrorAge(it) }
        InputState.Init, InputState.Success -> listOf()
    }
    val countryErrors: List<String> = when (val countryState = loginFormCreateGamerState.countryUiState.state) {
        is InputState.Error<InputCountryError> -> countryState.errors.map { messageResolver.getErrorCountry(it) }
        InputState.Init, InputState.Success -> listOf()
    }
    Column {
        DefaultOutlinedTextFieldLI(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            value = loginFormCreateGamerState.aliasUiState.value,
            label = R.string.txt_label_alias,
            leadingIcon = Icons.Default.Person,
            cdLeadingIcon = null,
            errors = aliasErrors
        ) {

        }
        Spacer(modifier = Modifier.height(10.dp))
        DefaultOutlinedTextFieldLI(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            value = loginFormCreateGamerState.ageUiState.value,
            label = R.string.txt_label_age,
            leadingIcon = Icons.Default.Attribution,
            cdLeadingIcon = null,
            errors = ageErrors
        )
        Spacer(modifier = Modifier.height(10.dp))

    }
}