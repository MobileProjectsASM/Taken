package com.asm.data.sources.remote.impl.firebase.mappers

import com.asm.data.sources.remote.impl.firebase.data.GamerFirebase
import com.asm.domain.entities.Gamer

class GamerMapper {

    fun getGamerFirebase(gamer: Gamer) = GamerFirebase(
        gamerId = gamer.gamerId,
        gamerNickName = gamer.gamerNickName,
        gamerAge = gamer.gamerAge,
        gamerCountry = gamer.gamerCountry,
        gamerImage = gamer.gamerImage
    )
}