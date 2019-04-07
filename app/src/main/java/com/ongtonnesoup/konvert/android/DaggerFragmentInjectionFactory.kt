package com.ongtonnesoup.konvert.android

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.ajalt.timberkt.Timber
import com.ongtonnesoup.konvert.di.scopes.PerAppForegroundProcess
import javax.inject.Inject
import javax.inject.Provider

@PerAppForegroundProcess
class DaggerFragmentInjectionFactory @Inject constructor(
        private val creators: Map<Class<out Fragment>, @JvmSuppressWildcards Provider<Fragment>> // TODO Why do we need the suppression again?
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        Timber.d { "Size: ${creators.keys.elementAt(0)}" }
        val fragmentClass = loadFragmentClass(classLoader, className)
        Timber.d { "Find for $fragmentClass" }
        val provider = creators[fragmentClass]

        if (provider == null) {
            Timber.d { "No provider" }
            return super.instantiate(classLoader, className)
        }

        try {
            return provider.get()
        } catch (exception: Exception) {
            throw RuntimeException(exception)
        }
    }
}
