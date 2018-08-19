package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.AsyncUtils

import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.domain.interactor.AddressInteractor

import com.kg.gettransfer.presentation.view.SearchView

import kotlinx.coroutines.experimental.Job

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class SearchPresenter(private val cc: CoroutineContexts,
	                  private val router: Router,
	                  private val addressInteractor: AddressInteractor): MvpPresenter<SearchView>() {
	
	val compositeDisposable = Job()
	val utils = AsyncUtils(cc)
	
	companion object {
		@JvmField val ADDRESS_PREDICTION_SIZE = 3
	}
	
	override fun onFirstViewAttach() {
		utils.launchAsync(compositeDisposable) {
			val currentAddress = utils.asyncAwait { addressInteractor.getCachedAddress() }
			if(currentAddress != null) viewState.setAddressFrom(currentAddress)
		}
	}

	/*
	fun requestAddressListByPrediction(prediction: String) {
		if(prediction.length < ADDRESS_PREDICTION_SIZE) {
			viewState.setAddressList(emptyList())
			return
		}
		Timber.d("------ request list for prediction $prediction")
		utils.launchAsyncTryCatchFinally(compositeDisposable, {
			viewState.blockInterface(true)
			
			val list = utils.asyncAwait { addressInteractor.getAutocompletePredictions(prediction) }
			viewState.setAddressList(list)
		}, {e ->
			Timber.e(e)
			// if(e is ...) @TODO: обработать ошибки таймаута
			viewState.setError(R.string.err_address_service_xxx, false)
		}, {
			viewState.blockInterface(false)
		})
	}
	*/
	
	fun onDestinationAddressSelected(address: GTAddress) {
		Timber.d("select address from list: $address")
		//viewState.setAddress(address)
	}
	
	fun onBackCommandClick() = viewState.finish()
}
