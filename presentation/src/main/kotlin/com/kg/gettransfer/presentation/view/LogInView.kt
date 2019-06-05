package com.kg.gettransfer.presentation.view

import android.support.annotation.StringRes
import com.kg.gettransfer.domain.ApiException

interface LogInView : BaseView {
    fun setEmail(login: String)
    fun showError(show: Boolean, error: ApiException)
    fun showValidationError(show: Boolean, errorType: Int)
    fun showLoginInfo(@StringRes title: Int, @StringRes info: Int)
    fun showChangePasswordDialog()
    fun showErrorText(b: Boolean, text: String?)

    companion object {
        val EXTRA_NEXT_SCREEN = "${LogInView::class.java.name}.next_screen"
        val EXTRA_EMAIL_TO_LOGIN = "${LogInView::class.java.name}.email_to_login"
        val EXTRA_TRANSFER_ID = "${LogInView::class.java.name}.transfer_id"
        val EXTRA_OFFER_ID = "${LogInView::class.java.name}.offer_id"
        val EXTRA_RATE = "${LogInView::class.java.name}.rate"
    }
}
