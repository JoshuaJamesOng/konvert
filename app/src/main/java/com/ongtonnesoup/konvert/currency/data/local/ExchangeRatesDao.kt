package com.ongtonnesoup.konvert.currency.data.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import android.arch.persistence.room.PrimaryKey

@Dao
interface ExchangeRatesDao {

    @Query("select * from exchange_rates")
    fun getAll(): List<ExchangeRate>

    @Insert(onConflict = REPLACE)
    fun insert(exchangeRates: ExchangeRate): Long

    @Query("delete from exchange_rates")
    fun clear()

    @Entity(tableName = "exchange_rates")
    data class ExchangeRate(
            val currency: String,
            val rate: Double,
            val timestamp: Long = System.currentTimeMillis(),
            @PrimaryKey(autoGenerate = true) val id: Long = 0
    )
}
