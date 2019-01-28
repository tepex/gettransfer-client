package com.kg.gettransfer.presentation.ui

import android.annotation.TargetApi

import android.net.Uri

import android.os.Build
import android.os.Bundle

import android.support.v7.widget.Toolbar

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.setUserAgent
import com.kg.gettransfer.presentation.model.OfferModel

import com.kg.gettransfer.presentation.presenter.PaymentPresenter

import com.kg.gettransfer.presentation.view.PaymentView

import kotlinx.android.synthetic.main.activity_payment.*
import timber.log.Timber

class PaymentActivity: BaseActivity(), PaymentView {
    companion object {
        private const val PAYMENT_RESULT_SUCCESSFUL = "/api/payments/successful"
        private const val PAYMENT_RESULT_FAILED = "/api/payments/failed"
        private const val PG_ORDER_ID = "pg_order_id"
    }

    @InjectPresenter
    internal lateinit var presenter: PaymentPresenter

    override fun getPresenter(): PaymentPresenter = presenter

    @ProvidePresenter
    fun createPaymentPresenter() = PaymentPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.transferId = intent.getLongExtra(PaymentView.EXTRA_TRANSFER_ID, 0)
        presenter.offerId    = intent.getLongExtra(PaymentView.EXTRA_OFFER_ID, 0)
        presenter.percentage = intent.getIntExtra(PaymentView.EXTRA_PERCENTAGE, 0)
        presenter.bookNowTransportId = intent.getStringExtra(PaymentView.EXTRA_BOOK_NOW_TRANSPORT_ID)

        setContentView(R.layout.activity_payment)

        setToolbar(toolbar as Toolbar, R.string.LNG_PAYMENT)
        webView.settings.javaScriptEnabled = true
        webView.setUserAgent()
        webView.webViewClient = object: WebViewClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if(view?.url == null) return true
                handleUri(request!!.url)
                return false
            }

            // for pre-lollipop
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if(view == null || url == null) return true
                handleUri(Uri.parse(url))
                return false
            }
        }

        webView.loadUrl(intent.getStringExtra(PaymentView.EXTRA_URL))
    }

    private fun handleUri(uri: Uri?) {
        val path = uri?.path
        if(path.equals(PAYMENT_RESULT_SUCCESSFUL)) changePaymentStatus(uri, true)
        else if(path.equals(PAYMENT_RESULT_FAILED)) changePaymentStatus(uri, false)
    }

    private fun changePaymentStatus(uri: Uri?, success: Boolean) {
        val orderId = uri?.getQueryParameter(PG_ORDER_ID)!!.toLong()
        presenter.changePaymentStatus(orderId, success)
    }
}
