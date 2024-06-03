package com.asm.data.sources.local.mappers

import com.asm.data.sources.local.entities.GamerRoom
import javax.inject.Inject
import com.asm.domain.entities.Gamer as GamerDomain

class GamerMapper @Inject constructor() {
    fun getGamer(gamerDomain: GamerDomain): GamerRoom = GamerRoom(
        gamerDomain.gamerId,
        gamerDomain.gamerNickName,
        gamerDomain.gamerAge,
        gamerDomain.gamerCountry,
        gamerDomain.gamerImage
    )

    fun toGamerDomain(gamerRoom: GamerRoom): GamerDomain = GamerDomain(
        gamerRoom.gamerId,
        gamerRoom.gamerNickname,
        gamerRoom.gamerAge,
        gamerRoom.gamerCountry,
        gamerRoom.gamerImage
    )
}