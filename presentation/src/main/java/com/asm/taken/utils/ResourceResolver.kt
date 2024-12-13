package com.asm.taken.utils

import android.content.Context
import com.asm.taken.R
import com.asm.taken.model.InputOtpError
import com.asm.taken.model.InputPasswordError
import com.asm.taken.model.InputPhoneCodeError
import com.asm.taken.model.InputPhoneNumberError
import com.asm.taken.model.InputUserIdError
import com.asm.taken.model.LoginWithPhoneError
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceResolver @Inject constructor(
    @ApplicationContext val context: Context
) {
    fun getString(resource: Int): String = context.getString(resource)
}

class MessageResolver @Inject constructor(
    @ApplicationContext val context: Context
) {
    fun getErrorUserId(error: InputUserIdError): String = when (error) {
        InputUserIdError.EMPTY -> context.getString(R.string.err_empty_field)
    }

    fun getErrorPassword(error: InputPasswordError): String = when (error) {
        InputPasswordError.EMPTY -> context.getString(R.string.err_empty_field)
        InputPasswordError.LEAST_THAN_8_CHARACTERS -> context.getString(R.string.err_min_8_characters)
        InputPasswordError.LEAST_ONE_NUMBER -> context.getString(R.string.err_least_one_number)
        InputPasswordError.LEAST_ONE_SPECIAL_CHARACTER -> context.getString(R.string.err_least_one_character)
        InputPasswordError.LEAST_ONE_UPPERCASE -> context.getString(R.string.err_least_one_uppercase)
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

    fun getErrorLoginWithPhone(error: LoginWithPhoneError): String = when (error) {
        LoginWithPhoneError.SEND_OTP_ERROR -> context.getString(R.string.err_send_otp)
        LoginWithPhoneError.AUTH_ERROR -> context.getString(R.string.err_auth_with_phone)
        LoginWithPhoneError.VERIFY_GAMER_EXISTS -> context.getString(R.string.err_process_gamer)
        LoginWithPhoneError.UNKNOWN_ERROR -> context.getString(R.string.err_unknown)
    }

    fun getErrorVerifyOtp(error: InputOtpError): String = when (error) {
        InputOtpError.EMPTY -> context.getString(R.string.err_otp_empty)
        InputOtpError.BE_6_DIGITS -> context.getString(R.string.err_otp_be_6_digits)
        InputOtpError.ONLY_INT_NUMBERS -> context.getString(R.string.err_only_int_numbers)
    }
}