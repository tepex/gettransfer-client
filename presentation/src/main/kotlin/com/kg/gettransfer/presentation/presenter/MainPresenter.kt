package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.LocationInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.view.MainView

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class MainPresenter(cc: CoroutineContexts,
                    router: Router,
                    systemInteractor: SystemInteractor,
                    private val locationInteractor: LocationInteractor,
                    private val routeInteractor: RouteInteractor): BasePresenter<MainView>(cc, router, systemInteractor) {

    private lateinit var lastAddressPoint: LatLng
    private var lastPoint: LatLng? = null
    private var minDistance: Int = 30

    private var available: Boolean = false

    override fun onFirstViewAttach() {
        systemInteractor.putLastMode(Screens.PASSENGER_MODE)
        utils.launchAsyncTryCatch( {
            // Проверка досупности сервиса геолокации
            available = utils.asyncAwait { locationInteractor.checkLocationServicesAvailability() }
            if(available) updateCurrentLocationAsync()
            else viewState.setError(true, R.string.err_location_service_not_available)
        }, { e -> Timber.e(e) } )
        
        // Создать листенер для обновления текущей локации
        // https://developer.android.com/training/location/receive-location-updates
    }
    
    @CallSuper
    override fun attachView(view: MainView) {
        super.attachView(view)
        viewState.setUser(Mappers.getUserModel(systemInteractor.account))
    }

    fun updateCurrentLocation() {
        utils.launchAsyncTryCatch(
            { updateCurrentLocationAsync() },
            { e -> viewState.setError(e) })
    }

    private suspend fun updateCurrentLocationAsync() {
        viewState.blockInterface(true)
        val currentAddress = utils.asyncAwait { routeInteractor.getCurrentAddress() }
        lastAddressPoint = Mappers.point2LatLng(currentAddress.point!!)

        onCameraMove(lastAddressPoint)
        viewState.setMapPoint(lastAddressPoint)
        viewState.setAddressFrom(currentAddress.name)
    }
    
    fun onCameraMove(lastPoint: LatLng) {
        this.lastPoint = lastPoint
        viewState.moveCenterMarker(lastPoint)
        viewState.blockInterface(true)
    }
    
    fun onCameraIdle() {
        if(lastPoint == null) return
		/* Не запрашивать адрес, если перемещение составило менее minDistance 
        val distance = FloatArray(2)
        Location.distanceBetween(lastPoint!!.latitude, lastPoint!!.longitude,
                                 lastAddressPoint.latitude, lastAddressPoint.longitude, distance)
        //if(distance.get(0) < minDistance) return
        */

        lastAddressPoint = lastPoint!!
        utils.launchAsyncTryCatchFinally({
            val currentAddress = utils.asyncAwait { routeInteractor.getAddressByLocation(Mappers.latLng2Point(lastPoint!!)) }
            viewState.setAddressFrom(currentAddress.name)
        }, { e -> viewState.setError(e)
        }, { viewState.blockInterface(false) })
    }

    fun onSearchClick(addresses: Pair<String, String>) {
        if(routeInteractor.from != null) router.navigateTo(Screens.FIND_ADDRESS, addresses)
    }

    fun onLoginClick()          { router.navigateTo(Screens.LOGIN) }
    fun onAboutClick()          { router.navigateTo(Screens.ABOUT) }
    fun readMoreClick()         { router.navigateTo(Screens.READ_MORE) }
    fun onSettingsClick()       { router.navigateTo(Screens.SETTINGS) }
    fun onRequestsClick()       { router.navigateTo(Screens.REQUESTS) }
    fun onBecomeACarrierClick() {
        if(systemInteractor.account.loggedIn) {
            if(systemInteractor.account.groups!!.indexOf("carrier/driver") >= 0) router.navigateTo(Screens.CARRIER_MODE)
            else router.navigateTo(Screens.REG_CARRIER)
        }
        else router.navigateTo(Screens.LOGIN)
    }
}
