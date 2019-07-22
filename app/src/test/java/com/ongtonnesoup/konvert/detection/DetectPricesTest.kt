package com.ongtonnesoup.konvert.detection

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.rubylichtenstein.rxtest.assertions.should
import com.rubylichtenstein.rxtest.assertions.shouldEmit
import com.rubylichtenstein.rxtest.extentions.test
import com.rubylichtenstein.rxtest.matchers.values
import io.reactivex.Observable
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object DetectPricesTest : Spek({

    describe("When detecting prices") {
        val testCases = mapOf(
            "Product Name" to Metadata(containsNumber = false),
            "12" to Metadata(containsNumber = true),
            "12.00" to Metadata(containsNumber = true),
            "£12" to Metadata(expectedSymbol = "£"),
            "$12.00" to Metadata(expectedSymbol = "$"),
            "Reduced to €12.00" to Metadata(expectedSymbol = "€"),
            "Was ¥12 now ¥10" to Metadata(expectedSymbol = "¥")
        )

        testCases.forEach { (detectedText, metadata) ->
            val gateway = mock<OcrGateway>()
            val cut = DetectPrices(gateway)

            context("$detectedText") {
                val parsedText = ParsedText(detectedText)
                whenever(gateway.init()).thenReturn(Observable.just(parsedText))

                val containsNumber = metadata.containsNumber

                it(if (containsNumber) "should be returned" else "should not be returned") {
                    cut.detectPrices().test {
                        if (containsNumber) {
                            val hasSymbol = metadata.expectedSymbol != null
                            if (hasSymbol) {
                                val symbol = metadata.expectedSymbol
                                it shouldEmit Number.Price(parsedText.text, Currency(symbol!!))
                            } else {
                                it shouldEmit Number.PossiblePrice(parsedText.text)
                            }
                        } else {
                            it should values() // Not emit
                        }
                    }
                }
            }
        }
    }
})

private class Metadata(
    val containsNumber: Boolean = true,
    val expectedSymbol: String? = null
) {
    fun containsNumber() = expectedSymbol != null
}
