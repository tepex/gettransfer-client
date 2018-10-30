package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.Point

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.MainView

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class MainPresenter(cc: CoroutineContexts,
                    router: Router,
                    systemInteractor: SystemInteractor,
                    private val routeInteractor: RouteInteractor): BasePresenter<MainView>(cc, router, systemInteractor) {

    private lateinit var lastAddressPoint: LatLng
    private var lastPoint: LatLng? = null
    private var minDistance: Int = 30

    private var available: Boolean = false
    private var currentLocation: String = ""

    private val MARKER_ELEVATION = 5f
    private var markerStateLifted = false
    private var isMarkerAnimating = true

    var screenForReturnAfterLogin: String? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        systemInteractor.lastMode = Screens.PASSENGER_MODE
        utils.launchAsyncTryCatch( {
            updateCurrentLocationAsync()
            isMarkerAnimating = false
            markerStateLifted = false
        }, { e -> Timber.e(e) } )

        // Создать листенер для обновления текущей локации
        // https://developer.android.com/training/location/receive-location-updates
    }

    companion object {
        @JvmField val EVENT_MENU = "menu"
        @JvmField val EVENT_MAIN = "main"

        //navigation drawer log params
        @JvmField val LOGIN_CLICKED      = "login"
        @JvmField val TRANSFER_CLICKED   = "transfers"
        @JvmField val SETTINGS_CLICKED   = "settings"
        @JvmField val ABOUT_CLICKED      = "about"
        @JvmField val DRIVER_CLICKED     = "driver"
        @JvmField val CUSTOMER_CLICKED   = "customer"
        @JvmField val BEST_PRICE_CLICKED = "best_price"
        @JvmField val SHARE_CLICKED      = "share"

        //other buttons log params
        @JvmField val MY_PLACE_CLICKED   = "my_place"
//        @JvmField val SHOW_ROUTE_CLICKED = "show_route"
//        @JvmField val CAR_INFO_CLICKED = "car_info"
//        @JvmField val BACK_CLICKED = "back"
        @JvmField val POINT_ON_MAP_CLICKED = "point_on_map"
        @JvmField val AIRPORT_CLICKED      = "predefined_airport"
        @JvmField val TRAIN_CLICKED        = "predefined_train"
        @JvmField val HOTEL_CLICKED        = "predefined_hotel"
        @JvmField val LAST_PLACE_CLICKED   = "last_place"
        @JvmField val SWAP_CLICKED         = "swap"
    }

    @CallSuper
    override fun attachView(view: MainView) {
        super.attachView(view)
        viewState.setProfile(Mappers.getProfileModel(systemInteractor.account.user.profile))
    }

    fun updateCurrentLocation() {
        utils.launchAsyncTryCatch(
            { updateCurrentLocationAsync() },
            { e -> viewState.setError(e) })
        logEvent(MY_PLACE_CLICKED)
    }

    private suspend fun updateCurrentLocationAsync() {
        viewState.blockInterface(true)
        val currentAddress = utils.asyncAwait { routeInteractor.getCurrentAddress() }
        lastAddressPoint = Mappers.point2LatLng(currentAddress.cityPoint.point!!)

        onCameraMove(lastAddressPoint, !comparePointsWithRounding(lastAddressPoint, lastPoint))
        viewState.setMapPoint(lastAddressPoint)
        viewState.setAddressFrom(currentAddress.cityPoint.name!!)
        currentLocation = currentAddress.cityPoint.name!!
    }


    fun onCameraMove(lastPoint: LatLng, animateMarker: Boolean) {
        if(!markerStateLifted && !isMarkerAnimating && animateMarker) {
            viewState.setMarkerElevation(true, MARKER_ELEVATION)
            markerStateLifted = true
        }
        this.lastPoint = lastPoint
        viewState.moveCenterMarker(lastPoint)
        viewState.blockInterface(true)

    }

    fun onCameraIdle(latLngBounds: LatLngBounds) {
        if(markerStateLifted && !isMarkerAnimating) {
            viewState.setMarkerElevation(false, -MARKER_ELEVATION)
            markerStateLifted = false
        }
        if(lastPoint == null) return
		/* Не запрашивать адрес, если перемещение составило менее minDistance
        val distance = FloatArray(2)
        Location.distanceBetween(lastPoint!!.latitude, lastPoint!!.longitude,
                                 lastAddressPoint.latitude, lastAddressPoint.longitude, distance)
        //if(distance.get(0) < minDistance) return
        */

        lastAddressPoint = lastPoint!!
        val latLonPair: Pair<Point, Point>
        val nePoint = Point(latLngBounds.northeast.latitude, latLngBounds.northeast.longitude)
        val swPoint = Point(latLngBounds.southwest.latitude, latLngBounds.southwest.longitude)
        latLonPair = Pair(nePoint, swPoint)
        utils.launchAsyncTryCatchFinally({
            val currentAddress = utils.asyncAwait { routeInteractor.getAddressByLocation(Mappers.latLng2Point(lastPoint!!), latLonPair) }
            viewState.setAddressFrom(currentAddress.cityPoint.name!!)
            currentLocation = currentAddress.cityPoint.name!!
        }, { e -> viewState.setError(e)
        }, { viewState.blockInterface(false) })
    }

    fun setMarkerAnimating(animating: Boolean){
        isMarkerAnimating = animating
    }

    fun setAddressFields() {
        viewState.setAddressFrom(routeInteractor.from?.address ?: "")
        viewState.setAddressTo(routeInteractor.to?.address ?: "")
        viewState.initSearchForm()
    }

    fun onSearchClick(addresses: Pair<String, String>) {
        navigateToFindAddress(addresses)
    }

    fun onNextClick(addresses: Pair<String, String>) {
        navigateToFindAddress(addresses)
    }

    private fun navigateToFindAddress(addresses: Pair<String, String>) {
        routeInteractor.from?.let { router.navigateTo(Screens.FIND_ADDRESS, addresses) }
    }

    fun onNextClick()           { router.navigateTo(Screens.CREATE_ORDER) }
    fun onAboutClick()          { router.navigateTo(Screens.ABOUT) ;     logEvent(ABOUT_CLICKED) }
    fun readMoreClick()         { router.navigateTo(Screens.READ_MORE) ; logEvent(BEST_PRICE_CLICKED) }
    fun onSettingsClick()       { router.navigateTo(Screens.SETTINGS) ;  logEvent(SETTINGS_CLICKED) }
    fun onRequestsClick()       { router.navigateTo(Screens.REQUESTS) ;  logEvent(TRANSFER_CLICKED) }
    fun onLoginClick() {
        screenForReturnAfterLogin = Screens.REQUESTS
        router.navigateTo(Screens.LOGIN)
        logEvent(LOGIN_CLICKED)
    }

    fun onBecomeACarrierClick() {
        logEvent(DRIVER_CLICKED)
        if(systemInteractor.isLoggedIn()) {
            if(systemInteractor.account.groups!!.indexOf(Account.GROUP_CARRIER_DRIVER) >= 0) router.navigateTo(Screens.CARRIER_MODE)
            else router.navigateTo(Screens.REG_CARRIER)
        }
        else {
            screenForReturnAfterLogin = Screens.CARRIER_MODE
            router.navigateTo(Screens.LOGIN)
        }
    }

    private fun comparePointsWithRounding(point1: LatLng?, point2: LatLng?): Boolean {
        if(point2 == null || point1 == null) return false
        val criteria = 0.000_001

        var latDiff = point1.latitude - point1.latitude
        if(latDiff < 0) latDiff *= -1
        var lngDiff = point2.longitude - point2.longitude
        if(lngDiff < 0) lngDiff *= -1
        return latDiff < criteria && lngDiff < criteria
    }

    private fun comparePointsWithRounding(point1: LatLng?, point2: LatLng?): Boolean {
        if(point2 == null || point1 == null) return false
        val criteria = 0.000_001

        var latDiff = point1.latitude - point1.latitude
        if(latDiff < 0) latDiff *= -1
        var lngDiff = point2.longitude - point2.longitude
        if(lngDiff < 0) lngDiff *= -1
        return latDiff < criteria && lngDiff < criteria
    }

    fun logEvent(value: String) {
        mFBA.logEvent(EVENT_MENU, createSingeBundle(PARAM_KEY_NAME, value))
    }
}
