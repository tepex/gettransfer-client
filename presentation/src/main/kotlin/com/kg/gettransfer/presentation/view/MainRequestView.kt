package com.kg.gettransfer.presentation.view

interface MainRequestView {
    fun setView(addressFrom: String? = null, addressTo: String? = null, duration: String? = null, networkAvailable: Boolean = true)
    fun setBadge(count: String)
    fun showBadge(show: Boolean)
    fun setNumberPickerValue(duration: String)
    fun onNetworkWarning(disconnected: Boolean)
    fun blockSelectedField(field: String)
    fun setVisibilityBtnMyLocation(isVisible: Boolean)
}