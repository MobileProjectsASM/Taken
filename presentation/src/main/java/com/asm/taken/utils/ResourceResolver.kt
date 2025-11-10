package com.asm.taken.utils

import android.content.Context
import androidx.annotation.StringRes
import com.asm.domain.errors.GeneralError
import com.asm.taken.R
import com.asm.taken.model.AuthError
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
import com.asm.taken.model.LoginFailure
import com.asm.taken.model.SendOtpError
import com.asm.taken.model.SignUpError
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MessageResolver @Inject constructor(
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

    fun getErrorLogin(error: LoginFailure): String = when (error) {
        is LoginFailure.AuthFailure -> when (error.authError) {
            AuthError.ERROR_INVALID_EMAIL -> context.getString(R.string.err_email_invalid)
            AuthError.ERROR_WRONG_PASSWORD -> context.getString(R.string.err_incorrect_email_or_password)
            AuthError.ERROR_USER_NOT_FOUND -> context.getString(R.string.err_user_not_found)
            AuthError.ERROR_INVALID_LOGIN_CREDENTIALS -> context.getString(R.string.err_invalid_login_credentials)
            AuthError.ERROR_USER_DISABLED -> context.getString(R.string.err_user_disabled)
            AuthError.ERROR_TOO_MANY_REQUESTS -> context.getString(R.string.err_too_many_request)
            AuthError.NETWORK_CONNECTION -> context.getString(R.string.err_network_connection)
            AuthError.UNKNOWN_ERROR -> context.getString(R.string.err_unknown)
        }
        is LoginFailure.RegisterFailure -> when (val generalFailure = error.generalFailure) {
            is GeneralError.ClientError -> TODO()
            GeneralError.NetworkError -> TODO()
            is GeneralError.ServerError -> TODO()
            GeneralError.Unknown -> TODO()
            GeneralError.ConnectionError -> TODO()
        }
        is LoginFailure.SendOtpFailure -> when (error.sendOtpError) {
            SendOtpError.PHONE_NUMBER_INVALID_ERROR -> context.getString(R.string.err_phone_number_invalid)
            SendOtpError.NETWORK_CONNECTION -> context.getString(R.string.err_network_connection)
            SendOtpError.SERVER_ERROR, SendOtpError.UNKNOWN_ERROR -> context.getString(R.string.err_send_otp)
        }
        is LoginFailure.SignUpFailure -> when (error.signUpError) {
            SignUpError.NETWORK_CONNECTION -> context.getString(R.string.err_network_connection)
            SignUpError.EMAIL_ALREADY_IN_USE -> context.getString(R.string.err_email_already_in_use)
            SignUpError.INVALID_EMAIL -> context.getString(R.string.err_email_invalid)
            SignUpError.WEAK_PASSWORD -> context.getString(R.string.err_weak_password)
            SignUpError.UNKNOWN_ERROR -> context.getString(R.string.err_sign_up)
        }

        LoginFailure.LogoutFailure -> context.getString(R.string.err_logout)
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

    fun getMessage(@StringRes resId: Int) = context.getString(resId)
}