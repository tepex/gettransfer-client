package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.presentation.presenter.PaymentPresenter
import com.kg.gettransfer.presentation.view.PaymentView
import kotlinx.android.synthetic.main.activity_payment.*
import org.koin.android.ext.android.inject

fun Context.getPaymentActivityLaunchIntent(paymentUrl: String): Intent {
    var intent = Intent(this, PaymentActivity::class.java)
    intent.putExtra("url", paymentUrl)
    return intent
}

class PaymentActivity: BaseActivity(), PaymentView {

    private val PAYMENT_RESULT = "payment_result"
    private val SUCCESS = "success"

    @InjectPresenter
    internal lateinit var presenter: PaymentPresenter

    private val paymentInteractor: PaymentInteractor by inject()

    override fun getPresenter(): PaymentPresenter = presenter

    @ProvidePresenter
    fun createPaymentPresenter(): PaymentPresenter = PaymentPresenter(coroutineContexts, router, systemInteractor, paymentInteractor)

    protected override var navigator = BaseNavigator(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {

            @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (view != null && view.url != null) {
                    val uri = request?.url
                    val path = uri?.path
                    if (path.equals("/api/payments/successful")) {
                        val orderId = uri?.getQueryParameter("pg_order_id")!!.toLong()
                        presenter.changeStatusPayment(orderId, "successful")
                    }
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

        }

        webView.loadUrl(intent.getStringExtra("url"))
    }

}