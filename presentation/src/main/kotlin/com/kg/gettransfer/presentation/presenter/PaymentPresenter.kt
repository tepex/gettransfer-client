package com.kg.gettransfer.presentation.presenter

import android.os.Bundle

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.model.BookNowOffer

import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.presentation.mapper.PaymentStatusRequestMapper

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentStatusRequestModel

import com.kg.gettransfer.presentation.presenter.PaymentSettingsPresenter.Companion.PRICE_30

import com.kg.gettransfer.presentation.view.PaymentView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class PaymentPresenter : BasePresenter<PaymentView>() {
    private val paymentInteractor: PaymentInteractor by inject()
    private val mapper: PaymentStatusRequestMapper by inject()

    private var offer: Offer? = null
    private var bookNowOffer: BookNowOffer? = null

    internal var transferId = 0L
    internal var offerId    = 0L
    internal var percentage = 0
    internal var bookNowTransportId = ""


    fun changePaymentStatus(orderId: Long, success: Boolean) {
        utils.launchSuspend {
            viewState.blockInterface(true)
            val model = PaymentStatusRequestModel(null, orderId, true, success)
            val result = utils.asyncAwait { paymentInteractor.changeStatusPayment(mapper.fromView(model)) }
            if (result.error != null) {
                Timber.e(result.error!!)
                viewState.setError(result.error!!)
                router.exit()
            } else {
                if (result.model.success) {
                    router.navigateTo(Screens.ChangeMode(Screens.PASSENGER_MODE))
                    router.navigateTo(Screens.PaymentSuccess(transferId, offerId))
                    logEventEcommercePurchase()
                    logEvent(Analytics.RESULT_SUCCESS)
                } else {
                    router.exit()
                    router.navigateTo(Screens.PaymentError(transferId))
                    logEvent(Analytics.RESULT_FAIL)
                }
            }
            viewState.blockInterface(false)
        }
    }

    private fun logEventEcommercePurchase() {
        val bundle = Bundle()
        val fbBundle = Bundle()
        val map = mutableMapOf<String, Any?>()

        bundle.putString(Analytics.TRANSACTION_ID, transferId.toString())
        map[Analytics.TRANSACTION_ID] = transferId.toString()
        bundle.putString(Analytics.PROMOCODE, transferInteractor.transferNew?.promoCode)
        map[Analytics.PROMOCODE] = transferInteractor.transferNew?.promoCode
        fbBundle.putAll(bundle)

        map[Analytics.CURRENCY] = systemInteractor.currency.currencyCode
        bundle.putString(Analytics.CURRENCY, systemInteractor.currency.currencyCode)

        offer = offerInteractor.getOffer(offerId)
        utils.launchSuspend {
            val result = utils.asyncAwait {
                transferInteractor.getTransfer(transferId)
            }
            if (result.error == null) {
                if (result.model.bookNowOffers.isNotEmpty()) {
                    bookNowOffer = result.model.bookNowOffers.filterKeys { it.name == bookNowTransportId }.values.first()
                }
            }
        }

        var price: Double = if (offer != null) offer!!.price.amount
        else bookNowOffer!!.amount

        when (percentage) {
            OfferModel.FULL_PRICE -> {
                bundle.putDouble(Analytics.VALUE, price)
                map[Analytics.VALUE] = price
            }
            OfferModel.PRICE_30 -> {
                price *= PRICE_30
                bundle.putDouble(Analytics.VALUE, price)
                map[Analytics.VALUE] = price
            }
        }

        analytics.logEventEcommerce(Analytics.EVENT_ECOMMERCE_PURCHASE, bundle, map)
        analytics.logEventEcommercePurchaseFB(fbBundle, price.toBigDecimal(), systemInteractor.currency)
    }

    fun logEvent(value: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.STATUS] = value

        analytics.logEvent(Analytics.EVENT_MAKE_PAYMENT, createStringBundle(Analytics.STATUS, value), map)
    }
}
