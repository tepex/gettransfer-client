package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import androidx.annotation.CallSuper
import com.android.volley.VolleyError
import com.checkout.android_sdk.PaymentForm
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.CheckoutPaymentPresenter
import com.kg.gettransfer.presentation.view.CheckoutPaymentView
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import com.checkout.android_sdk.Response.CardTokenisationFail
import com.checkout.android_sdk.Response.CardTokenisationResponse
import com.checkout.android_sdk.PaymentForm.PaymentFormCallback
import com.checkout.android_sdk.Utils.Environment
import kotlinx.android.synthetic.main.activity_checkout_payment.*

class CheckoutPaymentActivity: BaseActivity(), CheckoutPaymentView {

    @InjectPresenter
    internal lateinit var presenter: CheckoutPaymentPresenter

    override fun getPresenter(): CheckoutPaymentPresenter = presenter

    @ProvidePresenter
    fun createPaymentPresenter() = CheckoutPaymentPresenter()

    private lateinit var paymentForm: PaymentForm

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout_payment)
        paymentForm = checkout_card_form
        presenter.paymentId = intent.getLongExtra(CheckoutPaymentView.EXTRA_PAYMENT_ID, 0L)
    }

    override fun initPaymentForm(environment: Environment, publicKey: String) {
        paymentForm.mSubmitFormListener = object : PaymentFormCallback {
            override fun onFormSubmit() {
                // form submit initiated; you can potentially display a loader
            }

            override fun onTokenGenerated(response: CardTokenisationResponse) {
                // your token is here: response.token
                presenter.onTokenGenerated(response.token)
                paymentForm.clearForm() // this clears the Payment Form
            }

            override fun onError(response: CardTokenisationFail) {
                // token request error
            }

            override fun onNetworkError(error: VolleyError) {
                // network error
            }

            override fun onBackPressed() {
                // the user decided to leave the payment page
                paymentForm.clearForm() // this clears the Payment Form
            }
        }
        paymentForm.setEnvironment(environment)
        paymentForm.setKey(publicKey)
        paymentForm.includeBilling(false)

        }

    override fun redirectTo3ds(redirectUrl: String, successUrl: String, failedUrl: String) {
        paymentForm.set3DSListener(object: PaymentForm.On3DSFinished {
            override fun onSuccess(token: String) {
               presenter.handle3DS(true)
            }

            override fun onError(errorMessage: String) {
                presenter.handle3DS(false)
            }
        })
        paymentForm.handle3DS(redirectUrl, successUrl, failedUrl)
    }
}