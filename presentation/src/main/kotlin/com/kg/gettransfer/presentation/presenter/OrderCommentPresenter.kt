package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kg.gettransfer.domain.interactor.OrderInteractor
import com.kg.gettransfer.presentation.view.CommentView
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

@InjectViewState
class OrderCommentPresenter: MvpPresenter<CommentView>(), KoinComponent {

    private val orderInteractor: OrderInteractor by inject()

    override fun attachView(view: CommentView) {
        super.attachView(view)
        with(orderInteractor) {
            comment?.let { viewState.setComment(it) }
        }
    }

    fun setComment(comment: String) {
        orderInteractor.comment = if (comment.isNotEmpty()) comment else null
        viewState.setComment(comment)
    }
}