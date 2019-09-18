package com.kg.gettransfer.presentation.ui

import android.app.Activity
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
import com.kg.gettransfer.domain.model.Ratings
import com.kg.gettransfer.domain.model.TransportType
import com.kg.gettransfer.extensions.isGone
import com.kg.gettransfer.extensions.isVisible

import com.kg.gettransfer.presentation.delegate.Either
import com.kg.gettransfer.presentation.delegate.OfferItemBindDelegate

import com.kg.gettransfer.presentation.model.BookNowOfferModel
import com.kg.gettransfer.presentation.model.LocaleModel
import com.kg.gettransfer.presentation.model.OfferItemModel
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentRequestModel
import com.kg.gettransfer.presentation.model.ProfileModel
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.model.getEmptyImageRes
import com.kg.gettransfer.presentation.model.getImageRes
import com.kg.gettransfer.presentation.model.getModelsRes
import com.kg.gettransfer.presentation.model.getNameRes

import com.kg.gettransfer.presentation.presenter.PaymentOfferPresenter
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper
import com.kg.gettransfer.presentation.ui.helpers.LanguageDrawer
import com.kg.gettransfer.presentation.view.CreateOrderView
import com.kg.gettransfer.presentation.view.PaymentOfferView

import com.kg.gettransfer.utilities.PhoneNumberFormatter

import io.sentry.Sentry
import io.sentry.event.BreadcrumbBuilder

import kotlinx.android.synthetic.main.activity_payment_offer.*
import kotlinx.android.synthetic.main.layout_payments.*
import kotlinx.android.synthetic.main.layout_prices.*
import kotlinx.android.synthetic.main.offer_tiny_payment.*
import kotlinx.android.synthetic.main.payment_refund.*
import kotlinx.android.synthetic.main.paymet_gtr_bonus.*
import kotlinx.android.synthetic.main.toolbar_nav_payment.view.*
import kotlinx.android.synthetic.main.view_currency_converting_info.view.*
import kotlinx.android.synthetic.main.view_input_account_field.view.*
import kotlinx.android.synthetic.main.view_transport_capacity.view.*

import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast

import timber.log.Timber

@Suppress("TooManyFunctions")
class PaymentOfferActivity : BaseActivity(),
    PaymentOfferView,
    PaymentMethodNonceCreatedListener,
    BraintreeErrorListener,
    BraintreeCancelListener {

    private var errorField: View? = null

    @InjectPresenter
    internal lateinit var presenter: PaymentOfferPresenter

    override fun getPresenter(): PaymentOfferPresenter = presenter

    @ProvidePresenter
    fun createPaymentSettingsPresenter() = PaymentOfferPresenter()

    private var selectedPercentage = PaymentRequestModel.FULL_PRICE

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // presenter.params =
        // JSON.parse(PaymentOfferView.Params.serializer(), intent.getStringExtra(PaymentOfferView.EXTRA_PARAMS))

        setContentView(R.layout.activity_payment_offer)
        initListeners()
        initToolbar()
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
        btnGetPayment.setOnClickListener {
            clearHighLightErrorField(errorField)
            presenter.onPaymentClicked()
        }
        View.OnClickListener { changePayment(rbCard, PaymentRequestModel.PLATRON) }.apply {
            rbCard.setOnClickListener(this)
            layoutCard.setOnClickListener(this)
        }
        View.OnClickListener { changePayment(rbPaypal, PaymentRequestModel.PAYPAL) }.apply {
            rbPaypal.setOnClickListener(this)
            layoutPaypal.setOnClickListener(this)
        }
        View.OnClickListener { changePayment(rbBalance, PaymentRequestModel.GROUND) }.apply {
            rbBalance.setOnClickListener(this)
            layoutBalance.setOnClickListener(this)
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

    private fun changePayment(view: View, payment: String) {
        when (view.id) {
            R.id.rbCard -> {
                rbCard.isChecked = true
                rbPaypal.isChecked = false
                rbBalance.isChecked = false
            }
            R.id.rbPaypal -> {
                rbPaypal.isChecked = true
                rbCard.isChecked = false
                rbBalance.isChecked = false
            }
            R.id.rbBalance -> {
                rbBalance.isChecked = true
                rbCard.isChecked = false
                rbPaypal.isChecked = false
            }
        }
        presenter.selectedPayment = payment
        presenter.changePayment(payment)
    }

    @Suppress("NestedBlockDepth")
    override fun setOffer(offer: OfferModel, paymentPercentages: List<Int>) {
        if (paymentPercentages.isNotEmpty()) {
            if (paymentPercentages.size == 1) {
                hidePaymentPercentage()
            } else {
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
        setCapacity(offer.vehicle.transportType)
    }

    override fun setBookNowOffer(bookNowOffer: BookNowOfferModel) {
        hidePaymentPercentage()
        setCarInfo(bookNowOffer)
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

    private fun setCarInfo(offer: OfferItemModel?) {
        when (offer) {
            is OfferModel -> showCarInfoOffer(offer)
            is BookNowOfferModel -> showCarInfoBookNow(offer.transportType.id)
        }
    }

    private fun showCarInfoBookNow(transportTypeId: TransportType.ID) {
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

    private fun showCarInfoOffer(offer: OfferModel) {
        with(offer.vehicle) {
            tvModel.text = name
            if (photos.isEmpty()) {
                color?.let { col ->
                    ivCarColor.isVisible = true
                    ivCarColor.setImageDrawable(Utils.getCarColorFormRes(this@PaymentOfferActivity, col))
                }
            }
            photos.firstOrNull().also { photo ->
                Utils.bindMainOfferPhoto(
                    ivCarPhoto,
                    content,
                    path = photo,
                    resource = transportType.id.getEmptyImageRes()
                )
            }
            tvClass.text = getString(transportType.nameId)
        }
        with(offer.carrier) {
            OfferItemBindDelegate.bindLanguages(
                Either.Single(languages_container_tiny),
                languages,
                layoutParamsRes = LanguageDrawer.LanguageLayoutParamsRes.OFFER_PAYMENT_VIEW)
            OfferItemBindDelegate.bindRating(layoutRating, ratings, approved)
        }
        offer.isNameSignPresent?.let { isNameSignPresent ->
            offer.isWithNameSign?.let { isWithNameSign ->
                imgWithNameSign.isVisible = isNameSignPresent && isWithNameSign
                imgMissingNameSign.isVisible = isNameSignPresent && !isWithNameSign
                tvMissingNameSign.isVisible = isNameSignPresent && !isWithNameSign
            }
        }
    }

    private fun hidePaymentPercentage() {
        layoutPrices.isVisible = false
    }

    override fun showOfferError() {
        toast(getString(R.string.LNG_RIDE_OFFER_CANCELLED))
    }

    override fun setCommission(paymentCommission: String, dateRefund: String) {
        getString(R.string.LNG_PAYMENT_COMISSION3, paymentCommission).run {
            tvCommission.text = this
            cardsCommission.text = this
            paypalCommission.text = this
        }
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

    private fun changePaymentSettings(view: View?) {
        when (view?.id) {
            R.id.rbPay100 -> selectPaymentPercentage(PaymentRequestModel.FULL_PRICE)
            R.id.rbPay30 -> selectPaymentPercentage(PaymentRequestModel.PRICE_30)
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

    override fun startPaypal(dropInRequest: DropInRequest, brainteeToken: String) {
        blockInterface(false)
        when {
            payPalInstalled()  -> startActivityForResult(dropInRequest.getIntent(this), PAYMENT_REQUEST_CODE)
            browserInstalled() -> {
                val braintreeFragment = BraintreeFragment.newInstance(this, presenter.braintreeToken)
                PayPal.requestOneTimePayment(braintreeFragment, dropInRequest.payPalRequest)
            }
            else               -> longToast(getString(R.string.LNG_PAYMENT_INSTALL_PAYPAL))
        }
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYMENT_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val result: DropInResult? = data?.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT)
                    result?.paymentMethodNonce?.nonce?.let { presenter.confirmPayment(it) }
                }
                RESULT_CANCELED    -> presenter.changePayment(PaymentRequestModel.PAYPAL)
                else               -> {
                    val error = data?.getSerializableExtra(DropInActivity.EXTRA_ERROR)
                    if (error is Exception) {
                        Timber.e(error)
                    }
                }
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
        presenter.confirmPayment(paymentMethodNonce?.nonce ?: "")
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
        toolbar.tvSubTitle.text = transferModel.from.let { from ->
            transferModel.to?.let { from.plus(" - ").plus(it) } ?: transferModel.duration?.let { duration ->
                from.plus(" - ").plus(HourlyValuesHelper.getValue(duration, this))
            } ?: from
        }
        toolbar.tvSubTitle2.text = SystemUtils.formatDateTimeNoYearShortMonth(transferModel.dateTime)
    }

    override fun setAuthUiVisible(hasAccount: Boolean, profile: ProfileModel, balance: String?) {
        if (hasAccount && (profile.email.isNullOrEmpty() || profile.phone.isNullOrEmpty())) {
            ll_auth_container.isVisible = true
            initEmailTextChangeListeners()
            initPhoneTextChangeListeners()
            profile.email?.let { emailLayout.fieldText.setText(it) }
            profile.phone?.let { phoneLayout.fieldText.setText(it) }
        } else {
            ll_auth_container.isVisible = false
            setBalance(balance)
        }
    }

    private fun setBalance(balance: String?) {
        balance.isNullOrEmpty().run {
            layoutBalance.isVisible = !this
            tvCommission.isVisible = this
            cardsCommission.isVisible = !this
            paypalCommission.isVisible = !this
        }
        tvBalance.text = getString(R.string.LNG_PAYMENT_FROM_BALANCE_AVAILABLE, balance)
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

    companion object {
        const val PAYMENT_REQUEST_CODE = 100
        const val PAYPAL_PACKAGE_NAME = "com.paypal.android.p2pmobile"

        const val INVALID_EMAIL = 1
        const val INVALID_PHONE = 2

        const val DELAY = 150L
        const val VELOCITY_Y = 2000
    }
}
