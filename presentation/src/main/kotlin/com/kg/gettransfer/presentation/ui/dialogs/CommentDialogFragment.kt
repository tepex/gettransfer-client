package com.kg.gettransfer.presentation.ui.dialogs

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import android.view.*
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.material.chip.Chip
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.presenter.CommentPresenter
import com.kg.gettransfer.presentation.ui.afterTextChanged
import com.kg.gettransfer.presentation.view.CommentView
import kotlinx.android.synthetic.main.layout_comment.*

class CommentDialogFragment : BaseBottomSheetDialogFragment(), CommentView {

    override val layout: Int = R.layout.layout_comment

    private var onCommentLister: OnCommentListener? = null

    private var comment: String = ""

    @InjectPresenter
    lateinit var presenter: CommentPresenter

    @ProvidePresenter
    fun providePresenter() = CommentPresenter()

    companion object {
        const val EXTRA_COMMENT = "comment"
        const val EXTRA_HINTS = "hints"
        const val COMMENT_DIALOG_TAG = "comment_dialog_tag"
        const val COMMENT_REQUEST_CODE = 111

        fun newInstance(comment: String = "", hints: Array<String>? = emptyArray()) =
                CommentDialogFragment().apply {
                    arguments = Bundle().apply {
                        putString(EXTRA_COMMENT, comment)
                        putStringArray(EXTRA_HINTS, hints)
                    }
                }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onCommentLister = (activity as? OnCommentListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomSheetState(view, BottomSheetBehavior.STATE_EXPANDED)
    }

    override fun onResume() {
        super.onResume()
        etComment.requestFocus()
        showKeyboard()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        hideKeyboard()
        sendCommentToTargetFragment(comment)
    }

    override fun onDetach() {
        super.onDetach()
        onCommentLister?.onSetComment(comment)
        onCommentLister = null
    }

    override fun initUx(savedInstanceState: Bundle?) {
        super.initUx(savedInstanceState)
        tvSend.setOnClickListener {
            comment = etComment.text.toString().trim()
            onCommentLister?.onSetComment(comment)
            sendCommentToTargetFragment(comment)
            setBottomSheetState(this@CommentDialogFragment.view!!, BottomSheetBehavior.STATE_HIDDEN)
            hideKeyboard()
        }
        etComment.afterTextChanged {
            comment = it.trim()
            checkHints()
        }
    }

    private fun checkHints() {
        for (i in 0 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            if (chip.isChecked) {
                if (!comment.contains(chip.text)) {
                    chip.isChecked = false
                    chip.isVisible = true
                }
            } else {
                if (comment.contains(chip.text)) {
                    chip.isChecked = true
                    chip.isVisible = false
                }
            }
        }
    }

    private fun sendCommentToTargetFragment(comment: String) {
        val intent = Intent().apply { putExtra(EXTRA_COMMENT, comment) }
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
    }

    override fun initUi(savedInstanceState: Bundle?) {
        super.initUi(savedInstanceState)
        comment = arguments?.getString(EXTRA_COMMENT) ?: ""
        etComment.setText(comment)
        addChipsForHits()
    }

    private fun addChipsForHits() {
        val hints = arguments?.getStringArray(EXTRA_HINTS)
        if (!hints.isNullOrEmpty()) {
            for (hint in hints) {
                val chip = Chip(chipGroup.context)
                chip.text = hint
                chip.isClickable = true
                chip.isCheckable = true
                chip.isCheckedIconVisible = false
                chip.setChipMinHeightResource(R.dimen.comment_hint_height)
                chip.setOnClickListener { hintClick(chip.text.toString(), it) }
                chipGroup.addView(chip)
            }
        }
    }

    private fun hintClick(text: String, chip: View) {
        comment = if (comment.isEmpty()) comment.plus(text) else comment.plus(" $text")
        etComment.setText(comment)
        etComment.setSelection(comment.length)
        chip.isVisible = false
        onCommentLister?.onSetComment(comment)
    }

    interface OnCommentListener {
        fun onSetComment(comment: String)
    }
}