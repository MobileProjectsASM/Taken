package com.asm.data.sources.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.asm.data.sources.local.entities.CountryInfoRoom

@Dao
interface CountryInfoDao {

    @Query("SELECT * FROM country_info")
    suspend fun getCountriesInfo(): List<CountryInfoRoom>

    @Insert
    suspend fun saveCountriesInfo(countriesInfo: List<CountryInfoRoom>)
}