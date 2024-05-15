package com.asm.data.sources.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.asm.data.sources.local.entities.Gamer

@Dao
interface GamerDao {
    @Insert
    fun insertGamer(gamer: Gamer)

    @Query("SELECT * FROM gamers WHERE gamer_id = :gamerId")
    fun getGamerById(gamerId: String): Gamer

    @Query("SELECT exists(SELECT 1 FROM gamers WHERE gamer_id = :gamerId) AS gamer_exists")
    fun gamerExists(gamerId: String): Int
}