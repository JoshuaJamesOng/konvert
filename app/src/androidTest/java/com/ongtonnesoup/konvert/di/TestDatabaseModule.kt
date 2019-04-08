package com.ongtonnesoup.konvert.currency.di

import androidx.room.Room
import android.content.Context
import com.ongtonnesoup.konvert.currency.data.local.AppDatabase
import com.ongtonnesoup.konvert.di.qualifiers.ContextType
import com.ongtonnesoup.konvert.di.qualifiers.Type
import com.ongtonnesoup.konvert.di.scopes.PerProcess
import dagger.Module
import dagger.Provides

@Module
object TestDatabaseModule {

    @PerProcess
    @Provides
    @JvmStatic
    fun provideDatabase(@ContextType(Type.APPLICATION) context: Context): AppDatabase {
        return Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .allowMainThreadQueries() // STOPSHIP
                .build()
    }
}
