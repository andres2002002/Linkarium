package com.habitiora.linkarium.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.habitiora.linkarium.data.local.room.AppDatabase
import com.habitiora.linkarium.data.local.room.DatabaseContract
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, DatabaseContract.DATABASE_NAME)
            .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
            .fallbackToDestructiveMigration(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideLinkSeedDao(appDatabase: AppDatabase) = appDatabase.linkSeedDao()

    @Provides
    @Singleton
    fun provideLinkGardenDao(appDatabase: AppDatabase) = appDatabase.linkGardenDao()
}