package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.presentation.view.CommentView
import org.koin.standalone.inject

@InjectViewState
class CommentPresenter: BasePresenter<CommentView>() {

    private val orderInteractor: OrderInteractor by inject()

    override fun attachView(view: CommentView) {
        super.attachView(view)
        with(orderInteractor) {
            comment?.let { viewState.setComment(it) }
        }
    }

    fun setComment(comment: String) {
        if (comment.isEmpty()) orderInteractor.comment = null else orderInteractor.comment = comment
        viewState.setComment(comment)
    }
}