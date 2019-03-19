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

import com.kg.gettransfer.presentation.presenter.PaymentOfferPresenter

import com.kg.gettransfer.presentation.view.PaymentOfferView

import kotlinx.android.synthetic.main.activity_payment_offer.*

import kotlinx.serialization.json.JSON
import org.jetbrains.anko.toast

class PaymentOfferActivity : BaseActivity(), PaymentOfferView {
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
        setButton()

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
        if (paymentPercentages.isNotEmpty()) {
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
            selectPaymentPercentage(selectedPercentage)
        }
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

    override fun setCommission(paymentCommission: String) {
        presenter.params.dateRefund?.let {
            commission.text = getString(R.string.LNG_PAYMENT_COMISSION2, paymentCommission, SystemUtils.formatDateTime(it))
        }
    }

    private fun changePaymentSettings(view: View?) {
        when (view?.id) {
            R.id.payFullPriceButton    -> selectPaymentPercentage(PaymentRequestModel.FULL_PRICE)
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
        this.selectedPercentage = selectedPercentage
    }
}
