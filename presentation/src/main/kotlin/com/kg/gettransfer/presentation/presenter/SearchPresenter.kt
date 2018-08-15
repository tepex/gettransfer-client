package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.AsyncUtils

import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.domain.interactor.AddressInteractor

import com.kg.gettransfer.presentation.view.SearchView

import kotlinx.coroutines.experimental.*

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class SearchPresenter(private val cc: CoroutineContexts,
	                  private val router: Router,
	                  private val addressInteractor: AddressInteractor): MvpPresenter<SearchView>() {
	
	val compositeDisposable = Job()
	val utils = AsyncUtils(cc)
	
	override fun onFirstViewAttach() {
		val addr = addressInteractor.getCachedAddress()
		if(addr != null) viewState.setAddressFrom(addr.address)
	}

	fun requestAddressListByPrediction(prediction: String) = utils.launchAsync(compositeDisposable) {
		utils.asyncAwait {
			try {
				Thread.sleep(2000)
			} catch(e: InterruptedException) {
				Timber.w(e)
			}
		}
		val list = ArrayList<GTAddress>()
		for(i in 1..40) list.add(GTAddress("$prediction Item ${i+1}"))
		viewState.setAddressList(list)
	}

	fun onDestinationAddressSelected(address: GTAddress) {
		Timber.d("select address: %s", address);
	}
	
	fun onBackCommandClick() = viewState.finish()
}
