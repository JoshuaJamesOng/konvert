package com.ongtonnesoup.konvert.currency.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(ExchangeRatesDao.ExchangeRate::class), version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun exchangeRatesDao(): ExchangeRatesDao

}
