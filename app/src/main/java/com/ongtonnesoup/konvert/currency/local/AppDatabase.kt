package com.ongtonnesoup.konvert.currency.local

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = arrayOf(ExchangeRatesDao.ExchangeRate::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun exchangeRatesDao(): ExchangeRatesDao

}
