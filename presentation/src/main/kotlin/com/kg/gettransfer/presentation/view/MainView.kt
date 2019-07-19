package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.presentation.model.ProfileModel

@Suppress("TooManyFunctions")
@StateStrategyType(OneExecutionStateStrategy::class)
interface MainView : BaseView {
    fun setMapPoint(point: LatLng, withAnimation: Boolean, showBtnMyLocation: Boolean)
    fun moveCenterMarker(point: LatLng)
    fun setAddressFrom(address: String)
    fun setAddressTo(address: String)
    fun setProfile(profile: ProfileModel, isLoggedIn: Boolean, hasAccount: Boolean)
    fun setMarkerElevation(up: Boolean)
    fun changeFields(hourly: Boolean)
    fun blockSelectedField(block: Boolean, field: String)
    fun selectFieldFrom()
    fun setFieldTo()
    fun onBackClick(isAddressNavigating: Boolean, isTo: Boolean)
    fun showReadMoreDialog()
    fun setTripMode(duration: Int?)
    fun showDetailedReview()
    fun askRateInPlayMarket()
    fun thanksForRate()
    fun showBadge(show: Boolean)
    fun setCountEvents(count: Int)
    fun openMapToSetPoint()
    fun recreateRequestFragment()
    fun defineAddressRetrieving(block: (withGps: Boolean) -> Unit)
    fun showRateForLastTrip(transferId: Long, vehicle: String, color: String)
    fun setBalance(balance: String?)

    companion object {
        val EXTRA_RATE_TRANSFER_ID = "${MainView::class.java.name}.rate_transfer_id"
        val EXTRA_RATE_VALUE = "${MainView::class.java.name}.rate_value"
    }
}
