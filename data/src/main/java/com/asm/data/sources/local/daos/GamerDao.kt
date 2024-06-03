package com.asm.data.sources.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.asm.data.sources.local.entities.GamerRoom

@Dao
interface GamerDao {
    @Insert
    suspend fun insertGamer(gamerRoom: GamerRoom)

    @Query("SELECT * FROM gamers WHERE gamer_id = :gamerId")
    suspend fun getGamerById(gamerId: String): GamerRoom

    @Query("SELECT exists(SELECT 1 FROM gamers WHERE gamer_id = :gamerId) AS gamer_exists")
    suspend fun gamerExists(gamerId: String): Int
}