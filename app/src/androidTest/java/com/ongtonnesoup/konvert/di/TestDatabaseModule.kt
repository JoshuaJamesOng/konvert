package com.ongtonnesoup.konvert.currency.di

import android.arch.persistence.room.Room
import android.content.Context
import com.ongtonnesoup.konvert.currency.data.local.AppDatabase
import dagger.Module
import dagger.Provides

@Module
object TestDatabaseModule {

    @Provides
    @JvmStatic
    fun provideDatabase(context: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .allowMainThreadQueries() // STOPSHIP
                .build()
    }

}
