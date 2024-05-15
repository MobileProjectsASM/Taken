package com.asm.data.sources.local.mappers

import com.asm.data.sources.local.entities.Gamer
import com.asm.domain.entities.Gamer as GamerDomain

class GamerMapper {
    fun getGamer(gamerDomain: GamerDomain): Gamer = Gamer(
        gamerDomain.gamerId,
        gamerDomain.gamerNickName,
        gamerDomain.gamerAge,
        gamerDomain.gamerCountry,
        gamerDomain.gamerImage
    )

    fun toGamerDomain(gamer: Gamer): GamerDomain = GamerDomain(
        gamer.gamerId,
        gamer.gamerNickname,
        gamer.gamerAge,
        gamer.gamerCountry,
        gamer.gamerImage
    )
}