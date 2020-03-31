package com.kg.gettransfer.presentation.view

interface SmsCodeView : OpenDeepLinkScreenView {
    fun startTimer()
    fun tickTimer(millisUntilFinished: Long, interval: Long)
    fun finishTimer()
    fun showCodeExpiration(codeExpiration: Int)

    companion object {
        val EXTRA_IS_PHONE = "${SmsCodeView::class.java.name}.isPhone"
    }
}
