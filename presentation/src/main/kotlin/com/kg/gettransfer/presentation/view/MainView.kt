package com.kg.gettransfer.presentation.view

import android.support.annotation.StringRes

import com.arellomobile.mvp.MvpView

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener

@StateStrategyType(OneExecutionStateStrategy::class)
//interface MainView: MvpView, OnCameraMoveListener
interface MainView: MvpView {
	fun qqq(s: String)
	
	fun blockInterface(block: Boolean)
	fun setMapPoint(current: LatLng)
	fun setAddressFrom(addressFrom: String)
	fun setAddressTo(addressTo: String)
	fun setError(@StringRes errId: Int, finish: Boolean = false)
}
