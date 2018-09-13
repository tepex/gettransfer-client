package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import android.util.Pair
import com.arellomobile.mvp.InjectViewState
import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.AddressInteractor
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.interactor.LocationInteractor
import com.kg.gettransfer.domain.model.Point
import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.MainView
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@InjectViewState
class MainPresenter(cc: CoroutineContexts,
                    router: Router,
                    apiInteractor: ApiInteractor,
                    private val locationInteractor: LocationInteractor,
                    private val addressInteractor: AddressInteractor): BasePresenter<MainView>(cc, router, apiInteractor) {

    private lateinit var lastAddressPoint: LatLng
    private var lastPoint: LatLng? = null
    private var minDistance: Int = 30
    private lateinit var dateTimeFormat: DateFormat

    var date: Date = Date()
        set(value) {
            field = value
            viewState.setDateTimeTransfer(dateTimeFormat.format(date))
        }

    override fun onFirstViewAttach() {
        utils.launchAsyncTryCatch(compositeDisposable, {
            // Проверка досупности сервиса геолокации
            val available = utils.asyncAwait { locationInteractor.checkLocationServicesAvailability() }
            if(available) updateCurrentLocation()
            else { viewState.setError(true, R.string.err_location_service_not_available) }
        }, { e -> Timber.e(e) })
        
        // Создать листенер для обновления текущей локации
        // https://developer.android.com/training/location/receive-location-updates
    }
    
    @CallSuper
    override fun attachView(view: MainView) {
        super.attachView(view)
        utils.launchAsyncTryCatchFinally(compositeDisposable, {
            viewState.blockInterface(false)
            account = utils.asyncAwait { apiInteractor.getAccount() }
            viewState.showLoginInfo(account)
            dateTimeFormat = SimpleDateFormat(Utils.DATE_TIME_PATTERN, account.locale)
            date = Date()
        }, { e ->
                if(e is ApiException) viewState.setError(false, R.string.err_server_code, e.code.toString(), e.details)
                else viewState.setError(false, R.string.err_server, e.message)
        }, { viewState.blockInterface(false) })
    }

    fun updateCurrentLocation() {
        Timber.d("update current location")
        utils.launchAsyncTryCatchFinally(compositeDisposable, {
            viewState.blockInterface(true)
            val currentAddress = utils.asyncAwait { addressInteractor.getCurrentAddress() }
            lastAddressPoint = LatLng(currentAddress.point!!.latitude, currentAddress.point!!.longitude)
            onCameraMove(lastAddressPoint)
            viewState.setMapPoint(lastAddressPoint)
            viewState.setAddressFrom(currentAddress.name)
        }, { e -> viewState.setError(false, R.string.err_server, e.message)
        }, { viewState.blockInterface(false) })
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
    fun onRequestsClick() {router.navigateTo(Screens.REQUESTS)}
//    fun onBackCommandClick() { router.exit() }
}
