package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.domain.model.GTAddress

@StateStrategyType(OneExecutionStateStrategy::class)
interface SearchAddressView: BaseView {
    fun setAddressList(list: List<GTAddress>)
    fun returnLastAddress(addressName: String)
}
