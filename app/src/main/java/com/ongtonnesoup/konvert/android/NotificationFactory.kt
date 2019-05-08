package com.ongtonnesoup.konvert.android

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import com.ongtonnesoup.konvert.R
import com.ongtonnesoup.konvert.di.qualifiers.ContextType
import javax.inject.Inject

private const val DEFAULT_CHANNEL_ID = "DEFAULT_NOTIFICATION_CHANNEL"

class NotificationFactory @Inject constructor(@ContextType private val context: Context) {

    fun createNotification(
        @StringRes title: Int,
        @StringRes text: Int,
        @DrawableRes icon: Int? = null,
        channelId: String = DEFAULT_CHANNEL_ID
    ): Notification {
        val builder = with(NotificationCompat.Builder(context, channelId)) {
            icon?.let { setSmallIcon(it) }
            setContentTitle(context.getString(title))
            setContentText(context.getString(text))
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
        }

        return builder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createChannel(
        @StringRes nameId: Int = R.string.default_channel_name,
        @StringRes descriptionId: Int = R.string.default_channel_description,
        channelId: String = DEFAULT_CHANNEL_ID,
        importance: Int = NotificationManager.IMPORTANCE_DEFAULT
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(nameId)
            val descriptionText = context.getString(descriptionId)
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
