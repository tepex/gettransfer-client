package com.kg.gettransfer.presentation.view

import android.support.annotation.StringRes

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface WebPageView: MvpView {
    companion object {
        val EXTRA_SCREEN = "${WebPageView::class.java.name}.screen"
        
        const val SCREEN_LICENSE       = "license_agreement"
        const val SCREEN_REG_CARRIER   = "registration_carrier"
        const val SCREEN_CARRIER       = "carrier_mode"
        const val SCREEN_RESTORE_PASS  = "restore_password"

        const val INIT_WITH_STRING     = 0
    }

    fun initActivity(@StringRes title: Int, @StringRes url: Int, strUrl: String? = null)
    fun finish()
}