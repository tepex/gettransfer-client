package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.core.domain.GTAddress

@StateStrategyType(OneExecutionStateStrategy::class)
interface SearchAddressView: BaseView {
    fun setAddressList(list: List<GTAddress>)
    fun returnLastAddress(addressName: String)
}
