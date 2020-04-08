package com.ongtonnesoup.konvert.detection

import io.reactivex.Observable

interface OcrGateway {

    fun init(): Observable<List<ParsedText>>

    fun release()

    class InitializationError : RuntimeException()
}
