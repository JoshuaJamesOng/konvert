package com.ongtonnesoup.konvert.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.ongtonnesoup.konvert.android.DaggerFragmentInjectionFactory
import com.ongtonnesoup.konvert.di.key.FragmentKey
import com.ongtonnesoup.konvert.home.HomeFragment
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class FragmentBindingModule {

    @Binds
    @IntoMap
    @FragmentKey(HomeFragment::class)
    abstract fun bindHomeFragment(homeFragment: HomeFragment) : Fragment

    @Binds
    abstract fun bindFragmentFactory(factory: DaggerFragmentInjectionFactory): FragmentFactory
}