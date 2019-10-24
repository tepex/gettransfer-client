package com.kg.gettransfer.presentation.view

import androidx.annotation.StringRes
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.domain.model.GTAddress

@StateStrategyType(OneExecutionStateStrategy::class)
interface SearchView : BaseView {
    fun setAddressListByAutoComplete(list: List<GTAddress>)
    fun onFindPopularPlace(isToField: Boolean, place: String)
    fun setAddressFrom(address: String, sendRequest: Boolean, isEditing: Boolean)
    fun setAddressTo(address: String, sendRequest: Boolean, isEditing: Boolean)
    fun showHourlyDurationDialog(durationValue: Int?)
    fun changeViewToHourlyDuration(durationValue: Int?)
    fun setHourlyDuration(duration: Int?)
    fun setSuggestedAddresses(addressesList: List<GTAddress>)
    fun markFieldFilled(isToField: Boolean)
    fun setFocus(isToField: Boolean)
    fun changeFocusToDestField()
    fun onAddressError(@StringRes message: Int, address: GTAddress, fieldTo: Boolean)
    fun goToMap()
    fun goToBack()
    fun goToCreateOrder()
}
