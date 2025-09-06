package com.habitiora.linkarium.di

import com.habitiora.linkarium.data.local.room.dao.LinkGardenEntityDao
import com.habitiora.linkarium.data.local.room.dao.LinkSeedEntityDao
import com.habitiora.linkarium.data.repository.LinkGardenRepository
import com.habitiora.linkarium.data.repository.LinkGardenRepositoryImp
import com.habitiora.linkarium.data.repository.LinkSeedRepository
import com.habitiora.linkarium.data.repository.LinkSeedRepositoryImp
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
    fun provideLinkSeedRepository(linkSeedDao: LinkSeedEntityDao): LinkSeedRepository {
        return LinkSeedRepositoryImp(linkSeedDao)
    }

    @Provides
    @Singleton
    fun provideLinkGardenRepository(linkGardenDao: LinkGardenEntityDao): LinkGardenRepository {
        return LinkGardenRepositoryImp(linkGardenDao)
    }
}