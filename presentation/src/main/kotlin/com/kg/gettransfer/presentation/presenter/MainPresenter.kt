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

import com.kg.gettransfer.domain.model.Point

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.view.MainView

import kotlinx.coroutines.experimental.*

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class MainPresenter(private val cc: CoroutineContexts,
	                private val router: Router,
	                private val locationInteractor: LocationInteractor,
	                private val addressInteractor: AddressInteractor): MvpPresenter<MainView>() {
	                
	var granted = false
	
	val compositeDisposable = Job()
	
	val utils = AsyncUtils(cc)
	
	override fun onFirstViewAttach() {
		utils.launchAsync(compositeDisposable) {
			Timber.d("onFirstViewAttach()")
			if(granted) {
				// Проверка досупности сервиса геолокации
				val available = utils.asyncAwait {
					locationInteractor.checkLocationServicesAvailability() 
				}
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
	
//	fun onFabClick() = launch(cc.ui, parent = compositeDisposable) {
//val s = withContext(cc.bg) { myCoroutine() }
	fun onFabClick() = utils.launchAsync(compositeDisposable) {
		Timber.d("Start coroutine. ${Thread.currentThread().name}")
		val s = utils.asyncAwait { myCoroutine() }
		Timber.d("end coroutine, ${Thread.currentThread().name}")
	}
	
	private fun myCoroutine(): String {
		Timber.d("start qqq: ${Thread.currentThread().name}")
		//delay(3000)
		try {
			Thread.sleep(2000)
		} catch(e: InterruptedException) {
			Timber.d(e)
		}
		Timber.d("end qqq")
		return "wqeweqw"
	}
	
	fun updateCurrentLocation() = utils.launchAsync(compositeDisposable) {
		Timber.d("update current location")
		viewState.blockInterface(true)
		 
		val result = utils.asyncAwait { locationInteractor.getCurrentLocation() }
		if(result.point != null) {
			val point: Point = result.point as Point
			val latLng = LatLng(point.latitude, point.longitude)
			viewState.setMapPoint(latLng)
			
			val addr = utils.asyncAwait { addressInteractor.getAddressByLocation(point)	}
			if(addr != null) viewState.setAddressFrom(addr.address)
		}
		else Timber.e(result.error)
		viewState.blockInterface(false)
	}
	
	fun onSearchClick() {
		Timber.d("onSearchClick")
		router.navigateTo(Screens.FIND_ADDRESS)
	}
	
	fun onAboutClick() {
		Timber.d("about click")
		router.navigateTo(Screens.ABOUT)
	}

	fun readMoreClick() {
		Timber.d("read more click")
		router.navigateTo(Screens.READ_MORE)
	}
	
	fun onBackCommandClick() {
		router.exit()
	}
}
