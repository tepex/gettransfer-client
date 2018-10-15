package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.RequiresApi
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.interactor.PaymentInteractor
import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.presenter.PaymentPresenter
import com.kg.gettransfer.presentation.view.PaymentView
import kotlinx.android.synthetic.main.activity_payment.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import kotlin.reflect.jvm.internal.impl.renderer.ClassifierNamePolicy

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

    protected override var navigator = object: BaseNavigator(this) {
        @CallSuper
        protected override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
            val intent = super.createActivityIntent(context, screenKey, data)
            if(intent != null) return intent

            when(screenKey) {
                Screens.PASSENGER_MODE -> return Intent(context, RequestsActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            return null
        }
    }


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

    override fun setError(e: Throwable) {
        Timber.e(e)
        Utils.showError(this, true, getString(R.string.err_server, e.message))
    }

    override fun showMessage() {
        Toast.makeText(this, "Payment successful", Toast.LENGTH_SHORT).show()
    }
}