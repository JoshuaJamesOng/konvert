package com.ongtonnesoup.konvert.currency.local

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import android.arch.persistence.room.PrimaryKey
import io.reactivex.Flowable

@Dao
interface ExchangeRatesDao {

    @Query("select * from exchange_rates")
    fun getAll(): Flowable<List<ExchangeRate>>

    @Insert(onConflict = REPLACE)
    fun insert(exchangeRates: ExchangeRate)

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