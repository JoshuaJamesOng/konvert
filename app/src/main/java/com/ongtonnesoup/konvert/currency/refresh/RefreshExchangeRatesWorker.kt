package com.ongtonnesoup.konvert.currency.refresh

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.R
import com.ongtonnesoup.konvert.android.NotificationFactory
import com.ongtonnesoup.konvert.state.RefreshState
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val SYNC_CHANNEL_ID = "SYNC_NOTIFICATION_CHANNEL"
private const val SYNC_NOTIFICATION_ID = 20190509

class RefreshExchangeRatesWorker(context: Context, params: WorkerParameters) :
    Worker(context, params) {

    @Inject
    lateinit var interactor: RefreshThenRescheduleExchangeRates

    @Inject
    lateinit var notificationFactory: NotificationFactory

    override fun doWork(): Result {
        Timber.d { "Starting update exchange rates job" }
        inject()

        val status = runBlocking {
            interactor.refreshThenReschedule()
        }

        if (isSyncNotificationRequired()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel()
            }
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        notificationFactory.createChannel(
            R.string.sync_channel_name,
            R.string.sync_channel_description,
            SYNC_CHANNEL_ID
        )
    }

    private fun showSyncNotification() {
        val notification = notificationFactory.createNotification(
            title = R.string.sync_notification_title,
            text = R.string.sync_notification_text,
            channelId = SYNC_CHANNEL_ID
        ) // TODO Show notification with base currency hero'd

        with(NotificationManagerCompat.from(applicationContext)) {
            notify(SYNC_NOTIFICATION_ID, notification)
        }
    }

    interface Injector {
        fun inject(target: RefreshExchangeRatesWorker)
    }
}
