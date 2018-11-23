package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.v7.widget.Toolbar

import android.text.SpannableString
import android.text.style.ImageSpan

import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentRequestModel

import com.kg.gettransfer.presentation.presenter.PaymentSettingsPresenter

import com.kg.gettransfer.presentation.view.PaymentSettingsView

import kotlinx.android.synthetic.main.activity_payment_settings.*

import kotlinx.serialization.json.JSON

class PaymentSettingsActivity: BaseActivity(), PaymentSettingsView {
    @InjectPresenter
    internal lateinit var presenter: PaymentSettingsPresenter

    override fun getPresenter(): PaymentSettingsPresenter = presenter

    @ProvidePresenter
    fun createPaymentSettingsPresenter() = PaymentSettingsPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.params = JSON.parse(PaymentSettingsView.Params.serializer(), intent.getStringExtra(PaymentSettingsView.EXTRA_PARAMS))
        
        setContentView(R.layout.activity_payment_settings)
        payFullPriceTitle.text = getString(R.string.LNG_PAYMENT_TERM_NOW, 100)
        payThirdOfPriceTitle.text = getString(R.string.LNG_PAYMENT_TERM_NOW, 30)
        setButton()
        setCommission()

        setToolbar(toolbar as Toolbar, R.string.LNG_PAYMENT_SETTINGS)
    }

    private fun setButton() {
        val image = ImageSpan(this, R.drawable.credit_card)
        val string = SpannableString(getString(R.string.LNG_PAYMENT_PAY))
        val title = SpannableString("  $string")
        title.setSpan(image, 0, 1, 0)
        btnGetPayment.text = title
    }

    override fun setOffer(offer: OfferModel) {
        fullPrice.text = offer.price.base.default
        thirdOfPrice.text = getString(R.string.LNG_PAYMENT_TERM_LATER, OfferModel.PRICE_70, offer.price.percentage30)
        payFullPriceButton.setOnClickListener    { changePaymentSettings(it) }
        payThirdOfPriceButton.setOnClickListener { changePaymentSettings(it) }
        btnGetPayment.setOnClickListener { presenter.getPayment() }
    }

    private fun setCommission() {
        presenter.params.dateRefund?.let {
            commission.text = getString(R.string.LNG_PAYMENT_COMISSION, Utils.getFormattedDate(systemInteractor.locale, it))
        }
    }

    private fun changePaymentSettings(view: View?) {
        when(view?.id) {
            R.id.payFullPriceButton -> {
                fullPriceCheckIcon.visibility = View.VISIBLE
                thirdOfPriceCheckIcon.visibility = View.GONE
                presenter.changePrice(PaymentRequestModel.FULL_PRICE)
            }
            R.id.payThirdOfPriceButton -> {
                thirdOfPriceCheckIcon.visibility = View.VISIBLE
                fullPriceCheckIcon.visibility = View.GONE
                presenter.changePrice(PaymentRequestModel.PRICE_30)
            }
        }
    }
}
