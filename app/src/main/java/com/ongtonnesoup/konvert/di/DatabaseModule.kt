package com.ongtonnesoup.konvert.di

import android.arch.persistence.room.Room
import android.content.Context
import com.ongtonnesoup.konvert.currency.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Singleton
    @Provides
    @JvmStatic
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "konvert-db")
                .allowMainThreadQueries() // STOPSHIP
                .build()
    }

}
