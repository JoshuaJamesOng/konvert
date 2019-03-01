package com.ongtonnesoup.konvert

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.ongtonnesoup.konvert.android.getProcessComponent
import com.ongtonnesoup.konvert.android.setFragmentManagers
import com.ongtonnesoup.konvert.common.Dispatchers
import com.ongtonnesoup.konvert.di.ApplicationComponent
import com.ongtonnesoup.konvert.initialisation.CheckLocalRatesAvailable
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Provider

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

        val viewModel = ViewModelProviders.of(this, MainViewModelFactory(checkLocalRatesAvailable, dispatchers)).get(MainViewModel::class.java)
    }

    override fun get(): ApplicationComponent = component
}

class MainViewModel(
        private val checkLocalRatesAvailable: CheckLocalRatesAvailable,
        private val dispatchers: Dispatchers
) : ViewModel() {
    init {
        checkRates()
    }

    private fun checkRates() {
        GlobalScope.launch {
            withContext(dispatchers.execution) {
                checkLocalRatesAvailable.checkLocalRatesAvailable()
            }
        }
    }
}

class MainViewModelFactory(
        private val checkLocalRatesAvailable: CheckLocalRatesAvailable,
        private val dispatchers: Dispatchers
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(checkLocalRatesAvailable, dispatchers) as T
    }
}