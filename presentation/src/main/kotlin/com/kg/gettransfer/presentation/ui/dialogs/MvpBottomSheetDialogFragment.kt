package com.kg.gettransfer.presentation.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import com.arellomobile.mvp.MvpAppCompatDialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
//import leakcanary.AppWatcher
import java.lang.RuntimeException

open class MvpBottomSheetDialogFragment : MvpAppCompatDialogFragment() {

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		context?.let {
			return BottomSheetDialog(it, theme)
		} ?: throw RuntimeException("MvpBottomSheetDialogFragment: context is null")
	}

	override fun onDestroy() {
		super.onDestroy()
//		AppWatcher.objectWatcher.watch(this)
	}
}