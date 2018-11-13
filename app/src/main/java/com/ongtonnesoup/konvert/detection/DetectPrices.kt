package com.ongtonnesoup.konvert.detection

import io.reactivex.Observable
import javax.inject.Inject

private const val CONTAINS_NUMBERS = ".*\\d+.*"
private const val CONTAINS_SYMBOL = "\\p{Sc}"

class DetectPrices @Inject constructor(private val gateway: OcrGateway) {

    fun detectPrices(): Observable<Number> {
        return gateway.init()
                .filter { isNumber(it) }
                .map { parsedText ->
                    val currency = getCurrency(parsedText)

                    // TODO Check symbol is near numbers and not e.g. 'Save £££ on the 13/10/2018`
                    currency?.let { symbol ->
                        Number.Price(parsedText.text, symbol)
                    } ?: run {
                        Number.PossiblePrice(parsedText.text)
                    }
                }
                .doOnDispose { gateway.release() }
    }

    private fun isNumber(parsedText: ParsedText) = parsedText.text.matches(CONTAINS_NUMBERS.toRegex())

    private fun getCurrency(parsedText: ParsedText): Currency? {
        val result = CONTAINS_SYMBOL.toRegex().find(parsedText.text)
        return if (result != null) Currency(result.value) else null
    }
}
