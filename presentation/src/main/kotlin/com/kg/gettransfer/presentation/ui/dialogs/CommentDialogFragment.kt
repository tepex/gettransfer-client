package com.kg.gettransfer.presentation.ui.dialogs

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.view.View
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomSheetState(view, BottomSheetBehavior.STATE_EXPANDED)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        showKeyboard()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        hideKeyboard()
    }

    override fun initUx(savedInstanceState: Bundle?) {
        super.initUx(savedInstanceState)
        tvDone.setOnClickListener {
            val comment = etPopupComment.text.toString().trim()
            presenter.setComment(comment)
            setBottomSheetState( this@CommentDialogFragment.view!!, BottomSheetBehavior.STATE_HIDDEN)
            hideKeyboard()
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