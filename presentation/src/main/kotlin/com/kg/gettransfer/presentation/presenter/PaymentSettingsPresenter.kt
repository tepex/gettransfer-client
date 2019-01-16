package com.kg.gettransfer.presentation.presenter

import android.os.Bundle

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.facebook.appevents.AppEventsConstants

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.model.BookNowOffer

import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.presentation.mapper.PaymentRequestMapper

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentRequestModel

import com.kg.gettransfer.presentation.view.PaymentSettingsView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class PaymentSettingsPresenter : BasePresenter<PaymentSettingsView>() {
    private val paymentInteractor: PaymentInteractor by inject()

    private val paymentRequestMapper: PaymentRequestMapper by inject()

    private var offer: Offer? = null
    private var bookNowOffer: BookNowOffer? = null
    internal lateinit var params: PaymentSettingsView.Params

    private lateinit var paymentRequest: PaymentRequestModel

    @CallSuper
    override fun attachView(view: PaymentSettingsView?) {
        super.attachView(view)
        offer = params.offerId?.let { offerInteractor.getOffer(it) }
        offer?.let {
            paymentRequest = PaymentRequestModel(params.transferId, params.offerId, null)
            viewState.setOffer(offerMapper.toView(it), params.paymentPercentages)
            return
        }
        utils.launchSuspend {
            val result = utils.asyncAwait {
                transferInteractor.getTransfer(params.transferId)
            }
            if (result.error == null) {
                paymentRequest = PaymentRequestModel(params.transferId, null, params.bookNowTransportId)
                bookNowOffer = result.model.bookNowOffers?.filterKeys { it.name == params.bookNowTransportId }?.values?.first()
                viewState.setBookNowOffer(bookNowOffer)
            } else {
                viewState.setError(ApiException(ApiException.NOT_FOUND, "Offer [${params.offerId}] not found!"))
            }
        }
    }

    /*
    @CallSuper
    override fun onDestroy() {
        router.removeResultListener(LoginPresenter.RESULT_CODE)
        super.onDestroy()
    }
    */

    fun getPayment() = utils.launchSuspend {
        viewState.blockInterface(true)

        val result = utils.asyncAwait { paymentInteractor.getPayment(paymentRequestMapper.fromView(paymentRequest)) }
        if (result.error != null) {
            Timber.e(result.error!!)
            viewState.setError(result.error!!)
        } else {
            logEventBeginCheckout()
            router.navigateTo(Screens.Payment(params.transferId, offer?.id, result.model.url!!,
                    paymentRequest.percentage, params.bookNowTransportId))
        }
        viewState.blockInterface(false)
    }

    private fun logEventBeginCheckout() {
        val bundle = Bundle()
        val fbBundle = Bundle()
        val map = mutableMapOf<String, Any?>()

        bundle.putInt(Analytics.SHARE, paymentRequest.percentage)
        map[Analytics.SHARE] = paymentRequest.percentage
        bundle.putString(Analytics.PROMOCODE, transferInteractor.transferNew?.promoCode)
        map[Analytics.PROMOCODE] = transferInteractor.transferNew?.promoCode
        fbBundle.putAll(bundle)

        bundle.putString(Analytics.CURRENCY, systemInteractor.currency.currencyCode)
        fbBundle.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, systemInteractor.currency.currencyCode)
        map[Analytics.CURRENCY] = systemInteractor.currency.currencyCode

        var price: Double = if (offer != null) offer!!.price.amount
         else bookNowOffer!!.amount

        when (paymentRequest.percentage) {
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

        analytics.logEventEcommerce(Analytics.EVENT_BEGIN_CHECKOUT, bundle, map)
        analytics.logEventBeginCheckoutFB(fbBundle, price)
    }

    fun changePrice(price: Int)        { paymentRequest.percentage = price }
    fun changePayment(payment: String) { paymentRequest.gatewayId  = payment }
    fun onAgreementClicked() = router.navigateTo(Screens.LicenceAgree)

    companion object {
        const val PRICE_30 = 0.3
    }
}
