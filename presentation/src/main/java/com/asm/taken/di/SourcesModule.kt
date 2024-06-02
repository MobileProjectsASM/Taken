package com.asm.taken.di

import com.asm.data.sources.local.impl.GamerRoomSource
import com.asm.data.sources.local.impl.MultimediaDeviceSource
import com.asm.data.sources.local.interfaces.GamerLocalSource
import com.asm.data.sources.local.interfaces.MultimediaLocalSource
import com.asm.data.sources.remote.impl.GamerFireStoreSource
import com.asm.data.sources.remote.impl.MultimediaStorageSource
import com.asm.data.sources.remote.interfaces.GamerRemoteSource
import com.asm.data.sources.remote.interfaces.MultimediaRemoteSource
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
}