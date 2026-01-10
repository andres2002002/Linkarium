package com.habitiora.linkarium.di

import com.habitiora.linkarium.core.GardenPdfGenerator
import com.habitiora.linkarium.data.local.datasource.LinkEntryDataSource
import com.habitiora.linkarium.data.local.datasource.LinkEntryDataSourceImpl
import com.habitiora.linkarium.data.local.room.dao.LinkGardenEntityDao
import com.habitiora.linkarium.data.local.room.dao.LinkSeedEntityDao
import com.habitiora.linkarium.data.local.datasource.LinkGardenDataSource
import com.habitiora.linkarium.data.local.datasource.LinkGardenDataSourceImpl
import com.habitiora.linkarium.data.local.datasource.LinkSeedDataSource
import com.habitiora.linkarium.data.local.datasource.LinkSeedDataSourceImpl
import com.habitiora.linkarium.data.local.datasource.LinkTagDataSource
import com.habitiora.linkarium.data.local.datasource.LinkTagDataSourceImpl
import com.habitiora.linkarium.data.local.room.AppDatabase
import com.habitiora.linkarium.data.local.room.dao.LinkEntryEntityDao
import com.habitiora.linkarium.data.local.room.dao.LinkTagEntityDao
import com.habitiora.linkarium.data.repository.ExportRepository
import com.habitiora.linkarium.data.repository.ExportRepositoryImpl
import com.habitiora.linkarium.data.repository.LinkGardenRepository
import com.habitiora.linkarium.data.repository.LinkGardenRepositoryImpl
import com.habitiora.linkarium.data.repository.LinkSeedRepository
import com.habitiora.linkarium.data.repository.LinkSeedRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    // region DataSources
    @Singleton
    @Binds
    abstract fun bindLinkGardenDataSource(
        impl: LinkGardenDataSourceImpl
    ): LinkGardenDataSource

    @Singleton
    @Binds
    abstract fun bindLinkSeedDataSource(
        impl: LinkSeedDataSourceImpl
    ): LinkSeedDataSource

    @Singleton
    @Binds
    abstract fun bindLinkEntryDataSource(
        impl: LinkEntryDataSourceImpl
    ): LinkEntryDataSource

    @Singleton
    @Binds
    abstract fun bindLinkTagDataSource(
        impl: LinkTagDataSourceImpl
    ): LinkTagDataSource
    // endregion

    // region Repositories
    @Singleton
    @Binds
    abstract fun bindLinkGardenRepository(
        impl: LinkGardenRepositoryImpl
    ): LinkGardenRepository

    @Singleton
    @Binds
    abstract fun bindLinkSeedRepository(
        impl: LinkSeedRepositoryImpl
    ): LinkSeedRepository

    @Binds
    @Singleton
    abstract fun bindExportRepository(
        impl: ExportRepositoryImpl
    ): ExportRepository
    // endregion
}