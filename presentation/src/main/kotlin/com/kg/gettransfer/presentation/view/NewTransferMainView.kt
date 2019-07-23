package com.kg.gettransfer.presentation.view

interface NewTransferMainView : BaseNewTransferView {
    fun setEventCount(isVisible: Boolean, count: Int)
    fun onNetworkWarning(disconnected: Boolean)
    fun switchToMap()
}