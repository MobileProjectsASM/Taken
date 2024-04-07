package com.asm.taken.utils

interface ResourceResolver {
    fun getString(resource: Int): String
}