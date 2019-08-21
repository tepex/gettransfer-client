package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.MvpView

interface BaseNetworkWarning : MvpView {
    fun onNetworkWarning(available: Boolean)
}
