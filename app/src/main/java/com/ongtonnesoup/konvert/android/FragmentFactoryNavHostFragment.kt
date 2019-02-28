package com.ongtonnesoup.konvert.android

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import javax.inject.Inject

class FragmentFactoryNavHostFragment : NavHostFragment() {

    @Inject
    lateinit var fragmentFactory: FragmentFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        getApplicationComponent(this).inject(this)
        setFragmentManagers(this, fragmentFactory)
        super.onCreate(savedInstanceState)
    }

    override fun createFragmentNavigator(): Navigator<out FragmentNavigator.Destination> {
        return FragmentFactoryFragmentNavigator(requireContext(), childFragmentManager, id, fragmentFactory)
    }

    @Navigator.Name("fragment")
    class FragmentFactoryFragmentNavigator(context: Context, manager: FragmentManager, containerId: Int, private val fragmentFactory: FragmentFactory) : FragmentNavigator(context, manager, containerId) {
        override fun instantiateFragment(context: Context,
                                         fragmentManager: FragmentManager,
                                         className: String,
                                         args: Bundle?): Fragment {
            return fragmentFactory.instantiate(context.classLoader, className, args) // TODO class loader
        }
    }
}