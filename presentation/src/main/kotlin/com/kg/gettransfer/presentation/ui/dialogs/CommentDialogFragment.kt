package com.kg.gettransfer.presentation.ui.dialogs

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.OrderCommentPresenter
import com.kg.gettransfer.presentation.ui.afterTextChanged
import com.kg.gettransfer.presentation.view.CommentView
import kotlinx.android.synthetic.main.layout_comment.*

class CommentDialogFragment : BaseBottomSheetDialogFragment(), CommentView {

    override val layout: Int = R.layout.layout_comment

    private var onCommentLister: OnCommentListener? = null

    @InjectPresenter
    lateinit var presenter: OrderCommentPresenter

    @ProvidePresenter
    fun providePresenter() = OrderCommentPresenter()

    companion object {
        private const val EXTRA_COMMENT = "comment"
        const val COMMENT_DIALOG_TAG = "comment_dialog_tag"

        fun newInstance(comment: String) = CommentDialogFragment().apply {
            arguments = Bundle().apply { putString(EXTRA_COMMENT, comment) }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        onCommentLister = activity as OnCommentListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomSheetState(view, BottomSheetBehavior.STATE_EXPANDED)
    }

    override fun onResume() {
        super.onResume()
        etPopupComment.requestFocus()
        showKeyboard()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        hideKeyboard()
    }

    override fun onDetach() {
        super.onDetach()
        onCommentLister = null
    }

    override fun initUx(savedInstanceState: Bundle?) {
        super.initUx(savedInstanceState)
        tvDone.setOnClickListener {
            val comment = etPopupComment.text.toString().trim()
            presenter.setComment(comment)
            setBottomSheetState(this@CommentDialogFragment.view!!, BottomSheetBehavior.STATE_HIDDEN)
            hideKeyboard()
        }
        etPopupComment.afterTextChanged { presenter.setComment(it.trim()) }
    }

    override fun initUi(savedInstanceState: Bundle?) {
        super.initUi(savedInstanceState)
        etPopupComment.setText(arguments?.getString(EXTRA_COMMENT).toString())
    }

    override fun setComment(comment: String) {
        onCommentLister?.onSetComment(comment)
    }

    interface OnCommentListener {
        fun onSetComment(comment: String)
    }
}