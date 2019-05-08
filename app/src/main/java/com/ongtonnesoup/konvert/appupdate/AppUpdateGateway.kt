package com.ongtonnesoup.konvert.appupdate

interface AppUpdateGateway<T> {

    fun isUpdateRequired(): Boolean

    fun isUpdateInProgress(): Boolean

    fun update(updater: T)
}
