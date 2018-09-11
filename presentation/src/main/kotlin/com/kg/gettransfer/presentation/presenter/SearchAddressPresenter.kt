package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.domain.interactor.AddressInteractor
import com.kg.gettransfer.domain.interactor.ApiInteractor

import com.kg.gettransfer.presentation.view.SearchAddressView

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class SearchAddressPresenter(cc: CoroutineContexts,
                             router: Router,
                             apiInteractor: ApiInteractor,
	                         private val addressInteractor: AddressInteractor): BasePresenter<SearchAddressView>(cc, router, apiInteractor) {
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
			lastResult = utils.asyncAwait { addressInteractor.getAutocompletePredictions(prediction) }
			lastRequest = prediction
			viewState.setAddressList(lastResult!!)
		}, {e ->
			Timber.e(e)
			// if(e is ...) @TODO: обработать ошибки таймаута
			viewState.setError(false, R.string.err_address_service_xxx)
		})
	}
}
