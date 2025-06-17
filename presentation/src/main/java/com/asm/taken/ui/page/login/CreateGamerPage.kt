package com.asm.taken.ui.page.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attribution
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Card
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.asm.taken.R
import com.asm.taken.model.CountriesUiState
import com.asm.taken.model.CountryUiState
import com.asm.taken.model.InputAgeError
import com.asm.taken.model.InputAliasError
import com.asm.taken.model.InputCountryError
import com.asm.taken.model.InputState
import com.asm.taken.model.LoginFormCreateGamerUiState
import com.asm.taken.ui.CircularProgressDialog
import com.asm.taken.ui.DefaultOutlinedTextFieldLI
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.ui.puzzleFontFamily
import com.asm.taken.utils.MessageResolver
import com.asm.taken.vm.LoginVM

@Composable
fun CreateGamerPage(
    loginVM: LoginVM,
    navController: NavController,
    messageResolver: MessageResolver,
    snackBarHostState: SnackbarHostState
) {
    val countriesUiState: CountriesUiState by loginVM.countriesUiState.collectAsStateWithLifecycle()
    CreateGamerSection(
        loginVM = loginVM,
        messageResolver = messageResolver,
        countriesUiState = countriesUiState,
        snackBarHostState = snackBarHostState
    )
}

@Composable
fun CreateGamerSection(
    loginVM: LoginVM,
    snackBarHostState: SnackbarHostState,
    countriesUiState: CountriesUiState,
    messageResolver: MessageResolver
) {
    when (countriesUiState) {
        is CountriesUiState.Failure -> ErrorCountries(
            loginVM = loginVM,
            snackBarHostState = snackBarHostState,
            messageResolver = messageResolver
        )

        CountriesUiState.Loading -> CircularProgressDialog()

        is CountriesUiState.Successful -> PanelCreateGamer(
            loginVM = loginVM,
            countriesUiState = countriesUiState.countriesInfo,
            messageResolver = messageResolver
        )
    }
}

@Composable
fun ErrorCountries(
    loginVM: LoginVM,
    snackBarHostState: SnackbarHostState,
    messageResolver: MessageResolver
) {
    LaunchedEffect(true) {
        snackBarHostState.showSnackbar(messageResolver.getMessage(R.string.err_get_countries))
    }
    PanelCreateGamer(
        loginVM = loginVM,
        countriesUiState = null,
        messageResolver = messageResolver
    )
}

@Composable
fun PanelCreateGamer(
    loginVM: LoginVM,
    countriesUiState: List<CountryUiState>?,
    messageResolver: MessageResolver,
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
                    countriesUiState = countriesUiState,
                    messageResolver = messageResolver
                ) {

                }
            }
        }
        Box(modifier = Modifier.height(250.dp))
    }
}

@Composable
fun FormCreateGamer(
    loginVM: LoginVM,
    countriesUiState: List<CountryUiState>?,
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
        CountryInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            countriesUiState = countriesUiState,
            value = loginFormCreateGamerState.countryUiState.value,
            countryErrors = countryErrors
        )
    }
}

@Composable
fun CountryInput(
    modifier: Modifier = Modifier,
    countriesUiState: List<CountryUiState>?,
    value: String,
    countryErrors: List<String>
) {
    if (countriesUiState == null) {
        DefaultOutlinedTextFieldLI(
            modifier = modifier,
            value = value,
            label = R.string.txt_label_country,
            leadingIcon = Icons.Default.Public,
            cdLeadingIcon = null,
            errors = countryErrors
        ) {

        }
    } else {
        var showChoseCountryDialog by rememberSaveable {
            mutableStateOf(false)
        }

        if (showChoseCountryDialog) {
            ChooseCountryDialog(
                country = value,
                countriesUiState = countriesUiState
            ) {
                showChoseCountryDialog = false
            }
        }
        DefaultOutlinedTextFieldLI(
            modifier = modifier,
            value = value,
            label = R.string.txt_label_country,
            leadingIcon = Icons.Default.Public,
            cdLeadingIcon = null,
            readOnly = true,
            errors = countryErrors,
            onClickable = {
                showChoseCountryDialog = true
            }
        )
    }
}

@Composable
fun ChooseCountryDialog(
    country: String,
    countriesUiState: List<CountryUiState>,
    onCountrySelected: (String) -> Unit
) {
    Dialog(
        onDismissRequest = { onCountrySelected(country) }
    ) {
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .height(400.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    fontSize = dimensionResource(
                        id = R.dimen.title_text_size
                    ).value.sp,
                    text = stringResource(id = R.string.txt_ttl_choose_country),
                    fontFamily = puzzleFontFamily,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn {
                    items(countriesUiState) {
                        ItemCountry(countryUiState = it) { countryUiState ->
                            onCountrySelected(countryUiState.country)
                        }
                    }
                }
            }
        }
    }
}