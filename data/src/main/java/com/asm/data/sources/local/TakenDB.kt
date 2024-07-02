package com.asm.data.sources.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.asm.data.sources.local.daos.CountryInfoDao
import com.asm.data.sources.local.daos.GameDao
import com.asm.data.sources.local.daos.GamerDao
import com.asm.data.sources.local.daos.LevelDao
import com.asm.data.sources.local.entities.CountryInfoRoom
import com.asm.data.sources.local.entities.GameRoom
import com.asm.data.sources.local.entities.GamerRoom
import com.asm.data.sources.local.entities.LevelRoom

@Database(entities = [
    GamerRoom::class,
    GameRoom::class,
    LevelRoom::class,
    CountryInfoRoom::class],
    version = 1
)
abstract class TakenDB: RoomDatabase() {
    abstract fun getGamerDao(): GamerDao

    abstract fun getGameDao(): GameDao

    abstract fun getLevelDao(): LevelDao

    abstract fun getCountryInfoDao(): CountryInfoDao
}