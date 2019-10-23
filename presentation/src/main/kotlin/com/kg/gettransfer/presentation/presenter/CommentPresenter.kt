package com.kg.gettransfer.presentation.presenter

import moxy.InjectViewState
import moxy.MvpPresenter

import com.kg.gettransfer.presentation.view.CommentView

import org.koin.core.KoinComponent

@InjectViewState
class CommentPresenter: MvpPresenter<CommentView>(), KoinComponent {
}
