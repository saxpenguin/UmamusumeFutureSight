package com.saxpenguin.umamusumefuturesight.di

import android.content.Context
import androidx.room.Room
import com.saxpenguin.umamusumefuturesight.data.local.AppDatabase
import com.saxpenguin.umamusumefuturesight.data.local.BannerDao
import com.saxpenguin.umamusumefuturesight.BuildConfig
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        val builder = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "umamusume_db"
        )
        
        if (BuildConfig.DEBUG) {
            builder.fallbackToDestructiveMigration()
        }
        
        return builder.build()
    }

    @Provides
    fun provideBannerDao(database: AppDatabase): BannerDao {
        return database.bannerDao()
    }
}
