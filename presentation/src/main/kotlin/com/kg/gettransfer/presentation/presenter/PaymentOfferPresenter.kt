package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.exceptions.InvalidArgumentException
import com.braintreepayments.api.models.PayPalRequest

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
import io.sentry.Sentry

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
    private var currentTransfer: Transfer? = null
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
            getOffers()
        }
        getPaymentRequest()
    }

    private fun getPaymentRequest() {
        paymentRequest =
                if (params.bookNowTransportId == null)
                    PaymentRequestModel(params.transferId, params.offerId, null)
                else
                    PaymentRequestModel(params.transferId, null, params.bookNowTransportId)
    }

    private suspend fun getOffers() {
        val offersResult = utils.asyncAwait { offerInteractor.getOffers(params.transferId) }
        offersResult.error?.let { checkResultError(it) }
        if (offersResult.error == null || (offersResult.error != null && offersResult.fromCache)) {
            offer = params.offerId?.let { offerInteractor.getOffer(it) }
            offer?.let {
                viewState.setOffer(offerMapper.toView(it), params.paymentPercentages) }
        }
        getTransfer()
    }

    private suspend fun getTransfer() {
        val transferResult = utils.asyncAwait { transferInteractor.getTransfer(params.transferId) }
        transferResult.error?.let { checkResultError(it) }
        if (transferResult.error == null || (transferResult.error != null && transferResult.fromCache)) {
            currentTransfer = transferResult.model
            if (params.bookNowTransportId != null) {
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
//            fetchData { offerInteractor.getOffers(params.transferId) }
//                    ?.let {
//                        params.offerId
//                                ?.let { id -> offerInteractor.getOffer(id) }
//                                ?.let { mOffer ->
//                                    offer = mOffer
//                                    paymentRequest = PaymentRequestModel(params.transferId, params.offerId, null)
//                                    viewState.setOffer(offerMapper.toView(mOffer), params.paymentPercentages)
//                                    return@launchSuspend
//                                }
//                    }

//            fetchResult { transferInteractor.getTransfer(params.transferId) }
//                    .also {
//                        it.hasData()?.let { currentTransfer ->
//                            paymentRequest = PaymentRequestModel(params.transferId, null, params.bookNowTransportId)
//                            if (currentTransfer.bookNowOffers.isNotEmpty()) {
//                                val filteredBookNow = currentTransfer.bookNowOffers.filterKeys { id -> id.toString() == params.bookNowTransportId }
//                                if (filteredBookNow.isNotEmpty()) bookNowOffer = filteredBookNow.values.first()
//                            }
//                            viewState.setBookNowOffer(bookNowOffer)
//                        }
//
//                        it.isDataError()?.let {
//                            viewState.setError(ApiException(ApiException.NOT_FOUND, "Offer [${params.offerId}] not found!"))
//                        }
//
//                    }
        }
    }

    fun getPayment() = utils.launchSuspend {
        viewState.blockInterface(true, true)

        paymentRequest.let {
            it.gatewayId = selectedPayment
            if (it.gatewayId == PaymentRequestModel.PLATRON) {
                val paymentResult = utils.asyncAwait { paymentInteractor.getPayment(paymentRequestMapper.fromView(it)) }
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
        }
        if (offer == null && bookNowOffer == null) viewState.showOfferError()

//        fetchDataOnly { offerInteractor.getOffers(params.transferId) }
//                ?.let {
//                    offer = params.offerId?.let { offerInteractor.getOffer(it) }
//                    fetchData(withCacheCheck = false, checkLoginError = false) { paymentInteractor.getPayment(paymentRequestMapper.fromView(paymentRequest)) }
//                            ?.let {
//                                logEventBeginCheckout()
//                                router.navigateTo(Screens.Payment(params.transferId,
//                                        offer?.id,
//                                        it.url!!,
//                                        paymentRequest.percentage,
//                                        params.bookNowTransportId)) } }
//
//        viewState.blockInterface(false)
//        if (offer == null && bookNowOffer == null) viewState.showOfferError()

    }

    private fun getBraintreeToken() {
        utils.launchSuspend {
            val tokenResult = utils.asyncAwait { paymentInteractor.getBrainTreeToken() }
            if (tokenResult.error != null) viewState.setError(tokenResult.error!!)
            else braintreeToken = tokenResult.isSuccess()?.token ?: ""
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
            setupPaypal(params?.amount, params?.currency)
        }
    }

    private fun setupPaypal(amount: String?, currency: String?) {
        try {
            val dropInRequest = DropInRequest().clientToken(braintreeToken)

            val paypal = PayPalRequest(amount)
                    .currencyCode(currency).intent(PayPalRequest.INTENT_AUTHORIZE)
            dropInRequest.paypalRequest(paypal)
            viewState.startPaypal(dropInRequest)
        } catch (e: InvalidArgumentException) {
            Sentry.capture(e)
        }

    }

    fun confirmPayment(nonce: String) {
        viewState.blockInterface(true, true)
        currentTransfer?.let {
            router.navigateTo(
                    Screens.PayPalConnection(
                            paymentId, nonce, it.id,
                            params.offerId, paymentRequest.percentage, params.bookNowTransportId))
        }
    }


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
            currentTransfer?.duration != null -> Analytics.TRIP_HOURLY
            currentTransfer?.dateReturnLocal != null -> Analytics.TRIP_ROUND
            else -> Analytics.TRIP_DESTINATION
        }
        var price = 0.0
        if (offer != null) offer!!.price.amount
        else if (bookNowOffer != null) bookNowOffer!!.amount
        if (paymentRequest.percentage == OfferModel.PRICE_30) price *= PRICE_30

        val beginCheckout = analytics.BeginCheckout(
                paymentRequest.percentage,
                currentTransfer?.promoCode,
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
