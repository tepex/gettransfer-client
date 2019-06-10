package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState
import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.exceptions.InvalidArgumentException
import com.braintreepayments.api.models.PayPalRequest

import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.domain.model.BookNowOffer
import com.kg.gettransfer.domain.model.Offer
import com.kg.gettransfer.domain.model.OfferItem
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.presentation.mapper.PaymentRequestMapper
import com.kg.gettransfer.presentation.mapper.ProfileMapper

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentRequestModel
import com.kg.gettransfer.presentation.ui.SystemUtils

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
    private val profileMapper: ProfileMapper by inject()

    private var transfer: Transfer? = null
    private var offer: OfferItem? = null
    private var isBookNowOffer: Boolean = false

    //internal lateinit var params: PaymentOfferView.Params
    private var url: String? = null
    internal var braintreeToken = ""
    private var paymentId = 0L
    internal var selectedPayment = PaymentRequestModel.PLATRON

    private var loginScreenIsShowed = false

    private lateinit var paymentRequest: PaymentRequestModel

    @CallSuper
    override fun attachView(view: PaymentOfferView) {
        super.attachView(view)
        viewState.blockInterface(false)
        with(paymentInteractor) {
            transfer = selectedTransfer
            offer = selectedOffer
            offer?.let { isBookNowOffer = offer is BookNowOffer }
        }
        getPaymentRequest()
        viewState.setAuthUiVisible(accountManager.hasAccount, profileMapper.toView(accountManager.remoteProfile))
        transfer?.let { transfer ->
            viewState.setToolbarTitle(transferMapper.toView(transfer))
            transfer.dateRefund?.let { dateRefund ->
                val commission = sessionInteractor.paymentCommission
                viewState.setCommission(
                        if (commission % 1.0 == 0.0) commission.toInt().toString() else commission.toString(),
                        SystemUtils.formatDateTime(dateRefund)
                )
            }
        }
        enablePaymentBtn()
        transfer?.paymentPercentages?.let { percentages ->
            offer?.let { offer ->
                if (isBookNowOffer) viewState.setBookNowOffer(bookNowOfferMapper.toView(offer as BookNowOffer))
                else viewState.setOffer(offerMapper.toView(offer as Offer), percentages)
            }
        }
        if (loginScreenIsShowed) {
            loginScreenIsShowed = false
            if (accountManager.remoteProfile.hasData()) getPayment()
        }
    }

    private fun getPaymentRequest() {
        transfer?.id?.let { transferId ->
            offer?.let { offer ->
                paymentRequest = when (offer) {
                    is Offer -> PaymentRequestModel(transferId, offer.id, null)
                    is BookNowOffer -> PaymentRequestModel(transferId, null, offer.transportType.id.name)
                }
            }
        }
    }

    fun setEmail(email: String) {
        accountManager.tempProfile.email = email
        enablePaymentBtn()
    }

    fun setPhone(phone: String) {
        accountManager.tempProfile.phone = phone
        enablePaymentBtn()
    }

    fun enablePaymentBtn() {
        with(accountManager.tempProfile) {
            viewState.enablePayment(!email.isNullOrEmpty() && !phone.isNullOrEmpty())
        }
    }

    private fun getPayment() {
        if (transfer == null || offer == null) {
            viewState.blockInterface(false)
            viewState.showOfferError()
            return
        }
        utils.launchSuspend {
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
        }
    }

    fun onPaymentClicked() {
        if (accountManager.remoteProfile.hasData())
            getPayment()
        else
            putAccount()
    }

    private fun putAccount() {
        val error = accountManager.isValidEmailAndPhoneFieldsForPay()
        if (error == null) {
            utils.launchSuspend { pushAccount() }
        } else {
            viewState.showFieldError(error.stringId)
        }
    }

    private suspend fun pushAccount() =
        fetchResultOnly { accountManager.putAccount(true, updateTempUser = true) }
            .run {
                when {
                    error?.isAccountExistError() ?: false  -> onAccountExists()
                    error != null                          -> viewState.setError(error!!)
                    else                                   -> getPayment()
                }
            }

    private fun onAccountExists() {
        loginScreenIsShowed = true
        redirectToLogin()
    }

    private fun redirectToLogin() {
        with(accountManager) {
            router.navigateTo(Screens.MainLogin(Screens.CLOSE_AFTER_LOGIN, when{
                remoteProfile.phone.isNullOrEmpty() -> tempProfile.phone
                remoteProfile.email.isNullOrEmpty() -> tempProfile.email
                else -> tempProfile.phone
            }))
        }
    }

    /*private fun isValid(input: String, isPhone: Boolean) =
        LoginHelper
                .validateInput(input, isPhone)
                .let {
                    if (it != CREDENTIALS_VALID)
                        viewState.showBadCredentialsInfo(it)
                    it == CREDENTIALS_VALID
                }*/


    private fun getBraintreeToken() {
        utils.launchSuspend {
            val tokenResult = utils.asyncAwait { paymentInteractor.getBrainTreeToken() }
            if (tokenResult.error != null) {
                viewState.setError(tokenResult.error!!)
                viewState.blockInterface(false)
            } else {
                braintreeToken = tokenResult.isSuccess()?.token ?: ""
                createNewPayment()
            }
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
            viewState.startPaypal(dropInRequest, braintreeToken)
        } catch (e: InvalidArgumentException) {
            Sentry.capture(e)
            viewState.blockInterface(false)
        }

    }

    fun confirmPayment(nonce: String) {
        viewState.blockInterface(true, true)
        transfer?.let {
            router.navigateTo(
                Screens.PayPalConnection(
                    paymentId,
                    nonce,
                    it.id,
                    if (!isBookNowOffer) (offer as Offer).id else null,
                    paymentRequest.percentage,
                    if (isBookNowOffer) (offer as BookNowOffer).transportType.id.name else null)
            )
        }
    }


    private fun navigateToPayment() {
        router.navigateTo(Screens.Payment(
            url,
            paymentRequest.percentage,
            selectedPayment)
        )
    }

    private fun logEventBeginCheckout() {
        val offerType = if (offer != null ) Analytics.REGULAR else Analytics.NOW
        val requestType = when {
            transfer?.duration != null -> Analytics.TRIP_HOURLY
            transfer?.dateReturnLocal != null -> Analytics.TRIP_ROUND
            else -> Analytics.TRIP_DESTINATION
        }
        var price = 0.0
        offer?.let {
            if (!isBookNowOffer) (it as Offer).price.amount
            else (it as BookNowOffer).amount
        }
        if (paymentRequest.percentage == OfferModel.PRICE_30) price *= PRICE_30

        val beginCheckout = analytics.BeginCheckout(
            paymentRequest.percentage,
            transfer?.promoCode,
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
