package com.ongtonnesoup.konvert.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.transaction
import com.ongtonnesoup.konvert.BuildConfig

import com.ongtonnesoup.konvert.R
import com.ongtonnesoup.konvert.detection.DetectionFragment

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        if (BuildConfig.USE_OCR) {
            val detectionFragment = DetectionFragment() // TODO Use Fragment Factory
            childFragmentManager.transaction {
                add(R.id.fragment_container, detectionFragment)
            }
        }

        return view
    }
}
