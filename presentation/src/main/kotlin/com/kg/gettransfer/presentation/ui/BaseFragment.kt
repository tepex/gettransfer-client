package com.kg.gettransfer.presentation.ui

import androidx.annotation.StringRes
import com.arellomobile.mvp.MvpAppCompatFragment
import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.extensions.hideKeyboard
import com.kg.gettransfer.extensions.showKeyboard

import com.kg.gettransfer.presentation.view.BaseView
import io.sentry.Sentry
import io.sentry.event.BreadcrumbBuilder

import org.koin.core.KoinComponent
import timber.log.Timber

abstract class BaseFragment : MvpAppCompatFragment(), KoinComponent, BaseView {

    private val loadingFragment by lazy { LoadingFragment() }

    private fun showLoading() {
        if (!loadingFragment.isAdded) {
            childFragmentManager.beginTransaction().apply {
                replace(android.R.id.content, loadingFragment)
                commit()
            }
        }
    }

    private fun hideLoading() {
        if (loadingFragment.isAdded) {
            childFragmentManager.beginTransaction().apply {
                remove(loadingFragment)
                commit()
            }
        }
    }

    protected fun showKeyboard() {
        requireActivity().currentFocus?.showKeyboard()
    }

    protected fun hideKeyboard(): Boolean {
        requireActivity().currentFocus?.let { focus ->
            focus.hideKeyboard()
            focus.clearFocus()
        }
        return true
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        if (block) {
            if (useSpinner) showLoading()
        } else {
            hideLoading()
        }
    }

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        val errMessage = getString(errId, *args)
        Timber.e(RuntimeException(errMessage))
        Utils.showError(requireContext(), finish, errMessage)
    }

    override fun setError(e: ApiException) {
        Timber.e(e, "code: ${e.code}")
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage(e.details).build())
        Sentry.capture(e)
        if (e.code != ApiException.NETWORK_ERROR) {
            Utils.showError(
                requireContext(),
                false,
                getString(R.string.LNG_ERROR) + ": " + e.message
            )
        }
    }

    override fun setError(e: DatabaseException) {
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage("CacheError: ${e.details}").build())
        Sentry.capture(e)
    }

    override fun setTransferNotFoundError(transferId: Long, dismissCallBack: (() -> Unit)?) {
        BottomSheetDialog
            .newInstance()
            .apply {
                imageId = R.drawable.transfer_error
                title = getString(R.string.LNG_ERROR)
                text = getString(R.string.LNG_TRANSFER_NOT_FOUND, transferId.toString())
                isShowCloseButton = true
                isShowOkButton = false
                dismissCallBack?.let { onDismissCallBack = it }
            }
            .show(childFragmentManager)
    }
}
