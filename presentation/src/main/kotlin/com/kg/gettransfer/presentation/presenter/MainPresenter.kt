package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.LocationInteractor

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.view.MainView

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class MainPresenter(val router: Router, val locationInteractor: LocationInteractor): MvpPresenter<MainView>() {
	var granted = false
	private val compositeDisposable = Job()
	
	override fun onFirstViewAttach()
	{
		Timber.d("onFirstViewAttach()")
		if(!granted) return
		// Проверка досупности сервиса геолокации
		launch(UI, parent = compositeDisposable) {
			val available = async(CommonPool) {
				locationInteractor.checkLocationServicesAvailability() }.await()
			
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
	
	fun onFabClick() = launch(UI) {
		val s = async(CommonPool) { myCoroutine() }.await()
		Timber.d("async return: $s, ${Thread.currentThread().name}")
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
		/*
		getViewState().blockInterface(true)
		val disposable = locationInteractor.getCurrentLocation()
			.doOnSubscribe({_ -> blockInterface(true)})
			.doAfterTerminate({blockInterface(false)})
			.map({point -> LatLng(point.latitude, point.longitude)})
			.subscribeOn(schedulersProvider.io())
			.observeOn(schedulersProvider.ui())
			.subscribe({latLng -> onCurrentLocationChanged(latLng)}, {ex -> Timber.e(ex)})
		unsubscribeOnDestroy(disposable)
		*/
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
