package com.ongtonnesoup.konvert.currency

import android.app.Application
import android.content.Context
import android.support.test.runner.AndroidJUnitRunner

class TestJUnitRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, TestApplication::class.java.name, context)
    }
}