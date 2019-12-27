package com.kg.gettransfer.presentation.ui

import android.annotation.TargetApi
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.CheckoutcomPaymentPresenter
import com.kg.gettransfer.presentation.view.CheckoutcomPaymentView
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.checkout.android_sdk.Utils.AfterTextChangedListener
import com.kg.gettransfer.presentation.presenter.BaseCardPaymentPresenter
import kotlinx.android.synthetic.main.activity_checkout_payment.*
import kotlinx.android.synthetic.main.view_checkoutcom_card_field.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.checkout.android_sdk.Utils.CardUtils
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.presentation.presenter.CheckoutcomPaymentPresenter.Companion.CARD_CVC_ERROR
import com.kg.gettransfer.presentation.presenter.CheckoutcomPaymentPresenter.Companion.CARD_DATE_ERROR
import com.kg.gettransfer.presentation.presenter.CheckoutcomPaymentPresenter.Companion.CARD_DATE_LENGTH
import com.kg.gettransfer.presentation.presenter.CheckoutcomPaymentPresenter.Companion.CARD_NUMBER_ERROR
import com.kg.gettransfer.utilities.CardDateFormatter
import io.sentry.Sentry
import io.sentry.event.BreadcrumbBuilder
import timber.log.Timber

class CheckoutcomPaymentActivity : BaseActivity(), CheckoutcomPaymentView {

    @InjectPresenter
    internal lateinit var presenter: CheckoutcomPaymentPresenter

    override fun getPresenter(): CheckoutcomPaymentPresenter = presenter

    @ProvidePresenter
    fun createPaymentPresenter() = CheckoutcomPaymentPresenter()

    @Suppress("UnsafeCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout_payment)
        presenter.paymentId = intent.getLongExtra(CheckoutcomPaymentView.EXTRA_PAYMENT_ID, 0L)

        setToolbar(toolbar as Toolbar, R.string.LNG_PAYMENT)
        setPrice(intent.getStringExtra(CheckoutcomPaymentView.EXTRA_AMOUNT_FORMATTED) ?: "")
        cardDate.setMaxLength(CARD_DATE_LENGTH)
        initTextChangedListeners()
        payButton.setOnClickListener { presenter.onPayButtonPressed() }
    }

    private fun initTextChangedListeners() {
        cardNumber.field_input.addTextChangedListener(object : AfterTextChangedListener() {
            var lastCardNumber = ""

            override fun afterTextChanged(cardNumber: Editable) {
                val stringNumber = cardNumber.toString()
                if (lastCardNumber != stringNumber) {
                    val number = cardNumber.replace("\\D".toRegex(), "")
                    presenter.cardNumber = number
                    lastCardNumber = CardUtils.getFormattedCardNumber(number)
                    cardNumber.replace(0, stringNumber.length, lastCardNumber)
                }
            }
        })

        cardDate.field_input.addTextChangedListener(CardDateFormatter())
        cardDate.field_input.addTextChangedListener { date ->
            if (!date.isNullOrEmpty() && date.contains(CardDateFormatter.DELIMITER)) {
                val dateArr = date.toString().split(CardDateFormatter.DELIMITER)
                presenter.cardMonth = dateArr[0]
                presenter.cardYear = dateArr[1]
            } else {
                presenter.cardMonth = ""
                presenter.cardYear = ""
            }
        }

        cardCVC.field_input.addTextChangedListener { presenter.cardCVC = it.toString() }
    }

    private fun setPrice(price: String) {
        paidSum.text = price
    }

    override fun setCardNumberLength(length: Int) {
        cardNumber.setMaxLength(length)
    }

    override fun setCVCLength(length: Int) {
        cardCVC.setMaxLength(length)
        val cvcText = cardCVC.text
        if (cvcText.length > length) cardCVC.text = cvcText.substring(0, length)
    }

    override fun setCardTypeIcon(iconResId: Int) {
        cardTypeIcon.setImageDrawable(if (iconResId == 0) null else ContextCompat.getDrawable(this, iconResId))
    }

    override fun redirectTo3ds(redirectUrl: String) {
        val web = WebView(this)
        web.loadUrl(redirectUrl)
        web.settings.javaScriptEnabled = true
        web.webViewClient = object : WebViewClient() {

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (view?.url == null) return true
                request?.url?.path?.let {
                    handleUri(it)
                }
                return false
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (view == null || url == null) return true
                Uri.parse(url).path?.let {
                    handleUri(it)
                }
                return false
            }
        }
        val matchParentParams = ViewGroup.LayoutParams.MATCH_PARENT
        web.layoutParams = ViewGroup.LayoutParams(matchParentParams, matchParentParams)
        webView.addView(web)
    }

    private fun handleUri(path: String) {
        when (path) {
            BaseCardPaymentPresenter.PAYMENT_RESULT_SUCCESSFUL -> changePaymentStatus(true)
            BaseCardPaymentPresenter.PAYMENT_RESULT_FAILED     -> changePaymentStatus(false)
        }
    }

    private fun changePaymentStatus(isSuccess: Boolean) {
        hidePaymentForm()
        presenter.changePaymentStatus(isSuccess)
    }

    override fun highLightErrorField(fields: List<Int>) {
        fields.forEach { field ->
            when (field) {
                CARD_NUMBER_ERROR -> cardNumber
                CARD_DATE_ERROR   -> cardDate
                CARD_CVC_ERROR    -> cardCVC
                else              -> null
            }?.showError()
        }
    }

    private fun hidePaymentForm() {
        webView.isVisible = false
        content.isVisible = false
    }

    override fun setError(e: ApiException) {
        Timber.e(e, "code: ${e.code}")
        Sentry.getContext().recordBreadcrumb(
            BreadcrumbBuilder().setMessage("Checkoutcom token error: ${e.details}").build())
        Sentry.capture(e)
        if (e.code == ApiException.NETWORK_ERROR) {
            Utils.showError(this, false, getString(R.string.LNG_NETWORK_ERROR))
        } else {
            Utils.showError(this, true, getString(R.string.LNG_UNEXPECTED_PAYMENT_ERROR))
        }
    }
}
