package com.kg.gettransfer.presentation.view

import androidx.annotation.StringRes

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.braintreepayments.api.dropin.DropInRequest

import com.kg.gettransfer.domain.model.Currency
import com.kg.gettransfer.presentation.model.BookNowOfferModel

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.ProfileModel
import com.kg.gettransfer.presentation.model.TransferModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface PaymentOfferView : BaseView {

    fun setOffer(offer: OfferModel, paymentPercentages: List<Int>)
    fun setBookNowOffer(bookNowOffer: BookNowOfferModel)
    fun showOfferError()
    fun setCommission(paymentCommission: String, dateRefund: String)
    fun startPaypal(dropInRequest: DropInRequest, brainteeToken: String)
    fun setToolbarTitle(transferModel: TransferModel)
    fun setCurrencyConvertingInfo(offerCurrency: Currency, ownCurrency: Currency)

    fun setAuthUiVisible(hasAccount: Boolean, profile: ProfileModel, balance: String?)
    fun showBadCredentialsInfo(field: Int)

    fun showFieldError(@StringRes stringId: Int)
    fun highLightError(error: CreateOrderView.FieldError?)
}
