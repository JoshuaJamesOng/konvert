package com.ongtonnesoup.konvert.currency.refresh

import com.ongtonnesoup.konvert.state.AppState
import com.ongtonnesoup.konvert.state.RefreshState
import com.ongtonnesoup.konvert.state.updateRefreshState
import io.reactivex.Completable
import javax.inject.Inject

class ScheduleRefresh @Inject constructor(
        private val scheduler: Scheduler,
        private val appState: AppState
) {

    fun scheduleRefresh(): Completable {
        return scheduler.schedule()
                .doOnComplete { updateRefreshState(appState, RefreshState.SCHEDULED) }
    }

}