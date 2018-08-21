package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.presentation.model.Address

@StateStrategyType(OneExecutionStateStrategy::class)
//interface MainView: MvpView, OnCameraMoveListener
interface SearchView: BaseView {
	fun setAddressList(list: List<Address>)
	fun setAddressFrom(address: String)
	fun setAddressTo(address: String)
}
