package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.model.Account

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.model.Mappers
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

    override fun onFirstViewAttach() {
        systemInteractor.lastMode = Screens.PASSENGER_MODE
        utils.launchAsyncTryCatch( {
            // @TODO выкинуть эту порнографию в …
            //if(GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable() == ConnectionResult.SUCCESS)

                updateCurrentLocationAsync()
            //else viewState.setError(true, R.string.err_location_service_not_available)
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
        viewState.setProfile(Mappers.getProfileModel(systemInteractor.account))
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

        onCameraMove(lastAddressPoint)
        viewState.setMapPoint(lastAddressPoint)
        viewState.setAddressFrom(currentAddress.cityPoint.name!!)
        currentLocation = currentAddress.cityPoint.name!!
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
            viewState.setAddressFrom(currentAddress.cityPoint.name!!)
            currentLocation = currentAddress.cityPoint.name!!
        }, { e -> viewState.setError(e)
        }, { viewState.blockInterface(false) })
    }

    fun setAddressFields() {
        viewState.setAddressFrom(routeInteractor.from?.address ?: "")
        viewState.setAddressTo(routeInteractor.to?.address ?: "")
    }

    fun onSearchClick(addresses: Pair<String, String>) {
        routeInteractor.from?.let {
            router.navigateTo(Screens.FIND_ADDRESS, addresses)
        }
    }

    fun onLoginClick()          { router.navigateTo(Screens.LOGIN) ;     logEvent(LOGIN_CLICKED)}
    fun onAboutClick()          { router.navigateTo(Screens.ABOUT) ;     logEvent(ABOUT_CLICKED) }
    fun readMoreClick()         { router.navigateTo(Screens.READ_MORE) ; logEvent(BEST_PRICE_CLICKED) }
    fun onSettingsClick()       { router.navigateTo(Screens.SETTINGS) ;  logEvent(SETTINGS_CLICKED) }
    fun onRequestsClick()       { router.navigateTo(Screens.REQUESTS) ;  logEvent(TRANSFER_CLICKED) }
    fun onBecomeACarrierClick() {
        logEvent(DRIVER_CLICKED)
        if(systemInteractor.isLoggedIn()) {
            if(systemInteractor.account.groups!!.indexOf(Account.GROUP_CARRIER_DRIVER) >= 0) router.navigateTo(Screens.CARRIER_MODE)
            else router.navigateTo(Screens.REG_CARRIER)
        }
        else router.navigateTo(Screens.LOGIN)
    }

    fun logEvent(value: String) {
        mFBA.logEvent(EVENT_MENU,createSingeBundle(PARAM_KEY_NAME, value))
    }
}
