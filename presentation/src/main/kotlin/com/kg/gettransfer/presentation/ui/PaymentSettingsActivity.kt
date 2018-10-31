package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent

import android.os.Bundle
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.presentation.Screens

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PaymentRequestModel
import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.presenter.PaymentSettingsPresenter

import com.kg.gettransfer.presentation.view.PaymentSettingsView

import kotlinx.android.synthetic.main.activity_payment_settings.*

import org.koin.android.ext.android.inject

fun Context.getPaymentSettingsActivityLaunchIntent(): Intent {
    return Intent(this, PaymentSettingsActivity::class.java)
}

class PaymentSettingsActivity: BaseActivity(), PaymentSettingsView {

    @InjectPresenter
    internal lateinit var presenter: PaymentSettingsPresenter

    private val transferInteractor: TransferInteractor by inject()
    private val offerInteractor: OfferInteractor by inject()
    private val paymentInteractor: PaymentInteractor by inject()

    override fun getPresenter(): PaymentSettingsPresenter = presenter

    @ProvidePresenter
    fun createPaymentSettingsPresenter(): PaymentSettingsPresenter = 
        PaymentSettingsPresenter(coroutineContexts, router, systemInteractor, transferInteractor, offerInteractor, paymentInteractor)

    protected override var navigator = object: BaseNavigator(this) {
        override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
            when(screenKey) {
                Screens.PAYMENT -> return context.getPaymentActivityLaunchIntent(data as Bundle)
            }
            return null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_settings)
        val image = ImageSpan(this, R.drawable.credit_card)
        val string = SpannableString(getString(R.string.pay))
        var title = SpannableString(" $string")
        title.setSpan(image, 0, 1, 0)
        btnGetPayment.text = title
    }

    override fun setOffer(offer: OfferModel) {
        fullPrice.text = offer.price.base.default
        thirdOfPrice.text = getString(R.string.price_part_1, offer.price.percentage30)
        payFullPriceButton.setOnClickListener { view ->  changePaymentSettings(view) }
        payThirdOfPriceButton.setOnClickListener { view ->  changePaymentSettings(view) }
        btnGetPayment.setOnClickListener { presenter.getPayment() }
    }

    override fun setCommission(transfer: TransferModel) {
        if (transfer.refund_date != null) {
            commission.text = getString(R.string.commission, Utils.getFormattedDate(systemInteractor.locale, transfer.refund_date))
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
