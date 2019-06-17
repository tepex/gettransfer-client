package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.View
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.getIntOrNull
import com.kg.gettransfer.presentation.ui.dialogs.BaseBottomSheetDialogFragment
import kotlinx.android.synthetic.main.error_bs_dialog.*

class ErrorBottomSheet : BaseBottomSheetDialogFragment() {
    override val layout = R.layout.error_bs_dialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let { setUi(it) }
    }

    private fun setUi(bundle: Bundle) {
        with(bundle) {
            getIntOrNull(IMAGE_RES)
                    ?.let { error_icon.setImageResource(it) }
            getString(TITLE)
                    ?.let { error_title.text = it }
            getString(DESCRIPTION)
                    ?.let { error_description.text = it }
        }
        iv_close_dialog.setOnClickListener {
            dismiss()
        }
    }

    fun show(manager: FragmentManager?) {
        super.show(manager, TAG)
    }

    fun putArgs(bundle: Bundle): ErrorBottomSheet {
        arguments = bundle
        return this
    }

    companion object {
        fun newInstance() = ErrorBottomSheet()

        const val TITLE       = "error_title"
        const val DESCRIPTION = "error_description"
        const val IMAGE_RES   = "error_image"

        const val TAG = ".presentation.ui.ErrorBottomSheet"
    }
}