package com.asm.taken.utils

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.asm.taken.R
import com.asm.taken.model.InputAgeError
import com.asm.taken.model.InputAliasError
import com.asm.taken.model.InputCountryError
import com.asm.taken.model.InputEmailError
import com.asm.taken.model.InputImageError
import com.asm.taken.model.InputImageError.*
import com.asm.taken.model.InputPasswordError
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@Composable
fun getErrorEmail(error: InputEmailError): String = when (error) {
    InputEmailError.EMPTY -> stringResource(R.string.err_empty_field)
    InputEmailError.EMAIL_INVALID -> stringResource(R.string.err_email_invalid)
}

@Composable
fun getErrorPassword(error: InputPasswordError): String = when (error) {
    InputPasswordError.EMPTY -> stringResource(R.string.err_empty_field)
    InputPasswordError.LEAST_THAN_8_CHARACTERS -> stringResource(R.string.err_min_8_characters)
    InputPasswordError.LEAST_ONE_NUMBER -> stringResource(R.string.err_least_one_number)
    InputPasswordError.LEAST_ONE_SPECIAL_CHARACTER -> stringResource(R.string.err_least_one_character)
    InputPasswordError.LEAST_ONE_UPPERCASE -> stringResource(R.string.err_least_one_uppercase)
}

@Composable
fun getErrorAlias(error: InputAliasError): String = when (error) {
    InputAliasError.EMPTY -> stringResource(R.string.err_empty_field)
}

@Composable
fun getErrorAge(error: InputAgeError): String = when (error) {
    InputAgeError.EMPTY -> stringResource(R.string.err_empty_field)
    InputAgeError.ONLY_NUMBERS -> stringResource(R.string.err_only_number)
    InputAgeError.GREATER_THAN_100 -> stringResource(R.string.err_age_greater_than_100)
    InputAgeError.LESS_THAN_8 -> stringResource(R.string.err_age_less_than_8)
}

@Composable
fun getErrorCountry(error: InputCountryError): String = when (error) {
    InputCountryError.EMPTY -> stringResource(R.string.err_empty_field)
}

@Composable
fun getErrorImage(error: InputImageError): String = when (error) {
    IMAGE_IS_VERY_WEIGHT -> stringResource(R.string.err_image_is_very_weight)
    UNKNOWN_ERROR -> stringResource(R.string.err_unknown)
}

class ResourceResolver @Inject constructor(
    @ApplicationContext val context: Context
) {
    fun getString(@StringRes resId: Int) = context.getString(resId)
}