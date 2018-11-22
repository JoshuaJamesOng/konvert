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
import com.ongtonnesoup.konvert.android.KonvertFragmentFactory
import com.ongtonnesoup.konvert.di.ApplicationComponent
import javax.inject.Provider

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.USE_OCR) {
            // TODO This nicely
            val component = getApplicationComponent(requireActivity())

            childFragmentManager.apply {

                val konvertFragmentFactory = KonvertFragmentFactory(component)
                fragmentFactory = konvertFragmentFactory
                commit {
                    add(R.id.fragment_container, konvertFragmentFactory.createDetectionFragment()) // TODO should we go through FF methods
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