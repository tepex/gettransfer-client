package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import android.support.annotation.StringRes

import com.kg.gettransfer.presentation.model.Address

@StateStrategyType(OneExecutionStateStrategy::class)
interface SearchAddressView: MvpView {
	fun setAddressList(list: List<Address>)
	fun setError(@StringRes errId: Int, finish: Boolean = false)
}
