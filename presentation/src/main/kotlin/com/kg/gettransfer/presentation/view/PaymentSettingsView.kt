package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.presentation.model.OfferModel

import com.kg.gettransfer.utilities.DateSerializer

import java.util.Date

import kotlinx.serialization.Serializable

@StateStrategyType(OneExecutionStateStrategy::class)
interface PaymentSettingsView: BaseView {
    companion object {
        val EXTRA_PARAMS = "${PaymentSettingsView::class.java.name}.params"
        fun getParams()
    }
    
    @Serializable
    data class Params(@Serializable(with = DateSerializer::class) val dateRefund: Date?,
                      val transferId: Long,
                      val offerId: Long)
    
    fun setOffer(offer: OfferModel)
}
