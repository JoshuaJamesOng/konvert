package com.ongtonnesoup.konvert.appupdate

interface AppUpdateGateway<T> {

    fun isUpdateRequired(): Boolean

    fun update(updater: T)

}