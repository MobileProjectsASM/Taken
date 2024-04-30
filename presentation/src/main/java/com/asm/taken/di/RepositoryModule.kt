package com.asm.taken.di

import com.asm.data.repositories.GamerRepositoryImpl
import com.asm.domain.entities.Game
import com.asm.domain.repositories.GameRepository
import com.asm.domain.repositories.GamerRepository
import com.asm.domain.repositories.LevelRepository
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
    abstract fun getLevelRepository(levelRepository: LevelRepository): LevelRepository

    @ActivityScoped
    @Binds
    abstract fun getGameRepository(gameRepository: GameRepository): GameRepository
}