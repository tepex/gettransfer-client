package com.kg.gettransfer.presentation.view

import android.support.annotation.StringRes

import com.arellomobile.mvp.MvpView

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.presentation.model.OfferModel

interface BaseView : MvpView {
    fun blockInterface(block: Boolean, useSpinner: Boolean = false)
    fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?)
    fun setError(e: ApiException)
    fun showOffer(offer: OfferModel)
}
