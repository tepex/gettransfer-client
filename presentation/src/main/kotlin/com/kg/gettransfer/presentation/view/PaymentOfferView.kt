package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.braintreepayments.api.dropin.DropInRequest
import com.kg.gettransfer.domain.model.BookNowOffer
import com.kg.gettransfer.presentation.model.BookNowOfferModel

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.utilities.DateSerializer

import java.util.Date

import kotlinx.serialization.Serializable

@StateStrategyType(OneExecutionStateStrategy::class)
interface PaymentOfferView : BaseView {

    fun setOffer(offer: OfferModel, paymentPercentages: List<Int>)
    fun setBookNowOffer(bookNowOffer: BookNowOfferModel?)
    fun showOfferError()
    fun setCommission(paymentCommission: String)
    fun startPaypal(dropInRequest: DropInRequest)
    fun setToolbarTitle(transferModel: TransferModel)
    fun setPaymentEnabled(enabled: Boolean)

    fun setAuthUiVisible(visible: Boolean)
    fun showBadCredentialsInfo()
    fun setEmail(email: String)
    fun setPhone(phone: String)
    fun redirectToLogin()
    fun onAccountCreated()

    companion object {
        val EXTRA_PARAMS = "${PaymentOfferView::class.java.name}.params"
    }

    @Serializable
    data class Params(
        @Serializable(with = DateSerializer::class) val dateRefund: Date?,
        val transferId: Long,
        val offerId: Long?,
        val paymentPercentages: List<Int>,
        val bookNowTransportId: String?
    )
}
