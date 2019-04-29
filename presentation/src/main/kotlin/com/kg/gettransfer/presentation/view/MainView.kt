package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.presentation.model.ProfileModel

@StateStrategyType(OneExecutionStateStrategy::class)
//interface MainView: MvpView, OnCameraMoveListener
interface MainView: BaseView {
    fun setMapPoint(point: LatLng, withAnimation: Boolean, showBtnMyLocation: Boolean)
    fun moveCenterMarker(point: LatLng)
    fun setAddressFrom(address: String)
    fun setAddressTo(address: String)
    fun setProfile(profile: ProfileModel)
    fun setMarkerElevation(up: Boolean, elevation: Float)
    fun changeFields(hourly: Boolean)
    fun blockSelectedField(block: Boolean, field: String)
    fun selectFieldFrom()
    fun setFieldTo()
    fun onBackClick(isAddressNavigating: Boolean, isTo: Boolean)
    fun showReadMoreDialog()
    fun setTripMode(duration: Int?)
    fun showDetailedReview(tappedRate: Float, offerId: Long)
    fun askRateInPlayMarket()
    fun thanksForRate()
    fun showBadge(show: Boolean)
    fun setCountEvents(count: Int)
    fun openMapToSetPoint()
    fun recreateRequestFragment()
    fun defineAddressRetrieving(block:(withGps: Boolean) -> Unit)
    fun showRateForLastTrip()

    companion object {
        const val MAP_SCREEN     = 1
        const val REQUEST_SCREEN = 2
    }
}
