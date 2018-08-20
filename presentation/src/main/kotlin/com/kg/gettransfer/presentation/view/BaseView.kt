package com.kg.gettransfer.presentation.view

import android.support.annotation.StringRes
import com.arellomobile.mvp.MvpView

import com.kg.gettransfer.domain.model.GTAddress

interface BaseView: MvpView {
	fun blockInterface(block: Boolean)
	fun setAddressFrom(address: GTAddress) 
	fun setError(@StringRes errId: Int, finish: Boolean = false)
	fun finish()
}
