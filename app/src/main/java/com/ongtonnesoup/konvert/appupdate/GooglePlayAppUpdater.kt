package com.ongtonnesoup.konvert.appupdate

import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.ongtonnesoup.konvert.di.qualifiers.ContextType
import com.ongtonnesoup.konvert.di.scopes.PerAppForegroundProcess
import javax.inject.Inject

@PerAppForegroundProcess
class GooglePlayAppUpdater @Inject constructor(@ContextType private val context: Context) :
    AppUpdateGateway<GooglePlayAppUpdater.Updater> {

    override fun isUpdateRequired(): Boolean {
        return getAppUpdate { _, updateInfo ->
            val isUpdateAvailable =
                updateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val isUpdateRequired = updateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            return@getAppUpdate isUpdateAvailable && isUpdateRequired
        } ?: false
    }

    override fun update(updater: Updater) {
        getAppUpdate(andThen = { updateManager, updateInfo ->
            updater.update(updateManager, updateInfo)
        })
    }

    private fun <T> getAppUpdate(
        andThen: (AppUpdateManager, AppUpdateInfo) -> T
    ): T? {
        val updateManager = AppUpdateManagerFactory.create(context)

        val updateInfoTask = updateManager.appUpdateInfo

        return runCatching {
            val updateInfo = updateInfoTask.result
            andThen.invoke(updateManager, updateInfo)
        }.getOrNull()
    }

    interface Updater {
        fun update(updateManager: AppUpdateManager, updateInfo: AppUpdateInfo)
    }
}
