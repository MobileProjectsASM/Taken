package com.asm.data.repositories

import com.asm.data.sources.hardware.Connection
import com.asm.data.sources.local.interfaces.LevelLocalSource
import com.asm.data.sources.remote.interfaces.LevelRemoteSource
import javax.inject.Inject

class LevelRepositoryImpl @Inject constructor(
    levelRemoteSource: LevelRemoteSource,
    levelLocalSource: LevelLocalSource,
    connection: Connection,
) {
}