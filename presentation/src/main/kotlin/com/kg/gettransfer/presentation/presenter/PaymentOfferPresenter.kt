package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.exceptions.InvalidArgumentException
import com.braintreepayments.api.models.PayPalRequest

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.interactor.PaymentInteractor

import com.kg.gettransfer.domain.model.BookNowOffer
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.OfferItem
import com.kg.gettransfer.domain.model.Payment
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.extensions.newChainFromMain

import com.kg.gettransfer.presentation.mapper.PaymentRequestMapper
import com.kg.gettransfer.presentation.mapper.ProfileMapper

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentRequestModel
import com.kg.gettransfer.presentation.model.map

import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.view.PaymentOfferView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import io.sentry.Sentry

import org.koin.core.inject

@InjectViewState
@Suppress("TooManyFunctions")
class PaymentOfferPresenter : BasePresenter<PaymentOfferView>() {

    companion object {
        const val PRICE_30 = 0.3
    }

    private val paymentInteractor: PaymentInteractor by inject()
    private val orderInteractor: OrderInteractor by inject()

    private val paymentRequestMapper: PaymentRequestMapper by inject()
    private val profileMapper: ProfileMapper by inject()

    private var transfer: Transfer? = null
    private var offer: OfferItem? = null

    private var url: String? = null
    internal var braintreeToken = ""
    private var paymentId = 0L
    internal var selectedPayment = PaymentRequestModel.PLATRON

    private var loginScreenIsShowed = false

    private lateinit var paymentRequest: PaymentRequestModel

    @Suppress("ComplexMethod")
    override fun attachView(view: PaymentOfferView) {
        super.attachView(view)
        viewState.blockInterface(false)
        with(paymentInteractor) {
            transfer = selectedTransfer
            offer = selectedOffer
        }
        getPaymentRequest()
        with(accountManager) {
            val balance = remoteAccount.partner?.availableMoney?.default

            viewState.setAuthUiVisible(
                    hasAccount,
                    profileMapper.toView(remoteProfile),
                    balance)
        }
        transfer?.let { transfer ->
            viewState.setToolbarTitle(transfer.map(systemInteractor.transportTypes.map { it.map() }))
            transfer.dateRefund?.let { dateRefund ->
                val commission = systemInteractor.paymentCommission
                viewState.setCommission(
                    if (commission % 1.0 == 0.0) commission.toInt().toString() else commission.toString(),
                    SystemUtils.formatDateTime(dateRefund)
                )
            }
        }

        transfer?.paymentPercentages?.let { percentages ->
            offer?.let { offer ->
                when (offer) {
                    is BookNowOffer -> viewState.setBookNowOffer(offer.map())
                    is Offer        -> viewState.setOffer(offerMapper.toView(offer), percentages)
                }
            }
        }

        if (loginScreenIsShowed) {
            loginScreenIsShowed = false
            if (accountManager.hasData) {
                getPayment()
            }
        }
    }

    override fun onDestroy() {
        accountManager.initTempUser()
        super.onDestroy()
    }

    private fun getPaymentRequest() {
        transfer?.id?.let { transferId ->
            offer?.let { offer ->
                paymentRequest = when (offer) {
                    is Offer -> PaymentRequestModel(transferId, offer.id, null)
                    is BookNowOffer -> PaymentRequestModel(transferId, null, offer.transportType.id.name.toLowerCase())
                }
            }
        }
    }

    fun setEmail(email: String) {
        accountManager.tempProfile.email = email.trim()
    }

    fun setPhone(phone: String) {
        accountManager.tempProfile.phone = phone.trim()
    }

    private fun getPayment() {
        if (transfer == null || offer == null) {
            viewState.blockInterface(false)
            viewState.showOfferError()
            return
        }
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            paymentRequest.gatewayId = selectedPayment
            if (selectedPayment == PaymentRequestModel.PLATRON) {
                val paymentResult =
                    utils.asyncAwait { paymentInteractor.getPayment(paymentRequestMapper.fromView(paymentRequest)) }
                if (paymentResult.error != null) {
                    paymentResult.error?.let { err ->
                        log.error("get payment error", err)
                        viewState.setError(err)
                    }
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
    }

    private suspend fun payByBalance(paymentModel: PaymentRequestModel) {
        val paymentResult = getPaymentResult(paymentModel)
        val err = paymentResult.error
        if (err != null) {
            log.error("get by balance payment error", err)
            router.navigateTo(Screens.PaymentError(paymentRequest.transferId))
            analytics.logEvent(Analytics.EVENT_MAKE_PAYMENT, Analytics.STATUS, Analytics.RESULT_FAIL)
        } else {
            router.newChainFromMain(Screens.PaymentSuccess(paymentRequest.transferId, paymentRequest.offerId))
            analytics.logEvent(Analytics.EVENT_MAKE_PAYMENT, Analytics.STATUS, Analytics.RESULT_SUCCESS)
        }
        viewState.blockInterface(false)
    }

    private suspend fun payByCard(paymentModel: PaymentRequestModel) {
        val paymentResult = getPaymentResult(paymentModel)
        val err = paymentResult.error
        if (err != null) {
            log.error("get by card payment error", err)
            viewState.setError(err)
        } else {
            url = paymentResult.model.url
            router.navigateTo(Screens.Payment(url, paymentRequest.percentage, selectedPayment))
        }
        viewState.blockInterface(false)
    }

    private suspend fun getPaymentResult(paymentModel: PaymentRequestModel): Result<Payment> =
        utils.asyncAwait { paymentInteractor.getPayment(paymentRequestMapper.fromView(paymentModel)) }

    fun onPaymentClicked() {
        if (accountManager.hasData) getPayment() else putAccount()
    }

    private fun putAccount() {
        val error = accountManager.isValidEmailAndPhoneFieldsForPay()
        if (error == null) {
            utils.launchSuspend { pushAccount() }
        } else {
            viewState.showFieldError(error.stringId)
            viewState.highLightError(error)
        }
    }

    private suspend fun pushAccount() {
        val result = fetchResultOnly { accountManager.putAccount() }
        result.error?.let { err ->
            if (err.isAccountExistError()) onAccountExists(err.checkExistedAccountField()) else viewState.setError(err)
        } ?: getPayment()
    }

    private fun onAccountExists(existedField: String) {
        loginScreenIsShowed = true
        val phoneOrEmail = when (existedField) {
            ApiException.EMAIL_EXISTED -> accountManager.tempProfile.email
            ApiException.PHONE_EXISTED -> accountManager.tempProfile.phone
            else -> throw UnsupportedOperationException()
        }
        phoneOrEmail?.let { redirectToLogin(it) }
    }

    private fun redirectToLogin(existedEmailOrPhone: String) {
        router.navigateTo(Screens.MainLogin(Screens.CLOSE_AFTER_LOGIN, existedEmailOrPhone))
    }

    private fun getBraintreeToken() {
        utils.launchSuspend {
            val tokenResult = utils.asyncAwait { paymentInteractor.getBrainTreeToken() }
            if (tokenResult.error != null) {
                tokenResult.error?.let { err ->
                    viewState.setError(err)
                    viewState.blockInterface(false)
                }
            } else {
                braintreeToken = tokenResult.isSuccess()?.token ?: ""
                createNewPayment()
            }
        }
    }

    private suspend fun createNewPayment() {
        val result = utils.asyncAwait { paymentInteractor.getPayment(paymentRequestMapper.fromView(paymentRequest)) }
        if (result.error != null) {
            result.error?.let { err ->
                log.error("create new payment error", err)
                viewState.setError(err)
                viewState.blockInterface(false)
            }
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
            viewState.startPaypal(dropInRequest, braintreeToken)
        } catch (e: InvalidArgumentException) {
            Sentry.capture(e)
            viewState.blockInterface(false)
        }
    }

    fun confirmPayment(nonce: String) {
        viewState.blockInterface(true, true)
        transfer?.let { transfer ->
            router.navigateTo(
                Screens.PayPalConnection(
                    paymentId,
                    nonce,
                    transfer.id,
                    offer?.let { if (it is Offer) it.id else null },
                    paymentRequest.percentage,
                    offer?.let { if (it is BookNowOffer) it.transportType.id.name.toLowerCase() else null }
                )
            )
        }
    }

    private fun navigateToPayment() {
        router.navigateTo(
            Screens.Payment(
                url,
                paymentRequest.percentage,
                selectedPayment
            )
        )
    }

    @Suppress("ComplexMethod")
    private fun logEventBeginCheckout() {
        val offerType = if (offer != null) Analytics.REGULAR else Analytics.NOW
        val requestType = when {
            transfer?.duration != null        -> Analytics.TRIP_HOURLY
            transfer?.dateReturnLocal != null -> Analytics.TRIP_ROUND
            else                              -> Analytics.TRIP_DESTINATION
        }
        var price = offer?.let { offer ->
            when (offer) {
                is Offer        -> offer.price.amount
                is BookNowOffer -> offer.amount
            }
        } ?: 0.0
        if (paymentRequest.percentage == OfferModel.PRICE_30) price *= PRICE_30

        val beginCheckout = analytics.BeginCheckout(
            paymentRequest.percentage,
            transfer?.promoCode,
            orderInteractor.duration,
            selectedPayment,
            offerType,
            requestType,
            sessionInteractor.currency.code,
            price
        )
        beginCheckout.sendAnalytics()
    }

    fun changePrice(price: Int) {
        paymentRequest.percentage = price
    }

    fun changePayment(payment: String) {
        paymentRequest.gatewayId = payment
    }

    fun onAgreementClicked() = router.navigateTo(Screens.LicenceAgree)
}
