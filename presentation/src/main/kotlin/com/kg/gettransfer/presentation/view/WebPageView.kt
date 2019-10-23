package com.kg.gettransfer.presentation.view

import androidx.annotation.StringRes

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface WebPageView: MvpView {
    companion object {
        val EXTRA_SCREEN = "${WebPageView::class.java.name}.screen"
        
        const val SCREEN_LICENSE       = "license_agreement"
        const val SCREEN_REG_CARRIER   = "registration_carrier"
        const val SCREEN_CARRIER       = "carrier_mode"
        const val SCREEN_RESTORE_PASS  = "restore_password"
        const val SCREEN_TRANSFERS     = "carrier_transfers"
    }

    fun initActivity(@StringRes title: Int?, strUrl: String, stringTitle: String? = null)
    fun finish()
}