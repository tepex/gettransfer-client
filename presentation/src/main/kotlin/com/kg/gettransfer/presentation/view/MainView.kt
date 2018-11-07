package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.google.android.gms.maps.model.LatLng
import com.kg.gettransfer.presentation.model.ProfileModel

@StateStrategyType(OneExecutionStateStrategy::class)
//interface MainView: MvpView, OnCameraMoveListener
interface MainView: BaseView {
    fun setMapPoint(point: LatLng, withAnimation: Boolean)
    fun moveCenterMarker(point: LatLng)
    fun initSearchForm()
    fun setAddressFrom(address: String)
    fun setAddressTo(address: String)
    fun setProfile(profile: ProfileModel)
    fun setMarkerElevation(up: Boolean, elevation: Float)

    fun blockSelectedField(block: Boolean, field: String)
    fun selectFieldFrom()
    fun setFieldTo()
    fun onBackClick()
}
