package com.kg.gettransfer.presentation.ui

import android.annotation.TargetApi
import android.app.DownloadManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.CallSuper

import android.webkit.WebViewClient
import android.webkit.WebView
import android.webkit.WebResourceRequest
import android.webkit.URLUtil

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.extensions.setUserAgent
import com.kg.gettransfer.presentation.presenter.HandleUrlPresenter
import com.kg.gettransfer.presentation.view.HandleUrlView
import com.kg.gettransfer.presentation.view.BaseHandleUrlView.Companion.RC_WRITE_FILE

import com.kg.gettransfer.utilities.GTDownloadManager.Companion.VOUCHERS_FOLDER

import kotlinx.android.synthetic.main.activity_handle_url.*

import org.jetbrains.anko.longToast

import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

import java.io.File

@Suppress("TooManyFunctions")
class HandleUrlActivity : BaseActivity(),
    HandleUrlView,
    EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    @InjectPresenter
    internal lateinit var presenter: HandleUrlPresenter

    override fun getPresenter(): HandleUrlPresenter = presenter

    @ProvidePresenter
    fun createHandleUrlPresenter() = HandleUrlPresenter()

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_handle_url)
        if (intent?.action == Intent.ACTION_VIEW) {
            intent.data?.let { presenter.handleIntent(it) }
        }
    }

    @CallSuper
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.action == Intent.ACTION_VIEW) {
            intent.data?.let { presenter.handleIntent(it) }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        onPermissionDenied()
    }

    @Suppress("EmptyFunctionBlock")
    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRationaleDenied(requestCode: Int) {
        onPermissionDenied()
    }

    override fun onRationaleAccepted(requestCode: Int) {}

    override fun setError(e: ApiException) {
        longToast(e.details)
        finish()
    }

    @CallSuper
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(RC_WRITE_FILE)
    override fun downloadVoucher() {
        val perms = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            presenter.openMainScreen()
            downloadVoucher(presenter.url)
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER_QUESTION),
                RC_WRITE_FILE, *perms
            )
        }
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
            setDestinationInExternalPublicDir(folderName, fileName)
        }
        val dm = getSystemService(DOWNLOAD_SERVICE)
        if (dm is DownloadManager) {
            dm.enqueue(request)
            longToast(getString(R.string.LNG_DOWNLOADING))
        }
    }

    private fun getVouchersFolderName() = getString(R.string.app_name) + File.separator + VOUCHERS_FOLDER

    private fun onPermissionDenied() {
        presenter.openMainScreen()
        longToast(getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER_ACCESS))
    }

    override fun showWebView(url: String) {
        splashLayout.isVisible = false
        webView.settings.javaScriptEnabled = true
        webView.setUserAgent()
        webView.webViewClient = object : WebViewClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
            }

            // for pre-lollipop
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                view?.loadUrl(url)
                return true
            }
        }
        webView.loadUrl(url)
    }
}
