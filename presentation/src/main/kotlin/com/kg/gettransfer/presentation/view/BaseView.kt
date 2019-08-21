package com.kg.gettransfer.presentation.view

import androidx.annotation.StringRes

import com.arellomobile.mvp.MvpView

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException

interface BaseView : MvpView {
    fun blockInterface(block: Boolean, useSpinner: Boolean = false)
    fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?)
    fun setError(e: ApiException)
    fun setError(e: DatabaseException)
    fun setTransferNotFoundError(transferId: Long)
}
