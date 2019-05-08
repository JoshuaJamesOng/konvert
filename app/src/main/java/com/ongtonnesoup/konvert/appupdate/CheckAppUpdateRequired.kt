package com.ongtonnesoup.konvert.appupdate

import javax.inject.Inject

class CheckAppUpdateRequired @Inject constructor(
    private val gateway: AppUpdateGateway<*>
) {
    fun appUpdateRequired(): Boolean {
        return gateway.isUpdateInProgress() || gateway.isUpdateRequired()
    }
}