package com.kg.gettransfer.presentation.view

interface NewTransferMainView : BaseNewTransferView {
    fun initDateTimeFields()
    fun setEventCount(isVisible: Boolean, count: Int)
    fun switchToMap()
}