package com.ongtonnesoup.konvert.di

import androidx.room.Room
import android.content.Context
import com.ongtonnesoup.konvert.currency.data.local.AppDatabase
import com.ongtonnesoup.konvert.di.scopes.PerAppForegroundProcess
import com.ongtonnesoup.konvert.di.scopes.PerProcess
import dagger.Module
import dagger.Provides

@Module
object DatabaseModule {

    @PerProcess
    @Provides
    @JvmStatic
    fun provideDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "konvert-db")
                .allowMainThreadQueries() // STOPSHIP
                .build()
    }

}
