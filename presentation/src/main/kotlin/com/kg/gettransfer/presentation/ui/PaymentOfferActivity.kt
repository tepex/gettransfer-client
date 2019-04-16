package com.kg.gettransfer.presentation.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle

import android.support.v7.widget.Toolbar

import android.text.SpannableString
import android.text.style.ImageSpan

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
import com.kg.gettransfer.domain.model.BookNowOffer
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentRequestModel

import com.kg.gettransfer.presentation.presenter.PaymentOfferPresenter

import com.kg.gettransfer.presentation.view.PaymentOfferView

import kotlinx.android.synthetic.main.activity_payment_offer_new.*

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

        setContentView(R.layout.activity_payment_offer_new)
        setButton()

        setToolbar(toolbar as Toolbar, R.string.LNG_PAYMENT_SETTINGS)
        tv_payment_agreement.setOnClickListener { presenter.onAgreementClicked() }
        btnGetPayment.setOnClickListener { presenter.getPayment() }
        rbCard.setOnClickListener { changePayment(it, PaymentRequestModel.PLATRON) }
        rbPaypal.setOnClickListener { changePayment(it, PaymentRequestModel.PAYPAL) }
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

    private fun setButton() {
        val image = ImageSpan(this, R.drawable.credit_card)
        val string = SpannableString(getString(R.string.LNG_PAYMENT_PAY))
        val title = SpannableString("  $string")
        title.setSpan(image, 0, 1, 0)
        btnGetPayment.text = title
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
        setPriceInfo(offer.price.base.preferred, offer.price.base.def)
    }

    private fun setPriceInfo(preferredPrice: String?, default: String?) {
        tvPriceInfo.text = getString(R.string.LNG_RIDE_PAY_CHARGE, preferredPrice, default)
    }

    override fun setBookNowOffer(bookNowOffer: BookNowOffer?) {
        hidePaymentPercentage()
        setPriceInfo(bookNowOffer?.base?.preferred, bookNowOffer?.base?.def)
    }

    private fun hidePaymentPercentage() {
        groupPrice.isVisible = false
    }

    override fun showOfferError() {
        toast(getString(R.string.LNG_RIDE_OFFER_CANCELLED))
    }

    override fun setCommission(paymentCommission: String) {
        presenter.params.dateRefund?.let {
            tvCommission.text = getString(R.string.LNG_PAYMENT_COMISSION2, paymentCommission, SystemUtils.formatDateTime(it))
        }
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
}
