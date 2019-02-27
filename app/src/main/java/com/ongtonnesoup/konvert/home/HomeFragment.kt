package com.ongtonnesoup.konvert.home

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
import kotlinx.android.synthetic.main.fragment_home.*
import com.ongtonnesoup.konvert.android.BUNDLE_KEY_SAVED_VIEWMODEL_STATE as SAVED_STATE

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fun createDetectionFragment(bundle: Bundle?): DetectionFragment {
            return DetectionFragment()
        }

        // TODO Set-up DI for this stuff https://www.captechconsulting.com/blogs/using-androidxs-fragmentfactory-with-dagger-for-fragment-dependency-injection
        val fragmentFactory = InitializerFragmentFactory().apply {
            addInitializer { bundle ->
                createDetectionFragment(bundle)
            }
        }

        childFragmentManager.apply {
            this.fragmentFactory = fragmentFactory
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val initialState: State? = savedInstanceState?.getParcelable(SAVED_STATE)
                ?: State(BuildConfig.USE_OCR)
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
        outState.putParcelable(SAVED_STATE, viewModel.observableState.value)
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
        childFragmentManager.apply {
            findFragmentByTag(DetectionFragment.TAG)?.let {
                if (isAdded) return@apply
            }

            val fragment = fragmentFactory.instantiate(
                    requireActivity().classLoader,
                    DetectionFragment::class.java.name,
                    null)

            commit {
                replace(R.id.fragment_container, fragment, DetectionFragment.TAG)
            }
        }
    }

    private fun navigateToSettings() {
        val showSettings = HomeFragmentDirections.actionShowSettings()
        findNavController().navigate(showSettings)
    }
}
