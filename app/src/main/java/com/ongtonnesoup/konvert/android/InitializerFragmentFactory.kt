package com.ongtonnesoup.konvert.android

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import kotlin.reflect.KClass

class InitializerFragmentFactory : FragmentFactory() {
    private val initializers = mutableMapOf<String, (args: Bundle?) -> Fragment>()
    operator fun get(clazz: KClass<Fragment>): ((args: Bundle?) -> Fragment)? {
        return initializers[clazz.java.name]
    }

    operator fun <F : Fragment> set(clazz: KClass<F>, initializer: (args: Bundle?) -> F) {
        initializers[clazz.java.name] = initializer
    }

    override fun instantiate(classLoader: ClassLoader, className: String, args: Bundle?) =
            initializers[className]?.invoke(args)
                    ?: super.instantiate(classLoader, className, args)
}

inline fun <reified F : Fragment> InitializerFragmentFactory.addInitializer(
        noinline initializer: (args: Bundle?) -> F
) {
    this[F::class] = initializer
}
