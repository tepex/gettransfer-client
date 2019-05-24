package com.kg.gettransfer.presentation.ui.dialogs

import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.presentation.presenter.CommentPresenter
import com.kg.gettransfer.presentation.view.CommentView
import kotlinx.android.synthetic.main.layout_popup_comment.*

class CommentDialogFragment : BaseBottomSheetDialogFragment(), CommentView {

    override val layout: Int = R.layout.layout_popup_comment

    private var onCommentLister: OnCommentListener? = null

    @InjectPresenter
    lateinit var presenter: CommentPresenter

    @ProvidePresenter
    fun providePresenter() = CommentPresenter()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        onCommentLister = activity as OnCommentListener
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    override fun initUx(savedInstanceState: Bundle?) {
        super.initUx(savedInstanceState)
        tvDone.setOnClickListener {
            val comment = etPopupComment.text.toString().trim()
            presenter.setComment(comment)
        }
    }

    override fun setComment(comment: String) {
        onCommentLister?.onSetComment(comment)
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {

    }

    override fun setError(finish: Boolean, errId: Int, vararg args: String?) {
    }

    override fun setError(e: ApiException) {
    }

    override fun setError(e: DatabaseException) {
    }

    interface OnCommentListener {
        fun onSetComment(comment: String)
    }
}