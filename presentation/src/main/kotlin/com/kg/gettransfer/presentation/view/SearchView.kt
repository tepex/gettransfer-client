package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.presentation.model.PopularPlace

@StateStrategyType(OneExecutionStateStrategy::class)
//interface MainView: MvpView, OnCameraMoveListener
interface SearchView: BaseView {
	fun setAddressList(list: List<Any>)
	fun onFindPopularPlace(isTo: Boolean, place: String)
	fun setAddressFrom(address: String, sendRequest: Boolean)
	fun setAddressTo(address: String, sendRequest: Boolean)
}
