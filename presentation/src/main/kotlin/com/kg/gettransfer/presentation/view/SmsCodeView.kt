package com.kg.gettransfer.presentation.view

interface SmsCodeView : BaseView {
    fun showErrorText(show: Boolean, text: String? = null)
    fun showValidationError(b: Boolean, invaliD_EMAIL: Int)
    fun startTimer()
    fun tickTimer(millisUntilFinished: Long, interval: Long)
    fun finishTimer()
    fun setBtnDoneIsEnabled(isEnabled: Boolean)
}
