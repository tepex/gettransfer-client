package com.kg.gettransfer.presentation.view

import android.support.annotation.StringRes
import com.kg.gettransfer.domain.ApiException

interface LoginView: BaseView, SmsCodeView {
    companion object {
        val EXTRA_NEXT_SCREEN    = "${LoginView::class.java.name}.next_screen"
        val EXTRA_EMAIL_TO_LOGIN = "${LoginView::class.java.name}.email_to_login"
        val EXTRA_TRANSFER_ID    = "${LoginView::class.java.name}.transfer_id"
        val EXTRA_OFFER_ID       = "${LoginView::class.java.name}.offer_id"
        val EXTRA_RATE           = "${LoginView::class.java.name}.rate"
    }

    fun setEmail(login: String)

    fun showError(show: Boolean, error: ApiException)
    fun showValidationError(show: Boolean, errorType: Int)
    fun showLoginInfo(@StringRes title: Int, @StringRes info: Int)

    fun showPasswordFragment(show:Boolean, showingView: Int)
    fun showChangePasswordDialog()
}
