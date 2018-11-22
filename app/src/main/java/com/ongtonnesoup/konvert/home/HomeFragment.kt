package com.ongtonnesoup.konvert.home

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import com.ongtonnesoup.konvert.BuildConfig
import com.ongtonnesoup.konvert.R
import com.ongtonnesoup.konvert.android.InitializerFragmentFactory
import com.ongtonnesoup.konvert.android.addInitializer
import com.ongtonnesoup.konvert.detection.DetectionFragment
import com.ongtonnesoup.konvert.detection.DetectionViewModel
import com.ongtonnesoup.konvert.di.ApplicationComponent
import javax.inject.Provider

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.USE_OCR) {
            // TODO This nicely
            val component = getApplicationComponent(requireActivity())

            childFragmentManager.apply {
                fun createDetectionFragment(bundle: Bundle?): DetectionFragment {
                    val vm = DetectionViewModel(component)
                    return DetectionFragment(bundle, vm)
                }

                val fragmentFactory = InitializerFragmentFactory().apply {
                    addInitializer {bundle ->
                        createDetectionFragment(bundle)
                    }
                }
                this.fragmentFactory = fragmentFactory
                commit {
                    add(R.id.fragment_container, createDetectionFragment(null)) // TODO should we go through FF methods
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}

private fun getApplicationComponent(activity: Activity): ApplicationComponent {
    val provider = activity.applicationContext as Provider<ApplicationComponent>
    return provider.get()
}