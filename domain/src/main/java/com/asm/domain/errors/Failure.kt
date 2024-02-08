package com.asm.domain.errors

sealed class Failure {
    data object NetworkConnection : Failure()
    data object ServerError : Failure()
    data object UnknownError : Failure()

    abstract class FeatureFailure : Failure()
}

sealed class RegisterFailure: Failure() {
    data object GamerExists: RegisterFailure()
}
