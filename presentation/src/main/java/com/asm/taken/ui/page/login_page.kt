package com.asm.taken.ui.page

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.asm.taken.R
import com.asm.taken.model.FormUiState
import com.asm.taken.model.PasswordUiState
import com.asm.taken.model.UserIdUiState
import com.asm.taken.ui.DefaultButton
import com.asm.taken.ui.DefaultImageButton
import com.asm.taken.ui.DefaultOutlinedTextFieldLI
import com.asm.taken.ui.DefaultOutlinedTextFieldTI
import com.asm.taken.ui.DefaultText
import com.asm.taken.ui.DefaultTextButton
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.ui.puzzleFontFamily
import com.asm.taken.utils.ResourceResolver
import com.asm.taken.vm.LoginVM


@Composable
fun LoginPage(
    loginVM: LoginVM,
    resourceResolver: ResourceResolver,
    navController: NavHostController,
    signInWithGoogle: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val configuration = LocalConfiguration.current
        val radius = configuration.screenWidthDp
        Box(
            modifier = Modifier
                .height(500.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = (radius * 0.8).dp))
                .background(color = colorResource(id = R.color.purple_200).copy(alpha = 0.4f))
        )
        Box(
            modifier = Modifier
                .height(500.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = radius.dp))
                .background(color = colorResource(id = R.color.purple_200))
        )
        Box(
            modifier = Modifier
                .height(270.dp)
                .fillMaxWidth()
        ) {
            Image(
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.Center),
                painter = painterResource(id = R.drawable.puzzle),
                contentDescription = stringResource(
                    id = R.string.txt_cd_icon_app
                )
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                fontSize = dimensionResource(id = R.dimen.app_name_size).value.sp,
                text = stringResource(id = R.string.app_name),
                fontFamily = puzzleFontFamily,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.height(250.dp))
            CardLogin(loginVM, resourceResolver)
            CardSocialMedia(signInWithGoogle)
            Box(modifier = Modifier.height(250.dp))
        }
    }
}

@Composable
fun CardLogin(loginVM: LoginVM, resourceResolver: ResourceResolver) {
    var isPasswordVisible: Boolean by rememberSaveable { mutableStateOf(false) }
    val formUiState: FormUiState by loginVM.formUiStateSTF.collectAsState()
    val userIdMessage = getUserIdMessage(resourceResolver, formUiState.userIdUiState)
    val passwordMessage = getPasswordMessage(resourceResolver, formUiState.passwordUiState)
    val isBtnLoginEnable = formUiState.userIdUiState is UserIdUiState.IsValid && formUiState.passwordUiState is PasswordUiState.IsValid
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
                text = stringResource(id = R.string.txt_title_login_dialog)
            )
            DefaultText(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.txt_inf_login_dialog),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(50.dp))
            DefaultOutlinedTextFieldLI(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                value = formUiState.userIdUiState.value ?: "",
                label = R.string.txt_label_user_id_login,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = Icons.Default.Person,
                cdLeadingIcon = R.string.txt_cd_icon_user_id,
                message = userIdMessage,
                isError = userIdMessage != null,
            ) {
                loginVM.updateDataLogin(it, formUiState.passwordUiState.value)
            }
            DefaultOutlinedTextFieldTI(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                value = formUiState.passwordUiState.value ?: "",
                label = R.string.txt_label_password,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                leadingIcon = Icons.Default.Lock,
                cdLeadingIcon = R.string.txt_cd_icon_user_id,
                trailingIcon = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                cdTrailingIcon = R.string.txt_cd_trailing_icon_info_,
                onClickTrailingIcon = {
                    isPasswordVisible = !isPasswordVisible
                },
                message = passwordMessage,
                isError = passwordMessage != null,
            ) {
                loginVM.updateDataLogin(formUiState.userIdUiState.value, it)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DefaultButton(
                    text = stringResource(id = R.string.txt_btn_login),
                    enable = isBtnLoginEnable
                ) {

                }
            }
        }
    }
}

@Composable
fun CardSocialMedia(signInWithGoogle: () -> Unit) {
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
                    cdIconButton = R.string.txt_cd_icon_button
                )
                DefaultImageButton(
                    imageSize = 40.dp,
                    iconButton = R.drawable.google,
                    cdIconButton = R.string.txt_cd_icon_button,
                    onClickButton = signInWithGoogle
                )
                DefaultImageButton(
                    imageSize = 40.dp,
                    iconButton = R.drawable.phone,
                    cdIconButton = R.string.txt_cd_icon_button
                )
            }
            DefaultTextButton(
                text = stringResource(id = R.string.txt_btn_with_email)
            ) {

            }
        }
    }
}

fun getUserIdMessage(resourceResolver: ResourceResolver, userIdUiState: UserIdUiState): String? = when (userIdUiState) {
    UserIdUiState.Init, is UserIdUiState.IsValid -> null
    UserIdUiState.IsEmpty -> resourceResolver.getString(R.string.err_empty_field)
}

fun getPasswordMessage(resourceResolver: ResourceResolver, passwordUiState: PasswordUiState): String? = when (passwordUiState) {
    PasswordUiState.Init, is PasswordUiState.IsValid -> null
    PasswordUiState.IsEmpty -> resourceResolver.getString(R.string.err_empty_field)
    is PasswordUiState.IsInvalid -> resourceResolver.getString(R.string.err_does_not_meet_pattern_password)
}