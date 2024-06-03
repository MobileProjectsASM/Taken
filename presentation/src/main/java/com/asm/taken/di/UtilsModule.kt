package com.asm.taken.di

import android.content.Context
import androidx.room.Room
import com.asm.data.sources.local.TakenDB
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UtilsModule {

    @Singleton
    @Provides
    fun providesTakenDb(@ApplicationContext context: Context): TakenDB = Room.databaseBuilder(
        context,
        TakenDB::class.java,
        "taken_db"
    ).build()

    @Singleton
    @Provides
    fun providesFireStore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Singleton
    @Provides
    fun providesGson(): Gson = Gson()
}