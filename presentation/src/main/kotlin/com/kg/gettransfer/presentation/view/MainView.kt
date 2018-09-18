package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.domain.model.Account

//import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener

@StateStrategyType(OneExecutionStateStrategy::class)
//interface MainView: MvpView, OnCameraMoveListener
interface MainView: BaseView {
    fun setMapPoint(point: LatLng)
    fun moveCenterMarker(point: LatLng)
    fun setAddressFrom(address: String)
    fun showLoginInfo(account: Account)
    fun setDateTimeTransfer(dateTime: String)
    fun setAccount(account: Account)
}
