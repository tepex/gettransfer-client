package com.kg.gettransfer.presentation.ui

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.setUneditable
import com.kg.gettransfer.presentation.presenter.ThanksForRatePresenter
import com.kg.gettransfer.presentation.ui.dialogs.BaseBottomSheetDialogFragment
import com.kg.gettransfer.presentation.view.ThanksForRateView
import kotlinx.android.synthetic.main.view_thanks_for_rate.*

class ThanksForRateFragment: BaseBottomSheetDialogFragment(), ThanksForRateView {

    @InjectPresenter
    internal lateinit var presenter: ThanksForRatePresenter

    @ProvidePresenter
    fun getPresenter() = ThanksForRatePresenter()

    override val layout: Int = R.layout.view_thanks_for_rate

    companion object {
        const val THANKS_FOR_RATE_TAG = "thanks_for_rate_tag"

        fun newInstance() = ThanksForRateFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        et_thanks_comment.setUneditable()
        et_thanks_comment.setOnClickListener {

        }
    }

    override fun initUx(savedInstanceState: Bundle?) {
        super.initUx(savedInstanceState)
        tvClose.setOnClickListener { dismiss() }
    }

    override fun setComment(text: String) {
        et_thanks_comment.setText(text)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        presenter.sendThanks()
    }
}