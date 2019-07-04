package com.kg.gettransfer.presentation.ui

import android.annotation.TargetApi
import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.*
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.presentation.presenter.HandleUrlPresenter
import com.kg.gettransfer.presentation.view.HandleUrlView
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.extensions.setUserAgent
import kotlinx.android.synthetic.main.activity_handle_url.*
import org.jetbrains.anko.longToast
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File

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
        private const val VOUCHERS_FOLDER = "Vouchers"
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
                            val transferId = it.substring(it.indexOf(SLASH) + 1, it.indexOf(QUESTION)).toLongOrNull()
                            val offerId = it.substring(it.lastIndexOf(EQUAL) + 1, it.length).toLongOrNull()
                            var bookNowTransportId: String? = null
                            if (offerId == null) bookNowTransportId = it.substring(it.lastIndexOf(EQUAL) + 1, it.length)

                            transferId?.let { id -> presenter.openOffer(id, offerId, bookNowTransportId) }
                            return
                        } else if (it.contains(OPEN_CHAT)) {
                            val chatId = it.substring(it.indexOf(SLASH) + 1, it.indexOf(QUESTION))
                            presenter.openChat(chatId)
                            return
                        }
                        val transferId = it.substring(it.indexOf(SLASH) + 1).toLongOrNull()
                        transferId?.let { id -> presenter.openTransfer(id) }
                        return
                    }
                }
                path?.startsWith(PASSENGER_RATE)!! -> {
                    val transferId = appLinkData.lastPathSegment?.toLongOrNull()
                    val rate = appLinkData.getQueryParameter(RATE)?.toIntOrNull()
                    if (transferId != null && rate != null) presenter.rateTransfer(transferId, rate)
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
            downloadVoucher(url)
        } else EasyPermissions.requestPermissions(
                this,
                getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER_QUESTION),
                RC_WRITE_FILE, *perms)
    }

    private fun downloadVoucher(url: String) {
        webView.loadUrl(url)
        webView.setDownloadListener { _, _, contentDisposition, mimetype, _ ->
            setupDownloadManager(url, contentDisposition, mimetype)
        }
    }

    private fun setupDownloadManager(url: String, contentDisposition: String?, mimeType: String?) {
        val folderName = getVouchersFolderName()
        val fileName = URLUtil.guessFileName(url, contentDisposition, mimeType)

        val request = DownloadManager.Request(Uri.parse(url)).apply {
            allowScanningByMediaScanner()
            setMimeType(mimeType)
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setDestinationInExternalPublicDir(
                    folderName,
                    fileName)
        }
        val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        dm.enqueue(request)
        longToast(getString(R.string.LNG_DOWNLOADING))
    }

    private fun getVouchersFolderName(): String
            = getString(R.string.app_name) + File.separator + VOUCHERS_FOLDER


    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        onPermissionDenied()
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRationaleDenied(requestCode: Int) {
        onPermissionDenied()
    }

    private fun onPermissionDenied() {
        presenter.openMainScreen()
        longToast(getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER_ACCESS))
    }

    override fun onRationaleAccepted(requestCode: Int) {}

    private fun showWebView(url: String) {
        splashLayout.isVisible = false
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