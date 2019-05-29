package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.MvpView

interface ThanksForRateView: MvpView {
    fun setComment(text: String)
    fun showTextEditorWithCurrentText(text: String)
}