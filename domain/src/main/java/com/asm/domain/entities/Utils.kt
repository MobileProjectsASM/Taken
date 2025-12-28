package com.asm.domain.entities

fun String.getImageExtension() = this.split("/").let { it[it.size - 1] }