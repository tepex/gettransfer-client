package com.kg.gettransfer.presentation.view

import androidx.annotation.StringRes

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

import com.braintreepayments.api.dropin.DropInRequest
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData

import com.kg.gettransfer.domain.model.Currency
import com.kg.gettransfer.presentation.model.BookNowOfferModel

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.ProfileModel
import com.kg.gettransfer.presentation.model.TransferModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface PaymentOfferView : BaseView {

    fun initGooglePayPaymentsClient(environment: Int)

    fun setOffer(offer: OfferModel, paymentPercentages: List<Int>, isNameSignPresent: Boolean)
    fun setBookNowOffer(bookNowOffer: BookNowOfferModel, isNameSignPresent: Boolean)
    fun setCommission(paymentCommission: String, dateRefund: String)
    fun setToolbarTitle(transferModel: TransferModel)
    fun setCurrencyConvertingInfo(offerCurrency: Currency, ownCurrency: Currency)
    fun setAuthUiVisible(hasAccount: Boolean, profile: ProfileModel, balance: String?)

    fun showOfferError()
    fun showBadCredentialsInfo(field: Int)
    fun showFieldError(@StringRes stringId: Int)
    fun highLightError(error: CreateOrderView.FieldError?)

    fun showGooglePayButton()
    fun hideGooglePayButton()

    fun startPaypal(dropInRequest: DropInRequest, token: String)
    fun startGooglePay(task: Task<PaymentData>)
}
