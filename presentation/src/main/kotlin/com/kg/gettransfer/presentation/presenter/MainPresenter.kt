package com.kg.gettransfer.presentation.presenter

import android.location.Location

import android.support.annotation.CallSuper
import android.util.Pair

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.AddressInteractor
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.interactor.LocationInteractor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.view.MainView

import kotlinx.coroutines.experimental.Job

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class MainPresenter(private val cc: CoroutineContexts,
                    private val router: Router,
                    private val locationInteractor: LocationInteractor,
                    private val addressInteractor: AddressInteractor,
                    private val apiInteractor: ApiInteractor): MvpPresenter<MainView>() {

    private val compositeDisposable = Job()
    private val utils = AsyncUtils(cc)
    private lateinit var account: Account
    private lateinit var lastAddressPoint: LatLng
    private var lastPoint: LatLng? = null
    private var minDistance: Int = 30

    override fun onFirstViewAttach() {
        utils.launchAsyncTryCatch(compositeDisposable, {
            // Проверка досупности сервиса геолокации
            val available = utils.asyncAwait { locationInteractor.checkLocationServicesAvailability() }
            if(available) updateCurrentLocation()
            else { viewState.setError(R.string.err_location_service_not_available, true) }
        }, { e -> Timber.e(e) })
    }
    
    @CallSuper
    override fun attachView(view: MainView) {
        super.attachView(view)
        utils.launchAsyncTryCatch(compositeDisposable, {
            account = utils.asyncAwait { apiInteractor.getAccount() }
            viewState.showLoginInfo(account)
        }, { e -> Timber.e(e) })
    }

    @CallSuper
    override fun onDestroy() {
        compositeDisposable.cancel()
        super.onDestroy()
    }

    fun updateCurrentLocation() {
        Timber.d("update current location")
        viewState.blockInterface(true)
        utils.launchAsyncTryCatchFinally(compositeDisposable, {
            val currentAddress = utils.asyncAwait { addressInteractor.getCurrentAddress() }
            lastAddressPoint = LatLng(currentAddress.point!!.latitude, currentAddress.point!!.longitude)
            onCameraMove(lastAddressPoint)
            viewState.setMapPoint(lastAddressPoint)
            viewState.setAddressFrom(currentAddress.name)
        }, { e ->
            Timber.e(e)
            viewState.setError(R.string.err_address_service_xxx, false)
        }, { viewState.blockInterface(false) })
    }
    
    fun onCameraMove(lastPoint: LatLng) {
        this.lastPoint = lastPoint
        viewState.moveCenterMarker(lastPoint)
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
        utils.launchAsyncTryCatch(compositeDisposable, {
            val currentAddress = utils.asyncAwait {
                addressInteractor.getAddressByLocation(Point(lastPoint!!.latitude, lastPoint!!.longitude))
            }
            viewState.setAddressFrom(currentAddress.name)
        }, { e -> Timber.e(e) })
    }
    
    fun onSearchClick(addresses: Pair<String, String>) { router.navigateTo(Screens.FIND_ADDRESS, addresses) }
    fun onLoginClick() { router.navigateTo(Screens.LOGIN) }
    fun onAboutClick() { router.navigateTo(Screens.ABOUT) }
    fun readMoreClick() { router.navigateTo(Screens.READ_MORE) }
    fun onSettingsClick() { router.navigateTo(Screens.SETTINGS) }
    fun onTransfersClick() {router.navigateTo(Screens.TRANSFERS)}
    fun onBackCommandClick() { router.exit() }
}
