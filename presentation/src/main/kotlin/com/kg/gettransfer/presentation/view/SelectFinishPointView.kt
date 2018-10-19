package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.google.android.gms.maps.model.LatLng

@StateStrategyType(OneExecutionStateStrategy::class)
interface SelectFinishPointView: BaseView{
    fun setMapPoint(point: LatLng)
    fun moveCenterMarker(point: LatLng)
    fun setAddressFrom(address: String)
    fun setAddressTo(address: String)
}