package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.arellomobile.mvp.MvpAppCompatActivity
import com.kg.gettransfer.R
import kotlinx.android.synthetic.main.activity_payment.*

fun Context.getPaymentActivityLaunchIntent(paymentUrl: String): Intent {
    var intent = Intent(this, PaymentActivity::class.java)
    intent.putExtra("url", paymentUrl)
    return intent
}

class PaymentActivity: MvpAppCompatActivity() {

    private val PAYMENT_RESULT = "payment_result"
    private val SUCCESS = "success"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (view != null && view.url != null) {
                    val uri = Uri.parse(view.url)
                    val result = uri.getQueryParameter(PAYMENT_RESULT)
                    if (result != null && result == SUCCESS) {
                        //send request
                    }
                }
                return super.shouldOverrideUrlLoading(view, request)
            }

        }

        webView.loadUrl(intent.getStringExtra("url"))
    }

}