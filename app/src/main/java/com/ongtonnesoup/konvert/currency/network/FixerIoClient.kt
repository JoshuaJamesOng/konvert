package com.ongtonnesoup.konvert.currency.network

import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import retrofit2.http.GET

interface FixerIoClient {

    @GET("latest")
    fun getLatest(base: String): Single<FixerIoResponse>

    data class FixerIoResponse(
            @SerializedName("base") val base: String,
            @SerializedName("date") val date: String,
            @SerializedName("rates") val rates: List<ExchangeRate>
    )

    data class ExchangeRate(
            @SerializedName("") val currency: String,
            @SerializedName("") val rate: Double
    )

}