package com.ongtonnesoup.konvert.detection

data class Price(
        private val text: String,
        private val currency: Currency?
)

data class Currency(
        private val symbol: String
)