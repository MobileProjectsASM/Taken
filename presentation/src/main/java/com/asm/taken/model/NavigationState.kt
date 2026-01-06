package com.asm.taken.model

import com.asm.domain.errors.GeneralError

sealed class NavigationState {
    data object Loading: NavigationState()
    data object SessionClosed: NavigationState()
    data class GamerCreated(val gamerId: String): NavigationState()
    data class Failure(val error: GeneralError): NavigationState()
}

sealed class EditGamerOperationsState {
    data object Loading: EditGamerOperationsState()
    data object GamerDeleted: EditGamerOperationsState()
    data object GamerUpdated: EditGamerOperationsState()
    data class Failure(val error: GeneralError): EditGamerOperationsState()
}
