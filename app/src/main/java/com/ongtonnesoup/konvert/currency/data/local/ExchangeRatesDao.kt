package com.ongtonnesoup.konvert.currency.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.PrimaryKey
import androidx.room.Query

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
