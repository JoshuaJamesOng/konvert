package com.ongtonnesoup.konvert.currency.refresh

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.state.RefreshState
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class RefreshExchangeRatesWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    @Inject
    lateinit var interactor: RefreshThenRescheduleExchangeRates

    override fun doWork(): Result {
        Timber.d { "Starting update exchange rates job" }
        inject()

        val status = runBlocking {
            interactor.refreshThenReschedule()
        }

        if (isSyncNotificationRequired()) {
            showSyncNotification()
        }

        return if (status == RefreshState.SCHEDULED) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    private fun inject() {
        (applicationContext as Injector).inject(this)
    }

    private fun isSyncNotificationRequired(): Boolean {
        val preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        return preferences.getBoolean("sync_notification", false)
    }

    private fun showSyncNotification() {
        TODO("Show notification with base currency hero'd")
    }

    interface Injector {
        fun inject(target: RefreshExchangeRatesWorker)
    }
}
