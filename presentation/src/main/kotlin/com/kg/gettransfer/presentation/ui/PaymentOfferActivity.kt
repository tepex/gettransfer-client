package com.kg.gettransfer.presentation.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.EditText

import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar

import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

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
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.Wallet
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.AutoResolveHelper

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.model.Currency
import com.kg.gettransfer.domain.model.Ratings
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.extensions.isInvisible
import com.kg.gettransfer.extensions.isVisible

import com.kg.gettransfer.presentation.delegate.Either
import com.kg.gettransfer.presentation.delegate.OfferItemBindDelegate
import com.kg.gettransfer.presentation.listeners.GoToPlayMarketListener

import com.kg.gettransfer.presentation.model.BookNowOfferModel
import com.kg.gettransfer.presentation.model.LocaleModel
import com.kg.gettransfer.presentation.model.OfferItemModel
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentRequestModel
import com.kg.gettransfer.presentation.model.ProfileModel
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.model.VehicleModel
import com.kg.gettransfer.presentation.model.getEmptyImageRes
import com.kg.gettransfer.presentation.model.getImageRes
import com.kg.gettransfer.presentation.model.getModelsRes
import com.kg.gettransfer.presentation.model.getNameRes

import com.kg.gettransfer.presentation.presenter.PaymentOfferPresenter
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper
import com.kg.gettransfer.presentation.ui.helpers.LanguageDrawer
import com.kg.gettransfer.presentation.view.CreateOrderView
import com.kg.gettransfer.presentation.view.PaymentOfferView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.PhoneNumberFormatter

import io.sentry.Sentry
import io.sentry.event.BreadcrumbBuilder

import kotlinx.android.synthetic.main.activity_payment_offer.*
import kotlinx.android.synthetic.main.layout_payments.*
import kotlinx.android.synthetic.main.offer_tiny_payment.*
import kotlinx.android.synthetic.main.payment_refund.*
import kotlinx.android.synthetic.main.paymet_gtr_bonus.*
import kotlinx.android.synthetic.main.toolbar_nav_payment.view.*
import kotlinx.android.synthetic.main.view_currency_converting_info.view.*
import kotlinx.android.synthetic.main.view_input_account_field.view.*
import kotlinx.android.synthetic.main.view_transport_capacity.view.*

import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import org.json.JSONObject

import timber.log.Timber

@Suppress("TooManyFunctions")
class PaymentOfferActivity : BaseActivity(),
    PaymentOfferView,
    PaymentMethodNonceCreatedListener,
    BraintreeErrorListener,
    BraintreeCancelListener,
    GoToPlayMarketListener {

    private var errorField: View? = null

    @InjectPresenter
    internal lateinit var presenter: PaymentOfferPresenter

    override fun getPresenter(): PaymentOfferPresenter = presenter

    @ProvidePresenter
    fun createPaymentSettingsPresenter() = PaymentOfferPresenter()

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_offer)
        initListeners()
        initToolbar()
    }

    override fun initGooglePayPaymentsClient(environment: Int) {
        presenter.googlePayPaymentsClient = Wallet.getPaymentsClient(
            this,
            Wallet.WalletOptions.Builder()
                .setEnvironment(environment)
                .build())
    }

    private fun initToolbar() {
        val tb = toolbar
        if (tb is Toolbar) {
            setSupportActionBar(tb)
            tb.tvTitle.text = getString(R.string.LNG_PAYMENT_SETTINGS)
            tb.btnBack.setOnClickListener { presenter.onBackCommandClick() }
            tb.tvSubTitle.isSelected = true
        }
    }

    private fun initListeners() {
        tvPaymentAgreement.setOnClickListener { presenter.onAgreementClicked() }
        View.OnClickListener {
            clearHighLightErrorField(errorField)
            presenter.onPaymentClicked()
        }.apply {
            btnGetPayment.setOnClickListener(this)
            btnGetPaymentWithGooglePay.setOnClickListener(this)
        }
        View.OnClickListener { presenter.changePaymentType(PaymentRequestModel.CARD) }.apply {
            rbCard.setOnClickListener(this)
            layoutCard.setOnClickListener(this)
        }
        View.OnClickListener { presenter.changePaymentType(PaymentRequestModel.PAYPAL) }.apply {
            rbPaypal.setOnClickListener(this)
            layoutPaypal.setOnClickListener(this)
        }
        View.OnClickListener { presenter.changePaymentType(PaymentRequestModel.GROUND) }.apply {
            rbBalance.setOnClickListener(this)
            layoutBalance.setOnClickListener(this)
        }
        View.OnClickListener { presenter.changePaymentType(PaymentRequestModel.GOOGLE_PAY) }.apply {
            rbGooglePay.setOnClickListener(this)
            layoutGooglePay.setOnClickListener(this)
        }
        addKeyBoardDismissListener { state ->
            Handler().postDelayed({
                if (state) {
                    sv_root.fling(VELOCITY_Y)         // need to show "Payment" button
                }
            }, DELAY)
        }
    }

    private fun initEmailTextChangeListeners() {
        emailLayout.fieldText.onTextChanged { text ->
            presenter.setEmail(text)
            clearHighLightErrorField(emailLayout)
        }
    }

    private fun initPhoneTextChangeListeners() {
        with(phoneLayout.fieldText) {
            onTextChanged { text ->
                clearHighLightErrorField(phoneLayout)
                if (text.isEmpty() && isFocused) {
                    setText("+")
                    setSelection(1)
                }
                presenter.setPhone("+".plus(text.replace(Regex("\\D"), "")))
            }
            addTextChangedListener(PhoneNumberFormatter())
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setPhoneCode()
                } else {
                    val phone = text?.trim()
                    if (phone != null && phone.length == 1) {
                        text?.clear()
                        presenter.setPhone("")
                    }
                }
            }
        }
    }

    private fun setPhoneCode() {
        with(phoneLayout.fieldText) {
            val phone = text?.trim()
            if (phone != null && phone.isEmpty()) {
                val phoneCode = Utils.getPhoneCodeByCountryIso(context)
                setText(if (phoneCode > 0) "+".plus(phoneCode) else "+")
            }
            fieldTouched(this)
        }
    }

    private fun fieldTouched(viewForFocus: EditText) {
        if (!isKeyBoardOpened) {
            showKeyboard()
        }
        viewForFocus.apply {
            requestFocus()
            post { setSelection(text.length) }
        }
    }

    override fun highLightError(error: CreateOrderView.FieldError?) {
        when (error) {
            CreateOrderView.FieldError.PHONE_FIELD   -> highLightErrorField(phoneLayout)
            CreateOrderView.FieldError.EMAIL_FIELD   -> highLightErrorField(emailLayout)
            CreateOrderView.FieldError.INVALID_PHONE -> highLightErrorField(phoneLayout)
            CreateOrderView.FieldError.INVALID_EMAIL -> highLightErrorField(emailLayout)
            else                                     -> return
        }
    }

    private fun highLightErrorField(view: View) {
        errorField = view
        view.setBackgroundResource(R.drawable.background_field_error)
        sv_root.smoothScrollTo(0, view.bottom)
    }

    private fun clearHighLightErrorField(view: View?) = view?.setBackgroundResource(0)

    override fun selectPaymentType(type: String) {
        clearPaymentsRadioButtons()
        when (type) {
            PaymentRequestModel.CARD       -> rbCard.isChecked = true
            PaymentRequestModel.PAYPAL     -> rbPaypal.isChecked = true
            PaymentRequestModel.GOOGLE_PAY -> rbGooglePay.isChecked = true
            PaymentRequestModel.GROUND     -> rbBalance.isChecked = true
        }
        tvCommission.isInvisible = type == PaymentRequestModel.GROUND
        changePayButton(type == PaymentRequestModel.GOOGLE_PAY)
    }

    private fun clearPaymentsRadioButtons() {
        rbCard.isChecked = false
        rbPaypal.isChecked = false
        rbBalance.isChecked = false
        rbGooglePay.isChecked = false
    }

    private fun changePayButton(isPaymentWithGooglePay: Boolean) {
        btnGetPayment.isVisible = !isPaymentWithGooglePay
        btnGetPaymentWithGooglePay.isVisible = isPaymentWithGooglePay
    }

    @Suppress("NestedBlockDepth")
    override fun setOffer(offer: OfferModel, isNameSignPresent: Boolean) {
        setCarInfoOffer(offer, isNameSignPresent)
        setPriceInfo(offer.price.base.def, offer.price.base.preferred)
        setCapacity(offer.vehicle.transportType)
    }

    override fun setBookNowOffer(bookNowOffer: BookNowOfferModel, isNameSignPresent: Boolean) {
        setCarInfoBookNow(bookNowOffer.transportType.id)
        setPriceInfo(bookNowOffer.base.def, bookNowOffer.base.preferred)
        setCapacity(bookNowOffer.transportType)
    }

    private fun setCapacity(transport: TransportTypeModel) {
        with(view_capacity) {
            transportType_сountPassengers.text = "x ${transport.paxMax}"
            transportType_сountBaggage.text = "x ${transport.luggageMax}"
        }
    }

    private fun setPriceInfo(default: String?, preferredPrice: String?) {
        tvPriceInfo.text =
        if (preferredPrice != null) {
            getString(R.string.LNG_RIDE_PAY_CHARGE, default, preferredPrice)
        } else {
            getString(R.string.LNG_RIDE_PAY_CHARGE2, default)
        }
    }

    private fun setCarInfoBookNow(transportTypeId: TransportType.ID) {
        tvClass.text = getString(transportTypeId.getNameRes())
        tvModel.text = getString(transportTypeId.getModelsRes())
        Utils.bindMainOfferPhoto(ivCarPhoto, content, resource = transportTypeId.getImageRes())
        OfferItemBindDelegate.bindRating(layoutRating, Ratings.BOOK_NOW_RATING, true)
        OfferItemBindDelegate.bindLanguages(
            Either.Single(languages_container_tiny),
            listOf(LocaleModel.BOOK_NOW_LOCALE_DEFAULT),
            layoutParamsRes = LanguageDrawer.LanguageLayoutParamsRes.OFFER_PAYMENT_VIEW
        )
    }

    private fun setCarInfoOffer(offer: OfferModel, isNameSignPresent: Boolean) {
        with(offer.vehicle) {
            tvModel.text = name
            tvClass.text = getString(transportType.nameId)
        }
        with(offer.carrier) {
            OfferItemBindDelegate.bindLanguages(
                Either.Single(languages_container_tiny),
                languages,
                layoutParamsRes = LanguageDrawer.LanguageLayoutParamsRes.OFFER_PAYMENT_VIEW)
            OfferItemBindDelegate.bindRating(layoutRating, ratings, approved)
        }
        OfferItemBindDelegate.bindNameSignPlate(this, imgNameSign, tvMissingNameSign,
            isNameSignPresent, offer.isWithNameSign)
    }

    override fun setCarPhotoOffer(vehicle: VehicleModel) {
        with(vehicle) {
            photos.firstOrNull().also { photo ->
                ivCarColor.isVisible = false
                Utils.bindMainOfferPhoto(
                    ivCarPhoto,
                    content,
                    path = photo,
                    resource = transportType.id.getEmptyImageRes()
                )
            } ?: color?.let { col ->
                ivCarColor.isVisible = true
                ivCarColor.setImageDrawable(Utils.getCarColorFormRes(this@PaymentOfferActivity, col))
            } ?: run {
                ivCarColor.isVisible = false
            }
        }
    }

    override fun showOfferError() {
        toast(getString(R.string.LNG_RIDE_OFFER_CANCELLED))
    }

    override fun showPaymentInProgressError() {
        Utils.showError(this, false, getString(R.string.LNG_RIDE_PAYMENT))
    }

    override fun setCommission(paymentCommission: String, dateRefund: String) {
        tvCommission.text = getString(R.string.LNG_PAYMENT_SERVICE_FEE, paymentCommission)
        tvRefundDate.text = getString(R.string.LNG_PAYMENT_REFUND, dateRefund)
    }

    override fun setCurrencyConvertingInfo(offerCurrency: Currency, ownCurrency: Currency) {
        view_currency_converting_info.isVisible = true
        view_currency_converting_info.tv_convert_description.text = getString(
            R.string.LNG_RIDE_PAY_CONVERT1,
            ownCurrency.symbol,
            ownCurrency.code,
            offerCurrency.symbol,
            offerCurrency.code
        )
        view_currency_converting_info.tv_payment_description.text = getString(
            R.string.LNG_RIDE_PAY_CONVERT2,
            offerCurrency.symbol,
            offerCurrency.code
        )
    }

    override fun startPaypal(dropInRequest: DropInRequest, token: String) {
        blockInterface(false)
        when {
            payPalInstalled()  -> startActivityForResult(dropInRequest.getIntent(this), PAYPAL_PAYMENT_REQUEST_CODE)
            browserInstalled() -> {
                val braintreeFragment = BraintreeFragment.newInstance(this, token)
                PayPal.requestOneTimePayment(braintreeFragment, dropInRequest.payPalRequest)
            }
            else               -> longToast(getString(R.string.LNG_PAYMENT_INSTALL_PAYPAL))
        }
    }

    override fun startGooglePay(task: Task<PaymentData>) {
        AutoResolveHelper.resolveTask(task, this, GOOGLE_PAY_PAYMENT_REQUEST_CODE)
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PAYPAL_PAYMENT_REQUEST_CODE     -> checkPaypalPaymentResult(resultCode, data)
            GOOGLE_PAY_PAYMENT_REQUEST_CODE -> checkGooglePayPaymentResult(resultCode, data)
        }
    }

    private fun checkPaypalPaymentResult(resultCode: Int, data: Intent?) {
        when (resultCode) {
            RESULT_OK       -> {
                val result: DropInResult? = data?.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT)
                result?.paymentMethodNonce?.nonce?.let { presenter.confirmPaypalPayment(it) }
            }
            RESULT_CANCELED -> presenter.changePaymentType(PaymentRequestModel.PAYPAL)
            else            -> {
                val error = data?.getSerializableExtra(DropInActivity.EXTRA_ERROR)
                if (error is Exception) {
                    Timber.e(error)
                }
            }
        }
    }

    private fun checkGooglePayPaymentResult(resultCode: Int, data: Intent?) {
        when (resultCode) {
            RESULT_OK       -> {
                val paymentData = data?.let { PaymentData.getFromIntent(it) }
                val json = paymentData?.toJson()?.let { JSONObject(it) }
                val token = json
                    ?.getJSONObject("paymentMethodData")
                    ?.getJSONObject("tokenizationData")
                    ?.getString("token")
                token?.let { presenter.processGooglePayPayment(it) }
            }
            RESULT_CANCELED -> presenter.changePaymentType(PaymentRequestModel.GOOGLE_PAY)
            AutoResolveHelper.RESULT_ERROR -> {
                val status = data?.let { AutoResolveHelper.getStatusFromIntent(it) }
                status?.statusMessage?.let { Timber.e(it) }
            }
        }
    }

    private fun payPalInstalled() = try {
        packageManager.getPackageInfo(PAYPAL_PACKAGE_NAME, PackageManager.GET_ACTIVITIES)
        true
    } catch (e: PackageManager.NameNotFoundException) {
        false
    }

    private fun browserInstalled() =
        packageManager.resolveActivity(
            Intent(Intent.ACTION_VIEW, Uri.parse("http://")),
            PackageManager.MATCH_DEFAULT_ONLY
        ) != null

    override fun onPaymentMethodNonceCreated(paymentMethodNonce: PaymentMethodNonce?) {
        presenter.confirmPaypalPayment(paymentMethodNonce?.nonce ?: "")
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
        presenter.changePaymentType(PaymentRequestModel.PAYPAL)
    }

    override fun setToolbarTitle(transferModel: TransferModel) {
        toolbar.tvSubTitle.text = transferModel.from.let { from ->
            transferModel.to?.let { from.plus(" - ").plus(it) } ?: transferModel.duration?.let { duration ->
                from.plus(" - ").plus(HourlyValuesHelper.getValue(duration, this))
            } ?: from
        }
        toolbar.tvSubTitle2.text = SystemUtils.formatDateTimeNoYearShortMonth(transferModel.dateTime)
    }

    override fun setAuthUi(hasAccount: Boolean, profile: ProfileModel) {
        ll_auth_container.isVisible = true
        initEmailTextChangeListeners()
        initPhoneTextChangeListeners()
        profile.email?.let { emailLayout.fieldText.setText(it) }
        profile.phone?.let { phoneLayout.fieldText.setText(it) }
    }

    override fun hideAuthUi() {
        ll_auth_container.isVisible = false
    }

    override fun setBalance(balance: String) {
        layoutBalance.isVisible = true
        tvBalance.text = getString(R.string.LNG_PAYMENT_FROM_BALANCE_AVAILABLE, balance)
    }

    override fun hideBalance() {
        layoutBalance.isVisible = false
    }

    override fun showBadCredentialsInfo(field: Int) {
        val errStringRes = when (field) {
            INVALID_EMAIL -> R.string.LNG_ERROR_EMAIL
            INVALID_PHONE -> R.string.LNG_ERROR_PHONE
            else          -> R.string.LNG_BAD_CREDENTIALS_ERROR
        }
        Utils.showError(this, false, getString(errStringRes))
    }

    override fun setError(e: ApiException) {
        Timber.e(e, "code: ${e.code}")
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage(e.details).build())
        Sentry.capture(e)
        val errorText = when {
            e.isBigPriceError()                  -> getString(R.string.LNG_BIG_PRICE_ERROR)
            e.isOfferUnavailableError()          -> getString(R.string.LNG_OFFER_NO_LONGER_AVAILABLE)
            e.code != ApiException.NETWORK_ERROR -> getString(R.string.LNG_ERROR) + ": " + e.message
            else                                 -> null
        }
        errorText?.let { Utils.showError(this, false, it) }
    }

    override fun showFieldError(@StringRes stringId: Int) {
        Utils.getAlertDialogBuilder(this).apply {
            setTitle(getString(stringId))
            setPositiveButton(R.string.LNG_OK) { dialog, _ ->
                dialog.dismiss()
                hideKeyboard()
            }
            show()
        }
    }

    override fun showGooglePayButton() {
        layoutGooglePay.isVisible = true
    }

    override fun hideGooglePayButton() {
        layoutGooglePay.isVisible = false
    }

    override fun showPaymentError(transferId: Long, gatewayId: String?) {
        Screens.PaymentError(supportFragmentManager, transferId, gatewayId).showDialog()
    }

    override fun onClickGoToDriverApp() {
        Utils.goToGooglePlay(this, getString(R.string.driver_app_market_package))
    }

    companion object {
        const val PAYPAL_PAYMENT_REQUEST_CODE = 100
        const val GOOGLE_PAY_PAYMENT_REQUEST_CODE = 42
        const val PAYPAL_PACKAGE_NAME = "com.paypal.android.p2pmobile"

        const val INVALID_EMAIL = 1
        const val INVALID_PHONE = 2

        const val DELAY = 150L
        const val VELOCITY_Y = 2000
    }
}
