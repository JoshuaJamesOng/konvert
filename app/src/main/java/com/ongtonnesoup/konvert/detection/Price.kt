package com.ongtonnesoup.konvert.detection

data class Price(
        val text: String,
        val currency: Currency?
)

data class Currency(
        val symbol: String
)
