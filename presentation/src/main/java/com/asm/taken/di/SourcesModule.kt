package com.asm.taken.di

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.hardware.impl.ConnectionSourceMobile
import com.asm.data.sources.hardware.impl.LoggerSourceMobile
import com.asm.data.sources.local.impl.GameRoomSource
import com.asm.data.sources.local.impl.GamerRoomSource
import com.asm.data.sources.local.impl.LevelRoomSource
import com.asm.data.sources.local.impl.MultimediaDeviceSource
import com.asm.data.sources.local.interfaces.GameLocalSource
import com.asm.data.sources.local.interfaces.GamerLocalSource
import com.asm.data.sources.local.interfaces.LevelLocalSource
import com.asm.data.sources.local.interfaces.MultimediaLocalSource
import com.asm.data.sources.remote.impl.GameFireStoreSource
import com.asm.data.sources.remote.impl.GamerFireStoreSource
import com.asm.data.sources.remote.impl.LevelFireStoreSource
import com.asm.data.sources.remote.impl.MultimediaStorageSource
import com.asm.data.sources.remote.interfaces.GameRemoteSource
import com.asm.data.sources.remote.interfaces.GamerRemoteSource
import com.asm.data.sources.remote.interfaces.LevelRemoteSource
import com.asm.data.sources.remote.interfaces.MultimediaRemoteSource
import com.asm.domain.utils.Logger
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SourcesModule {
    @Singleton
    @Binds
    abstract fun getMultimediaRemoteSource(multimediaStorageSource: MultimediaStorageSource): MultimediaRemoteSource

    @Singleton
    @Binds
    abstract fun getMultimediaLocalSource(multimediaDeviceSource: MultimediaDeviceSource): MultimediaLocalSource

    @Singleton
    @Binds
    abstract fun getGamerRemoteSource(gamerRemoteSource: GamerFireStoreSource): GamerRemoteSource

    @Singleton
    @Binds
    abstract fun getGamerLocalSource(gamerLocalSource: GamerRoomSource): GamerLocalSource

    @Singleton
    @Binds
    abstract fun getGameLocalSource(gameLocalSource: GameRoomSource): GameLocalSource

    @Singleton
    @Binds
    abstract fun getGameRemoteSource(gameRemoteSource: GameFireStoreSource): GameRemoteSource

    @Singleton
    @Binds
    abstract fun getLevelsLocalSource(levelLocalSource: LevelRoomSource): LevelLocalSource

    @Singleton
    @Binds
    abstract fun getLevelsRemoteSource(levelRemoteSource: LevelFireStoreSource): LevelRemoteSource

    @Singleton
    @Binds
    abstract fun providesLogger(logger: LoggerSourceMobile): Logger

    @Singleton
    @Binds
    abstract fun getConnection(connectionSourceMobile: ConnectionSourceMobile): ConnectionSource
}