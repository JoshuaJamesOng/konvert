package com.ongtonnesoup.konvert

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProviders
import com.ongtonnesoup.konvert.android.getProcessComponent
import com.ongtonnesoup.konvert.android.setFragmentManagers
import com.ongtonnesoup.konvert.common.Dispatchers
import com.ongtonnesoup.konvert.di.ApplicationComponent
import com.ongtonnesoup.konvert.initialisation.CheckLocalRatesAvailable
import javax.inject.Inject
import javax.inject.Provider
import com.ongtonnesoup.konvert.android.BUNDLE_KEY_SAVED_VIEWMODEL_STATE as SAVED_STATE

class MainActivity : AppCompatActivity(), Provider<ApplicationComponent> {

    @Inject
    lateinit var fragmentFactory: FragmentFactory

    @Inject
    lateinit var checkLocalRatesAvailable: CheckLocalRatesAvailable

    @Inject
    lateinit var dispatchers: Dispatchers

    private val component: ApplicationComponent by lazy {
        getProcessComponent(this).getApplicationComponent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        setFragmentManagers(this, fragmentFactory)
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val initialState: State? = savedInstanceState?.getParcelable(SAVED_STATE)
                ?: State()
        val viewModel = ViewModelProviders.of(this, MainViewModelFactory(initialState, checkLocalRatesAvailable, dispatchers)).get(MainViewModel::class.java)

        viewModel.dispatch(Action.CheckRates)
    }

    override fun get(): ApplicationComponent = component
}
