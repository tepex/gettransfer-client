package com.kg.gettransfer.presentation.presenter

import moxy.InjectViewState

import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.exceptions.InvalidArgumentException
import com.braintreepayments.api.models.PayPalRequest

import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.model.*

import com.kg.gettransfer.extensions.newChainFromMain

import com.kg.gettransfer.presentation.mapper.PaymentProcessRequestMapper
import com.kg.gettransfer.presentation.mapper.PaymentRequestMapper
import com.kg.gettransfer.presentation.mapper.ProfileMapper

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentProcessRequestModel
import com.kg.gettransfer.presentation.model.PaymentRequestModel
import com.kg.gettransfer.presentation.model.map

import com.kg.gettransfer.presentation.ui.helpers.GooglePayRequestsHelper

import com.kg.gettransfer.presentation.ui.SystemUtils
import com.kg.gettransfer.presentation.view.PaymentOfferView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import io.sentry.Sentry

import org.koin.core.inject

@InjectViewState
@Suppress("TooManyFunctions")
class PaymentOfferPresenter : BasePresenter<PaymentOfferView>() {

    private val paymentRequestMapper: PaymentRequestMapper by inject()
    private val paymentProcessRequestMapper: PaymentProcessRequestMapper by inject()
    private val profileMapper: ProfileMapper by inject()

    private var transfer: Transfer? = null
    private var offer: OfferItem? = null

    private var url: String? = null
    internal var braintreeToken = ""
    private var paymentId = 0L
    internal var selectedPayment = PaymentRequestModel.CHECKOUT

    private var loginScreenIsShowed = false

    private lateinit var paymentRequest: PaymentRequestModel

    lateinit var googlePayPaymentsClient: PaymentsClient
    private var googlePayPaymentId = 0L

    @Suppress("ComplexMethod")
    override fun attachView(view: PaymentOfferView) {
        super.attachView(view)
        utils.launchSuspend {
            viewState.blockInterface(false)

            //TODO: return when fixed configs request
            /*if (configsManager.getConfigs().checkoutCredentials.publicKey.isNotEmpty()) {
                viewState.initGooglePayPaymentsClient(GooglePayRequestsHelper.getEnvironment())
                isReadyToPayWithGooglePayRequest()
            }*/

            with(paymentInteractor) {
                transfer = selectedTransfer
                offer = selectedOffer
            }
            getPaymentRequest()
            with(accountManager) {
                val balance = remoteAccount.partner?.availableMoney?.default

                viewState.setAuthUiVisible(hasAccount, profileMapper.toView(remoteProfile), balance)
            }

            transfer?.let { transfer ->
                setInfo(transfer)
                setPaymentOptions(transfer)
            }
        }

        if (loginScreenIsShowed) {
            loginScreenIsShowed = false
            if (accountManager.hasData) {
                getPayment()
            }
        }
    }

    private suspend fun isReadyToPayWithGooglePayRequest() {
        val isReadyToPayRequest = GooglePayRequestsHelper.getIsReadyToPayRequest().toString()
        val request = IsReadyToPayRequest.fromJson(isReadyToPayRequest)
        val task = googlePayPaymentsClient.isReadyToPay(request)
        task.addOnCompleteListener {
            try {
                val result = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                if (result == true) {
                    viewState.showGooglePayButton()
                } else {
                    viewState.hideGooglePayButton()
                }
            } catch (e: ApiException) {
                viewState.hideGooglePayButton()
            }
        }
    }

    private suspend fun setInfo(transfer: Transfer) {
        viewState.setToolbarTitle(transfer.map(configsManager.getConfigs().transportTypes.map { it.map() }))
        transfer.dateRefund?.let { dateRefund ->
            val commission = configsManager.getConfigs().paymentCommission
            viewState.setCommission(
                if (commission % 1.0 == 0.0) commission.toInt().toString() else commission.toString(),
                SystemUtils.formatDateTime(dateRefund)
            )
        }
    }

    private fun setPaymentOptions(transfer: Transfer) {
        transfer.paymentPercentages?.let { percentages ->
            offer?.let { offer ->
                val nameSignPresent = !transfer.nameSign.isNullOrEmpty()
                when (offer) {
                    is BookNowOffer -> viewState.setBookNowOffer(offer.map(), nameSignPresent)
                    is Offer        -> viewState.setOffer(offerMapper.toView(offer), percentages, nameSignPresent)
                }
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
                    is BookNowOffer -> PaymentRequestModel(transferId, null, offer.transportType.id.toString())
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
            when (selectedPayment) {
                PaymentRequestModel.PLATRON    -> payByPlatron(paymentRequest)
                PaymentRequestModel.CHECKOUT   -> payByCheckoutcom(paymentRequest)
                PaymentRequestModel.PAYPAL     -> payByPaypal(paymentRequest)
                PaymentRequestModel.GOOGLE_PAY -> payByGooglePay(paymentRequest)
                else                           -> payByBalance(paymentRequest)
            }
            logEventBeginCheckout()
        }
    }

    private suspend fun payByPlatron(paymentModel: PaymentRequestModel) {
        val paymentResult = getPlatronPaymentResult(paymentModel)
        paymentResult.error?.let {
            log.error("get by platron payment error", it)
            viewState.setError(it)
        } ?: paymentResult.model.let {
            url = it.url
            router.navigateTo(Screens.Payment(url, paymentRequest.percentage, selectedPayment))
        }
        viewState.blockInterface(false)
    }

    private suspend fun getPlatronPaymentResult(paymentModel: PaymentRequestModel): Result<PlatronPayment> =
        utils.asyncAwait { paymentInteractor.getPlatronPayment(paymentRequestMapper.fromView(paymentModel)) }

    private suspend fun payByCheckoutcom(paymentModel: PaymentRequestModel) {
        val paymentResult = getCheckoutcomPaymentResult(paymentModel)
        paymentResult.error?.let {
            log.error("get by checkoutcom payment error", it)
            viewState.setError(it)
        } ?: paymentResult.model.paymentId.let {
            router.navigateTo(Screens.CheckoutPayment(it))
        }
        viewState.blockInterface(false)
    }

    private suspend fun getCheckoutcomPaymentResult(paymentModel: PaymentRequestModel): Result<CheckoutcomPayment> =
        utils.asyncAwait { paymentInteractor.getCheckoutcomPayment(paymentRequestMapper.fromView(paymentModel)) }

    private fun payByPaypal(paymentModel: PaymentRequestModel) {
        utils.launchSuspend {
            val tokenResult = utils.asyncAwait { paymentInteractor.getBrainTreeToken() }
            if (tokenResult.error != null) {
                tokenResult.error?.let { err ->
                    viewState.setError(err)
                    viewState.blockInterface(false)
                }
            } else {
                braintreeToken = tokenResult.isSuccess()?.token ?: ""
                createBraintreePayment(paymentModel)
            }
        }
    }

    private suspend fun createBraintreePayment(paymentModel: PaymentRequestModel) {
        val paymentResult = getBraintreePaymentResult(paymentModel)
        paymentResult.error?.let {
            log.error("create new payment error", it)
            viewState.setError(it)
            viewState.blockInterface(false)
        } ?: paymentResult.model.params.let {
            paymentId = it.paymentId
            setupPaypal(it.amount, it.currency)
        }
    }

    private suspend fun getBraintreePaymentResult(paymentModel: PaymentRequestModel): Result<BraintreePayment> =
        utils.asyncAwait { paymentInteractor.getBraintreePayment(paymentRequestMapper.fromView(paymentModel)) }

    private fun setupPaypal(amount: Float, currency: String) {
        try {
            val dropInRequest = DropInRequest().clientToken(braintreeToken)
            val paypal = PayPalRequest(amount.toString())
                    .currencyCode(currency).intent(PayPalRequest.INTENT_AUTHORIZE)
            dropInRequest.paypalRequest(paypal)
            viewState.startPaypal(dropInRequest, braintreeToken)
        } catch (e: InvalidArgumentException) {
            Sentry.capture(e)
            viewState.blockInterface(false)
        }
    }

    fun confirmPaypalPayment(nonce: String) {
        viewState.blockInterface(true, true)
        transfer?.let { transfer ->
            router.navigateTo(
                Screens.PayPalConnection(
                    paymentId,
                    nonce,
                    transfer.id,
                    offer?.let { if (it is Offer) it.id else null },
                    paymentRequest.percentage,
                    offer?.let { if (it is BookNowOffer) it.transportType.id.toString() else null }
                )
            )
        }
    }

    private fun payByGooglePay(paymentModel: PaymentRequestModel) {
        utils.launchSuspend {
            val paymentResult = getGooglePayPaymentResult(paymentModel)
            paymentResult.error?.let {
                log.error("get by google pay payment error", it)
                viewState.setError(it)
            } ?: paymentResult.model.params.let { params ->
                googlePayPaymentId = params.paymentId
                val paymentDataRequest = GooglePayRequestsHelper.getPaymentDataRequest(params.amount, params.currency).toString()
                val request = PaymentDataRequest.fromJson(paymentDataRequest)
                request?.let { viewState.startGooglePay(googlePayPaymentsClient.loadPaymentData(it)) }
            }
            viewState.blockInterface(false)
        }
    }

    private suspend fun getGooglePayPaymentResult(paymentModel: PaymentRequestModel): Result<GooglePayPayment> =
        utils.asyncAwait { paymentInteractor.getGooglePayPayment(paymentRequestMapper.fromView(paymentModel)) }

    fun processGooglePayPayment(token: String) {
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val paymentProcess = PaymentProcessRequestModel(googlePayPaymentId, token, false)
            val processResult = getProcessGooglePayPaymentResult(paymentProcess)
            processResult.error?.let {
                paymentError(it)
            } ?: processResult.model.payment.status.let {
                if (it == PaymentStatus.Status.SUCCESS) paymentSuccess()
                else paymentError()
            }
            viewState.blockInterface(false)
        }
    }

    private suspend fun getProcessGooglePayPaymentResult(paymentProcessModel: PaymentProcessRequestModel): Result<PaymentProcess> =
        utils.asyncAwait { paymentInteractor.processPayment(paymentProcessRequestMapper.fromView(paymentProcessModel)) }

    private suspend fun payByBalance(paymentModel: PaymentRequestModel) {
        val paymentResult = getGroundPaymentResult(paymentModel)
        paymentResult.error?.let {
            paymentError(it)
        } ?: paymentSuccess()
        viewState.blockInterface(false)
    }

    private suspend fun getGroundPaymentResult(paymentModel: PaymentRequestModel): Result<Unit> =
        utils.asyncAwait { paymentInteractor.getGroundPayment(paymentRequestMapper.fromView(paymentModel)) }

    private suspend fun paymentError(err: ApiException? = null) {
        err?.let { log.error("get by ${paymentRequest.gatewayId} payment error", it) }
        router.navigateTo(Screens.PaymentError(paymentRequest.transferId, paymentRequest.gatewayId))
        analytics.PaymentStatus(selectedPayment).sendAnalytics(Analytics.EVENT_PAYMENT_FAILED)
    }

    private suspend fun paymentSuccess() {
        router.newChainFromMain(Screens.PaymentSuccess(paymentRequest.transferId, paymentRequest.offerId))
        analytics.PaymentStatus(selectedPayment).sendAnalytics(Analytics.EVENT_PAYMENT_DONE)
        transfer?.let {
            val offerPaid = utils.asyncAwait { transferInteractor.isOfferPaid(it.id) }
            if (offerPaid.model.first) {
                transfer = offerPaid.model.second
                paymentInteractor.selectedTransfer = transfer
                analytics.EcommercePurchase().sendAnalytics()
            }
        }
    }

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
            else                       -> throw UnsupportedOperationException()
        }
        phoneOrEmail?.let { router.navigateTo(Screens.MainLogin(Screens.CLOSE_AFTER_LOGIN, it)) }
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

    companion object {
        const val PRICE_30 = 0.3
    }
}
