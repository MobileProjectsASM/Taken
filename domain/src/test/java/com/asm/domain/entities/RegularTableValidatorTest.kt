package com.asm.domain.entities

import kotlin.test.BeforeTest
import kotlin.test.Test

class RegularTableValidatorTest {

    private lateinit var regularTableValidator: RegularTableValidator

    @BeforeTest
    fun onBefore() {
        regularTableValidator = RegularTableValidator()
    }

}