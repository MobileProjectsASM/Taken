package com.asm.taken.ui.page.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.asm.taken.R
import com.asm.taken.model.CountriesUiState
import com.asm.taken.model.CountryUiState
import com.asm.taken.model.InputState
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultOutlinedTextFieldLI
import com.asm.taken.ui.DefaultText
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.ui.puzzleFontFamily
import com.asm.taken.utils.MessageResolver
import com.asm.taken.vm.LoginVM

@Composable
fun PhoneAuthPage(
    loginVM: LoginVM,
    snackBarHostState: SnackbarHostState
) {
    val countriesUiState: CountriesUiState by loginVM.countriesUiState.collectAsStateWithLifecycle()

    when (countriesUiState) {
        is CountriesUiState.Failure -> ErrorCountries(
            message = (countriesUiState as CountriesUiState.Failure).errorMessage,
            snackBarHostState = snackBarHostState,
            loginVM
        )
        CountriesUiState.Loading -> CircularProgressIndicator(
            modifier = Modifier.width(64.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
        is CountriesUiState.Successful -> PanelAuthPhone(
            countriesUiState = (countriesUiState as CountriesUiState.Successful).countriesInfo,
            loginVM
        )
    }
}

@Composable
fun ErrorCountries(
    message: String,
    snackBarHostState: SnackbarHostState,
    loginVM: LoginVM
) {
    LaunchedEffect(true) {
        snackBarHostState.showSnackbar(message, withDismissAction = true)
    }
    PanelAuthPhone(null, loginVM)
}

@Composable
fun PanelAuthPhone(countriesUiState: List<CountryUiState>?, loginVM: LoginVM) {
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
                    text = stringResource(id = R.string.txt_ttl_login_with_phone_number)
                )
                Spacer(modifier = Modifier.height(50.dp))
                FormPhoneNumber(
                    countriesUiState = countriesUiState,
                    loginVM
                ) {

                }
            }
        }
        Box(modifier = Modifier.height(250.dp))
    }
}

@Composable
fun FormPhoneNumber(
    countriesUiState: List<CountryUiState>?,
    loginVM: LoginVM,
    onSubmit: () -> Unit
) {
    val loginPhoneFormState by loginVM.loginFormPhoneUiState.collectAsStateWithLifecycle()
    val phoneCodeErrors: List<String> = when (val phoneCodeUiState = loginPhoneFormState.phoneCodeUiState.state) {
        is InputState.Error -> phoneCodeUiState.errors.map { stringResource(MessageResolver.getErrorPhoneCode(it)) }
        InputState.Init, InputState.Success -> listOf()
    }
    val phoneNumberErrors: List<String> = when (val phoneNumberUiState = loginPhoneFormState.phoneNumberUiState.state) {
        is InputState.Error -> phoneNumberUiState.errors.map { stringResource(MessageResolver.getErrorPhoneNumber(it)) }
        InputState.Init, InputState.Success -> listOf()
    }
    Column {
        PhoneCodeInput(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            countriesUiState = countriesUiState,
            codeValue = loginPhoneFormState.phoneCodeUiState.value,
            phoneNumberValue = loginPhoneFormState.phoneNumberUiState.value,
            phoneCodeErrors = phoneCodeErrors,
            loginVM = loginVM
        )
        Spacer(modifier = Modifier.height(10.dp))
        DefaultOutlinedTextFieldLI(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            value = loginPhoneFormState.phoneNumberUiState.value, 
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            label = R.string.txt_label_pn, 
            leadingIcon = Icons.Default.Phone, 
            cdLeadingIcon = R.string.txt_cd_li_phone_number,
            errors = phoneNumberErrors
        ) {
            loginVM.validatePhoneNumberForm(loginPhoneFormState.phoneCodeUiState.value, it)
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DefaultButton(
                text = stringResource(id = R.string.txt_btn_send),
                enable = loginPhoneFormState.phoneNumberUiState.state is InputState.Success && loginPhoneFormState.phoneCodeUiState.state is InputState.Success,
                onClickButton = onSubmit
            )
        }
    }
}

@Composable
fun PhoneCodeInput(
    modifier: Modifier = Modifier,
    countriesUiState: List<CountryUiState>?, 
    codeValue: String,
    phoneNumberValue: String,
    phoneCodeErrors: List<String>,
    loginVM: LoginVM
) {
    if (countriesUiState == null) {
        DefaultOutlinedTextFieldLI(
            modifier = modifier,
            value = codeValue,
            label = R.string.txt_label_code,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = Icons.Default.Pin,
            cdLeadingIcon = R.string.txt_cd_li_code,
            errors = phoneCodeErrors
        ) {
            loginVM.validatePhoneNumberForm(it, phoneNumberValue)
        }
    } else {
        var showChosePhoneCodeDialog by rememberSaveable {
            mutableStateOf(false)
        }

        ChoosePhoneCodeDialog(showDialog = showChosePhoneCodeDialog, countriesUiState = countriesUiState) {
            showChosePhoneCodeDialog = false
            loginVM.validatePhoneNumberForm(it, phoneNumberValue)
        }
        DefaultOutlinedTextFieldLI(
            modifier = modifier,
            value = codeValue,
            label = R.string.txt_label_code,
            leadingIcon = Icons.Default.Pin,
            cdLeadingIcon = R.string.txt_cd_li_code,
            readOnly = true,
            errors = phoneCodeErrors,
            onClickable = {
                showChosePhoneCodeDialog = true
            }
        )
    }
}

@Composable
fun ChoosePhoneCodeDialog(
    showDialog: Boolean,
    countriesUiState: List<CountryUiState>,
    onPhoneCodeSelected: (String) -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = {
            onPhoneCodeSelected("")
        }) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .padding(
                            horizontal = 16.dp,
                            vertical = 10.dp
                        )
                        .height(500.dp)
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = dimensionResource(
                            id = R.dimen.title_text_size
                        ).value.sp,
                        text = stringResource(id = R.string.txt_ttl_choose_phone_code),
                        fontFamily = puzzleFontFamily,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn {
                        items(countriesUiState) {
                            ItemCountry(countryUiState = it) { countryUiState ->
                                onPhoneCodeSelected(countryUiState.phoneCode)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemCountry(countryUiState: CountryUiState, onClick: (CountryUiState) -> Unit) {
    Row(
        modifier = Modifier
            .padding()
            .clickable { onClick(countryUiState) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            modifier = Modifier.size(50.dp),
            model = countryUiState.flag,
            contentDescription = stringResource(id = R.string.txt_cd_country_flag)
        )
        Spacer(modifier = Modifier.width(10.dp))
        DefaultText(
            modifier = Modifier.fillMaxWidth(),
            text = countryUiState.country
        )
    }
}