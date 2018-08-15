package com.kg.gettransfer.presentation.view

import android.support.annotation.StringRes
import com.arellomobile.mvp.MvpView

interface BaseView: MvpView {
	fun blockInterface(block: Boolean)
	fun setAddressFrom(address: String) 
	fun setAddressTo(address: String)
	fun setError(@StringRes errId: Int, finish: Boolean = false)
	fun finish()
}
