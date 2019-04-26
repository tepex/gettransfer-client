package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.ui.dialogs.BaseBottomSheetDialogFragment
import kotlinx.android.synthetic.main.view_thanks_for_rate.*

class ThanksForRateFragment: BaseBottomSheetDialogFragment() {

    override val layout: Int = R.layout.view_thanks_for_rate

    companion object {
        const val THANKS_FOR_RATE_TAG = "thanks_for_rate_tag"

        fun newInstance() = ThanksForRateFragment()
    }

    override fun initUx(savedInstanceState: Bundle?) {
        super.initUx(savedInstanceState)
        tvClose.setOnClickListener { dismiss() }
    }
}