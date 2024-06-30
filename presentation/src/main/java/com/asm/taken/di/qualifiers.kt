package com.asm.taken.di

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CountryInfoRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CountryInfoInterceptor