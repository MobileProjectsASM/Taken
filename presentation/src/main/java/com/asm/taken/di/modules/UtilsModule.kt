package com.asm.taken.di.modules

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.asm.data.sources.local.TakenDB
import com.asm.data.sources.local.deserializer.SessionTypeAdapterFactory
import com.asm.data.sources.remote.impl.rest.api_service.CountryInfoClient
import com.asm.data.sources.remote.impl.rest.interceptors.CountryInfoInterceptor
import com.asm.taken.R
import com.asm.taken.di.CountryInfoRetrofit
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class UtilsModule {

    @Provides
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences = context.getSharedPreferences(context.getString(R.string.session_file), Context.MODE_PRIVATE)

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
    fun providesStorageReference(): FirebaseStorage = FirebaseStorage.getInstance()

    @Singleton
    @Provides
    fun providesFirebaseFunctions(): FirebaseFunctions = FirebaseFunctions.getInstance()

    @Singleton
    @Provides
    fun providesCountryInfoClient(
        @CountryInfoRetrofit retrofit: Retrofit
    ): CountryInfoClient = retrofit.create(CountryInfoClient::class.java)

    @CountryInfoRetrofit
    @Provides
    fun providesRetrofitCountries(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit = Retrofit.Builder()
        .baseUrl(context.getString(com.asm.data.R.string.api_base_url_countries_info))
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    fun providesGson(
        sessionTypeAdapterFactory: SessionTypeAdapterFactory,
    ): Gson {
        return GsonBuilder()
            .registerTypeAdapterFactory(sessionTypeAdapterFactory)
            .create()
    }

    @Provides
    fun providesOkHttpClient(interceptor: Interceptor): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .build()

    @Provides
    fun providesCountryInfoInterceptor(@ApplicationContext context: Context): Interceptor = CountryInfoInterceptor(
        apiHost = context.getString(com.asm.data.R.string.api_host_country_service),
        apiKey = context.getString(com.asm.data.R.string.api_key_country_service)
    )
}