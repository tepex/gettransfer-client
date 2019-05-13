package com.kg.gettransfer.presentation.view

interface SmsCodeView {
    fun updateTimerResendCode()
    fun showErrorText(show: Boolean, text: String? = null)
}