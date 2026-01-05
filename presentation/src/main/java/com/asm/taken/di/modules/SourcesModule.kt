package com.asm.taken.di.modules

import com.asm.data.sources.hardware.ConnectionSource
import com.asm.data.sources.hardware.impl.ConnectionSourceMobile
import com.asm.data.sources.hardware.impl.LoggerSourceMobile
import com.asm.data.sources.local.impl.CountryInfoRoomSource
import com.asm.data.sources.local.impl.GamerRoomSource
import com.asm.data.sources.local.impl.LevelRoomSource
import com.asm.data.sources.local.impl.MultimediaDeviceSource
import com.asm.data.sources.local.impl.SessionSharedPreferencesSource
import com.asm.data.sources.local.interfaces.CountryInfoLocalSource
import com.asm.data.sources.local.interfaces.GamerLocalSource
import com.asm.data.sources.local.interfaces.LevelLocalSource
import com.asm.data.sources.local.interfaces.MultimediaLocalSource
import com.asm.data.sources.local.interfaces.SessionLocalSource
import com.asm.data.sources.remote.abstract_remotes.AuthRemoteSource
import com.asm.data.sources.remote.abstract_remotes.CountryInfoRemoteSource
import com.asm.data.sources.remote.abstract_remotes.GameRemoteSource
import com.asm.data.sources.remote.abstract_remotes.GamerRemoteSource
import com.asm.data.sources.remote.abstract_remotes.LevelRemoteSource
import com.asm.data.sources.remote.abstract_remotes.MultimediaRemoteSource
import com.asm.data.sources.remote.impl.firebase.AuthFirebaseSource
import com.asm.data.sources.remote.impl.firebase.GameFireStoreSource
import com.asm.data.sources.remote.impl.firebase.GamerFirebaseSource
import com.asm.data.sources.remote.impl.firebase.LevelFireStoreSource
import com.asm.data.sources.remote.impl.firebase.MultimediaStorageSource
import com.asm.data.sources.remote.impl.rest.CountryInfoRestServiceSource
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
    abstract fun getGamerRemoteSource(gamerRemoteSource: GamerFirebaseSource): GamerRemoteSource

    @ViewModelScoped
    @Binds
    abstract fun getGamerLocalSource(gamerLocalSource: GamerRoomSource): GamerLocalSource

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

    @ViewModelScoped
    @Binds
    abstract fun getCountryInfoLocalSource(countryInfoLocalSource: CountryInfoRoomSource): CountryInfoLocalSource

    @ViewModelScoped
    @Binds
    abstract fun getCountryInfoRemoteSource(countryInfoRemoteSource: CountryInfoRestServiceSource): CountryInfoRemoteSource

    @ViewModelScoped
    @Binds
    abstract fun getSessionLocalSource(sessionSharedPreferencesSource: SessionSharedPreferencesSource): SessionLocalSource

    @ViewModelScoped
    @Binds
    abstract fun getAuthRemoteSource(authFirebaseSource: AuthFirebaseSource): AuthRemoteSource

}