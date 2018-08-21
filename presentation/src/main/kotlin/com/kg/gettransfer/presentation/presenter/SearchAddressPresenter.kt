package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.AsyncUtils

import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.domain.interactor.AddressInteractor

import com.kg.gettransfer.presentation.view.SearchAddressView

import kotlinx.coroutines.experimental.Job

import timber.log.Timber

@InjectViewState
class SearchAddressPresenter(private val cc: CoroutineContexts,
	                         private val addressInteractor: AddressInteractor): MvpPresenter<SearchAddressView>() {
	
	val compositeDisposable = Job()
	val utils = AsyncUtils(cc)
	
	/* Cache. @TODO */
	private var lastRequest: String? = null
	private var lastResult: List<GTAddress>? = null
	
	companion object {
		@JvmField val ADDRESS_PREDICTION_SIZE = 3
	}
	
	fun requestAddressListByPrediction(prediction: String) {
		if(prediction.length < ADDRESS_PREDICTION_SIZE) {
			viewState.setAddressList(emptyList())
			return
		}
		if(prediction == lastRequest && lastResult != null) {
			Timber.d("------ From cache $lastRequest")
			viewState.setAddressList(lastResult!!)
			return
		}
			
		Timber.d("------ request list for prediction $prediction")
		utils.launchAsyncTryCatch(compositeDisposable, {
			lastResult = utils.asyncAwait { 
				addressInteractor.getAutocompletePredictions(prediction)
			}
			lastRequest = prediction
			viewState.setAddressList(lastResult!!)
		}, {e ->
			Timber.e(e)
			// if(e is ...) @TODO: обработать ошибки таймаута
			viewState.setError(R.string.err_address_service_xxx, false)
		})
	}
}
