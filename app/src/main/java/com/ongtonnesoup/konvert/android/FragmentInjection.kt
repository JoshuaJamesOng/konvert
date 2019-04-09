package com.ongtonnesoup.konvert.android

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory

fun setFragmentManagers(activity: AppCompatActivity, fragmentFactory: FragmentFactory) {
    activity.supportFragmentManager.fragmentFactory = fragmentFactory
}

fun setFragmentManagers(fragment: Fragment, fragmentFactory: FragmentFactory) {
    fragment.requireFragmentManager().fragmentFactory = fragmentFactory
    fragment.childFragmentManager.fragmentFactory = fragmentFactory
}
