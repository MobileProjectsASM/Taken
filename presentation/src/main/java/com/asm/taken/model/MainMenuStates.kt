package com.asm.taken.model

import com.asm.domain.entities.Gamer

data class MainMenuState(
    val gamer: Gamer,
    val itHasProgress: Boolean,
    val menuProcessType: MenuProcessType = MenuProcessType.Idle
)

sealed class MenuProcessType {
    data object Idle: MenuProcessType()
    data class SessionCloseProcess(val process: CommonProcessState<Unit>): MenuProcessType()
    data class CreateNewGameProcess(val process: CommonProcessState<Unit>): MenuProcessType()
}