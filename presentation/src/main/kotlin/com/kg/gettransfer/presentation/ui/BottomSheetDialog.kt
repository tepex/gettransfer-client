package com.kg.gettransfer.presentation.ui

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.FragmentManager
import android.view.View
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.extensions.setThrottledClickListener
import com.kg.gettransfer.presentation.ui.dialogs.BaseBottomSheetDialogFragment
import kotlinx.android.synthetic.main.view_bottomsheet_dialog.*

/**
 * The bottomsheet dialog
 *
 * @property title - title for dialog
 * @property text - teat for dialog
 * @property buttonOkText - text on the positive button
 * @property buttonCancelText - text on the negative button
 * @property onClickOkButton - listener for the positive button
 * @property onClickCancelButton - listener for the negative button
 * @property onDismissCallBack - this listener works after the dialog is closed
 * @property imageId - image for the dialog
 * @property isShowCancelButton - show or hide negative button
 */
class BottomSheetDialog : BaseBottomSheetDialogFragment() {
    override val layout: Int = R.layout.view_bottomsheet_dialog
    var title: String = ""
    var text: String = ""
    var buttonOkText: String = ""
    var buttonCancelText: String = ""
    var onClickOkButton: (() -> Unit)? = null
    var onClickCancelButton: (() -> Unit)? = null
    var onDismissCallBack: (() -> Unit)? = null
    var imageId: Int = R.drawable.ic_invalid_credentials
    var isShowCancelButton: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (title.isNotEmpty()) bottomSheetDialogTitle.text = title
        else bottomSheetDialogTitle.isVisible = false

        if (text.isNotEmpty()) bottomSheetDialogDetail.text = text
        else bottomSheetDialogDetail.isVisible = false

        if (buttonOkText.isNotEmpty()) bottomSheetDialogOkButton.text = buttonOkText

        bottomSheetDialogImage.setImageResource(imageId)

        bottomSheetDialogOkButton.setThrottledClickListener(500L) {
            onClickOkButton?.invoke()
            dismiss()
        }

        bottomSheetDialogCancelButton.setThrottledClickListener(500L) {
            onClickCancelButton?.invoke()
            dismiss()
        }

        bottomSheetDialogCancelButton.isVisible = isShowCancelButton
    }

    fun show(manager: FragmentManager?) {
        super.show(manager, TAG)
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        onDismissCallBack?.invoke()
    }

    companion object {
        const val TAG = ".presentation.ui.BottomSheetDialog"

        fun newInstance() = BottomSheetDialog()
    }
}
