package com.asm.data.repositories

import com.asm.data.sources.hardware.Connection
import com.asm.data.sources.local.interfaces.GameLocalSource
import com.asm.data.sources.remote.interfaces.GameRemoteSource
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    gameLocalSource: GameLocalSource,
    gameRemoteSource: GameRemoteSource,
    connection: Connection,
) {
}