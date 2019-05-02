package com.kg.gettransfer.presentation.view

interface LoginViewNew: BaseView {
    companion object {
        val EXTRA_SCREEN_FOR_RETURN = "${LoginViewNew::class.java.name}.previous_screen"
        val EXTRA_EMAIL_TO_LOGIN    = "${LoginViewNew::class.java.name}.email_to_login"
        val EXTRA_TRANSFER_ID       = "${LoginViewNew::class.java.name}.transfer_id"
        val EXTRA_OFFER_ID          = "${LoginViewNew::class.java.name}.offer_id"
        val EXTRA_RATE              = "${LoginViewNew::class.java.name}.rate"
    }

    fun showError(show: Boolean, message: String?)
    fun showValidationError(show: Boolean, errorType: Int)

    fun showPasswordFragment(show: Boolean)
}
