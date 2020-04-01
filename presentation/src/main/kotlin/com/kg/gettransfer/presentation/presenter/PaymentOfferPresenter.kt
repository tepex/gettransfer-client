package com.kg.gettransfer.presentation.presenter

import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.exceptions.InvalidArgumentException
import com.braintreepayments.api.models.PayPalRequest

import com.google.android.gms.wallet.IsReadyToPayRequest
import com.google.android.gms.wallet.PaymentDataRequest
import com.google.android.gms.wallet.PaymentsClient

import com.google.gson.JsonParser

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.domain.model.Balance
import com.kg.gettransfer.domain.model.BookNowOffer
import com.kg.gettransfer.domain.model.BraintreePayment
import com.kg.gettransfer.domain.model.CheckoutcomPayment
import com.kg.gettransfer.domain.model.GooglePayPayment

import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.OfferItem
import com.kg.gettransfer.domain.model.PaymentProcess
import com.kg.gettransfer.domain.model.PaymentProcessRequest
import com.kg.gettransfer.domain.model.PaymentRequest
import com.kg.gettransfer.domain.model.PlatronPayment
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.Token
import com.kg.gettransfer.domain.model.getOfferId

import com.kg.gettransfer.extensions.getOffer
import com.kg.gettransfer.extensions.newChainFromMain

import com.kg.gettransfer.presentation.mapper.ProfileMapper

import com.kg.gettransfer.presentation.model.map

import com.kg.gettransfer.presentation.ui.helpers.GooglePayRequestsHelper
import com.kg.gettransfer.presentation.ui.SystemUtils

import com.kg.gettransfer.presentation.view.PaymentOfferView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.Analytics

import io.sentry.core.Sentry

import moxy.InjectViewState

import org.koin.core.inject

@InjectViewState
@Suppress("TooManyFunctions")
class PaymentOfferPresenter : BasePresenter<PaymentOfferView>() {

    private val profileMapper: ProfileMapper by inject()

    lateinit var googlePayPaymentsClient: PaymentsClient

    private var transfer: Transfer? = null
    private var offer: OfferItem? = null

    private var selectedPayment = PaymentRequest.Gateway.CARD
    private var paymentId = 0L

    private lateinit var paymentRequest: PaymentRequest

    private var isTransferDataChanged = false
    private var isOfferDataChanged = false
    private var isCarPhotoChanged = false

    private var currency: String? = null
    private var loginScreenIsShowed = false
    private var isCanPayAfterLogIn = true

    @Suppress("ComplexMethod")
    override fun attachView(view: PaymentOfferView) {
        super.attachView(view)
        utils.launchSuspend {
            viewState.selectPaymentGateway(selectedPayment)
            updateTransferData()
            checkCurrencyChanging()
            if (!::googlePayPaymentsClient.isInitialized) {
                initGooglePayClient()
            } else {
                viewState.blockInterface(false)
            }
            checkTransferAndOfferDataChanging()
            getTransferAndOffer()
            checkAccount()
            transfer?.let { setInfo(it) }
            checkLoginScreen()
            checkPaymentStatus()
        }
    }

    private fun checkLoginScreen() {
        if (loginScreenIsShowed) {
            loginScreenIsShowed = false
            if (accountManager.hasData && isCanPayAfterLogIn) {
                isCanPayAfterLogIn = true
                getPayment()
            }
        }
    }

    private fun checkAccount() {
        with(accountManager) {
            val profileModel = profileMapper.toView(remoteProfile)
            if (hasAccount && (profileModel.email.isNullOrEmpty() || profileModel.phone.isNullOrEmpty())) {
                viewState.setAuthUi(hasAccount, profileModel)
            } else {
                viewState.hideAuthUi()
                checkBalance()
            }
        }
    }

    private fun checkBalance() {
        accountManager.remoteAccount.partner?.availableMoney?.let { availableMoney ->
            getShowingBalance(availableMoney)?.let { balance ->
                isCanPayAfterLogIn = false
                viewState.setBalance(balance)
            } ?: viewState.hideBalance()
        } ?: viewState.hideBalance()
    }

    private fun getTransferAndOffer() {
        with(paymentInteractor) {
            transfer = selectedTransfer
            offer = selectedOffer
        }
    }

    private fun checkTransferAndOfferDataChanging() {
        with(paymentInteractor) {
            if (transfer != selectedTransfer) isTransferDataChanged = true
            if (offer != selectedOffer) isOfferDataChanged = true
            offer?.let { oldOfferData ->
                val newCarPhoto = (selectedOffer as? Offer)?.vehicle?.photos?.firstOrNull()
                val oldCarPhoto = (oldOfferData as? Offer)?.vehicle?.photos?.firstOrNull()
                isCarPhotoChanged = oldCarPhoto != newCarPhoto
            } ?: run { isCarPhotoChanged = true }
        }
    }

    private suspend fun checkCurrencyChanging() {
        currency?.let { selectedCurrency ->
            if (selectedCurrency != sessionInteractor.currency.code) {
                viewState.blockInterface(true, true)
                isCanPayAfterLogIn = false
                transfer?.id?.let { updatePaymentData(it) }
            }
        }
        currency = sessionInteractor.currency.code
    }

    private suspend fun updatePaymentData(transferId: Long) {
        when (val selectedOffer = offer) {
            is Offer        -> getOffer(transferId, selectedOffer.id)
            is BookNowOffer -> getBookNowOffer(selectedOffer.transportType.id)
            else            -> null
        }.let { updatedOffer ->
            paymentInteractor.selectedOffer = updatedOffer
        }
    }

    private suspend fun updateTransferData() {
        transfer?.id?.let { transferId ->
            viewState.blockInterface(true, true)
            fetchDataOnly {
                transferInteractor.getTransfer(transferId)
            }.also { updatedTransfer ->
                paymentInteractor.selectedTransfer = updatedTransfer
            }
        }
    }

    private suspend fun getOffer(transferId: Long, offerId: Long) =
        fetchDataOnly {
            offerInteractor.getOffers(transferId)
        }?.getOffer(offerId)

    private fun getBookNowOffer(transportTypeId: TransportType.ID) =
        paymentInteractor.selectedTransfer?.bookNowOffers?.getOffer(transportTypeId)

    private suspend fun initGooglePayClient() {
        val environment = GooglePayRequestsHelper.getEnvironment()
        val publicKey = configsManager.getConfigs().checkoutcomCredentials.publicKey
        if (environment != null && publicKey.isNotEmpty()) {
            viewState.blockInterface(true, true)
            viewState.initGooglePayPaymentsClient(environment)
            isReadyToPayWithGooglePayRequest()
        } else {
            viewState.blockInterface(false)
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
                    changePaymentType(PaymentRequest.Gateway.GOOGLEPAY)
                } else {
                    viewState.hideGooglePayButton()
                }
            } catch (e: ApiException) {
                viewState.hideGooglePayButton()
            } finally {
                viewState.blockInterface(false)
            }
        }
    }

    private fun getShowingBalance(availableMoney: Balance) =
        when (val offer = offer) {
            is BookNowOffer -> offer.amount
            is Offer        -> offer.price.amount
            else            -> null
        }?.let { offerPrice ->
            if (availableMoney.amount >= offerPrice) {
                availableMoney.default
            } else {
                null
            }
        }

    private suspend fun setInfo(transfer: Transfer) {
        if (isTransferDataChanged) {
            viewState.setToolbarTitle(transfer.map(configsManager.getConfigs().transportTypes.map { it.map() }))
            transfer.dateRefund?.let { dateRefund ->
                val commission = configsManager.getConfigs().paymentCommission
                viewState.setCommission(
                    if (commission % 1.0 == 0.0) commission.toInt().toString() else commission.toString(),
                    SystemUtils.formatDateTime(dateRefund)
                )
            }
        }
        if (isTransferDataChanged || isOfferDataChanged) setPaymentOptions(transfer)
        infoUpdated()
    }

    private suspend fun setPaymentOptions(transfer: Transfer) {
        offer?.let { offer ->
            val nameSignPresent = !transfer.nameSign.isNullOrEmpty()
            when (offer) {
                is BookNowOffer -> viewState.setBookNowOffer(offer.map(), nameSignPresent)
                is Offer        -> {
                    viewState.setOffer(offer.map(), nameSignPresent)
                    if (isCarPhotoChanged) viewState.setCarPhotoOffer(offer.vehicle.map())
                }
            }
        }
    }

    private fun infoUpdated() {
        isTransferDataChanged = false
        isOfferDataChanged = false
        isCarPhotoChanged = false
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

    @Suppress("ComplexMethod")
    private fun getPayment() {
        if (transfer == null || offer == null) {
            viewState.blockInterface(false)
            viewState.showOfferError()
            return
        }
        if (transfer?.pendingPaymentId != null) {
            viewState.showPaymentInProgressError()
            return
        }
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            getPaymentRequest(selectedPayment)?.let { paymentRequest ->
                when (paymentRequest.gateway) {
                    PaymentRequest.Gateway.PLATRON     -> payByPlatron(paymentRequest)
                    PaymentRequest.Gateway.CHECKOUTCOM -> payByCheckoutcom(paymentRequest)
                    PaymentRequest.Gateway.BRAINTREE   -> payByPaypal(paymentRequest)
                    PaymentRequest.Gateway.GOOGLEPAY   -> payByGooglePay(paymentRequest)
                    else                               -> payByBalance(paymentRequest)
                }
                logEventBeginCheckout()
            }
        }
    }

    private suspend fun getPaymentRequest(selectedPayment: PaymentRequest.Gateway): PaymentRequest? {
        val gateway = when (selectedPayment) {
            PaymentRequest.Gateway.CARD -> configsManager.getConfigs().defaultCardGateway
            else                        -> selectedPayment
        }
        return transfer?.id?.let { transferId ->
            offer?.let { offer -> PaymentRequest(transferId, offer, gateway).also { paymentRequest = it } }
        }
    }

    private fun setError(err: ApiException, logText: String? = null) {
        log.error(logText ?: "get by $selectedPayment payment error", err)
        viewState.setError(err)
        viewState.blockInterface(false)
    }

    private suspend fun payByPlatron(paymentRequest: PaymentRequest) {
        val paymentResult = getPlatronPaymentResult(paymentRequest)
        paymentResult.error?.let {
            setError(it)
        } ?: paymentResult.model.url.let { url ->
            router.navigateTo(Screens.PlatronPayment(url))
            viewState.blockInterface(false)
        }
    }

    private suspend fun getPlatronPaymentResult(paymentRequest: PaymentRequest): Result<PlatronPayment> =
        utils.asyncAwait { paymentInteractor.getPlatronPayment(paymentRequest) }

    private suspend fun payByCheckoutcom(paymentRequest: PaymentRequest) {
        val paymentResult = getCheckoutcomPaymentResult(paymentRequest)
        paymentResult.error?.let {
            setError(it)
        } ?: paymentResult.model.let { payment ->
            router.navigateTo(Screens.CheckoutcomPayment(payment.paymentId, payment.amountFormatted))
            viewState.blockInterface(false)
        }
    }

    private suspend fun getCheckoutcomPaymentResult(paymentRequest: PaymentRequest): Result<CheckoutcomPayment> =
        utils.asyncAwait { paymentInteractor.getCheckoutcomPayment(paymentRequest) }

    private suspend fun payByPaypal(paymentRequest: PaymentRequest) {
        val tokenResult = utils.asyncAwait { paymentInteractor.getBrainTreeToken() }
        tokenResult.error?.let {
            setError(it, "get braintree token error")
        } ?: createBraintreePayment(paymentRequest, tokenResult.model.token)
    }

    private suspend fun createBraintreePayment(paymentRequest: PaymentRequest, token: String) {
        val paymentResult = getBraintreePaymentResult(paymentRequest)
        paymentResult.error?.let {
            setError(it)
        } ?: paymentResult.model.params.let { params ->
            paymentId = params.paymentId
            setupPaypal(params.amount, params.currency, token)
        }
    }

    private suspend fun getBraintreePaymentResult(paymentRequest: PaymentRequest): Result<BraintreePayment> =
        utils.asyncAwait { paymentInteractor.getBraintreePayment(paymentRequest) }

    private fun setupPaypal(amount: Float, currency: String, token: String) {
        try {
            val dropInRequest = DropInRequest().clientToken(token)
            val paypal = PayPalRequest(amount.toString())
                    .currencyCode(currency).intent(PayPalRequest.INTENT_AUTHORIZE)
            dropInRequest.paypalRequest(paypal)
            viewState.startPaypal(dropInRequest, token)
        } catch (e: InvalidArgumentException) {
            Sentry.captureException(e)
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

    private suspend fun payByGooglePay(paymentRequest: PaymentRequest) {
        val paymentResult = getGooglePayPaymentResult(paymentRequest)
        paymentResult.error?.let {
            setError(it)
        } ?: paymentResult.model.params.let { params ->
            paymentId = params.paymentId
            val paymentDataRequest =
                GooglePayRequestsHelper.getPaymentDataRequest(
                    params.amount,
                    params.currency,
                    params.countryCode,
                    params.gateway,
                    params.gatewayMerchantId
                ).toString()
            val request = PaymentDataRequest.fromJson(paymentDataRequest)
            request?.let { viewState.startGooglePay(googlePayPaymentsClient.loadPaymentData(it)) }
            viewState.blockInterface(false)
        }
    }

    private suspend fun getGooglePayPaymentResult(paymentRequest: PaymentRequest): Result<GooglePayPayment> =
        utils.asyncAwait { paymentInteractor.getGooglePayPayment(paymentRequest) }

    fun processGooglePayPayment(token: String) {
        utils.launchSuspend {
            viewState.blockInterface(true, true)
            val jsonToken = JsonParser().parse(token).asJsonObject
            val paymentProcess = PaymentProcessRequest(paymentId, Token.JsonToken(jsonToken))
            val processResult = getProcessGooglePayPaymentResult(paymentProcess)
            processResult.error?.let { paymentError(it) } ?: processResult.model.payment.isSuccess.let {
                if (it) paymentSuccess() else paymentError()
            }
            viewState.blockInterface(false)
        }
    }

    private suspend fun getProcessGooglePayPaymentResult(
        paymentProcess: PaymentProcessRequest
    ): Result<PaymentProcess> = utils.asyncAwait {
        paymentInteractor.processPayment(paymentProcess)
    }

    private suspend fun payByBalance(paymentRequest: PaymentRequest) {
        getGroundPaymentResult(paymentRequest).error?.let { paymentError(it) } ?: paymentSuccess()
        viewState.blockInterface(false)
    }

    private suspend fun getGroundPaymentResult(paymentRequest: PaymentRequest): Result<Unit> =
        utils.asyncAwait { paymentInteractor.getGroundPayment(paymentRequest) }

    private suspend fun paymentError(err: ApiException? = null) {
        log.error("get by $selectedPayment payment error", err)
        viewState.showPaymentError(paymentRequest.transferId, paymentRequest.gateway.toString())
        analytics.PaymentStatus(selectedPayment).sendAnalytics(Analytics.EVENT_PAYMENT_FAILED)
    }

    private suspend fun paymentSuccess() {
        analytics.PaymentStatus(selectedPayment).sendAnalytics(Analytics.EVENT_PAYMENT_DONE)
        router.newChainFromMain(
            Screens.PaymentSuccess(paymentRequest.transferId, paymentRequest.offerItem.getOfferId())
        )
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
        val result = fetchResultOnly { accountManager.putAccount(updateConfigs = true) }
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

    private fun checkPaymentStatus() {
        with(paymentInteractor) {
            if (isFailedPayment) {
                isFailedPayment = false
                selectedTransfer?.let { viewState.showPaymentError(it.id, null) }
            }
        }
    }

    fun changePaymentType(gateway: PaymentRequest.Gateway) {
        selectedPayment = gateway
        viewState.selectPaymentGateway(gateway)
    }
}
