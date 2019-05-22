package com.kg.gettransfer.presentation.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler

import android.support.v7.widget.Toolbar

import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.braintreepayments.api.BraintreeFragment
import com.braintreepayments.api.PayPal
import com.braintreepayments.api.dropin.DropInActivity
import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.dropin.DropInResult
import com.braintreepayments.api.exceptions.ErrorWithResponse
import com.braintreepayments.api.interfaces.BraintreeCancelListener
import com.braintreepayments.api.interfaces.BraintreeErrorListener
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener
import com.braintreepayments.api.models.PaymentMethodNonce

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.model.Currency
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.extensions.getString
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.delegate.OfferItemBindDelegate
import com.kg.gettransfer.presentation.mapper.TransportTypeMapper
import com.kg.gettransfer.presentation.model.*

import com.kg.gettransfer.presentation.presenter.PaymentOfferPresenter
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper

import com.kg.gettransfer.presentation.view.PaymentOfferView
import io.sentry.Sentry
import io.sentry.event.BreadcrumbBuilder

import kotlinx.android.synthetic.main.activity_payment_offer.*
import kotlinx.android.synthetic.main.layout_payments.*
import kotlinx.android.synthetic.main.layout_prices.*
import kotlinx.android.synthetic.main.offer_tiny_payment.*
import kotlinx.android.synthetic.main.toolbar_nav_payment.view.*
import kotlinx.android.synthetic.main.view_currency_converting_info.view.*

import kotlinx.serialization.json.JSON
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import timber.log.Timber
import java.lang.Exception

class PaymentOfferActivity : BaseActivity(), PaymentOfferView, PaymentMethodNonceCreatedListener,
        BraintreeErrorListener, BraintreeCancelListener {

    companion object {
        const val PAYMENT_REQUEST_CODE = 100
        const val PAYPAL_PACKAGE_NAME = "com.paypal.android.p2pmobile"

        const val INVALID_EMAIL     = 1
        const val INVALID_PHONE     = 2
    }

    @InjectPresenter
    internal lateinit var presenter: PaymentOfferPresenter

    override fun getPresenter(): PaymentOfferPresenter = presenter

    @ProvidePresenter
    fun createPaymentSettingsPresenter() = PaymentOfferPresenter()

    private var selectedPercentage = PaymentRequestModel.FULL_PRICE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.params = JSON.parse(PaymentOfferView.Params.serializer(), intent.getStringExtra(PaymentOfferView.EXTRA_PARAMS))

        setContentView(R.layout.activity_payment_offer)
        initListeners()
        initToolbar()

    }

    private fun initToolbar() {
        with(toolbar) {
            setSupportActionBar(this as Toolbar)
            tvTitle.text = getString(R.string.LNG_PAYMENT_SETTINGS)
            btnBack.setOnClickListener { presenter.onBackCommandClick() }
            tvSubTitle.isSelected = true
        }
    }

    private fun initListeners() {
        tvPaymentAgreement.setOnClickListener { presenter.onAgreementClicked() }
        btnGetPayment.setOnClickListener { presenter.onPaymentClicked() }
        rbCard.setOnClickListener { changePayment(it, PaymentRequestModel.PLATRON) }
        rbPaypal.setOnClickListener { changePayment(it, PaymentRequestModel.PAYPAL) }
        addKeyBoardDismissListener {
            Handler().postDelayed({
                if (it) sv_root.fling(2000)         //need to show "Payment" button
            }, 150)

        }
    }

    private fun initTextChangeListeners() {
        presenter.authEmail = ""
        presenter.authPhone = ""
        et_auth_email.onTextChanged {
            presenter.authEmail = it
            enablePayment()
        }
        et_auth_phone.onTextChanged {
            presenter.authPhone = it
            enablePayment()
        }
    }

    private fun enablePayment() {
        btnGetPayment.isEnabled = !et_auth_email.text.isNullOrEmpty()
                && !et_auth_phone.text.isNullOrEmpty()
    }

    private fun changePayment(view: View, payment: String) {
        when (view.id) {
            R.id.rbCard -> {
                rbCard.isChecked = true
                rbPaypal.isChecked = false
            }
            R.id.rbPaypal -> {
                rbPaypal.isChecked = true
                rbCard.isChecked = false
            }
        }
        presenter.selectedPayment = payment
        presenter.changePayment(payment)
    }

    override fun setOffer(offer: OfferModel, paymentPercentages: List<Int>) {
        if (paymentPercentages.isNotEmpty()) {
            if (paymentPercentages.size == 1) hidePaymentPercentage()
            else {
                paymentPercentages.forEach { percentage ->
                    when (percentage) {
                        OfferModel.FULL_PRICE -> {
                            rbPay100.text = getString(R.string.LNG_PAYMENT_TERM_PAY, OfferModel.FULL_PRICE.toString())
                            tvFullPrice.text = offer.price.base.preferred.plus(R.string.LNG_PAYMENT_TERM_NOW)
                            rbPay100.setOnClickListener { changePaymentSettings(it) }
                        }
                        OfferModel.PRICE_30 -> {
                            rbPay30.text = getString(R.string.LNG_PAYMENT_TERM_PAY, OfferModel.PRICE_30.toString())
                            tvThirdOfPrice.text = offer.price.percentage30.plus(R.string.LNG_PAYMENT_TERM_NOW)
                            tvLaterPrice.text = getString(R.string.LNG_PAYMENT_TERM_LATER, offer.price.percentage70)
                            rbPay30.setOnClickListener { changePaymentSettings(it) }
                        }
                    }
                }
            }
            selectPaymentPercentage(selectedPercentage)
        }
        setCarInfo(offer)
        setPriceInfo(offer.price.base.def, offer.price.base.preferred)
    }

    private fun setPriceInfo(default: String?, preferredPrice: String?) {
        tvPriceInfo.text  = if (preferredPrice != null)
            getString(R.string.LNG_RIDE_PAY_CHARGE, default, preferredPrice)
        else getString(R.string.LNG_RIDE_PAY_CHARGE2, default)
    }

    override fun setBookNowOffer(bookNowOffer: BookNowOfferModel?) {
        hidePaymentPercentage()
        setCarInfo(bookNowOffer)
        setPriceInfo(bookNowOffer?.base?.def, bookNowOffer?.base?.preferred)
    }

    private fun setCarInfo(offer: OfferItem?) {
        when (offer) {
            is OfferModel -> showCarInfoOffer(offer)
            is BookNowOfferModel -> showCarInfoBookNow()
        }
    }

    private fun showCarInfoBookNow() {
        val transportType = presenter.params.bookNowTransportId ?: ""
        val transportTypeId = TransportType.ID.parse(transportType)
        tvClass.text = getString(TransportTypeMapper.getNameById(transportTypeId))
        tvModel.text = getString(TransportTypeMapper.getModelsById(transportTypeId))
        Utils.bindMainOfferPhoto(ivCarPhoto, content, resource = TransportTypeMapper.getImageById(transportTypeId))
        OfferItemBindDelegate.bindRating(layoutRating, RatingsModel.BOOK_NOW_RATING, true)
        OfferItemBindDelegate.bindLanguages(
                multiLineContainer = languages_container_tiny,
                languages = listOf(LocaleModel.BOOK_NOW_LOCALE_DEFAULT),
                rowNumber = 6)
    }

    private fun showCarInfoOffer(offer: OfferModel) {
        tvModel.text =
                if (offer.vehicle.color != null)
                    Utils.getVehicleNameWithColor(this, offer.vehicle.name, offer.vehicle.color)
                else offer.vehicle.name
        offer.vehicle.photos.firstOrNull()
                .also {
                    Utils.bindMainOfferPhoto(
                            ivCarPhoto,
                            content,
                            path = it,
                            resource = TransportTypeMapper.getEmptyImageById(offer.vehicle.transportType.id)
                    )
                }
        tvClass.text = offer.vehicle.transportType.nameId?.let { getString(it) ?: "" }
        OfferItemBindDelegate.bindLanguages(
                multiLineContainer = languages_container_tiny,
                languages = offer.carrier.languages,
                rowNumber = 6)
        OfferItemBindDelegate.bindRating(layoutRating, offer.carrier.ratings, offer.carrier.approved)
    }

    private fun hidePaymentPercentage() {
        layoutPrices.isVisible = false
    }

    override fun showOfferError() {
        toast(getString(R.string.LNG_RIDE_OFFER_CANCELLED))
    }

    override fun setCommission(paymentCommission: String) {
        presenter.params.dateRefund?.let {
            tvCommission.text = getString(R.string.LNG_PAYMENT_COMISSION2, paymentCommission, SystemUtils.formatDateTime(it))
        }
    }

    override fun setCurrencyConvertingInfo(offerCurrency: Currency, ownCurrency: Currency) {
        view_currency_converting_info.isVisible = true
        view_currency_converting_info.tv_convert_description.text = getString(R.string.LNG_RIDE_PAY_CONVERT1,
                ownCurrency.symbol,
                ownCurrency.code,
                offerCurrency.symbol,
                offerCurrency.code
        )
        view_currency_converting_info.tv_payment_description.text = getString(R.string.LNG_RIDE_PAY_CONVERT2,
                offerCurrency.symbol,
                offerCurrency.code)
    }

    private fun changePaymentSettings(view: View?) {
        when (view?.id) {
            R.id.rbPay100 -> selectPaymentPercentage(PaymentRequestModel.FULL_PRICE)
            R.id.rbPay30  -> selectPaymentPercentage(PaymentRequestModel.PRICE_30)
        }
    }

    private fun selectPaymentPercentage(selectedPercentage: Int) {
        when (selectedPercentage) {
            PaymentRequestModel.FULL_PRICE -> {
                rbPay100.isChecked = true
                rbPay30.isChecked = false
            }
            PaymentRequestModel.PRICE_30 -> {
                rbPay100.isChecked = false
                rbPay30.isChecked = true
            }
        }
        presenter.changePrice(selectedPercentage)
        this.selectedPercentage = selectedPercentage
    }

    override fun startPaypal(dropInRequest: DropInRequest) {
        when {
            payPalInstalled() -> {
                blockInterface(false)
                startActivityForResult(dropInRequest.getIntent(this), PAYMENT_REQUEST_CODE)
            }
            browserInstalled() -> {
                val braintreeFragment = BraintreeFragment.newInstance(this, presenter.braintreeToken)
                PayPal.requestOneTimePayment(braintreeFragment, dropInRequest.payPalRequest)
            }
            else -> {
                blockInterface(false)
                longToast(getString(R.string.LNG_PAYMENT_INSTALL_PAYPAL))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYMENT_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val result: DropInResult = data?.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT)!!
                    val nonce = result.paymentMethodNonce?.nonce
                    presenter.confirmPayment(nonce!!)
                }
                RESULT_CANCELED -> presenter.changePayment(PaymentRequestModel.PAYPAL)
                else -> {
                    val error = data?.getSerializableExtra(DropInActivity.EXTRA_ERROR) as Exception
                    Timber.e(error)
                }
            }
        }
    }

    private fun payPalInstalled(): Boolean {
        return try {
            packageManager.getPackageInfo(PAYPAL_PACKAGE_NAME, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun browserInstalled(): Boolean {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://"))
        val resolveInfo = packageManager.resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY)
        return resolveInfo != null
    }

    override fun onPaymentMethodNonceCreated(paymentMethodNonce: PaymentMethodNonce?) {
        val nonce = paymentMethodNonce?.nonce ?: ""
        presenter.confirmPayment(nonce)
        blockInterface(true, true)
    }

    override fun onError(error: Exception?) {
        blockInterface(false)
        if (error is ErrorWithResponse) {
            val cardErrors = error.errorFor("creditCard")
            if (cardErrors != null) {
                // There is an issue with the credit card.
                val expirationMonthError = cardErrors.errorFor("expirationMonth")
                if (expirationMonthError != null) {
                    // There is an issue with the expiration month.
                    Timber.e(expirationMonthError.message)
                }
            }
        }
    }

    override fun onCancel(requestCode: Int) {
        presenter.changePayment(PaymentRequestModel.PAYPAL)
    }

    override fun setToolbarTitle(transferModel: TransferModel) {
        toolbar.tvSubTitle.text = transferModel.from
                .let { from ->
                    transferModel.to?.let {
                        from.plus(" - ").plus(it)
                    } ?: transferModel.duration?.let {
                        from.plus(" - ").plus(HourlyValuesHelper.getValue(it, this))
                    } ?: from
                }
        toolbar.tvSubTitle2.text = SystemUtils.formatDateTimeNoYearShortMonth(transferModel.dateTime)
    }

    override fun setAuthUiVisible(visible: Boolean) {
        ll_auth_container.isVisible = visible
        if (visible) initTextChangeListeners()
        btnGetPayment.isEnabled = !visible
    }

    override fun setPaymentEnabled(enabled: Boolean) {
        btnGetPayment.isEnabled = enabled
    }

    override fun showBadCredentialsInfo(field: Int) {
        val errStringRes = when (field) {
            INVALID_EMAIL    -> R.string.LNG_ERROR_EMAIL
            INVALID_PHONE    -> R.string.LNG_ERROR_PHONE
            else             -> R.string.LNG_BAD_CREDENTIALS_ERROR
        }
        Utils.showError(this, false, getString(errStringRes))
    }

    override fun redirectToLogin() {
        presenter.redirectToLogin(et_auth_email.getString())
    }

    override fun setEmail(email: String) {
        et_auth_email.setText(email)
    }

    override fun setPhone(phone: String) {
        et_auth_phone.setText(phone)
    }

    override fun setError(e: ApiException) {
        Timber.e("code: ${e.code}", e)
        val sentryMessage = if(e.code == ApiException.PHONE_REQUIRED_FOR_PAYMENT) getString(R.string.LNG_PHONE_REQUIRED_ERROR) else e.details
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage(sentryMessage).build())
        Sentry.capture(e)
        val errorText = when {
            e.code == ApiException.PHONE_REQUIRED_FOR_PAYMENT -> getString(R.string.LNG_PHONE_REQUIRED_ERROR)
            e.code != ApiException.NETWORK_ERROR -> getString(R.string.LNG_ERROR) + ": " + e.message
            else -> null
        }
        errorText?.let { Utils.showError(this, false, it) }
    }
}
