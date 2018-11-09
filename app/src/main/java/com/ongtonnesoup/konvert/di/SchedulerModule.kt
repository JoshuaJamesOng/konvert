package com.ongtonnesoup.konvert.di

import androidx.work.WorkManager
import com.ongtonnesoup.konvert.currency.refresh.Scheduler
import com.ongtonnesoup.konvert.currency.refresh.WorkManagerScheduler
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module(includes = [SchedulerModule.Statics::class])
abstract class SchedulerModule {

    @Binds
    abstract fun provideScheduler(workManagerScheduler: WorkManagerScheduler): Scheduler

    @Module
    object Statics {
        @Provides
        @JvmStatic
        fun provideWorkManager() = WorkManager.getInstance()
    }
}