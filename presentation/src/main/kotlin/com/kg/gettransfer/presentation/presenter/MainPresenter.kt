package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.domain.interactor.LocationInteractor

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.view.MainView

import kotlin.coroutines.experimental.CoroutineContext
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class MainPresenter(val router: Router, val locationInteractor: LocationInteractor): MvpPresenter<MainView>() {
	var granted = false
	private val compositeDisposable = Job()
	
	private val ui: CoroutineContext = UI
	private val bg: CoroutineContext = CommonPool
	
	override fun onFirstViewAttach()
	{
		Timber.d("onFirstViewAttach()")
		if(!granted) return
		// Проверка досупности сервиса геолокации
		launch(ui, parent = compositeDisposable) {
			val available = withContext(bg) {
				locationInteractor.checkLocationServicesAvailability() }
			
			Timber.d("location service available: $available")
			if(!available) viewState.setError(R.string.err_location_service_not_available)
			else updateCurrentLocation()
		}
	}
	
	@CallSuper
	override fun onDestroy() {
		super.onDestroy()
		compositeDisposable.cancel()
	}
	
	fun onFabClick() = launch(ui, parent = compositeDisposable) {
		Timber.d("Start coroutine. ${Thread.currentThread().name}")
		val s = withContext(bg) { myCoroutine() }
		Timber.d("end coroutine, ${Thread.currentThread().name}")
		viewState.qqq(s)
	}
	
	private fun myCoroutine(): String {
		Timber.d("start qqq: ${Thread.currentThread().name}")
		//delay(3000)
		try {
			Thread.sleep(3000)
		} catch(e: InterruptedException) {
			Timber.d(e)
		}
		Timber.d("end qqq")
		return "wqeweqw"
	}
	
	fun updateCurrentLocation()
	{
		Timber.d("update current location")
		viewState.blockInterface(true)
		launch(ui, parent = compositeDisposable) {
			val latLng = withContext(bg) {
				val point = locationInteractor.getCurrentLocation()
				if(point != null) LatLng(point.latitude, point.longitude)
				else null
			}
			viewState.blockInterface(false)
			Timber.d("onCurrentLocationChanged: ${latLng}")
			viewState.setMapPoint(latLng)
			
			
			//requestAddress(latLng)
		}
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
