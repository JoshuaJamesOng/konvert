package com.ongtonnesoup.konvert.home

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.ongtonnesoup.konvert.BuildConfig
import com.ongtonnesoup.konvert.R
import com.ongtonnesoup.konvert.android.InitializerFragmentFactory
import com.ongtonnesoup.konvert.android.addInitializer
import com.ongtonnesoup.konvert.detection.DetectionFragment
import com.ongtonnesoup.konvert.detection.DetectionViewModel
import com.ongtonnesoup.konvert.di.ApplicationComponent
import kotlinx.android.synthetic.main.fragment_home.*
import javax.inject.Provider

private const val BUNDLE_KEY_STATE = "BUNDLE_KEY_STATE"

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (BuildConfig.USE_OCR) {
            // TODO This nicely

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val initialState: State? = savedInstanceState?.getParcelable(BUNDLE_KEY_STATE) ?: State(BuildConfig.USE_OCR)
        viewModel = ViewModelProviders.of(this, HomeViewModelFactory(initialState)).get(HomeViewModel::class.java)

        viewModel.observableState.observe(this, Observer { state ->
            state?.let { renderState(state) }
        })

        viewModel.observableEffects.observe(this, Observer { effect ->
            effect?.let { renderEffect(effect) }
        })

        settingsLink.setOnClickListener {
            viewModel.dispatch(Action.ShowSettings)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(BUNDLE_KEY_STATE, viewModel.observableState.value)
    }

    private fun renderState(state: State) {
        with(state) {
            if (showCameraView) showCameraView()
        }
    }

    private fun renderEffect(effect: Effect) {
        when (effect) {
            Effect.ShowSettings -> navigateToSettings()
        }
    }

    private fun showCameraView() {
        val component = getApplicationComponent(requireActivity())

        childFragmentManager.apply {
            fun createDetectionFragment(bundle: Bundle?): DetectionFragment {
                val vm = DetectionViewModel(component)
                return DetectionFragment(bundle, vm)
            }

            val fragmentFactory = InitializerFragmentFactory().apply {
                addInitializer { bundle ->
                    createDetectionFragment(bundle)
                }
            }
            this.fragmentFactory = fragmentFactory
            commit {
                add(R.id.fragment_container, createDetectionFragment(null)) // TODO should we go through FF methods
            }
        }
    }

    private fun navigateToSettings() {
        val showSettings = HomeFragmentDirections.actionShowSettings()
        findNavController().navigate(showSettings)
    }
}

private fun getApplicationComponent(activity: Activity): ApplicationComponent {
    val provider = activity.applicationContext as Provider<ApplicationComponent>
    return provider.get()
}
