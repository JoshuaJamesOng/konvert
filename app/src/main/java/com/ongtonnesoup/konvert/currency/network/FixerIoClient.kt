package com.ongtonnesoup.konvert.currency.network

import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface FixerIoClient {

    @GET("latest")
    fun getLatest(@Query("base") base: String): Single<FixerIoResponse>

    data class FixerIoResponse(
            @SerializedName("base") val base: String,
            @SerializedName("date") val date: String,
            @SerializedName("rates") val rates: Map<String, Double>
    )
//
//    data class ExchangeRate(
//            @SerializedName("") val currency: String,
//            @SerializedName("") val rate: Double
//    )

}