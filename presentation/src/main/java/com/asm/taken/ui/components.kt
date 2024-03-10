package com.asm.taken.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.asm.taken.R
import com.asm.taken.ui.theme.Purple80
import com.asm.taken.ui.theme.PurpleGrey80

@Composable
fun AppTitle(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = Color.Black
) {
    Text(
        modifier = modifier,
        fontSize = dimensionResource(id = R.dimen.app_name_size).value.sp,
        text = text,
        fontFamily = puzzleFontFamily,
        fontWeight = FontWeight.Bold,
        textAlign = textAlign,
        color = color
    )
}

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
fun PuzzleDefaultText(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PuzzleDefaultOutlinedText(
    modifier: Modifier,
    value: String = "",
    @StringRes label: Int,
    leadingIcon: ImageVector,
    @StringRes cdLeadingIcon: Int,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onValueChanged: (String) -> Unit,
    isPasswordVisible: Boolean? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        modifier = modifier,
        value = value,
        onValueChange = onValueChanged,
        label = {
            PuzzleDefaultText(text = stringResource(id = label))
        },
        textStyle = TextStyle(fontFamily = puzzleFontFamily),
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = stringResource(id = cdLeadingIcon),
                tint = colorResource(id = R.color.purple_200)
            )
        },
        keyboardOptions = keyboardOptions,
        visualTransformation = if (isPasswordVisible == null || isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = trailingIcon,
        singleLine = true
    )
}

@Composable
fun PuzzleDefaultOutlinedTrailingIcon(
    modifier: Modifier,
    value: String = "",
    @StringRes label: Int,
    leadingIcon: ImageVector,
    trailingIcon: ImageVector? = null,
    @StringRes cdLeadingIcon: Int,
    @StringRes cdTrailingIcon: Int,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onClickTrailingIcon: (() -> Unit)? = null,
    isPasswordVisible: Boolean? = null,
    onValueChanged: (String) -> Unit,
) {
    PuzzleDefaultOutlinedText(
        modifier = modifier,
        value = value,
        label = label,
        leadingIcon = leadingIcon,
        keyboardOptions = keyboardOptions,
        onValueChanged = onValueChanged,
        isPasswordVisible = isPasswordVisible,
        cdLeadingIcon = cdLeadingIcon
    ) {
        if (trailingIcon != null) {
            IconButton(onClick = { onClickTrailingIcon?.invoke() }) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = stringResource(
                        id = cdTrailingIcon
                    )
                )
            }
        }
    }
}

@Composable
fun PuzzleDefaultButton(
    modifier: Modifier = Modifier,
    text: String,
    onClickButton: (() -> Unit)? = null
) {
    Button(
        modifier = modifier,
        onClick = { onClickButton?.invoke() },
        enabled = true,
        colors = ButtonDefaults.buttonColors(
            containerColor = Purple80,
            contentColor = Color.White,
            disabledContainerColor = PurpleGrey80,
            disabledContentColor = Color.White
        )
    ) {
        PuzzleDefaultText(
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