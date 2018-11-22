package com.ongtonnesoup.konvert.android

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.ongtonnesoup.konvert.detection.DetectionFragment
import com.ongtonnesoup.konvert.detection.DetectionViewModel
import com.ongtonnesoup.konvert.di.ApplicationComponent

class KonvertFragmentFactory(private val component: ApplicationComponent) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String, args: Bundle?): Fragment {
        return if (className == DetectionFragment::class.java.name) {
            createDetectionFragment()
        } else {
            super.instantiate(classLoader, className, args)
        }
    }

    fun createDetectionFragment(): DetectionFragment {
        val vm = DetectionViewModel(component)
        return DetectionFragment(vm)
    }
}
