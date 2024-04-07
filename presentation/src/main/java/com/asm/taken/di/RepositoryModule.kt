package com.asm.taken.di

import com.asm.taken.utils.ResourceResolver
import com.asm.taken.utils.ResourceResolverImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
internal object RepositoryModule {
    @Provides
    @ViewModelScoped
    fun getResourceResolver(): ResourceResolver {
        return ResourceResolverImpl()
    }
}