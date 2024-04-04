package com.asm.taken.model

sealed class TextState {
    data object Init : TextState()
    data class Error(val message: String) : TextState()
    data object Valid : TextState()
}
