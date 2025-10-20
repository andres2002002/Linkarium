package com.habitiora.linkarium.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.habitiora.linkarium.data.local.room.AppDatabase
import com.habitiora.linkarium.data.local.room.DatabaseContract
import com.habitiora.linkarium.data.local.room.migrations.MIGRATION_1_2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton
import timber.log.Timber
import java.util.concurrent.Executors

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DatabaseContract.DATABASE_NAME)
/*            .setQueryCallback({ sqlQuery, bindArgs ->
                Timber.d("SQL: $sqlQuery args: $bindArgs")
            }, Executors.newSingleThreadExecutor())*/
            .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    @Provides
    @Singleton
    fun provideLinkSeedDao(appDatabase: AppDatabase) = appDatabase.linkSeedDao()

    @Provides
    @Singleton
    fun provideLinkGardenDao(appDatabase: AppDatabase) = appDatabase.linkGardenDao()

    @Provides
    @Singleton
    fun provideLinkTagDao(appDatabase: AppDatabase) = appDatabase.linkTagDao()

    @Provides
    @Singleton
    fun provideLinkEntryDao(appDatabase: AppDatabase) = appDatabase.linkEntryDao()
}