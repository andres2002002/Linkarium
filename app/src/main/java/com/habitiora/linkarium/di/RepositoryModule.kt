package com.habitiora.linkarium.di

import com.habitiora.linkarium.data.local.room.dao.LinkGardenEntityDao
import com.habitiora.linkarium.data.local.room.dao.LinkSeedEntityDao
import com.habitiora.linkarium.data.local.datasource.LinkGardenDataSource
import com.habitiora.linkarium.data.local.datasource.LinkGardenDataSourceImpl
import com.habitiora.linkarium.data.local.datasource.LinkSeedDataSource
import com.habitiora.linkarium.data.local.datasource.LinkSeedDataSourceImpl
import com.habitiora.linkarium.data.repository.LinkGardenRepository
import com.habitiora.linkarium.data.repository.LinkGardenRepositoryImpl
import com.habitiora.linkarium.data.repository.LinkSeedRepository
import com.habitiora.linkarium.data.repository.LinkSeedRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideLinkSeedDataSource(linkSeedDao: LinkSeedEntityDao): LinkSeedDataSource {
        return LinkSeedDataSourceImpl(linkSeedDao)
    }

    @Provides
    @Singleton
    fun provideLinkGardenRepository(linkGardenDataSource: LinkGardenDataSource): LinkGardenRepository {
        return LinkGardenRepositoryImpl(linkGardenDataSource)
    }

    @Provides
    @Singleton
    fun provideLinkGardenDataSource(linkGardenDao: LinkGardenEntityDao): LinkGardenDataSource {
        return LinkGardenDataSourceImpl(linkGardenDao)
    }

    @Provides
    @Singleton
    fun provideLinkSeedRepository(linkSeedDataSource: LinkSeedDataSource): LinkSeedRepository {
        return LinkSeedRepositoryImpl(linkSeedDataSource)
    }

}