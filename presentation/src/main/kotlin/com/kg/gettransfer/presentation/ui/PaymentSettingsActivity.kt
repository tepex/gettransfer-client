package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.v7.widget.Toolbar

import android.text.SpannableString
import android.text.style.ImageSpan

import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.BookNowOffer
import com.kg.gettransfer.extensions.*
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentRequestModel

import com.kg.gettransfer.presentation.presenter.PaymentSettingsPresenter

import com.kg.gettransfer.presentation.view.PaymentSettingsView

import kotlinx.android.synthetic.main.activity_payment_settings.*

import kotlinx.serialization.json.JSON
import org.jetbrains.anko.toast

class PaymentSettingsActivity : BaseActivity(), PaymentSettingsView {
    @InjectPresenter
    internal lateinit var presenter: PaymentSettingsPresenter

    override fun getPresenter(): PaymentSettingsPresenter = presenter

    @ProvidePresenter
    fun createPaymentSettingsPresenter() = PaymentSettingsPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.params = JSON.parse(PaymentSettingsView.Params.serializer(), intent.getStringExtra(PaymentSettingsView.EXTRA_PARAMS))

        setContentView(R.layout.activity_payment_settings)
        setButton()
        setCommission()

        setToolbar(toolbar as Toolbar, R.string.LNG_PAYMENT_SETTINGS)
        tv_payment_agreement.setOnClickListener { presenter.onAgreementClicked() }
        btnGetPayment.setOnClickListener { presenter.getPayment() }
    }

    private fun setButton() {
        val image = ImageSpan(this, R.drawable.credit_card)
        val string = SpannableString(getString(R.string.LNG_PAYMENT_PAY))
        val title = SpannableString("  $string")
        title.setSpan(image, 0, 1, 0)
        btnGetPayment.text = title
    }

    override fun setOffer(offer: OfferModel, paymentPercentages: List<Int>) {
        paymentPercentages.forEach { percentage ->
            when (percentage) {
                OfferModel.FULL_PRICE -> {
                    payFullPriceButton.isVisible = true
                    payFullPriceTitle.text = getString(R.string.LNG_PAYMENT_TERM_NOW, OfferModel.FULL_PRICE)
                    fullPrice.text = offer.price.base.def
                    payFullPriceButton.setOnClickListener { changePaymentSettings(it) }
                }
                OfferModel.PRICE_30 -> {
                    payThirdOfPriceButton.isVisible = true
                    payThirdOfPriceTitle.text = getString(R.string.LNG_PAYMENT_TERM_NOW, OfferModel.PRICE_30)
                    thirdOfPrice.text = getString(R.string.LNG_PAYMENT_TERM_LATER, OfferModel.PRICE_70, offer.price.percentage30)
                    payThirdOfPriceButton.setOnClickListener { changePaymentSettings(it) }
                }
            }
        }
        selectPaymentPercentage(paymentPercentages.first())
    }

    override fun setBookNowOffer(bookNowOffer: BookNowOffer?) {
        payFullPriceButton.isVisible = true
        payFullPriceTitle.text = getString(R.string.LNG_PAYMENT_TERM_NOW, OfferModel.FULL_PRICE)
        fullPriceCheckIcon.isVisible = false
        payThirdOfPriceButton.isVisible = false
        fullPrice.text = bookNowOffer?.base?.def
    }

    override fun showOfferError() {
        toast(getString(R.string.LNG_RIDE_OFFER_CANCELLED))
    }

    private fun setCommission() {
        presenter.params.dateRefund?.let {
            commission.text = getString(R.string.LNG_PAYMENT_COMISSION, SystemUtils.formatDateTime(it))
        }
    }

    private fun changePaymentSettings(view: View?) {
        when (view?.id) {
            R.id.payFullPriceButton    -> PaymentRequestModel.FULL_PRICE
            R.id.payThirdOfPriceButton -> selectPaymentPercentage(PaymentRequestModel.PRICE_30)
        }
    }

    private fun selectPaymentPercentage(selectedPercentage: Int) {
        when (selectedPercentage) {
            PaymentRequestModel.FULL_PRICE -> {
                fullPriceCheckIcon.isVisible = true
                thirdOfPriceCheckIcon.isVisible = false
                presenter.changePrice(PaymentRequestModel.FULL_PRICE)
            }
            PaymentRequestModel.PRICE_30 -> {
                thirdOfPriceCheckIcon.isVisible = true
                fullPriceCheckIcon.isVisible = false
                presenter.changePrice(PaymentRequestModel.PRICE_30)
            }
        }
    }
}
