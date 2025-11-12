package com.asm.taken.utils

import android.content.Context
import androidx.annotation.StringRes
import com.asm.taken.R
import com.asm.taken.model.InputAgeError
import com.asm.taken.model.InputAliasError
import com.asm.taken.model.InputCountryError
import com.asm.taken.model.InputEmailError
import com.asm.taken.model.InputImageError
import com.asm.taken.model.InputImageError.*
import com.asm.taken.model.InputOtpError
import com.asm.taken.model.InputPasswordError
import com.asm.taken.model.InputPhoneCodeError
import com.asm.taken.model.InputPhoneNumberError
import com.asm.taken.model.InputRepeatValueError
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceResolver @Inject constructor(
    @ApplicationContext val context: Context
) {
    fun getErrorEmail(error: InputEmailError): String = when (error) {
        InputEmailError.EMPTY -> context.getString(R.string.err_empty_field)
        InputEmailError.EMAIL_INVALID -> context.getString(R.string.err_email_invalid)
    }

    fun getErrorPassword(error: InputPasswordError): String = when (error) {
        InputPasswordError.EMPTY -> context.getString(R.string.err_empty_field)
        InputPasswordError.LEAST_THAN_8_CHARACTERS -> context.getString(R.string.err_min_8_characters)
        InputPasswordError.LEAST_ONE_NUMBER -> context.getString(R.string.err_least_one_number)
        InputPasswordError.LEAST_ONE_SPECIAL_CHARACTER -> context.getString(R.string.err_least_one_character)
        InputPasswordError.LEAST_ONE_UPPERCASE -> context.getString(R.string.err_least_one_uppercase)
    }

    fun getErrorPasswordRepeat(error: InputRepeatValueError): String = when (error) {
        InputRepeatValueError.IS_NOT_SAME_VALUE -> context.getString(R.string.err_password_is_not_same)
    }

    fun getErrorPhoneCode(error: InputPhoneCodeError): String = when (error) {
        InputPhoneCodeError.EMPTY -> context.getString(R.string.err_empty_field)
        InputPhoneCodeError.LESS_THAN_4_DIGITS -> context.getString(R.string.err_less_than_4_digits)
        InputPhoneCodeError.ONLY_INT_NUMBERS -> context.getString(R.string.err_only_int_numbers)
    }

    fun getErrorPhoneNumber(error: InputPhoneNumberError): String = when (error) {
        InputPhoneNumberError.EMPTY -> context.getString(R.string.err_empty_field)
        InputPhoneNumberError.ONLY_INT_NUMBERS -> context.getString(R.string.err_only_int_numbers)
    }

    fun getErrorVerifyOtp(error: InputOtpError): String = when (error) {
        InputOtpError.EMPTY -> context.getString(R.string.err_otp_empty)
        InputOtpError.BE_6_DIGITS -> context.getString(R.string.err_otp_be_6_digits)
        InputOtpError.ONLY_INT_NUMBERS -> context.getString(R.string.err_only_int_numbers)
    }

    fun getErrorAlias(error: InputAliasError): String = when (error) {
        InputAliasError.EMPTY -> context.getString(R.string.err_empty_field)
    }

    fun getErrorAge(error: InputAgeError): String = when (error) {
        InputAgeError.EMPTY -> context.getString(R.string.err_empty_field)
        InputAgeError.ONLY_NUMBERS -> context.getString(R.string.err_only_number)
        InputAgeError.GREATER_THAN_100 -> context.getString(R.string.err_age_greater_than_100)
        InputAgeError.LESS_THAN_8 -> context.getString(R.string.err_age_less_than_8)
    }

    fun getErrorCountry(error: InputCountryError): String = when (error) {
        InputCountryError.EMPTY -> context.getString(R.string.err_empty_field)
    }

    fun getErrorImage(error: InputImageError): String = when (error) {
        IMAGE_IS_VERY_WEIGHT -> context.getString(R.string.err_image_is_very_weight)
        UNKNOWN_ERROR -> context.getString(R.string.err_unknown)
    }

    fun getString(@StringRes resId: Int) = context.getString(resId)
}