package com.kg.gettransfer.presentation.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kg.gettransfer.R
import android.util.DisplayMetrics


abstract class BaseBottomSheetDialogFragment : MvpBottomSheetDialogFragment() {

	protected abstract val layout: Int

	protected open val style: Int = R.style.DialogStyle

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(DialogFragment.STYLE_NORMAL, style)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(layout, container, false)
	}

	override fun setupDialog(dialog: Dialog?, style: Int) {
		super.setupDialog(dialog, style)
		dialog?.run {
			setOnShowListener {
				(view?.parent as? View)?.let {
					val displayMetrics = DisplayMetrics()
					activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)
					val height = displayMetrics.heightPixels
					BottomSheetBehavior.from(it).apply { peekHeight = height }
				}
			}
		}
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initUi(savedInstanceState)
		initUx(savedInstanceState)
	}

	protected open fun initUi(savedInstanceState: Bundle?) {}

	protected open fun initUx(savedInstanceState: Bundle?) {}

}