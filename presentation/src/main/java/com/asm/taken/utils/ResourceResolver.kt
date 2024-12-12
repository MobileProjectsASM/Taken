package com.asm.taken.utils

import android.content.Context
import androidx.compose.ui.res.stringResource
import com.asm.taken.R
import com.asm.taken.model.InputOtpError
import com.asm.taken.model.InputPasswordError
import com.asm.taken.model.InputPhoneCodeError
import com.asm.taken.model.InputPhoneNumberError
import com.asm.taken.model.InputUserIdError
import com.asm.taken.model.SendOtpError
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceResolver @Inject constructor(
    @ApplicationContext val context: Context
) {
    fun getString(resource: Int): String = context.getString(resource)
}

class MessageResolver {
    companion object {
        fun getErrorUserId(error: InputUserIdError): Int = when (error) {
            InputUserIdError.EMPTY -> R.string.err_empty_field
        }

        fun getErrorPassword(error: InputPasswordError): Int = when (error) {
            InputPasswordError.EMPTY -> R.string.err_empty_field
            InputPasswordError.LEAST_THAN_8_CHARACTERS -> R.string.err_min_8_characters
            InputPasswordError.LEAST_ONE_NUMBER -> R.string.err_least_one_number
            InputPasswordError.LEAST_ONE_SPECIAL_CHARACTER -> R.string.err_least_one_character
            InputPasswordError.LEAST_ONE_UPPERCASE -> R.string.err_least_one_uppercase
        }

        fun getErrorPhoneCode(error: InputPhoneCodeError): Int = when (error) {
            InputPhoneCodeError.EMPTY -> R.string.err_empty_field
            InputPhoneCodeError.LESS_THAN_4_DIGITS -> R.string.err_less_than_4_digits
            InputPhoneCodeError.ONLY_INT_NUMBERS -> R.string.err_only_int_numbers
        }

        fun getErrorPhoneNumber(error: InputPhoneNumberError): Int = when (error) {
            InputPhoneNumberError.EMPTY -> R.string.err_empty_field
            InputPhoneNumberError.ONLY_INT_NUMBERS -> R.string.err_only_int_numbers
        }

        fun getErrorSendOtp(error: SendOtpError): Int = when (error) {
            SendOtpError.SEND_OTP_ERROR -> R.string.err_send_otp
            SendOtpError.AUTH_ERROR -> R.string.err_auth_with_phone
            SendOtpError.UNKNOWN_ERROR -> R.string.err_unknown
        }

        fun getErrorVerifyOtp(error: InputOtpError): Int = when (error) {
            InputOtpError.EMPTY -> R.string.err_otp_empty
            InputOtpError.BE_6_DIGITS -> R.string.err_otp_be_n_digits
            InputOtpError.ONLY_INT_NUMBERS -> R.string.err_only_int_numbers
        }
    }
}