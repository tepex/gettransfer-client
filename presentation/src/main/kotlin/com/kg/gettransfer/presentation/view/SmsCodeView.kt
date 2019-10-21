package com.kg.gettransfer.presentation.view

interface SmsCodeView : OpenNextScreenView {
    fun showErrorText(show: Boolean, text: String? = null)
    fun showValidationError(b: Boolean, invaliD_EMAIL: Int)
    fun startTimer()
    fun tickTimer(millisUntilFinished: Long, interval: Long)
    fun finishTimer()
    fun setBtnDoneIsEnabled(isEnabled: Boolean)
}
