package com.asm.taken.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceResolver @Inject constructor(
    @ApplicationContext val context: Context
) {
    fun getString(resource: Int): String = context.getString(resource)
}