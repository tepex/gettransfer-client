package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import androidx.annotation.CallSuper
import com.android.volley.VolleyError
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.CheckoutcomPaymentPresenter
import com.kg.gettransfer.presentation.view.CheckoutcomPaymentView
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.checkout.android_sdk.Response.CardTokenisationFail
import com.checkout.android_sdk.Response.CardTokenisationResponse
import com.checkout.android_sdk.Utils.Environment
import com.kg.gettransfer.presentation.presenter.BaseCardPaymentPresenter
import com.kg.gettransfer.presentation.ui.custom.CheckoutcomPaymentForm
import kotlinx.android.synthetic.main.activity_checkout_payment.*

class CheckoutcomPaymentActivity : BaseActivity(), CheckoutcomPaymentView {

    @InjectPresenter
    internal lateinit var presenter: CheckoutcomPaymentPresenter

    override fun getPresenter(): CheckoutcomPaymentPresenter = presenter

    @ProvidePresenter
    fun createPaymentPresenter() = CheckoutcomPaymentPresenter()

    private lateinit var paymentForm: CheckoutcomPaymentForm

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout_payment)
        paymentForm = checkout_card_form
        presenter.paymentId = intent.getLongExtra(CheckoutcomPaymentView.EXTRA_PAYMENT_ID, 0L)
    }

    @Suppress("EmptyFunctionBlock")
    override fun initPaymentForm(environment: Environment, publicKey: String) {
        paymentForm.mSubmitFormListener = object : CheckoutcomPaymentForm.PaymentFormCallback {
            override fun onFormSubmit() {
                // form submit initiated; you can potentially display a loader
            }

            override fun onTokenGenerated(response: CardTokenisationResponse) {
                presenter.onTokenGenerated(response.token)
            }

            override fun onError(response: CardTokenisationFail) {
                setError(true, R.string.LNG_UNEXPECTED_PAYMENT_ERROR)
                errorToSentry("Checkoutcom token error", response.errorType)
            }

            override fun onNetworkError(error: VolleyError) {
                // network error
            }

            override fun onBackPressed() {
                presenter.onBackCommandClick()
            }
        }
        paymentForm.setEnvironment(environment)
        paymentForm.setKey(publicKey)
        paymentForm.includeBilling(false)
    }

    override fun redirectTo3ds(redirectUrl: String) {
        paymentForm.m3DSecureListener = object : CheckoutcomPaymentForm.On3DSFinished {
            override fun onSuccess() {
                presenter.changePaymentStatus(true)
            }

            override fun onError() {
                presenter.changePaymentStatus(false)
            }
        }
        paymentForm.handle3DS(
            redirectUrl,
            BaseCardPaymentPresenter.PAYMENT_RESULT_SUCCESSFUL,
            BaseCardPaymentPresenter.PAYMENT_RESULT_FAILED
        )
    }

    override fun clearForm() {
        paymentForm.clearForm()
    }

    override fun onBackPressed() {
        presenter.onBackCommandClick()
    }
}
