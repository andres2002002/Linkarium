package com.habitiora.linkarium.di

import com.habitiora.linkarium.core.UriValidator
import com.habitiora.linkarium.core.UriValidatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UriValidatorModule {

    @Binds
    @Singleton
    abstract fun bindUriValidator(
        impl: UriValidatorImpl
    ): UriValidator
}