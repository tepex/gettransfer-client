package com.kg.gettransfer.presentation.view

interface MainRequestView {
    fun setView(addressFrom: String? = null, addressTo: String? = null, duration: String? = null)
    fun setNumberPickerValue(duration: String)
}