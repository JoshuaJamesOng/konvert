package com.ongtonnesoup.konvert.detection

sealed class Number(val text: String, open val position: DetectionPosition) {
    data class PossiblePrice(val parsedText: String, override val position: DetectionPosition) :
        Number(parsedText, position)

    data class Price(
        val parsedText: String,
        val currency: Currency?,
        override val position: DetectionPosition
    ) : Number(parsedText, position)
}

data class Currency(
    val symbol: String
)
