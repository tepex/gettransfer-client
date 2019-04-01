package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.presentation.model.ProfileModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransferModel

@StateStrategyType(OneExecutionStateStrategy::class)
//interface MainView: MvpView, OnCameraMoveListener
interface MainView: BaseView {
    fun setMapPoint(point: LatLng, withAnimation: Boolean, showBtnMyLocation: Boolean = true)
    fun moveCenterMarker(point: LatLng)
    fun initSearchForm()
    fun setAddressFrom(address: String)
    fun setAddressTo(address: String)
    fun setProfile(profile: ProfileModel)
    fun setMarkerElevation(up: Boolean, elevation: Float)
    fun changeFields(hourly: Boolean)
    fun blockSelectedField(block: Boolean, field: String)
    fun selectFieldFrom()
    fun setFieldTo()
    fun onBackClick()
    fun showReadMoreDialog()
    fun setTripMode(duration: Int?)
    fun openReviewForLastTrip(transfer: TransferModel, startPoint: LatLng, vehicle: String, color: String, routeModel: RouteModel?)
    fun cancelReview()
    fun showDetailedReview(tappedRate: Float)
    fun askRateInPlayMarket()
    fun showRateInPlayMarket()
    fun thanksForRate()
    fun showBadge(show: Boolean)
    fun setCountEvents(count: Int)
    fun openMapToSetPoint()
}
