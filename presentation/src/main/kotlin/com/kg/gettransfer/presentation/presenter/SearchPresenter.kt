package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.AsyncUtils

import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.domain.interactor.AddressInteractor

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.view.SearchView

import kotlinx.coroutines.experimental.Job

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class SearchPresenter(private val cc: CoroutineContexts,
	                  private val router: Router,
	                  private val addressInteractor: AddressInteractor): MvpPresenter<SearchView>() {
	
	var isTo = false
	
	private val compositeDisposable = Job()
	private val utils = AsyncUtils(cc)
	
	private lateinit var addressFrom: GTAddress
	private var addressTo: GTAddress? = null
	
	companion object {
		@JvmField val ADDRESS_PREDICTION_SIZE = 3
	}
	
	override fun onFirstViewAttach() {
		utils.launchAsyncTryCatchFinally(compositeDisposable, {
			viewState.blockInterface(true)
			addressFrom = utils.asyncAwait { addressInteractor.getCurrentAddress() }
			viewState.setAddressFrom(addressFrom.name)
		}, { e ->
			Timber.e(e)
			viewState.setError(R.string.err_address_service_xxx, false)
		}, {viewState.blockInterface(false)})
	}

	fun onAddressSelected(selected: GTAddress) {
		Timber.d(">>>> ${selected.name} ${selected.isConcreteObject()}")
		
		if(isTo) addressTo = selected
		else addressFrom = selected
		
		if(addressFrom.isConcreteObject() && addressTo?.isConcreteObject() ?: false) {
			addressInteractor.route = Pair(addressFrom, addressTo!!)
			router.navigateTo(Screens.CREATE_ORDER)
			return
		}
		if(isTo) viewState.setAddressTo(selected.primary ?: selected.name)
		else viewState.setAddressFrom(selected.primary ?: selected.name)
	}

	fun onBackCommandClick() = viewState.finish()
	
	@CallSuper
	override fun onDestroy() {
		compositeDisposable.cancel()
		super.onDestroy()
	}
}
