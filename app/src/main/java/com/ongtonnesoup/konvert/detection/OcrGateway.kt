package com.ongtonnesoup.konvert.detection

import io.reactivex.Observable
import java.lang.RuntimeException

interface OcrGateway {

    fun init(): Observable<ParsedText>

    fun release()

    class InitializationError : RuntimeException()
}
