package com.ongtonnesoup.konvert.detection

import io.reactivex.Observable

private const val CONTAINS_NUMBERS = ".*\\d+.*"
private const val CONTAINS_SYMBOL = "\\p{Sc}"

class DetectPrices(private val gateway: OcrGateway) {

    fun detectPrices(): Observable<Price> {
        return gateway.init()
                .filter { isNumber(it) }
                .map { Price(it.text, getCurrency(it)) }
                .doOnDispose { gateway.release() }
    }

    private fun isNumber(parsedText: ParsedText) = parsedText.text.matches(CONTAINS_NUMBERS.toRegex())

    private fun getCurrency(parsedText: ParsedText): Currency? {
        val result = CONTAINS_SYMBOL.toRegex().find(parsedText.text)
        return if (result != null) Currency(result.value) else null
    }
}
