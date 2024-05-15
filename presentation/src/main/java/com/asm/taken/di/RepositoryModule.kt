package com.asm.taken.di

import com.asm.data.repositories.GameRepositoryImpl
import com.asm.data.repositories.GamerRepositoryImpl
import com.asm.data.repositories.LevelRepositoryImpl
import com.asm.data.repositories.MultimediaRepositoryImpl
import com.asm.data.sources.hardware.Connection
import com.asm.data.sources.local.impl.MultimediaDeviceSource
import com.asm.data.sources.local.interfaces.MultimediaLocalSource
import com.asm.data.sources.remote.impl.MultimediaStorageSource
import com.asm.data.sources.remote.interfaces.MultimediaRemoteSource
import com.asm.domain.entities.Game
import com.asm.domain.repositories.GameRepository
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.repositories.LevelRepository
import com.asm.domain.repositories.MultimediaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
abstract class RepositoryModule {
    @ActivityScoped
    @Binds
    abstract fun getGamerRepository(gamerRepository: GamerRepositoryImpl): GamerRepository

    @ActivityScoped
    @Binds
    abstract fun getLevelRepository(levelRepository: LevelRepositoryImpl): LevelRepository

    @ActivityScoped
    @Binds
    abstract fun getGameRepository(gameRepository: GameRepositoryImpl): GameRepository

    @ActivityScoped
    @Binds
    abstract fun getMultimediaRepository(multimediaRepository: MultimediaRepositoryImpl): MultimediaRepository

    @ActivityScoped
    @Binds
    abstract fun getMultimediaRemoteSource(multimediaStorageSource: MultimediaStorageSource): MultimediaRemoteSource

    @ActivityScoped
    @Binds
    abstract fun getMultimediaLocalSource(multimediaDeviceSource: MultimediaDeviceSource): MultimediaLocalSource

    @ActivityScoped
    @Binds
    abstract fun getConnection(): Connection
}