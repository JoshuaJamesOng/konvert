package com.ongtonnesoup.konvert.android

import android.app.Activity
import androidx.fragment.app.Fragment
import com.ongtonnesoup.konvert.di.ApplicationComponent
import com.ongtonnesoup.konvert.di.ProcessComponent

fun getProcessComponent(activity: Activity): ProcessComponent {
    val provider = activity.applicationContext as? ProcessComponent.Providerr
            ?: throw IllegalStateException("Application does not provide {${ProcessComponent::class}}")

    return provider.get()
}

fun getApplicationComponent(fragment: Fragment): ApplicationComponent {
    return getApplicationComponent(fragment.requireActivity())
}

fun getApplicationComponent(activity: Activity): ApplicationComponent {
    val provider = activity as? ApplicationComponent.Providerr
            ?: throw IllegalStateException("Activity does not provide {${ApplicationComponent::class}}")

    return provider.get()
}
