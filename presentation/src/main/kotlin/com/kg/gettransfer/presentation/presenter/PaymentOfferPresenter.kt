package com.kg.gettransfer.presentation.presenter

import moxy.InjectViewState

import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.exceptions.InvalidArgumentException
import com.braintreepayments.api.models.PayPalRequest

import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.OfferItem
import com.kg.gettransfer.domain.model.BookNowOffer
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.PlatronPayment
import com.kg.gettransfer.domain.model.BraintreePayment
import com.kg.gettransfer.domain.model.CheckoutcomPayment
import com.kg.gettransfer.domain.model.GooglePayPayment
import com.kg.gettransfer.domain.model.PaymentStatus
import com.kg.gettransfer.domain.model.PaymentProcess
import com.kg.gettransfer.domain.model.Result

import com.kg.gettransfer.extensions.newChainFromMain

import com.kg.gettransfer.presentation.mapper.PaymentProcessRequestMapper
import com.kg.gettransfer.presentation.mapper.PaymentRequestMapper
import com.kg.gettransfer.presentation.mapper.ProfileMapper

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

    lateinit var googlePayPaymentsClient: PaymentsClient

    private var transfer: Transfer? = null
    private var offer: OfferItem? = null

    private var selectedPayment = PaymentRequestModel.CARD
    private var paymentId = 0L

    private lateinit var paymentRequest: PaymentRequestModel

    private var loginScreenIsShowed = false

    @Suppress("ComplexMethod")
    override fun attachView(view: PaymentOfferView) {
        super.attachView(view)
        utils.launchSuspend {
            viewState.blockInterface(false)

            //TODO: return when fixed configs request
            /*if (configsManager.getConfigs().checkoutcomCredentials.publicKey.isNotEmpty()) {
                viewState.initGooglePayPaymentsClient(GooglePayRequestsHelper.getEnvironment())
                isReadyToPayWithGooglePayRequest()
            }*/

            with(paymentInteractor) {
                transfer = selectedTransfer
                offer = selectedOffer
            }
            with(accountManager) {
                val balance = remoteAccount.partner?.availableMoney?.default

                viewState.setAuthUiVisible(hasAccount, profileMapper.toView(remoteProfile), balance)
            }

            transfer?.let { transfer ->
                setInfo(transfer)
                setPaymentOptions(transfer)
            }
            viewState.selectPaymentType(selectedPayment)
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
        offer?.let { offer ->
            val nameSignPresent = !transfer.nameSign.isNullOrEmpty()
            when (offer) {
                is BookNowOffer -> viewState.setBookNowOffer(offer.map(), nameSignPresent)
                is Offer        -> viewState.setOffer(offerMapper.toView(offer), nameSignPresent)
            }
        }
    }

    override fun onDestroy() {
        accountManager.initTempUser()
        super.onDestroy()
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
            getPaymentRequest(selectedPayment)?.let {
                when (it.gatewayId) {
                    PaymentRequestModel.PLATRON     -> payByPlatron(it)
                    PaymentRequestModel.CHECKOUTCOM -> payByCheckoutcom(it)
                    PaymentRequestModel.PAYPAL      -> payByPaypal(it)
                    PaymentRequestModel.GOOGLE_PAY  -> payByGooglePay(it)
                    else                            -> payByBalance(it)
                }
                logEventBeginCheckout()
            }
        }
    }

    private suspend fun getPaymentRequest(selectedPayment: String): PaymentRequestModel? {
        val gatewayId = when(selectedPayment) {
            PaymentRequestModel.CARD -> configsManager.getConfigs().defaultCardGateway
            else                     -> selectedPayment
        }
        return transfer?.id?.let { transferId ->
            offer?.let { offer ->
                when (offer) {
                    is Offer -> PaymentRequestModel(transferId, offer.id, null, gatewayId)
                    is BookNowOffer -> PaymentRequestModel(transferId, null, offer.transportType.id.toString(), gatewayId)
                }.also { paymentRequest = it }
            }
        }
    }

    private fun setError(err: ApiException, logText: String? = null) {
        log.error(logText ?: "get by $selectedPayment payment error", err)
        viewState.setError(err)
        viewState.blockInterface(false)
    }

    private suspend fun payByPlatron(paymentModel: PaymentRequestModel) {
        val paymentResult = getPlatronPaymentResult(paymentModel)
        paymentResult.error?.let {
            setError(it)
        } ?: paymentResult.model.let {
            router.navigateTo(Screens.Payment(it.url))
            viewState.blockInterface(false)
        }
    }

    private suspend fun getPlatronPaymentResult(paymentModel: PaymentRequestModel): Result<PlatronPayment> =
        utils.asyncAwait { paymentInteractor.getPlatronPayment(paymentRequestMapper.fromView(paymentModel)) }

    private suspend fun payByCheckoutcom(paymentModel: PaymentRequestModel) {
        val paymentResult = getCheckoutcomPaymentResult(paymentModel)
        paymentResult.error?.let {
            setError(it)
        } ?: paymentResult.model.paymentId.let {
            router.navigateTo(Screens.CheckoutcomPayment(it))
            viewState.blockInterface(false)
        }
    }

    private suspend fun getCheckoutcomPaymentResult(paymentModel: PaymentRequestModel): Result<CheckoutcomPayment> =
        utils.asyncAwait { paymentInteractor.getCheckoutcomPayment(paymentRequestMapper.fromView(paymentModel)) }

    private suspend fun payByPaypal(paymentModel: PaymentRequestModel) {
        val tokenResult = utils.asyncAwait { paymentInteractor.getBrainTreeToken() }
        tokenResult.error?.let {
            setError(it, "get braintree token error")
        } ?: createBraintreePayment(paymentModel, tokenResult.model.token)
    }

    private suspend fun createBraintreePayment(paymentModel: PaymentRequestModel, token: String) {
        val paymentResult = getBraintreePaymentResult(paymentModel)
        paymentResult.error?.let {
            setError(it)
        } ?: paymentResult.model.params.let {
            paymentId = it.paymentId
            setupPaypal(it.amount, it.currency, token)
        }
    }

    private suspend fun getBraintreePaymentResult(paymentModel: PaymentRequestModel): Result<BraintreePayment> =
        utils.asyncAwait { paymentInteractor.getBraintreePayment(paymentRequestMapper.fromView(paymentModel)) }

    private fun setupPaypal(amount: Float, currency: String, token: String) {
        try {
            val dropInRequest = DropInRequest().clientToken(token)
            val paypal = PayPalRequest(amount.toString())
                    .currencyCode(currency).intent(PayPalRequest.INTENT_AUTHORIZE)
            dropInRequest.paypalRequest(paypal)
            viewState.startPaypal(dropInRequest, token)
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
                    offer?.let { if (it is Offer) it.id else null }
                )
            )
        }
    }

    private suspend fun payByGooglePay(paymentModel: PaymentRequestModel) {
        val paymentResult = getGooglePayPaymentResult(paymentModel)
        paymentResult.error?.let {
            setError(it)
        } ?: paymentResult.model.params.let { params ->
            paymentId = params.paymentId
            val paymentDataRequest = GooglePayRequestsHelper.getPaymentDataRequest(params.amount, params.currency).toString()
            val request = PaymentDataRequest.fromJson(paymentDataRequest)
            request?.let { viewState.startGooglePay(googlePayPaymentsClient.loadPaymentData(it)) }
            viewState.blockInterface(false)
        }
    }

    private suspend fun getGooglePayPaymentResult(paymentModel: PaymentRequestModel): Result<GooglePayPayment> =
        utils.asyncAwait { paymentInteractor.getGooglePayPayment(paymentRequestMapper.fromView(paymentModel)) }

    fun processGooglePayPayment(token: String) {
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val paymentProcess = PaymentProcessRequestModel(paymentId, token, false)
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
        log.error("get by $selectedPayment payment error", err)
        analytics.PaymentStatus(selectedPayment).sendAnalytics(Analytics.EVENT_PAYMENT_FAILED)
        router.navigateTo(Screens.PaymentError(paymentRequest.transferId, selectedPayment))
    }

    private suspend fun paymentSuccess() {
        analytics.PaymentStatus(selectedPayment).sendAnalytics(Analytics.EVENT_PAYMENT_DONE)
        router.newChainFromMain(Screens.PaymentSuccess(paymentRequest.transferId, paymentRequest.offerId))
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
        val price = offer?.let { offer ->
            when (offer) {
                is Offer        -> offer.price.amount
                is BookNowOffer -> offer.amount
            }
        } ?: 0.0

        val beginCheckout = analytics.BeginCheckout(
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

    fun onAgreementClicked() = router.navigateTo(Screens.LicenceAgree)

    fun changePaymentType(type: String) {
        selectedPayment = type
        viewState.selectPaymentType(type)
    }
}
