package com.ongtonnesoup.konvert.di

import android.content.Context
import com.ongtonnesoup.konvert.KonvertApplication
import com.ongtonnesoup.konvert.di.qualifiers.ContextType
import com.ongtonnesoup.konvert.di.qualifiers.Type
import com.ongtonnesoup.konvert.di.scopes.PerProcess
import dagger.BindsInstance
import dagger.Component

@PerProcess
@Component(
    modules = [
        ProcessModule::class,
        StateModule::class,
        NetworkModule::class,
        DatabaseModule::class,
        SchedulerModule::class
    ]
)
interface ProcessComponent {
    fun inject(application: KonvertApplication)

    fun getApplicationComponent(): ApplicationComponent

    fun getWorkerComponent(): WorkerComponent

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance @PerProcess @ContextType(Type.APPLICATION) context: Context
        ): ProcessComponent
    }

    // Not a typo. Dagger's generated code does not fully quality it's `Provider` import
    interface Providerr {
        fun get(): ProcessComponent
    }
}
