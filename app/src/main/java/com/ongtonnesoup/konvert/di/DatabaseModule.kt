package com.ongtonnesoup.konvert.di

import android.content.Context
import androidx.room.Room
import com.ongtonnesoup.konvert.currency.data.local.AppDatabase
import com.ongtonnesoup.konvert.di.qualifiers.ContextType
import com.ongtonnesoup.konvert.di.qualifiers.Type
import com.ongtonnesoup.konvert.di.scopes.PerProcess
import dagger.Module
import dagger.Provides

@Module
object DatabaseModule {

    @PerProcess
    @Provides
    @JvmStatic
    fun provideDatabase(@ContextType(Type.APPLICATION) context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "konvert-db")
            .allowMainThreadQueries() // STOPSHIP
            .build()
    }
}
