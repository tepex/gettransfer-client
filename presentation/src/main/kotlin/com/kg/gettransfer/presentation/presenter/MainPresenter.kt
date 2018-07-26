package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView

import com.kg.gettransfer.presentation.view.MainView

//import javax.inject.Inject

import timber.log.Timber

@InjectViewState
class MainPresenter: MvpPresenter<MainView>()
{
	private var granted = false
	private var minDistance: Int = 0
	private var cachedAddress = ""
	
	init
	{
		//App.appComponent.inject(this)
		/*
		minDistance = App.appComponent.getContext().getResources()
			.getInteger(R.integer.map_min_distance_to_request_address)
			*/
		//compositeDisposable = CompositeDisposable()
	}
	
	@CallSuper
	override fun onDestroy()
	{
		super.onDestroy()
		//compositeDisposable.clear()
	}
	
	override fun onFirstViewAttach()
	{
		Timber.d("granted: ${granted}")
		if(!granted) return
		
		checkLocationServiceAvailability()
		updateCurrentLocation()
	}
	
	/*
	private fun unsubscribeOnDestroy(disposable: Disposable)
	{
		compositeDisposable.add(disposable)
	}
	*/
	
	private fun checkLocationServiceAvailability() {
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
	
	fun setPermissionsGranted(granted: Boolean) {
		this.granted = granted
	}
	
	/**
	 * — Запросить из репозитория текущее положение
	 */
	fun updateCurrentLocation() {
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
}
