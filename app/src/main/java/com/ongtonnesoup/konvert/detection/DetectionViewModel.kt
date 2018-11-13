package com.ongtonnesoup.konvert.detection

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ongtonnesoup.konvert.di.scopes.PerFragment
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@PerFragment
class DetectionViewModel @Inject constructor(private val detectPrices: DetectPrices) : ViewModel() {

    private val _liveData = MutableLiveData<UiModel>()
    val liveData: LiveData<UiModel> = _liveData

    private val disposables = CompositeDisposable()

    @SuppressLint("CheckResult")
    fun startPresenting() {
        fun showPrice(price: Number) {
            _liveData.postValue(UiModel.Price(price.text))
        }

        fun showError(error: Throwable) {
            when (error) {
                is OcrGateway.InitializationError -> _liveData.postValue(UiModel.Error)
                else -> {
                    throw error
                }
            }
        }

        detectPrices.detectPrices()
                .doOnSubscribe { disposable -> disposables.add(disposable) }
                .subscribe(::showPrice, ::showError)
    }

    fun stopPresenting() {
        disposables.clear()
    }

    // TODO Name better
    sealed class UiModel {
        data class Price(val price: String) : UiModel()
        object Error : UiModel()
    }
}
