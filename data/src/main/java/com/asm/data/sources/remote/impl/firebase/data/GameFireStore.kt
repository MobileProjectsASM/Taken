package com.asm.data.sources.remote.impl.firebase.data

data class GameFireStore(
    val gameId: String,
    val levelId: String,
    val status: Map<String, Any?>
)