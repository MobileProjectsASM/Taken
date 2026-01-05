package com.asm.taken.di.modules

import com.asm.data.repositories.AuthRepositoryImpl
import com.asm.data.repositories.CountryInfoRepositoryImpl
import com.asm.data.repositories.GameRepositoryImpl
import com.asm.data.repositories.GamerRepositoryImpl
import com.asm.data.repositories.MultimediaRepositoryImpl
import com.asm.data.repositories.SessionRepositoryImpl
import com.asm.domain.repositories.AuthRepository
import com.asm.domain.repositories.CountryInfoRepository
import com.asm.domain.repositories.GameRepository
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.repositories.MultimediaRepository
import com.asm.domain.repositories.SessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @ViewModelScoped
    @Binds
    abstract fun getGamerRepository(gamerRepository: GamerRepositoryImpl): GamerRepository

    @ViewModelScoped
    @Binds
    abstract fun getMultimediaRepository(multimediaRepository: MultimediaRepositoryImpl): MultimediaRepository

    @ViewModelScoped
    @Binds
    abstract fun getCountryInfoRepository(countryInfoRepository: CountryInfoRepositoryImpl): CountryInfoRepository

    @ViewModelScoped
    @Binds
    abstract fun getSessionRepository(sessionRepositoryImpl: SessionRepositoryImpl): SessionRepository

    @ViewModelScoped
    @Binds
    abstract fun getGameRepository(gameRepositoryImpl: GameRepositoryImpl): GameRepository

    @ViewModelScoped
    @Binds
    abstract fun getAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository
}