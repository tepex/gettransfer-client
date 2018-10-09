package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent

import android.os.Bundle
import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.PaymentInteractor

import com.kg.gettransfer.presentation.Screens

import com.kg.gettransfer.presentation.model.OfferModel

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

    private val offerInteractor: OfferInteractor by inject()
    private val paymentInteractor: PaymentInteractor by inject()

    override fun getPresenter(): PaymentSettingsPresenter = presenter

    @ProvidePresenter
    fun createPaymentSettingsPresenter(): PaymentSettingsPresenter = 
        PaymentSettingsPresenter(coroutineContexts,
                                 router,
                                 systemInteractor,
                                 offerInteractor,
                                 paymentInteractor)

    protected override var navigator = object : BaseNavigator(this) {
        override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
            return if(screenKey == Screens.PAYMENT) context.getPaymentActivityLaunchIntent(data as String) else null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_settings)
    }

    override fun setOffer(offer: OfferModel) {
        fullPrice.text = offer.priceDefault
        thirdOfPrice.text = getString(R.string.price_part_1, offer.pricePercentage30)
        payFullPriceButton.setOnClickListener { view ->  changePaymentSettings(view) }
        payThirdOfPriceButton.setOnClickListener { view ->  changePaymentSettings(view) }
        btnGetPayment.setOnClickListener { presenter.getPayment() }
    }

    private fun changePaymentSettings(view: View?) {
        when(view?.id) {
            R.id.payFullPriceButton -> {
                fullPriceCheckIcon.visibility = View.VISIBLE
                thirdOfPriceCheckIcon.visibility = View.GONE
                presenter.changePrice(PaymentSettingsPresenter.FULL_PRICE)
            }
            R.id.payThirdOfPriceButton -> {
                thirdOfPriceCheckIcon.visibility = View.VISIBLE
                fullPriceCheckIcon.visibility = View.GONE
                presenter.changePrice(PaymentSettingsPresenter.PRICE_30)
            }
        }
    }

}