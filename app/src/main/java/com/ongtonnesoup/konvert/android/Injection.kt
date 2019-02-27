package com.ongtonnesoup.konvert.android

import android.app.Activity
import androidx.fragment.app.Fragment
import com.ongtonnesoup.konvert.di.ApplicationComponent
import javax.inject.Provider

fun getApplicationComponent(fragment: Fragment): ApplicationComponent {
    return getApplicationComponent(fragment.requireActivity())
}

private fun getApplicationComponent(activity: Activity): ApplicationComponent {
    val provider = activity.applicationContext as Provider<ApplicationComponent>
    return provider.get()
}