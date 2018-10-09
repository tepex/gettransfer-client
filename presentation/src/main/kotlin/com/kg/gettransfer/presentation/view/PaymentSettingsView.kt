package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.presentation.model.OfferModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface PaymentSettingsView: BaseView {
    fun setOffer(offer: OfferModel)
}