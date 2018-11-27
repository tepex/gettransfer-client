package com.kg.gettransfer.presentation.presenter

import android.os.Bundle

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentStatusRequestModel

import com.kg.gettransfer.presentation.presenter.PaymentSettingsPresenter.Companion.PRICE_30

import com.kg.gettransfer.presentation.view.PaymentView
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.utilities.Analytics
import com.kg.gettransfer.utilities.Analytics.Companion.CURRENCY
import com.kg.gettransfer.utilities.Analytics.Companion.EVENT_ECOMMERCE_PURCHASE
import com.kg.gettransfer.utilities.Analytics.Companion.EVENT_MAKE_PAYMENT
import com.kg.gettransfer.utilities.Analytics.Companion.PROMOCODE
import com.kg.gettransfer.utilities.Analytics.Companion.RESULT_FAIL
import com.kg.gettransfer.utilities.Analytics.Companion.RESULT_SUCCESS
import com.kg.gettransfer.utilities.Analytics.Companion.STATUS
import com.kg.gettransfer.utilities.Analytics.Companion.TRANSACTION_ID
import com.kg.gettransfer.utilities.Analytics.Companion.VALUE

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class PaymentPresenter: BasePresenter<PaymentView>() {
    private val paymentInteractor: PaymentInteractor by inject()
    private val offerInteractor: OfferInteractor by inject()
    private val transferInteractor: TransferInteractor by inject()

    private lateinit var offer: Offer
    
    internal var transferId = 0L
    internal var offerId    = 0L
    internal var percentage = 0

    fun changePaymentStatus(orderId: Long, success: Boolean) {
        utils.launchSuspend {
            viewState.blockInterface(true)
            val model = PaymentStatusRequestModel(null, orderId, true, success)
            val result = utils.asyncAwait { paymentInteractor.changeStatusPayment(Mappers.getPaymentStatusRequest(model)) }
            if(result.error != null) {
                Timber.e(result.error!!)
                viewState.setError(result.error!!)
                router.exit()
            } else {
                if(result.model.success) {
                    router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
                    viewState.showSuccessfulMessage()
                    offer = offerInteractor.getOffer(offerId)!!
                    logEventEcommercePurchase()
                    logEvent(RESULT_SUCCESS)
                } else {
                    router.exit()
                    viewState.showErrorMessage()
                    logEvent(RESULT_FAIL)
                }
            }
            viewState.blockInterface(false)
        }
    }

    private fun logEventEcommercePurchase() {
        val bundle = Bundle()
        val map = HashMap<String, Any?>()

        map[CURRENCY] = systemInteractor.currency.currencyCode
        bundle.putString(CURRENCY, systemInteractor.currency.currencyCode)

        var price = offer.price.amount

        when(percentage) {
            OfferModel.FULL_PRICE -> {
                bundle.putDouble(VALUE, price)
                map[VALUE] = price
            }
            OfferModel.PRICE_30 -> {
                price *= PRICE_30
                bundle.putDouble(VALUE, price)
                map[VALUE] = price
            }
        }

        bundle.putString(TRANSACTION_ID, transferId.toString())
        map[TRANSACTION_ID] = transferId
        bundle.putString(PROMOCODE, transferInteractor.transferNew?.promoCode)
        map[PROMOCODE] = transferInteractor.transferNew?.promoCode

        analytics.logEventEcommercePurchase(EVENT_ECOMMERCE_PURCHASE, bundle, map,
                price.toBigDecimal(), systemInteractor.currency)
    }

    fun logEvent(value: String) {
        val map = HashMap<String, Any>()
        map[STATUS] = value

        analytics.logEvent(EVENT_MAKE_PAYMENT, createStringBundle(STATUS, value), map)
    }

}
