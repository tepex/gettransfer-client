package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.exceptions.InvalidArgumentException
import com.braintreepayments.api.models.PayPalRequest

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.model.BookNowOffer

import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.Profile
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.mapper.PaymentRequestMapper

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentRequestModel
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper
import com.kg.gettransfer.presentation.ui.helpers.LoginHelper.CREDENTIALS_VALID

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
    private val orderInteractor: OrderInteractor by inject()

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
    val profile: Profile
        get() = sessionInteractor.account.user.profile
    lateinit var authEmail: String
    lateinit var authPhone: String

    @CallSuper
    override fun attachView(view: PaymentOfferView) {
        super.attachView(view)
        isUserAuthorized()
        sessionInteractor.paymentCommission.let {
            viewState.setCommission(if (it % 1.0 == 0.0) it.toInt().toString() else it.toString())
        }
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            getOffers()
            viewState.blockInterface(false)
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
        if (offersResult.error == null) fetchResultOnly { transferInteractor.setOffersUpdatedDate(params.transferId) }
        if (offersResult.error == null || (offersResult.error != null && offersResult.fromCache)) {
            offer = params.offerId?.let { offerInteractor.getOffer(it) }
            offer?.let {
                offerMapper.toView(it)
                        .also { viewModel ->
                            viewState.setOffer(viewModel, params.paymentPercentages)
                            if (viewModel.currency != sessionInteractor.currency.code)
                                viewState.setCurrencyConvertingInfo(
                                        sessionInteractor.currencies.first { c -> c.code == viewModel.currency },
                                        sessionInteractor.currency
                                )
                        }
            }
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
                viewState.setBookNowOffer(bookNowOffer?.let { bookNowOfferMapper.toView(it) })
            }
            viewState.setToolbarTitle(transferMapper.toView(transferResult.model))
        } else {
            viewState.setError(ApiException(ApiException.NOT_FOUND, "Offer [${params.offerId}] not found!"))
        }
    }

    private fun getPayment() = utils.launchSuspend {
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
    }

    fun onPaymentClicked() {
        if (profile.hasData())
            getPayment()
         else
            putAccount()
    }

    private fun putAccount() {
        if (!isValid(authEmail, false) || !isValid(authPhone, true)) return
        with(profile) {
            phone = LoginHelper.formatPhone(authPhone)
            email = authEmail
        }
        utils.launchSuspend { pushAccount() }
    }

    private suspend fun pushAccount() =
            fetchResultOnly { sessionInteractor.putAccount() }
                    .run {
                        when {
                            error?.isAccountExistError() ?: false  -> onAccountExists()
                            error != null                          -> viewState.setError(error!!)
                            else                                   -> getPayment()
                        }
                    }

    private fun onAccountExists() {
        profile.clear()
        viewState.redirectToLogin()
    }

    private fun isValid(input: String, isPhone: Boolean) =
        LoginHelper
                .validateInput(input, isPhone)
                .let {
                    if (it != CREDENTIALS_VALID)
                        viewState.showBadCredentialsInfo(it)
                    it == CREDENTIALS_VALID
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
                            paymentId,
                            nonce,
                            it.id,
                            params.offerId,
                            paymentRequest.percentage,
                            params.bookNowTransportId)
            )
        }
    }


    private fun navigateToPayment() {
        router.navigateTo(Screens.Payment(
                params.transferId,
                offer?.id,
                url,
                paymentRequest.percentage,
                params.bookNowTransportId,
                selectedPayment)
        )
    }

    private fun isUserAuthorized() {
        val isLoggedIn = profile.hasData()
        viewState.setAuthUiVisible(!isLoggedIn)
        if (!isLoggedIn) {
            with(profile) {
                phone?.let { viewState.setPhone(it) }
                email?.let { viewState.setEmail(it) }
            }
        }
    }

    fun redirectToLogin(phone: String) {
        router.navigateTo(Screens.Login(Screens.CLOSE_AFTER_LOGIN, phone))
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
                orderInteractor.duration,
                selectedPayment,
                offerType,
                requestType,
                sessionInteractor.currency.code,
                price)
        beginCheckout.sendAnalytics()
    }

    fun changePrice(price: Int)        { paymentRequest.percentage = price }
    fun changePayment(payment: String) { paymentRequest.gatewayId  = payment }
    fun onAgreementClicked() = router.navigateTo(Screens.LicenceAgree)
}
