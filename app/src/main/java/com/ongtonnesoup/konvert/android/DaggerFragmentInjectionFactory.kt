package com.ongtonnesoup.konvert.android

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.ajalt.timberkt.Timber
import javax.inject.Inject
import javax.inject.Provider

// TODO Scope
class DaggerFragmentInjectionFactory @Inject constructor(
        private val creators: Map<Class<out Fragment>, @JvmSuppressWildcards Provider<Fragment>> // TODO Why do we need the suppression again?
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String, args: Bundle?): Fragment {
        Timber.d { "Size: ${creators.keys.elementAt(0)}" }
        val fragmentClass = loadFragmentClass(classLoader, className)
        Timber.d { "Find for $fragmentClass" }
        val provider = creators[fragmentClass]

        if (provider == null) {
            Timber.d { "No provider" }
            return super.instantiate(classLoader, className, args)
        }

        try {
            val fragment = provider.get()
            fragment.arguments = args
            return fragment
        } catch (exception: Exception) {
            throw RuntimeException(exception)
        }
    }
}
