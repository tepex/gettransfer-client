package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.google.android.gms.maps.model.LatLng

@Suppress("TooManyFunctions")
@StateStrategyType(OneExecutionStateStrategy::class)
interface NewTransferMapView : BaseNewTransferView {
    fun setMapPoint(point: LatLng, withAnimation: Boolean, showBtnMyLocation: Boolean)
    fun moveCenterMarker(point: LatLng)
    fun setMarkerElevation(up: Boolean)

    fun initUIForSelectedField(field: String)
    fun setAddress(address: String)
    fun blockAddressField()

    fun navigateBack()
    fun showRestartDialog()
    fun restartApp()
}
