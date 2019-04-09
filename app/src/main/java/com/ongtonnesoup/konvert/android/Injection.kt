package com.ongtonnesoup.konvert.android

import android.app.Activity
import androidx.fragment.app.Fragment
import com.ongtonnesoup.konvert.di.ApplicationComponent
import com.ongtonnesoup.konvert.di.ProcessComponent
import javax.inject.Provider

fun getProcessComponent(activity: Activity): ProcessComponent {
    val provider = activity.applicationContext as Provider<ProcessComponent>
    return provider.get()
}

fun getApplicationComponent(fragment: Fragment): ApplicationComponent {
    return getApplicationComponent(fragment.requireActivity())
}

fun getApplicationComponent(activity: Activity): ApplicationComponent {
    val provider = activity as Provider<ApplicationComponent>
    return provider.get()
}
