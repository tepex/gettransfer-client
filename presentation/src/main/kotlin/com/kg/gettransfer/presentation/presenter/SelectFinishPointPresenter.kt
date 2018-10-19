package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper
import com.arellomobile.mvp.InjectViewState
import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.view.SelectFinishPointView
import ru.terrakok.cicerone.Router

@InjectViewState
class SelectFinishPointPresenter(cc: CoroutineContexts,
                                 router: Router,
                                 systemInteractor: SystemInteractor,
                                 private val routeInteractor: RouteInteractor): BasePresenter<SelectFinishPointView>(cc, router, systemInteractor){

    private lateinit var lastAddressPoint: LatLng
    private var lastPoint: LatLng? = null

    private var currentLocation: String = ""

    override fun onFirstViewAttach() {
        //onCameraMove(LatLng(routeInteractor.from!!.cityPoint.point!!.latitude, routeInteractor.from!!.cityPoint.point!!.longitude))
        //viewState.setMapPoint(LatLng(routeInteractor.from!!.cityPoint.point!!.latitude, routeInteractor.from!!.cityPoint.point!!.longitude))
    }

    @CallSuper
    override fun attachView(view: SelectFinishPointView) {
        super.attachView(view)
        //viewState.setMapPoint(LatLng(routeInteractor.from!!.cityPoint.point!!.latitude, routeInteractor.from!!.cityPoint.point!!.longitude))
    }

    fun initFinishLocation(){
        //onCameraMove(LatLng(routeInteractor.from!!.cityPoint.point!!.latitude, routeInteractor.from!!.cityPoint.point!!.longitude))
        viewState.setMapPoint(LatLng(routeInteractor.from!!.cityPoint.point!!.latitude, routeInteractor.from!!.cityPoint.point!!.longitude))
    }

    fun updateCurrentLocation() {
        utils.launchAsyncTryCatch(
                { updateCurrentLocationAsync() },
                { e -> viewState.setError(e) })
        logEvent(MainPresenter.MY_PLACE_CLICKED)
    }

    private suspend fun updateCurrentLocationAsync() {
        viewState.blockInterface(true)
        val currentAddress = utils.asyncAwait { routeInteractor.getCurrentAddress() }
        lastAddressPoint = Mappers.point2LatLng(currentAddress.cityPoint.point!!)

        onCameraMove(lastAddressPoint)
        viewState.setMapPoint(lastAddressPoint)
        viewState.setAddressTo(currentAddress.cityPoint.name!!)
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
            val currentAddress = utils.asyncAwait { routeInteractor.getAddressByLocation(false, Mappers.latLng2Point(lastPoint!!)) }
            viewState.setAddressTo(currentAddress.cityPoint.name!!)
            currentLocation = currentAddress.cityPoint.name!!
        }, { e -> viewState.setError(e)
        }, { viewState.blockInterface(false) })
    }

    fun onSearchClick(addresses: Pair<String, String>) {
        navigateToFindAddress(addresses)
    }

    fun onNextClick() {
        router.navigateTo(Screens.CREATE_ORDER)
    }

    private fun navigateToFindAddress(addresses: Pair<String, String>) {
        routeInteractor.from?.let {
            router.navigateTo(Screens.FIND_ADDRESS, addresses)
        }
    }

    fun setAddressFields() {
        viewState.setAddressFrom(routeInteractor.from?.address ?: "")
        viewState.setAddressTo(routeInteractor.to?.address ?: "")
    }

    fun logEvent(value: String) {
        mFBA.logEvent(MainPresenter.EVENT_MENU,createSingeBundle(PARAM_KEY_NAME, value))
    }
}