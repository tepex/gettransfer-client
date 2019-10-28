package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.extensions.hideKeyboard
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.extensions.setThrottledClickListener
import com.kg.gettransfer.presentation.presenter.SmsCodePresenter
import com.kg.gettransfer.presentation.view.BaseHandleUrlView
import com.kg.gettransfer.presentation.view.LogInView
import com.kg.gettransfer.presentation.view.SmsCodeView
import io.sentry.Sentry
import io.sentry.event.BreadcrumbBuilder
import kotlinx.android.synthetic.main.fragment_sms_code.*
import kotlinx.serialization.json.JSON
import org.jetbrains.anko.longToast
import pub.devrel.easypermissions.EasyPermissions
// import leakcanary.AppWatcher
import timber.log.Timber

@Suppress("TooManyFunctions")
class SmsCodeFragment : MvpAppCompatFragment(),
    SmsCodeView,
    EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    private val loadingFragment by lazy { LoadingFragment() }

    @InjectPresenter
    internal lateinit var presenter: SmsCodePresenter

    @ProvidePresenter
    fun smsCodePresenterProvider() = SmsCodePresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.setTimer()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_sms_code, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(presenter) {
            arguments?.let { bundle ->
                params = JSON.parse(LogInView.Params.serializer(), bundle.getString(LogInView.EXTRA_PARAMS) ?: "")
                isPhone = bundle.getBoolean(EXTERNAL_IS_PHONE)
            }
            pinItemsCount = pinView.itemCount
        }

        smsTitle.text = "${getString(presenter.getTitleId())} ${presenter.params.emailOrPhone}"

        pinView.onTextChanged { code ->
            presenter.pinCode = code
            presenter.checkAfterPinChanged()
        }

        loginBackButton.setOnClickListener { view ->
            view.hideKeyboard()
            presenter.back()
        }

        btnResendCode.setOnClickListener { view ->
            view.hideKeyboard()
            presenter.sendVerificationCode()
            presenter.setTimer()
        }

        btnDone.setThrottledClickListener { view ->
            view.hideKeyboard()
            presenter.onLoginClick()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        AppWatcher.objectWatcher.watch(this)
        presenter.cancelTimer()
    }

    override fun setBtnDoneIsEnabled(isEnabled: Boolean) {
        btnDone.isEnabled = isEnabled
    }

    override fun startTimer() {
        btnResendCode.isEnabled = false
    }

    override fun tickTimer(millisUntilFinished: Long, interval: Long) {
        btnResendCode.text =
            getString(R.string.LNG_LOGIN_RESEND_WAIT, (millisUntilFinished / interval).toString())
                .plus(" ${getString(R.string.LNG_SEC)}")
    }

    override fun finishTimer() {
        btnResendCode.isEnabled = true
        btnResendCode.text = getText(R.string.LNG_LOGIN_RESEND_ALLOW)
    }

    override fun showErrorText(show: Boolean, text: String?) {
        context?.let { context ->
            pinView.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (show) R.color.color_gtr_red else R.color.color_gtr_green
                )
            )
        }
        wrongCodeError.isVisible = show
        text?.let { wrongCodeError.text = text }
    }

    // ----------TODO остатки от группы Base.---------------

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        if (block) {
            if (useSpinner) showLoading()
        } else hideLoading()
    }

    override fun setError(e: ApiException) {
        context?.let { pinView.setTextColor(ContextCompat.getColor(it, R.color.color_gtr_red)) }
        Timber.e("code: ${e.code}")
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage(e.details).build())
        Sentry.capture(e)
        if (e.code == ApiException.NOT_FOUND) {
            BottomSheetDialog
                .newInstance()
                .apply {
                    title = this@SmsCodeFragment.getString(R.string.LNG_ACCOUNT_NOTFOUND, presenter.params.emailOrPhone)
                    buttonOkText = this@SmsCodeFragment.getString(R.string.LNG_OK)
                    onDismissCallBack = {
                        presenter.back()
                    }
                }
                .show(requireFragmentManager())
        }
    }

    override fun setError(e: DatabaseException) {
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage("CacheError: ${e.details}").build())
        Sentry.capture(e)
    }

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        context?.let { Utils.showError(it, false, getString(errId, *args)) }
    }

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

    override fun setTransferNotFoundError(transferId: Long, dismissCallBack: (() -> Unit)?) {
        BottomSheetDialog
            .newInstance()
            .apply {
                imageId = R.drawable.transfer_error
                title = this@SmsCodeFragment.getString(R.string.LNG_ERROR)
                text = this@SmsCodeFragment.getString(R.string.LNG_TRANSFER_NOT_FOUND, transferId.toString())
                isShowCloseButton = true
                isShowOkButton = false
                dismissCallBack?.let { onDismissCallBack = it }
            }
            .show(requireFragmentManager())
    }

    companion object {

        const val EXTERNAL_IS_PHONE = ".presentation.ui.SmsCodeFragment_IS_PHONE"

        fun newInstance() = SmsCodeFragment()
    }

    // for downloading voucher

    override fun downloadVoucher() {
        val perms = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(requireContext(), *perms)) {
            presenter.downloadVoucher()
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
        requireActivity().longToast(getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER_ACCESS))
    }

    override fun setChatIsNoLongerAvailableError(dismissCallBack: () -> Unit) {
        BottomSheetDialog
            .newInstance()
            .apply {
                imageId = R.drawable.transfer_error
                title = this@SmsCodeFragment.getString(R.string.LNG_ERROR)
                text = this@SmsCodeFragment.getString(R.string.LNG_CHAT_NO_LONGER_AVAILABLE)
                isShowCloseButton = true
                isShowOkButton = false
                onDismissCallBack = dismissCallBack
            }
            .show(requireFragmentManager())
    }
}
