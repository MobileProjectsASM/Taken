package com.asm.taken.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asm.taken.R
import com.asm.taken.ui.theme.Purple40
import com.asm.taken.ui.theme.Purple80
import com.asm.taken.ui.theme.PurpleGrey80

@Composable
fun PuzzleGeneralTitle(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center
) {
    Text(
        modifier = modifier,
        fontSize = dimensionResource(
            id = R.dimen.title_text_size
        ).value.sp,
        text = text,
        fontFamily = puzzleFontFamily,
        fontWeight = FontWeight.Bold,
        textAlign = textAlign
    )
}

@Composable
fun DefaultText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(
        modifier = modifier,
        fontSize = dimensionResource(
            id = R.dimen.default_text_size
        ).value.sp,
        text = text,
        fontFamily = puzzleFontFamily,
        textAlign = textAlign
    )
}

@Composable
fun DefaultOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    @StringRes label: Int,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    message: String? = null,
    isError: Boolean = false,
    readOnly: Boolean = false,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        label = stringResource(id = label).toDefaultText(),
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        onValueChange = onValueChange,
        textStyle = TextStyle(fontFamily = puzzleFontFamily),
        supportingText = message?.toDefaultText(),
        isError = isError,
        readOnly = readOnly,
        singleLine = true,
    )
}

@Composable
fun DefaultOutlinedTextFieldLI(
    modifier: Modifier = Modifier,
    value: String,
    @StringRes label: Int,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: ImageVector,
    @StringRes cdLeadingIcon: Int,
    trailingIcon: @Composable (() -> Unit)? = null,
    message: String? = null,
    isError: Boolean = false,
    readOnly: Boolean = false,
    onValueChange: (String) -> Unit,
) {
    DefaultOutlinedTextField(
        modifier = modifier,
        value = value,
        label = label,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = stringResource(id = cdLeadingIcon),
                tint = colorResource(id = R.color.purple_200)
            )
        },
        trailingIcon = trailingIcon,
        message = message,
        isError = isError,
        readOnly = readOnly,
        onValueChange = onValueChange,
    )
}

@Composable
fun DefaultOutlinedTextFieldTI(
    modifier: Modifier = Modifier,
    value: String = "",
    @StringRes label: Int,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingIcon: ImageVector,
    @StringRes cdLeadingIcon: Int,
    trailingIcon: ImageVector,
    @StringRes cdTrailingIcon: Int,
    onClickTrailingIcon: (() -> Unit)? = null,
    message: String? = null,
    isError: Boolean = false,
    onValueChange: (String) -> Unit,
) {
    DefaultOutlinedTextFieldLI(
        modifier = modifier,
        value = value,
        label = label,
        keyboardOptions = keyboardOptions,
        visualTransformation = visualTransformation,
        leadingIcon = leadingIcon,
        cdLeadingIcon = cdLeadingIcon,
        trailingIcon = {
            IconButton(onClick = { onClickTrailingIcon?.invoke() }) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = stringResource(
                        id = cdTrailingIcon
                    )
                )
            }
        },
        message = message,
        isError = isError,
        onValueChange = onValueChange,
    )
}

@Composable
fun PasswordOutlinedTextField(
    modifier: Modifier = Modifier,
    @StringRes label: Int,
    password: String,
    leadingIcon: ImageVector,
    passwordErrorMessage: String? = null,
    onPasswordChange: (String) -> Unit
) {
    var isPasswordVisible: Boolean by rememberSaveable { mutableStateOf(false) }
    DefaultOutlinedTextFieldTI(
        modifier = modifier,
        value = password,
        label = label,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        leadingIcon = leadingIcon,
        cdLeadingIcon = R.string.txt_cd_li_password,
        trailingIcon = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
        cdTrailingIcon = R.string.txt_cd_ti_password,
        onClickTrailingIcon = {
            isPasswordVisible = !isPasswordVisible
        },
        message = passwordErrorMessage,
        isError = passwordErrorMessage != null,
        onValueChange = onPasswordChange
    )
}

@Composable
fun DefaultButton(
    modifier: Modifier = Modifier,
    enable: Boolean = true,
    text: String,
    onClickButton: (() -> Unit)? = null
) {
    Button(
        modifier = modifier,
        onClick = { onClickButton?.invoke() },
        enabled = enable,
        colors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.purple_200),
            contentColor = Color.White,
            disabledContainerColor = Purple80,
            disabledContentColor = Color.White
        )
    ) {
        DefaultText(
            text = text,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DefaultTextButton(
    modifier: Modifier = Modifier,
    text: String,
    onClickButton: (() -> Unit)? = null
) {
    TextButton(
        modifier = modifier,
        onClick = { onClickButton?.invoke() },
        colors = ButtonDefaults.textButtonColors(
            contentColor = Color.Black
        )
    ) {
        DefaultText(
            text = text,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DefaultImageButton(
    imageSize: Dp = 50.dp,
    @DrawableRes iconButton: Int,
    @StringRes cdIconButton: Int,
    onClickButton: (() -> Unit)? = null
) {
    TextButton(modifier = Modifier.size(70.dp), onClick = { onClickButton?.invoke() }) {
        Image(
            modifier = Modifier.size(imageSize),
            painter = painterResource(id = iconButton),
            contentDescription = stringResource(id = cdIconButton)
        )
    }
}

fun String.toDefaultText(
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start
): @Composable (() -> Unit) = {
    DefaultText(
        text = this,
        modifier = modifier,
        textAlign = textAlign
    )
}