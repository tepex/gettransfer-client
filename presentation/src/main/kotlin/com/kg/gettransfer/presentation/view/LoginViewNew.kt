package com.kg.gettransfer.presentation.view

import android.support.annotation.StringRes
import com.kg.gettransfer.domain.ApiException

interface LoginViewNew: BaseView, SmsCodeView {
    companion object {
        val EXTRA_SCREEN_FOR_RETURN = "${LoginViewNew::class.java.name}.previous_screen"
        val EXTRA_EMAIL_TO_LOGIN    = "${LoginViewNew::class.java.name}.email_to_login"
        val EXTRA_TRANSFER_ID       = "${LoginViewNew::class.java.name}.transfer_id"
        val EXTRA_OFFER_ID          = "${LoginViewNew::class.java.name}.offer_id"
        val EXTRA_RATE              = "${LoginViewNew::class.java.name}.rate"
    }

    fun showError(show: Boolean, error: ApiException)
    fun showValidationError(show: Boolean, errorType: Int)
    fun showLoginInfo(@StringRes title: Int, @StringRes info: Int)

    fun showPasswordFragment(show: Boolean, isPhone: Boolean)
}
