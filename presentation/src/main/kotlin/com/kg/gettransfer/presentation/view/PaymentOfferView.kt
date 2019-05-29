package com.kg.gettransfer.presentation.view

import android.support.annotation.StringRes
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.braintreepayments.api.dropin.DropInRequest
import com.kg.gettransfer.domain.model.Currency
import com.kg.gettransfer.presentation.model.BookNowOfferModel

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.ProfileModel
import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.utilities.DateSerializer

import kotlinx.serialization.Serializable
import java.util.*


@StateStrategyType(OneExecutionStateStrategy::class)
interface PaymentOfferView : BaseView {

    fun setOffer(offer: OfferModel, paymentPercentages: List<Int>)
    fun setBookNowOffer(bookNowOffer: BookNowOfferModel?)
    fun showOfferError()
    fun setCommission(paymentCommission: String)
    fun startPaypal(dropInRequest: DropInRequest)
    fun setToolbarTitle(transferModel: TransferModel)
    fun setCurrencyConvertingInfo(offerCurrency: Currency, ownCurrency: Currency)

    fun setAuthUiVisible(hasAccount: Boolean, profile: ProfileModel)
    fun showBadCredentialsInfo(field: Int)

    fun showFieldError(@StringRes stringId: Int)
    fun enablePayment(enable: Boolean)

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
