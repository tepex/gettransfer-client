package com.kg.gettransfer.presentation.ui

import android.annotation.TargetApi

import android.content.Context
import android.content.Intent

import android.net.Uri

import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.RequiresApi

import android.support.v7.widget.Toolbar

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

import android.widget.Toast

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.PaymentInteractor

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.presenter.PaymentPresenter
import com.kg.gettransfer.presentation.presenter.PaymentSettingsPresenter
import com.kg.gettransfer.presentation.view.PaymentView

import kotlinx.android.synthetic.main.activity_payment.*
import kotlinx.android.synthetic.main.toolbar.view.*

import org.koin.android.ext.android.inject

import timber.log.Timber

fun Context.getPaymentActivityLaunchIntent(paymentUrl: Bundle): Intent {
    var intent = Intent(this, PaymentActivity::class.java)
    intent.putExtra(PaymentActivity.PARAM_URL, paymentUrl.getString(PaymentSettingsPresenter.BUNDLE_KEY_URL))
    return intent
}

class PaymentActivity: BaseActivity(), PaymentView {
    companion object {
        private const val PAYMENT_RESULT_SUCCESSFUL = "/api/payments/successful"
        private const val PAYMENT_RESULT_FAILED = "/api/payments/failed"
        private const val SUCCESSFUL = "successful"
        private const val FAILED = "failed"
        private const val PG_ORDER_ID = "pg_order_id"
        internal const val PARAM_URL = "url"
    }

    @InjectPresenter
    internal lateinit var presenter: PaymentPresenter

    private val paymentInteractor: PaymentInteractor by inject()
    private val offerInteractor: OfferInteractor by inject()

    private lateinit var url: String
    private var price: Int = 0

    override fun getPresenter(): PaymentPresenter = presenter

    @ProvidePresenter
    fun createPaymentPresenter(): PaymentPresenter = PaymentPresenter(coroutineContexts, router, systemInteractor, paymentInteractor, offerInteractor)

    protected override var navigator = object: BaseNavigator(this) {
        @CallSuper
        protected override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
            val intent = super.createActivityIntent(context, screenKey, data)
            if(intent != null) return intent

            when(screenKey) {
                Screens.PASSENGER_MODE -> return Intent(context, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            return null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }

        webView.settings.javaScriptEnabled = true
        getBundleValues(intent.extras!!)
        webView.webViewClient = object: WebViewClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if(view != null && view.url != null) {
                    handleUri(request!!.url)
                    return false
                }
                return true
            }

            // for pre-lollipop
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if(view != null && url != null) {
                    handleUri(Uri.parse(url))
                    return false
                }
                return true
            }
        }
        webView.loadUrl(intent.getStringExtra(PARAM_URL))
    }

    private fun getBundleValues(bundle: Bundle) {
        url = bundle.getString(PaymentSettingsPresenter.BUNDLE_KEY_URL)!!
        price = bundle.getInt(PaymentSettingsPresenter.BUNDLE_KEY_PRICE)
    }

    private fun handleUri(uri: Uri?) {
        val path = uri?.path
        if(path.equals(PAYMENT_RESULT_SUCCESSFUL)) {
            initDataForAnalytics()
            changePaymentStatus(uri, true)
        }
        else if(path.equals(PAYMENT_RESULT_FAILED)) changePaymentStatus(uri, false)
    }

    private fun initDataForAnalytics() {
        presenter.price = price
    }

    private fun changePaymentStatus(uri: Uri?, success: Boolean) {
        val orderId = uri?.getQueryParameter(PG_ORDER_ID)!!.toLong()
        presenter.changePaymentStatus(orderId, success)
    }

    override fun setError(e: Throwable) {
        Timber.e(e)
        Utils.showError(this, true, getString(R.string.err_server, e.message))
    }

    override fun showSuccessfulMessage() {
        Utils.showShortToast(this, getString(R.string.payment_successful))
    }

    override fun showErrorMessage() {
        Utils.showShortToast(this, getString(R.string.payment_failed))
    }
}
