package com.kg.gettransfer.presentation.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import com.arellomobile.mvp.MvpAppCompatDialogFragment
import java.lang.RuntimeException


open class MvpBottomSheetDialogFragment : MvpAppCompatDialogFragment() {

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		context?.let {
			return BottomSheetDialog(it, theme)
		} ?: throw RuntimeException("MvpBottomSheetDialogFragment: context is null")
	}
}