package com.kg.gettransfer.presentation.ui.dialogs

import android.os.Bundle
import android.view.WindowManager
import com.kg.gettransfer.R

class CommentDialogFragment: BaseBottomSheetDialogFragment() {
    override val layout: Int = R.layout.layout_popup_comment

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }
}