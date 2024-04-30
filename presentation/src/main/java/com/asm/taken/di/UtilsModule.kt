package com.asm.taken.di

import com.asm.domain.utils.Logger
import com.asm.taken.core.LoggerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UtilsModule {
    @Singleton
    @Binds
    abstract fun providesLogger(logger: LoggerImpl): Logger
}