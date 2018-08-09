package com.kg.gettransfer.presentation.presenter

import android.location.Location

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.Point

import com.kg.gettransfer.domain.interactor.AddressInteractor
import com.kg.gettransfer.domain.interactor.LocationInteractor

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.view.MainView

import kotlin.coroutines.experimental.CoroutineContext
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class MainPresenter(private val router: Router, 
	                private val locationInteractor: LocationInteractor,
	                private val addressInteractor: AddressInteractor): MvpPresenter<MainView>() {
	                
	var granted = false
	
	private var lastPoint = Point()
	private var minDistance: Int = 0
	private var cachedAddress = ""

	val compositeDisposable = Job()
	
	val ui: CoroutineContext = UI
	val bg: CoroutineContext = CommonPool
	
	override fun onFirstViewAttach()
	{
		Timber.d("onFirstViewAttach()")
		if(!granted) return
		// Проверка досупности сервиса геолокации
		launch(ui, parent = compositeDisposable) {
			val available = withContext(bg) {
				locationInteractor.checkLocationServicesAvailability() }
			
			Timber.d("location service available: $available")
			if(!available) viewState.setError(R.string.err_location_service_not_available, true)
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
			Timber.d("onCurrentLocationChanged: ${latLng}")
			if(latLng != null) {
				viewState.setMapPoint(latLng)
				requestAddress(latLng)
			}
			else {
				viewState.blockInterface(false)
				//viewState.setError()
			}
		}
	}
	
	private fun requestAddress(latLng: LatLng) {
		val point = Point(latLng.latitude, latLng.longitude)
		// Не запрашивать адрес, если перемещение составило менее minDistance
		val dx = getDistance(point)
		if(dx < minDistance) return
		lastPoint = point;
		Timber.d("last point: $latLng")
		launch(ui, parent = compositeDisposable) {
			val address = withContext(bg) {
				addressInteractor.getAddressByLocation(point)
			}
			if(address != null && !cachedAddress.equals(address)) {
				viewState.setAddressFrom(address)
				cachedAddress = address
				viewState.blockInterface(false)
			}
		}
	}
	
	private fun getDistance(point: Point): Float {
		val distance = FloatArray(2)
		Location.distanceBetween(lastPoint.latitude, lastPoint.longitude,
			                     point.latitude, point.longitude, distance)
		return distance.get(0)
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
