package com.ongtonnesoup.konvert.detection

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.rubylichtenstein.rxtest.assertions.should
import com.rubylichtenstein.rxtest.assertions.shouldEmit
import com.rubylichtenstein.rxtest.extentions.test
import com.rubylichtenstein.rxtest.matchers.values
import io.reactivex.Observable
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

class DetectPricesTest : Spek({

    val testCases = mapOf(
            "Product Name" to Pair(false, null),
            "12" to Pair(true, null),
            "12.00" to Pair(true, null),
            "£12" to Pair(true, "£"),
            "$12.00" to Pair(true, "$"),
            "Reduced to €12.00" to Pair(true, "€"),
            "Was ¥12 now ¥10" to Pair(true, "¥")
    )

    given("price detector") {
        val gateway = mock<OcrGateway>()
        val cut = DetectPrices(gateway)

        testCases.forEach { detectedText, expected ->

            on("$detectedText") {
                val parsedText = ParsedText(detectedText)
                whenever(gateway.init()).thenReturn(Observable.just(parsedText))

                val containsNumber = expected.first

                it(if (containsNumber) "it is returned" else "it is not returned") {
                    cut.detectPrices().test {
                        if (containsNumber) {
                            val hasSymbol = expected.second != null
                            if (hasSymbol) {
                                val symbol = expected.second
                                it shouldEmit Price(parsedText.text, Currency(symbol!!))
                            } else {
                                it shouldEmit Price(parsedText.text, null)
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
