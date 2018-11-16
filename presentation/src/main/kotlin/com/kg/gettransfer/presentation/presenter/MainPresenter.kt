package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.model.Point

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.view.MainView

import com.yandex.metrica.YandexMetrica

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
    var isMarkerAnimating = true

    private var idleAndMoveCamera = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        systemInteractor.lastMode = Screens.PASSENGER_MODE
        systemInteractor.selectedField = FIELD_FROM
        utils.launchAsyncTryCatch( {
            if(routeInteractor.from != null) setLastLocation()
            else updateCurrentLocationAsync()
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

        @JvmField val FIELD_FROM = "field_from"
        @JvmField val FIELD_TO   = "field_to"
    }

    @CallSuper
    override fun attachView(view: MainView) {
        super.attachView(view)
        viewState.setProfile(Mappers.getProfileModel(systemInteractor.account.user.profile))
        changeUsedField(systemInteractor.selectedField)
    }

    fun switchUsedField(){
        when(systemInteractor.selectedField){
            FIELD_FROM -> changeUsedField(FIELD_TO)
            FIELD_TO -> changeUsedField(FIELD_FROM)
        }
    }

    fun changeUsedField(field: String){
        systemInteractor.selectedField = field

        val pointSelectedField: Point? = when(field){
            FIELD_FROM -> routeInteractor.from?.cityPoint?.point
            FIELD_TO -> routeInteractor.to?.cityPoint?.point
            else -> null
        }
        var latLngPointSelectedField: LatLng? = null
        if (pointSelectedField != null) latLngPointSelectedField = LatLng(pointSelectedField.latitude, pointSelectedField.longitude)
        when(systemInteractor.selectedField){
            FIELD_FROM -> viewState.selectFieldFrom()
            FIELD_TO -> viewState.setFieldTo()
        }
        if (latLngPointSelectedField != null) {
            idleAndMoveCamera = false
            viewState.setMapPoint(latLngPointSelectedField, false)
        }
    }

    fun updateCurrentLocation() {
        utils.launchAsyncTryCatch(
                { updateCurrentLocationAsync() },
                { e -> viewState.setError(false, R.string.err_server, e.message) })
        logEvent(MY_PLACE_CLICKED)
    }

    private fun setLastLocation(){
        viewState.blockInterface(true)
        val currentAddress = routeInteractor.from
        setPointAddress(currentAddress!!)
    }

    private suspend fun updateCurrentLocationAsync() {
        //viewState.blockInterface(true)
        viewState.blockSelectedField(true, systemInteractor.selectedField)
        val currentAddress = utils.asyncAwait { routeInteractor.getCurrentAddress() }
        setPointAddress(currentAddress)
    }

    private fun setPointAddress(currentAddress: GTAddress){
        lastAddressPoint = Mappers.point2LatLng(currentAddress.cityPoint.point!!)
        onCameraMove(lastAddressPoint, !comparePointsWithRounding(lastAddressPoint, lastPoint))
        viewState.setMapPoint(lastAddressPoint, true)
        //viewState.setAddressFrom(currentAddress.cityPoint.name!!)
        setAddressInSelectedField(currentAddress.cityPoint.name!!)

        lastAddressPoint = Mappers.point2LatLng(currentAddress.cityPoint.point!!)
    }

    fun onCameraMove(lastPoint: LatLng, animateMarker: Boolean) {
        if(idleAndMoveCamera) {
            if (!markerStateLifted && !isMarkerAnimating && animateMarker) {
                viewState.setMarkerElevation(true, MARKER_ELEVATION)
                markerStateLifted = true
            }
            this.lastPoint = lastPoint
            viewState.moveCenterMarker(lastPoint)
            //viewState.blockInterface(true)
            viewState.blockSelectedField(true, systemInteractor.selectedField)
        }
    }

    fun onCameraIdle(latLngBounds: LatLngBounds) {
        if(idleAndMoveCamera) {
            if (markerStateLifted && !isMarkerAnimating) {
                viewState.setMarkerElevation(false, -MARKER_ELEVATION)
                markerStateLifted = false
            }
            if (lastPoint == null) return
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
            
            utils.launchSuspend {
                val result = utils.asyncAwait {
                    routeInteractor.getAddressByLocation(
                            systemInteractor.selectedField == FIELD_FROM, Mappers.latLng2Point(lastPoint!!), latLonPair)
                }
                if(result.error != null) {
                    Timber.e("getAddressByLocation", result.error!!)
                    viewState.setError(result.error!!)
                } else {
                    currentLocation = result.model.cityPoint.name!!
                    setAddressInSelectedField(currentLocation)
                }
                viewState.blockInterface(false)
            }
        } else {
            idleAndMoveCamera = true
            setAddressFields()
        }
    }

    private fun setAddressInSelectedField(address: String) {
        when(systemInteractor.selectedField) {
            FIELD_FROM -> viewState.setAddressFrom(address)
            FIELD_TO -> viewState.setAddressTo(address)
        }
    }

    fun enablePinAnimation() { isMarkerAnimating = false }

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

    fun onNextClick()     { if(routeInteractor.from?.cityPoint != null && routeInteractor.to?.cityPoint != null) router.navigateTo(Screens.CREATE_ORDER) }
    fun onAboutClick()    { router.navigateTo(Screens.ABOUT) ;     logEvent(ABOUT_CLICKED) }
    fun readMoreClick()   { router.navigateTo(Screens.READ_MORE) ; logEvent(BEST_PRICE_CLICKED) }
    fun onSettingsClick() { router.navigateTo(Screens.SETTINGS) ;  logEvent(SETTINGS_CLICKED) }
    fun onRequestsClick() { router.navigateTo(Screens.REQUESTS) ;  logEvent(TRANSFER_CLICKED) }
    fun onLoginClick() {
        login("", "")
        logEvent(LOGIN_CLICKED)
    }

    fun onBecomeACarrierClick() {
        logEvent(DRIVER_CLICKED)
        if(systemInteractor.account.user.loggedIn) {
            if(systemInteractor.account.groups.indexOf(Account.GROUP_CARRIER_DRIVER) >= 0) router.navigateTo(Screens.CARRIER_MODE)
            else router.navigateTo(Screens.REG_CARRIER)
        }
        else {
            login(Screens.CARRIER_MODE, "")
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

    fun logEvent(value: String) {
        val map = HashMap<String, Any>()
        map[PARAM_KEY_NAME] = value

        mFBA.logEvent(EVENT_MENU, createSingeBundle(PARAM_KEY_NAME, value))
        eventsLogger.logEvent(EVENT_MENU, createSingeBundle(PARAM_KEY_NAME, value))
        YandexMetrica.reportEvent(EVENT_MENU, map)
    }

    fun onBackClick(){
        if(systemInteractor.selectedField == FIELD_TO) switchUsedField()
        else viewState.onBackClick()
    }
}
