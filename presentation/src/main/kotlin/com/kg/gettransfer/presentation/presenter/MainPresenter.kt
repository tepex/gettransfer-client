package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

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
	
	override fun onFirstViewAttach()
	{
		Timber.d("MainPresenter.onFirstViewAttach()")
		if(!granted) return
		// Проверка досупности сервиса геолокации
		/*
		launch() {
		}
		
		
		val available = locationInteractor.checkLocationServicesAvailability()
		Timber.d("location service available: $available")
		*/
		
		

		updateCurrentLocation()
	}
	
	fun onFabClick() = launch(UI) {
		val s = async(CommonPool) { myCoroutine() }.await()
		Timber.d("async return: $s")
		viewState.qqq(s)
	}
	
	private suspend fun myCoroutine(): String {
		Timber.d("start qqq")
		delay(3000)
		Timber.d("end qqq")
		return "wqeweqw"
	}
	
	private fun checkLocationServiceAvailability()
	{
		/*
		val googleDisposable = locationInteractor.checkLocationServicesAvailability()
			.filter({available -> !available})
			.flatMap({_ -> locationInteractor.getLocationServicesStatus()
				.toMaybe()
				.subscribeOn(schedulersProvider.io())
				.observeOn(schedulersProvider.ui())})
//				.subscribe(status -> getViewState().showGoogleApiMessage(status), Timber::e);

			.subscribeOn(schedulersProvider.io())
			.observeOn(schedulersProvider.ui())
			.subscribe({status -> Timber.i("Google API status: ${status}")}, {ex -> Timber.e(ex)})
		unsubscribeOnDestroy(googleDisposable)
		*/
	}
	
	internal fun updateCurrentLocation()
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
