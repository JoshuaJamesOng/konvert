package com.ongtonnesoup.konvert

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.ongtonnesoup.konvert.android.getProcessComponent
import com.ongtonnesoup.konvert.android.setFragmentManagers
import com.ongtonnesoup.konvert.appupdate.AppUpdateGateway
import com.ongtonnesoup.konvert.appupdate.CheckAppUpdateRequired
import com.ongtonnesoup.konvert.appupdate.GooglePlayAppUpdater
import com.ongtonnesoup.konvert.common.Dispatchers
import com.ongtonnesoup.konvert.di.ApplicationComponent
import com.ongtonnesoup.konvert.initialisation.CheckLocalRatesAvailable
import javax.inject.Inject
import com.ongtonnesoup.konvert.android.BUNDLE_KEY_SAVED_VIEWMODEL_STATE as SAVED_STATE

private const val APP_UPDATE_REQUEST_CODE = 20190508

class MainActivity : AppCompatActivity(), ApplicationComponent.Providerr {

    @Inject
    lateinit var fragmentFactory: FragmentFactory

    @Inject
    lateinit var checkAppUpdateRequired: CheckAppUpdateRequired

    @Inject
    lateinit var checkLocalRatesAvailable: CheckLocalRatesAvailable

    @Inject
    lateinit var appUpdateGateway: AppUpdateGateway<GooglePlayAppUpdater.Updater>

    @Inject
    lateinit var dispatchers: Dispatchers

    private val component: ApplicationComponent by lazy {
        getProcessComponent(this).getApplicationComponent()
    }

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        setFragmentManagers(this, fragmentFactory)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val initialState: State? = savedInstanceState?.getParcelable(SAVED_STATE)
            ?: State()
        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(
                initialState,
                checkAppUpdateRequired,
                checkLocalRatesAvailable,
                dispatchers
            )
        ).get(MainViewModel::class.java)

        viewModel.dispatch(Action.CheckRates)

        viewModel.observableState.observe(this, Observer { state ->
            state?.let { renderState(state) }
        })
    }

    override fun get(): ApplicationComponent = component

    private fun renderState(state: State) {
        with(state) {
            if (updateRequired) showUpdateDialog()
        }
    }

    private fun showUpdateDialog() {
        appUpdateGateway.update(object : GooglePlayAppUpdater.Updater {
            override fun update(updateManager: AppUpdateManager, updateInfo: AppUpdateInfo) {
                updateManager.startUpdateFlowForResult(
                    updateInfo,
                    AppUpdateType.IMMEDIATE,
                    this@MainActivity,
                    APP_UPDATE_REQUEST_CODE
                )
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            APP_UPDATE_REQUEST_CODE -> {
                when (resultCode) {
                    RESULT_OK ->
                        viewModel.dispatch(Action.UpdateComplete)

                    RESULT_CANCELED, ActivityResult.RESULT_IN_APP_UPDATE_FAILED ->
                        viewModel.dispatch(Action.RetryUpdate)
                }
            }
        }
    }
}
