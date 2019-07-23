package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.google.android.gms.maps.model.LatLng

@Suppress("TooManyFunctions")
@StateStrategyType(OneExecutionStateStrategy::class)
interface BaseNewTransferView : MvpView {
    fun blockFromField()
    fun blockToField()
    fun setAddressFrom(address: String)
    fun setAddressTo(address: String)
    fun selectFieldFrom()
    fun setFieldTo()
    fun setHourlyDuration(duration: Int?)
    fun updateTripView(isHourly: Boolean)
    fun defineAddressRetrieving(block: (withGps: Boolean) -> Unit)
    fun showHourlyDurationDialog(durationIndex: Int?)
}
