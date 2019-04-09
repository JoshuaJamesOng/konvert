package com.ongtonnesoup.common

import com.ongtonnesoup.konvert.common.Dispatchers
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}


// TODO could this be of use? rxCompletable: https://bit.ly/2VF2ZQh
fun toSuspendableCompletable(func: suspend () -> Unit, dispatchers: Dispatchers): Completable {
    return Completable.create { emitter ->
        GlobalScope.launch {
            withContext(dispatchers.execution) {
                runCatching {
                    func()
                    emitter.onComplete()
                }.getOrElse {
                    emitter.onError(it)
                }
            }
        }
    }
}
