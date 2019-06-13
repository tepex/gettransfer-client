package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kg.gettransfer.presentation.view.CommentView
import org.koin.standalone.KoinComponent

@InjectViewState
class CommentPresenter: MvpPresenter<CommentView>(), KoinComponent {
}