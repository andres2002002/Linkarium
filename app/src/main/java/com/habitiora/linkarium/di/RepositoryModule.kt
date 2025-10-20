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
    fun provideLinkGardenRepository(
        db: AppDatabase,
        linkGardenDataSource: LinkGardenDataSource
    ): LinkGardenRepository {
        return LinkGardenRepositoryImpl(
            db,
            linkGardenDataSource
        )
    }

    @Provides
    @Singleton
    fun provideLinkGardenDataSource(linkGardenDao: LinkGardenEntityDao): LinkGardenDataSource {
        return LinkGardenDataSourceImpl(linkGardenDao)
    }

    @Provides
    @Singleton
    fun provideLinkSeedRepository(
        db: AppDatabase,
        linkSeedDataSource: LinkSeedDataSource,
        linkEntryDataSource: LinkEntryDataSource,
        linkTagDataSource: LinkTagDataSource,
        linkGardenDataSource: LinkGardenDataSource
    ): LinkSeedRepository =
        LinkSeedRepositoryImpl(
            db,
            linkSeedDataSource,
            linkEntryDataSource,
            linkTagDataSource,
            linkGardenDataSource
        )


    @Provides
    @Singleton
    fun provideLinkEntryDataSource(linkEntryDao: LinkEntryEntityDao): LinkEntryDataSource =
        LinkEntryDataSourceImpl(linkEntryDao)

    @Provides
    @Singleton
    fun provideLinkTagDataSource(linkTagDao: LinkTagEntityDao): LinkTagDataSource =
        LinkTagDataSourceImpl(linkTagDao)

    @Singleton
    @Provides
    fun provideGardenPdfGenerator(): GardenPdfGenerator {
        return GardenPdfGenerator()
    }

    @Singleton
    @Provides
    fun provideExportRepository(
        db: AppDatabase,
        pdfGenerator: GardenPdfGenerator,
        gardenDataSource: LinkGardenDataSource,
        seedDataSource: LinkSeedDataSource,
        entryDataSource: LinkEntryDataSource,
        tagDataSource: LinkTagDataSource
    ): ExportRepository =
        ExportRepositoryImpl(
            db,
            pdfGenerator,
            gardenDataSource,
            seedDataSource,
            entryDataSource,
            tagDataSource
        )


}