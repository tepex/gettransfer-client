package com.kg.gettransfer.presentation.view

interface SmsCodeView : BaseView {
    fun updateTimerResendCode()
    fun showErrorText(show: Boolean, text: String? = null)
    fun showValidationError(b: Boolean, invaliD_EMAIL: Int)
}