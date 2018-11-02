package com.kg.gettransfer.presentation.view

import android.support.annotation.StringRes

import com.arellomobile.mvp.MvpView

interface BaseView: MvpView {
	fun blockInterface(block: Boolean, useSpinner: Boolean = false)
	fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?)
	fun setError(e: Throwable)
	fun showViewNetworkNotAvailable(isNetworkAvailable: Boolean)
}
