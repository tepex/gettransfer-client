package com.kg.gettransfer.presentation.view

interface LogInView : BaseView {
    fun setEmail(login: String)
    fun showValidationError(errorType: Int)
    fun showLoading()
    fun hideLoading()

    companion object {
        val EXTRA_NEXT_SCREEN = "${LogInView::class.java.name}.next_screen"
        val EXTRA_EMAIL_TO_LOGIN = "${LogInView::class.java.name}.email_to_login"
        val EXTRA_TRANSFER_ID = "${LogInView::class.java.name}.transfer_id"
        val EXTRA_OFFER_ID = "${LogInView::class.java.name}.offer_id"
        val EXTRA_RATE = "${LogInView::class.java.name}.rate"
    }
}
