package com.kg.gettransfer.presentation.view

import java.io.File

interface CommunicateView {
    fun sendEmail(emailCarrier: String?, logsFile: File?)
    fun callPhone(phoneCarrier: String?)
}