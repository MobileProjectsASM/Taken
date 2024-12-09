package com.asm.taken.utils

import android.content.Context
import androidx.annotation.StringRes
import com.asm.taken.R
import com.asm.taken.model.InputPasswordError
import com.asm.taken.model.InputUserIdError
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
    }
}