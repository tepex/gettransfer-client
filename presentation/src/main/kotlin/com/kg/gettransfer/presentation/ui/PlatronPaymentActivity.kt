package com.kg.gettransfer.presentation.ui

import android.annotation.TargetApi
import android.graphics.Bitmap

import android.net.Uri

import android.os.Build
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.webkit.SafeBrowsingResponse
import android.webkit.WebResourceRequest
import android.webkit.WebViewClient
import android.webkit.WebView

import androidx.appcompat.widget.Toolbar

import androidx.webkit.WebViewCompat
import androidx.webkit.WebViewFeature

import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.extensions.setUserAgent
import com.kg.gettransfer.presentation.presenter.BaseCardPaymentPresenter

import com.kg.gettransfer.presentation.presenter.PlatronPaymentPresenter

import com.kg.gettransfer.presentation.view.PlatronPaymentView

import kotlinx.android.synthetic.main.activity_platron_payment.*
import kotlinx.android.synthetic.main.activity_platron_payment.spinner
import org.jetbrains.anko.toast
import timber.log.Timber

class PlatronPaymentActivity : BaseActivity(), PlatronPaymentView {

    @InjectPresenter
    internal lateinit var presenter: PlatronPaymentPresenter

    private var safeBrowsingIsInitialized: Boolean = false

    override fun getPresenter(): PlatronPaymentPresenter = presenter

    @ProvidePresenter
    fun createPaymentPresenter() = PlatronPaymentPresenter()

    @Suppress("UnsafeCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_platron_payment)

        hideKeyboard()

        setToolbar(toolbar as Toolbar, R.string.LNG_PAYMENT)
        webView.settings.javaScriptEnabled = true
        webView.setUserAgent()
        webView.webViewClient = object : WebViewClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                if (view?.url == null) return true
                handleUri(request?.url)
                return false
            }

            // for pre-lollipop
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (view == null || url == null) return true
                handleUri(Uri.parse(url))
                return false
            }

            override fun onSafeBrowsingHit(
                view: WebView?,
                request: WebResourceRequest?,
                threatType: Int,
                callback: SafeBrowsingResponse?
            ) {
                // The "true" argument indicates that your app reports incidents like
                // this one to Safe Browsing.
                if (WebViewFeature.isFeatureSupported(
                        WebViewFeature.SAFE_BROWSING_RESPONSE_BACK_TO_SAFETY)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                        callback?.backToSafety(true)
                        toast("Unsafe web page blocked.")
                    }
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                showSpinner()
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                hideSpinner()
                super.onPageFinished(view, url)
            }
        }
        safeBrowsingIsInitialized = false
        checkSafeBrowsing()
        webView.loadUrl(intent.getStringExtra(PlatronPaymentView.EXTRA_URL))
    }

    private fun hideSpinner() {
        spinner.clearAnimation()
        spinner.isVisible = false
    }

    private fun showSpinner() {
        val anim = RotateAnimation(
            FROM_DEGREES,
            TO_DEGREES,
            Animation.RELATIVE_TO_SELF,
            PIVOT_X_VALUE,
            Animation.RELATIVE_TO_SELF,
            PIVOT_Y_VALUE)

        anim.duration = ANIM_DURATION
        anim.repeatCount = Animation.INFINITE
        spinner.isVisible = true
        spinner.startAnimation(anim)
    }

    private fun checkSafeBrowsing() {
        if (WebViewFeature.isFeatureSupported(WebViewFeature.START_SAFE_BROWSING)) {
            WebViewCompat.startSafeBrowsing(this) { success ->
                safeBrowsingIsInitialized = true
                if (!success) {
                    Timber.e("Unable to initialize Safe Browsing!")
                }
            }
        }
    }

    private fun handleUri(handledUri: Uri?) {
        handledUri?.let { uri ->
            when (uri.path) {
                BaseCardPaymentPresenter.PAYMENT_RESULT_SUCCESSFUL -> changePaymentStatus(uri, true)
                BaseCardPaymentPresenter.PAYMENT_RESULT_FAILED     -> changePaymentStatus(uri, false)
            }
        }
    }

    private fun changePaymentStatus(uri: Uri?, success: Boolean) {
        uri?.getQueryParameter(PG_ORDER_ID)?.toLong()?.let { paymentId ->
            presenter.paymentId = paymentId
            presenter.changePaymentStatus(success, uri.getQueryParameter(PG_FAILURE_DESCRIPTION))
        }
    }

    companion object {
        private const val PG_ORDER_ID            = "pg_order_id"
        private const val PG_FAILURE_DESCRIPTION = "pg_failure_description"

        private const val FROM_DEGREES = 0f
        private const val TO_DEGREES = 360f
        private const val PIVOT_X_VALUE = 0.5f
        private const val PIVOT_Y_VALUE = 0.5f
        private const val ANIM_DURATION = 1500L
    }
}
