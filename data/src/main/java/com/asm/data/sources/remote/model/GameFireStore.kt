package com.asm.data.sources.remote.model

data class GameFireStore(
    val gameId: String,
    val levelId: String,
    val status: Map<String, Any?>
)