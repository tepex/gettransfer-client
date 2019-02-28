package com.kg.gettransfer.presentation.ui

import android.annotation.TargetApi
import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.webkit.*
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.presentation.presenter.HandleUrlPresenter
import com.kg.gettransfer.presentation.view.HandleUrlView
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.extensions.setUserAgent
import kotlinx.android.synthetic.main.activity_handle_url.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class HandleUrlActivity : BaseActivity(), HandleUrlView, EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks {

    @InjectPresenter
    internal lateinit var presenter: HandleUrlPresenter

    override fun getPresenter(): HandleUrlPresenter = presenter

    @ProvidePresenter
    fun createHandleUrlPresenter() = HandleUrlPresenter()

    private lateinit var url: String

    companion object {
        const val RC_WRITE_FILE = 111
        const val PASSENGER_CABINET = "/passenger/cabinet"
        const val PASSENGER_RATE = "/passenger/rate"
        const val CARRIER_CABINET = "/carrier/cabinet"
        const val VOUCHER = "/transfers/voucher"
        const val CHOOSE_OFFER_ID = "choose_offer_id"
        const val OPEN_CHAT = "open_chat"
        const val TRANSFERS = "transfers"
        const val SLASH = "/"
        const val EQUAL = "="
        const val QUESTION = "?"
        const val RATE = "rate_val"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handle_url)
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val appLinkAction = intent?.action
        val appLinkData : Uri? = intent?.data
        url = appLinkData.toString()
        if (Intent.ACTION_VIEW == appLinkAction) {
            val path = appLinkData?.path
            when {
                path.equals(PASSENGER_CABINET) -> appLinkData?.fragment?.let {
                    if (it.startsWith(TRANSFERS)) {
                        if (it.contains(CHOOSE_OFFER_ID)) {
                            val transferId = it.substring(it.indexOf(SLASH) + 1, it.indexOf(QUESTION)).toLong()
                            val offerId = it.substring(it.lastIndexOf(EQUAL) + 1, it.length).toLong()
                            presenter.openOffer(transferId, offerId)
                            return
                        } else if (it.contains(OPEN_CHAT)) {
                            val chatId = it.substring(it.indexOf(SLASH) + 1, it.indexOf(QUESTION))
                            presenter.openChat(chatId)
                            return
                        }
                        val transferId = it.substring(it.indexOf(SLASH) + 1).toLong()
                        presenter.openTransfer(transferId)
                        return
                    }
                }
                path?.startsWith(PASSENGER_RATE)!! -> {
                    val transferId = appLinkData.lastPathSegment?.toLong()
                    val rate = appLinkData.getQueryParameter(RATE)?.toInt()
                    presenter.rateTransfer(transferId!!, rate!!)
                    return
                }
                path.contains(VOUCHER) -> {
                    checkPermissionForWrite()
                }
                else -> showWebView(url)
            }
        }
    }

    @AfterPermissionGranted(RC_WRITE_FILE)
    private fun checkPermissionForWrite() {
        val perms = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            presenter.openMainScreen()
            showWebView(url)
        } else EasyPermissions.requestPermissions(
                this,
                "Allow to download the voucher?",
                RC_WRITE_FILE, *perms)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        presenter.openMainScreen()
        toast("Allow access to download the voucher")
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRationaleDenied(requestCode: Int) {
        presenter.openMainScreen()
        toast("Allow access to download the voucher")
    }

    override fun onRationaleAccepted(requestCode: Int) {}

    private fun showWebView(url: String) {
        webView.settings.javaScriptEnabled = true
        webView.setUserAgent()
        webView.webViewClient = object: WebViewClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
            }

            // for pre-lollipop
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true;            }
        }
        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            val request = DownloadManager.Request(Uri.parse(url)).apply {
                allowScanningByMediaScanner()
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        URLUtil.guessFileName(url, contentDisposition, mimetype))
            }
            val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            dm.enqueue(request)
            longToast("Downloading voucher")
        }
        webView.loadUrl(url)
    }

    override fun setError(e: ApiException) {
        longToast(e.details)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}