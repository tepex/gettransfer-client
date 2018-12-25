package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.domain.model.GTAddress

@StateStrategyType(OneExecutionStateStrategy::class)
//interface MainView: MvpView, OnCameraMoveListener
interface SearchView: BaseView {
    companion object {
        val EXTRA_ADDRESS_FROM = "${SearchView::class.java.name}.address_from"
        val EXTRA_ADDRESS_TO   = "${SearchView::class.java.name}.address_to"
        val EXTRA_IS_CLICK_TO  = "${SearchView::class.java.name}.to_click"
        val EXTRA_BOUNDS  = "${SearchView::class.java.name}.bounds"
    }

    fun setAddressListByAutoComplete(list: List<GTAddress>)
    fun onFindPopularPlace(isTo: Boolean, place: String)
    fun setAddressFrom(address: String, sendRequest: Boolean, isEditing: Boolean)
    fun setAddressTo(address: String, sendRequest: Boolean, isEditing: Boolean)
    fun hideAddressTo()
    fun setSuggestedAddresses(addressesList: List<GTAddress>)
    fun updateIcon(isTo: Boolean)
    fun setFocus(isToField: Boolean)
    fun changeFocusToDestField()
}
