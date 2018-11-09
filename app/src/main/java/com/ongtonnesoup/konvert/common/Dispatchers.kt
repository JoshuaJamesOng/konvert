package com.ongtonnesoup.konvert.common

import android.os.AsyncTask
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import javax.inject.Inject

class Dispatchers @Inject constructor() {

    val execution: CoroutineDispatcher
        get() = AsyncTask.THREAD_POOL_EXECUTOR.asCoroutineDispatcher()

    val postExecution: CoroutineDispatcher
        get() = Dispatchers.Main
}