package com.kg.gettransfer.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.annotation.CallSuper

import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.extensions.setStatusBarColor
import com.kg.gettransfer.presentation.presenter.HandleUrlPresenter
import com.kg.gettransfer.presentation.view.OpenDeepLinkScreenView

import org.jetbrains.anko.longToast

import pub.devrel.easypermissions.EasyPermissions

@Suppress("TooManyFunctions")
class HandleUrlActivity : BaseActivity(),
    OpenDeepLinkScreenView,
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
        setStatusBarColor(R.color.colorPrimary)
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

    override fun setError(e: ApiException) {
        longToast(e.details)
        finish()
    }

    override fun downloadVoucher() {
        val perms = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            presenter.downloadVoucher()
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER_QUESTION),
                RC_WRITE_FILE, *perms
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        longToast(getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER_ACCESS))
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        presenter.downloadVoucher()
    }

    override fun onRationaleDenied(requestCode: Int) {
        onPermissionDenied()
    }

    override fun onRationaleAccepted(requestCode: Int) {}

    @CallSuper
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun onPermissionDenied() {
        presenter.openMainScreen()
        longToast(getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER_ACCESS))
    }

    companion object {
        const val RC_WRITE_FILE = 111
    }
}
