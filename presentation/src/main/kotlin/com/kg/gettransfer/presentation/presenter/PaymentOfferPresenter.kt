package com.kg.gettransfer.presentation.presenter

import android.os.Bundle

import android.support.annotation.CallSuper
import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AFInAppEventType

import com.arellomobile.mvp.InjectViewState
import com.facebook.appevents.AppEventsConstants

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.model.BookNowOffer

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.mapper.PaymentRequestMapper

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentRequestModel

import com.kg.gettransfer.presentation.view.PaymentOfferView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import org.koin.standalone.inject

import timber.log.Timber

@InjectViewState
class PaymentOfferPresenter : BasePresenter<PaymentOfferView>() {

    companion object {
        const val PRICE_30 = 0.3
    }

    private val paymentInteractor: PaymentInteractor by inject()
    private val routeInteractor: RouteInteractor by inject()

    private val paymentRequestMapper: PaymentRequestMapper by inject()

    private var offer: Offer? = null
    private var bookNowOffer: BookNowOffer? = null
    internal lateinit var params: PaymentOfferView.Params
    private var transfer: Transfer? = null
    private var url: String? = null
    internal var braintreeToken = ""
    private var paymentId = 0L
    internal var selectedPayment = PaymentRequestModel.PLATRON

    private lateinit var paymentRequest: PaymentRequestModel

    @CallSuper
    override fun attachView(view: PaymentOfferView) {
        super.attachView(view)
        viewState.blockInterface(false)
        utils.launchSuspend {
            val resultOffers = utils.asyncAwait { offerInteractor.getOffers(params.transferId) }
            if (resultOffers.error == null) {
                offer = params.offerId?.let { offerInteractor.getOffer(it) }
                offer?.let {
                    paymentRequest = PaymentRequestModel(params.transferId, params.offerId, null)
                    viewState.setOffer(offerMapper.toView(it), params.paymentPercentages)
                }
            }

            val transferResult = utils.asyncAwait { transferInteractor.getTransfer(params.transferId) }
            transferResult.error?.let { checkResultError(it) }
            if (transferResult.error == null || (transferResult.error != null && transferResult.fromCache)) {
                transfer = transferResult.model
                if (offer == null) {
                    paymentRequest = PaymentRequestModel(params.transferId, null, params.bookNowTransportId)
                    if (transferResult.model.bookNowOffers.isNotEmpty()) {
                        val filteredBookNow = transferResult.model.bookNowOffers.filterKeys { it.toString() == params.bookNowTransportId }
                        if (filteredBookNow.isNotEmpty()) {
                            bookNowOffer = filteredBookNow.values.first()
                        }
                    }
                    viewState.setBookNowOffer(bookNowOffer)
                }
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
        viewState.blockInterface(true, true)

        val offersResult = utils.asyncAwait { offerInteractor.getOffers(params.transferId) }
        offersResult.error?.let { checkResultError(it) }
        if (offersResult.error == null || (offersResult.error != null && offersResult.fromCache)) {
            offer = params.offerId?.let { offerInteractor.getOffer(it) }
            paymentRequest.gatewayId = selectedPayment
            if (paymentRequest.gatewayId == PaymentRequestModel.PLATRON) {
                val paymentResult = utils.asyncAwait { paymentInteractor.getPayment(paymentRequestMapper.fromView(paymentRequest)) }
                if (paymentResult.error != null) {
                    Timber.e(paymentResult.error!!)
                    viewState.setError(paymentResult.error!!)
                } else {
                    url = paymentResult.model.url
                    navigateToPayment()
                }
                viewState.blockInterface(false)
            } else {
                getBraintreeToken()
            }
            logEventBeginCheckout()

            if (offer == null && bookNowOffer == null) viewState.showOfferError()
        }
    }

    private fun getBraintreeToken() {
        utils.launchSuspend {
            val tokenResult = utils.asyncAwait { paymentInteractor.getBrainTreeToken() }
            if (tokenResult.error != null) viewState.setError(tokenResult.error!!)
            else braintreeToken = tokenResult.isNotError()?.token ?: ""
            createNewPayment()
        }
    }

    private suspend fun createNewPayment() {
        val result = utils.asyncAwait { paymentInteractor.getPayment(paymentRequestMapper.fromView(paymentRequest)) }
        if (result.error != null) {
            Timber.e(result.error!!)
            viewState.setError(result.error!!)
            viewState.blockInterface(false)
        } else {
            val params = result.model.params
            paymentId = params?.paymentId ?: 0L
            viewState.setupBraintree(params?.amount, params?.currency)
        }
    }

    fun confirmPayment(nonce: String) = router.navigateTo(
                Screens.PayPalConnection(
                        paymentId, nonce, params.transferId,
                        params.offerId, paymentRequest.percentage, params.bookNowTransportId))


    private fun navigateToPayment() {
        router.navigateTo(Screens.Payment(params.transferId,
                offer?.id,
                url,
                paymentRequest.percentage,
                params.bookNowTransportId,
                selectedPayment))
    }

    private fun logEventBeginCheckout() {
        val offerType = if (offer != null ) Analytics.REGULAR else Analytics.NOW
        val requestType = when {
            transfer?.duration != null -> Analytics.TRIP_HOURLY
            transfer?.dateReturnLocal != null -> Analytics.TRIP_ROUND
            else -> Analytics.TRIP_DESTINATION
        }
        var price: Double = if (offer != null) offer!!.price.amount else bookNowOffer!!.amount
        if (paymentRequest.percentage == OfferModel.PRICE_30) price *= PRICE_30

        val beginCheckout = analytics.BeginCheckout(
                paymentRequest.percentage,
                transferInteractor.transferNew?.promoCode,
                routeInteractor.duration,
                selectedPayment,
                offerType,
                requestType,
                systemInteractor.currency.currencyCode,
                price)
        beginCheckout.sendAnalytics()
    }

    fun changePrice(price: Int)        { paymentRequest.percentage = price }
    fun changePayment(payment: String) { paymentRequest.gatewayId  = payment }
    fun onAgreementClicked() = router.navigateTo(Screens.LicenceAgree)
}
