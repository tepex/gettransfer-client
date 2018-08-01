package com.kg.gettransfer.presentation.presenter

import android.location.Location

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.presentation.view.StartView

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class StartPresenter(val router: Router): MvpPresenter<StartView>() {
//	lateinit var locationInteractor: LocationInteractor
	
	override fun onFirstViewAttach()
	{
		/* @TODO: перенести в MainActivity
		checkLocationServiceAvailability() */
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
	
	fun onBackCommandClick() {
		router.exit()
	}
}
