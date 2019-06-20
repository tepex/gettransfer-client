package com.kg.gettransfer.presentation.view

interface SmsCodeView : BaseView {
    fun updateTimerResendCode()
    fun showErrorText(show: Boolean, text: String? = null)
    fun showValidationError(b: Boolean, invaliD_EMAIL: Int)

    companion object {
        val EXTERNAL_IS_PHONE = "${SmsCodeView::class.java.name}.is_phone"
        val EXTERNAL_EMAIL_OR_PHONE = "${SmsCodeView::class.java.name}.email_or_phone"
    }
}
