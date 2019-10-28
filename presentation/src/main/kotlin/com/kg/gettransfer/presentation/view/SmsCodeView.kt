package com.kg.gettransfer.presentation.view

interface SmsCodeView : OpenDeepLinkScreenView {
    fun showErrorText(show: Boolean, text: String? = null)
    fun showValidationError(b: Boolean, invalidEmail: Int)
    fun startTimer()
    fun tickTimer(millisUntilFinished: Long, interval: Long)
    fun finishTimer()
    fun setBtnDoneIsEnabled(isEnabled: Boolean)
}
