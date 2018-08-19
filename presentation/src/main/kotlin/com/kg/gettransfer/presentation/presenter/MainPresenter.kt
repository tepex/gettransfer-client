package com.kg.gettransfer.presentation.presenter

import android.location.Location

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.AsyncUtils

import com.kg.gettransfer.domain.interactor.AddressInteractor
import com.kg.gettransfer.domain.interactor.LocationInteractor

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.model.AddressPair
import com.kg.gettransfer.presentation.view.MainView

import kotlinx.coroutines.experimental.Job

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class MainPresenter(private val cc: CoroutineContexts,
	                private val router: Router,
	                private val locationInteractor: LocationInteractor,
	                private val addressInteractor: AddressInteractor): MvpPresenter<MainView>() {
	                
	var granted = false
	
	private val compositeDisposable = Job()
	private val utils = AsyncUtils(cc)
	
	override fun onFirstViewAttach() {
		utils.launchAsync(compositeDisposable) {
			Timber.d("onFirstViewAttach()")
			if(granted) {
				// Проверка досупности сервиса геолокации
				val available = utils.asyncAwait { locationInteractor.checkLocationServicesAvailability() }
				if(available) updateCurrentLocation()
				else viewState.setError(R.string.err_location_service_not_available, true)
			}
		}
	}
	
	@CallSuper
	override fun onDestroy() {
		compositeDisposable.cancel()
		super.onDestroy()
	}
	
	fun updateCurrentLocation() {
		utils.launchAsyncTryCatchFinally(compositeDisposable, {
			Timber.d("update current location")
			viewState.blockInterface(true)

			val currentAddress = utils.asyncAwait { addressInteractor.getCurrentAddress() }
			if(currentAddress != null) {
				viewState.setAddressFrom(currentAddress)
			}
		}, {e ->
			Timber.e(e)
			// if(e is ...) @TODO: обработать ошибки таймаута
			viewState.setError(R.string.err_address_service_xxx, false)
		}, { viewState.blockInterface(false) })
	}
	
	fun onSearchClick(addressPair: AddressPair) { router.navigateTo(Screens.FIND_ADDRESS, addressPair) }
	fun onAboutClick() { router.navigateTo(Screens.ABOUT) }
	fun readMoreClick() { router.navigateTo(Screens.READ_MORE) }
	fun onBackCommandClick() { router.exit() }
}
