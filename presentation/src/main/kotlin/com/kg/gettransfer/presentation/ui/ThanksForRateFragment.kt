package com.kg.gettransfer.presentation.ui

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.setUneditable
import com.kg.gettransfer.presentation.presenter.ThanksForRatePresenter
import com.kg.gettransfer.presentation.ui.dialogs.BaseBottomSheetDialogFragment
import com.kg.gettransfer.presentation.ui.dialogs.CommentDialogFragment
import com.kg.gettransfer.presentation.ui.dialogs.CommentDialogFragment.Companion.COMMENT_DIALOG_TAG
import com.kg.gettransfer.presentation.ui.dialogs.CommentDialogFragment.Companion.COMMENT_REQUEST_CODE
import com.kg.gettransfer.presentation.ui.dialogs.StoreDialogFragment
import com.kg.gettransfer.presentation.view.ThanksForRateView
import kotlinx.android.synthetic.main.view_thanks_for_rate.*

class ThanksForRateFragment : BaseBottomSheetDialogFragment(), ThanksForRateView {

    @InjectPresenter
    internal lateinit var presenter: ThanksForRatePresenter

    @ProvidePresenter
    fun getPresenter() = ThanksForRatePresenter()

    override val layout: Int = R.layout.view_thanks_for_rate

    companion object {
        const val TAG = "thanks_for_rate_tag"

        fun newInstance() = ThanksForRateFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etComment.setUneditable()
    }

    override fun initUx(savedInstanceState: Bundle?) {
        super.initUx(savedInstanceState)
        ivClose.setOnClickListener { dismiss() }
        etComment.setOnClickListener {
            showComment()
        }
    }

    private fun showComment() {
        val commentDialog = CommentDialogFragment.newInstance()
        commentDialog.setTargetFragment(this@ThanksForRateFragment, COMMENT_REQUEST_CODE)
        commentDialog.show(fragmentManager, COMMENT_DIALOG_TAG)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK &&
                requestCode == COMMENT_REQUEST_CODE) {
            val comment = data?.getStringExtra(CommentDialogFragment.EXTRA_COMMENT) ?: ""
            setComment(comment)
        }
    }

    private fun setComment(comment: String) {
        presenter.setComment(comment)
        etComment.setText(comment)
        dismiss()
    }

    override fun onDismiss(dialog: DialogInterface?) {
        presenter.sendThanks()
        super.onDismiss(dialog)
    }

    override fun askRateInPlayMarket() =
        StoreDialogFragment.newInstance().show(fragmentManager, StoreDialogFragment.STORE_DIALOG_TAG)
}