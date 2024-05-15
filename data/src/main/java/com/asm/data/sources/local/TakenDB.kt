package com.asm.data.sources.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.asm.data.sources.local.daos.GamerDao
import com.asm.data.sources.local.entities.Gamer

@Database(entities = [Gamer::class], version = 1)
abstract class TakenDB: RoomDatabase() {
    abstract fun getGamerDao(): GamerDao
}