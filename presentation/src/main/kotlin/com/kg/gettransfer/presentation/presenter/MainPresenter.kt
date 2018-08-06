package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.view.MainView

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class MainPresenter(val router: Router): MvpPresenter<MainView>() {
	override fun onFirstViewAttach()
	{
		Timber.d("MainPresenter.onFirstViewAttach()")
		updateCurrentLocation()
	}
	
	/*
	private fun checkLocationServiceAvailability()
	{
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
	}
	*/
	
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
		//router.navigateTo(Screens.START_SEARCH_SCREEN)
	}
	
	fun onAboutClick() {
		Timber.d("about click")
		router.navigateTo(Screens.ABOUT_ACTIVITY)
	}

	fun readMoreClick() {
		Timber.d("read more click")
		//router.navigateTo(Screens.READ_MORE_ACTIVITY)
	}
	
	fun onBackCommandClick() {
		router.exit()
	}
}
