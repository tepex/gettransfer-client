package com.kg.gettransfer.presentation.ui

import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.presentation.presenter.BaseLogInPresenter
import com.kg.gettransfer.presentation.view.BaseHandleUrlView
import com.kg.gettransfer.presentation.view.OpenDeepLinkScreenView
import io.sentry.Sentry
import io.sentry.event.BreadcrumbBuilder
import moxy.MvpAppCompatFragment
import org.jetbrains.anko.longToast
import pub.devrel.easypermissions.EasyPermissions

abstract class BaseLogInFragment : MvpAppCompatFragment(),
    OpenDeepLinkScreenView,
    EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    abstract fun getPresenter(): BaseLogInPresenter<*>

    // loading fragment

    private val loadingFragment by lazy { LoadingFragment() }

    private fun showLoading() {
        if (loadingFragment.isAdded) return
        fragmentManager?.beginTransaction()?.apply {
            replace(android.R.id.content, loadingFragment)
            commit()
        }
    }

    private fun hideLoading() {
        if (!loadingFragment.isAdded) return
        fragmentManager?.beginTransaction()?.apply {
            remove(loadingFragment)
            commit()
        }
    }

    // for downloading voucher

    override fun downloadVoucher() {
        val perms = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(requireContext(), *perms)) {
            getPresenter().downloadVoucher()
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER_QUESTION),
                BaseHandleUrlView.RC_WRITE_FILE, *perms
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        requireActivity().longToast(getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER_ACCESS))
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        getPresenter().downloadVoucher()
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
        getPresenter().openMainScreen()
        requireActivity().longToast(getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER_ACCESS))
    }

    // errors

    override fun setTransferNotFoundError(transferId: Long, dismissCallBack: (() -> Unit)?) {
        BottomSheetDialog
            .newInstance()
            .apply {
                imageId = R.drawable.transfer_error
                title = this@BaseLogInFragment.getString(R.string.LNG_ERROR)
                text = this@BaseLogInFragment.getString(R.string.LNG_TRANSFER_NOT_FOUND, transferId.toString())
                isShowCloseButton = true
                isShowOkButton = false
                dismissCallBack?.let { onDismissCallBack = it }
            }
            .show(requireFragmentManager())
    }

    override fun setError(e: ApiException) {}

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {}

    override fun setError(e: DatabaseException) {
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage("CacheError: ${e.details}").build())
        Sentry.capture(e)
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        if (block) {
            if (useSpinner) showLoading()
        } else hideLoading()
    }
}
