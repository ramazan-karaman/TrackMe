package com.example.trackme.di

import android.content.Context
import androidx.room.Room
import com.example.trackme.data.local.RunDao
import com.example.trackme.data.local.RunDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideRunDatabase(@ApplicationContext context: Context): RunDatabase{
        return Room.databaseBuilder(
            context,
            RunDatabase::class.java,
            "running_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideRunDao(db: RunDatabase): RunDao{
        return db.runDao()
    }
}