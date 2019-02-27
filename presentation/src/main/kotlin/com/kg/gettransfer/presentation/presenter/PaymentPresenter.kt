package com.kg.gettransfer.presentation.presenter

import android.os.Bundle
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AFInAppEventType

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.model.BookNowOffer

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.mapper.PaymentStatusRequestMapper

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentStatusRequestModel

import com.kg.gettransfer.presentation.presenter.PaymentSettingsPresenter.Companion.PRICE_30

import com.kg.gettransfer.presentation.view.PaymentView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics
import io.sentry.Sentry

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class PaymentPresenter : BasePresenter<PaymentView>() {
    private val paymentInteractor: PaymentInteractor by inject()
    private val mapper: PaymentStatusRequestMapper by inject()
    private val routeInteractor: RouteInteractor by inject()

    private var offer: Offer? = null
    private var bookNowOffer: BookNowOffer? = null
    private var transfer: Transfer? = null

    internal var transferId = 0L
    internal var offerId    = 0L
    internal var percentage = 0
    internal var bookNowTransportId = ""

    override fun attachView(view: PaymentView) {
        super.attachView(view)
        offer = offerInteractor.getOffer(offerId)
        utils.launchSuspend {
            val result = utils.asyncAwait { transferInteractor.getTransfer(transferId) }
            if (result.error == null || (result.error != null && result.fromCache)) {
                transfer = result.model
                if (offer == null) {
                    transfer?.let {
                        if (transfer!!.bookNowOffers.isNotEmpty()) {
                            if (bookNowTransportId.isNotEmpty()) {
                                val filteredBookNow = transfer!!.bookNowOffers.filterKeys { it.toString() == bookNowTransportId }
                                if (filteredBookNow.isNotEmpty()) {
                                    bookNowOffer = filteredBookNow.values.first()
                                }
                            }
                        }
                    }
                }
            } else {
                Sentry.capture(result.error)
            }
        }
    }

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
        val afMap = mutableMapOf<String, Any?>()

        bundle.putString(Analytics.TRANSACTION_ID, transferId.toString())
        map[Analytics.TRANSACTION_ID] = transferId.toString()
        bundle.putString(Analytics.PROMOCODE, transferInteractor.transferNew?.promoCode)
        map[Analytics.PROMOCODE] = transferInteractor.transferNew?.promoCode
        routeInteractor.duration?.let { bundle.putInt(Analytics.HOURS, it) }
        routeInteractor.duration?.let { map[Analytics.HOURS] = it }

        val offerType = if (offer != null) Analytics.REGULAR else Analytics.NOW
        bundle.putString(Analytics.OFFER_TYPE, offerType)
        map[Analytics.OFFER_TYPE] = offerType

        when {
            transfer?.duration != null -> Analytics.TRIP_HOURLY
            transfer?.dateReturnLocal != null -> Analytics.TRIP_ROUND
            else -> Analytics.TRIP_DESTINATION
        }.let {
            bundle.putString(Analytics.TRIP_TYPE, it)
            map[Analytics.TRIP_TYPE] = it
        }

        fbBundle.putAll(bundle)
        afMap.putAll(map)

        val currency = systemInteractor.currency.currencyCode
        map[Analytics.CURRENCY] = currency
        afMap[AFInAppEventParameterName.CURRENCY] = currency
        bundle.putString(Analytics.CURRENCY, currency)

        var price: Double = if (offer != null) offer!!.price.amount else bookNowOffer!!.amount

        when (percentage) {
            OfferModel.FULL_PRICE -> {
                bundle.putDouble(Analytics.VALUE, price)
                map[Analytics.VALUE] = price
                afMap[AFInAppEventParameterName.REVENUE] = price
            }
            OfferModel.PRICE_30 -> {
                price *= PRICE_30
                bundle.putDouble(Analytics.VALUE, price)
                map[Analytics.VALUE] = price
                afMap[AFInAppEventParameterName.REVENUE] = price
            }
        }
        analytics.logEventEcommerce(Analytics.EVENT_ECOMMERCE_PURCHASE, bundle, map)
        analytics.logEventEcommercePurchaseFB(fbBundle, price.toBigDecimal(), systemInteractor.currency)
        analytics.logEventToAppsFlyer(AFInAppEventType.PURCHASE, afMap)
    }

    fun logEvent(value: String) {
        val map = mutableMapOf<String, Any>()
        map[Analytics.STATUS] = value

        analytics.logEvent(Analytics.EVENT_MAKE_PAYMENT, createStringBundle(Analytics.STATUS, value), map)
    }
}
