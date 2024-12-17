package com.asm.taken.ui

import android.util.Log
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.interaction.collectIsFocusedAsState
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.asm.taken.R
import com.asm.taken.ui.theme.Purple80

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
    color: Color = Color.Unspecified,
    @DimenRes fontSize: Int = R.dimen.default_text_size,
    textAlign: TextAlign = TextAlign.Start
) {
    Text(
        modifier = modifier,
        color = color,
        fontSize = dimensionResource(
            id = fontSize
        ).value.sp,
        text = text,
        fontFamily = puzzleFontFamily,
        textAlign = textAlign,
        lineHeight = 15.sp
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
    errors: List<String> = listOf(),
    readOnly: Boolean = false,
    onClickable: (() -> Unit)? = null,
    onValueChange: ((String) -> Unit)? = null,
) {
    val mutableInteractionSource = remember { MutableInteractionSource() }
    if (onClickable != null) {
        LaunchedEffect(mutableInteractionSource) {
            mutableInteractionSource.interactions.collect { interaction ->
                Log.d("INTERACTION", "DefaultOutlinedTextField: $interaction")
                if (interaction is PressInteraction.Release) {
                    onClickable.invoke()
                }
            }
        }
    }

    Column {
        OutlinedTextField(
            modifier = modifier,
            value = value,
            label = stringResource(id = label).toDefaultText(),
            keyboardOptions = keyboardOptions,
            visualTransformation = visualTransformation,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            onValueChange = onValueChange ?: {},
            textStyle = TextStyle(fontFamily = puzzleFontFamily),
            isError = errors.isNotEmpty(),
            readOnly = readOnly,
            singleLine = true,
            interactionSource = mutableInteractionSource
        )
        if (errors.isNotEmpty()) {
            Spacer(modifier = Modifier.height(5.dp))
            errors.forEach { error ->
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp),
                ) {
                    Icon(
                        modifier = Modifier.size(size = 16.dp),
                        imageVector = Icons.Outlined.Cancel,
                        contentDescription = stringResource(R.string.txt_cd_error_input_icon),
                        tint = OutlinedTextFieldDefaults.colors().errorIndicatorColor
                    )
                    DefaultText(
                        error,
                        color = OutlinedTextFieldDefaults.colors().errorIndicatorColor,
                        fontSize = R.dimen.small_text_size
                    )
                }
            }
        }
    }
}

@Composable
fun OtpInput(
    size: Dp = 40.dp,
    text: TextFieldValue,
    colorScheme: ColorScheme,
    focusRequester: FocusRequester,
    interactionSource: MutableInteractionSource = remember {
        MutableInteractionSource()
    },
    onValueChange: (TextFieldValue) -> Unit,
) {
    val focused by interactionSource.collectIsFocusedAsState()
    BasicTextField(
        modifier = Modifier
            .size(size)
            .border(
                width = 1.5.dp,
                color = if (focused) colorScheme.primary else Color.Gray,
                shape = RoundedCornerShape(8.dp)
            )
            .background(colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
            .focusRequester(focusRequester),
        value = text,
        onValueChange = onValueChange,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        textStyle = TextStyle(
            fontFamily = puzzleFontFamily,
            textAlign = TextAlign.Center
        ),
        singleLine = true,
        interactionSource = interactionSource,
        cursorBrush = SolidColor(colorScheme.primary)
    ) { innerTextField ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            innerTextField()
        }
    }
}

@Composable
fun OtpMultiple(
    modifier: Modifier = Modifier,
    size: Dp,
    numberInputs: Int = 6,
    errors: List<String> = listOf(),
    onChange: (String) -> Unit
) {
    val otpInputStates = rememberSaveable {
        List(numberInputs) {
            Pair(mutableStateOf(TextFieldValue("", TextRange(0, 0))), FocusRequester())
        }
    }

    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally)
        ) {
            for (currentPosition in 0..<numberInputs) {
                val textInputMutableState = otpInputStates[currentPosition].first
                val focusRequester = otpInputStates[currentPosition].second
                OtpInput(
                    size = size,
                    text = textInputMutableState.value,
                    colorScheme = MaterialTheme.colorScheme,
                    focusRequester = focusRequester,
                ) { newValue ->
                    if (newValue.text.isEmpty()) {
                        textInputMutableState.value = newValue
                        if (currentPosition > 0) {
                            val beforeFocusRequester = otpInputStates[currentPosition - 1].second
                            beforeFocusRequester.requestFocus()
                        }
                        onChange(otpInputStates.getOtpValue())
                    } else if (newValue.text.length == 1) {
                        textInputMutableState.value = newValue.copy(
                            selection = TextRange(1)
                        )
                        if (currentPosition < numberInputs - 1) {
                            val nextFocusRequester = otpInputStates[currentPosition + 1].second
                            nextFocusRequester.requestFocus()
                        }
                        onChange(otpInputStates.getOtpValue())
                    } else {
                        var otpIndex = currentPosition
                        var stringIndex = 0
                        while (otpIndex < numberInputs && stringIndex < newValue.text.length) {
                            val currentMutableState = otpInputStates[otpIndex].first
                            val auxString = newValue.text[stringIndex].toString()
                            currentMutableState.value = TextFieldValue(auxString, TextRange(1))
                            otpIndex++
                            stringIndex++
                        }
                        val currentFocusRequester = if (otpIndex < numberInputs) {
                            otpInputStates[otpIndex].second
                        } else {
                            otpInputStates[numberInputs - 1].second
                        }
                        currentFocusRequester.requestFocus()
                        onChange(otpInputStates.getOtpValue())
                    }
                }
            }
        }
        if (errors.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            errors.forEach { error ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.size(size = 16.dp),
                        imageVector = Icons.Outlined.Cancel,
                        contentDescription = stringResource(R.string.txt_cd_error_input_icon),
                        tint = OutlinedTextFieldDefaults.colors().errorIndicatorColor
                    )
                    DefaultText(
                        error,
                        color = OutlinedTextFieldDefaults.colors().errorIndicatorColor,
                        fontSize = R.dimen.small_text_size
                    )
                }
            }
        }
    }
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
    errors: List<String> = listOf(),
    readOnly: Boolean = false,
    onClickable: (() -> Unit)? = null,
    onValueChange: ((String) -> Unit)? = null,
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
        errors = errors,
        readOnly = readOnly,
        onClickable = onClickable,
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
    errors: List<String> = listOf(),
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
        errors = errors,
        onValueChange = onValueChange,
    )
}

@Composable
fun PasswordOutlinedTextField(
    modifier: Modifier = Modifier,
    @StringRes label: Int,
    password: String,
    leadingIcon: ImageVector,
    errors: List<String> = listOf(),
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
        errors = errors,
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

@Composable
fun CircularProgressDialog() {
    ProgressDialog {
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp),
            color = MaterialTheme.colorScheme.secondary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = 10.dp
        )
    }
}

@Composable
fun ProgressDialog(progressContent: @Composable () -> Unit) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            progressContent()
        }
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

fun List<Pair<MutableState<TextFieldValue>, FocusRequester>>.getOtpValue(): String {
    var value = ""
    for (otpValue in this) {
        value += otpValue.first.value.text
    }
    return value
}