package com.kg.gettransfer.presentation.view

import moxy.MvpView

interface BaseNetworkWarning : MvpView {
    fun onNetworkWarning(available: Boolean)
}
