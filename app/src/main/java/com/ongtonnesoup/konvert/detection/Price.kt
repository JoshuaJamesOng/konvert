package com.ongtonnesoup.konvert.detection

sealed class Number(val text: String) {
    data class PossiblePrice(val parsedText: String) : Number(parsedText)

    data class Price(
            val parsedText: String,
            val currency: Currency?
    ) : Number(parsedText)
}

data class Currency(
        val symbol: String
)
