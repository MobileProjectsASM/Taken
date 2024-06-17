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
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class SourcesModule {
    @ViewModelScoped
    @Binds
    abstract fun getMultimediaRemoteSource(multimediaStorageSource: MultimediaStorageSource): MultimediaRemoteSource

    @ViewModelScoped
    @Binds
    abstract fun getMultimediaLocalSource(multimediaDeviceSource: MultimediaDeviceSource): MultimediaLocalSource

    @ViewModelScoped
    @Binds
    abstract fun getGamerRemoteSource(gamerRemoteSource: GamerFireStoreSource): GamerRemoteSource

    @ViewModelScoped
    @Binds
    abstract fun getGamerLocalSource(gamerLocalSource: GamerRoomSource): GamerLocalSource

    @ViewModelScoped
    @Binds
    abstract fun getGameLocalSource(gameLocalSource: GameRoomSource): GameLocalSource

    @ViewModelScoped
    @Binds
    abstract fun getGameRemoteSource(gameRemoteSource: GameFireStoreSource): GameRemoteSource

    @ViewModelScoped
    @Binds
    abstract fun getLevelsLocalSource(levelLocalSource: LevelRoomSource): LevelLocalSource

    @ViewModelScoped
    @Binds
    abstract fun getLevelsRemoteSource(levelRemoteSource: LevelFireStoreSource): LevelRemoteSource

    @ViewModelScoped
    @Binds
    abstract fun providesLogger(logger: LoggerSourceMobile): Logger

    @ViewModelScoped
    @Binds
    abstract fun getConnection(connectionSourceMobile: ConnectionSourceMobile): ConnectionSource
}