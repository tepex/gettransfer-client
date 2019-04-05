package com.kg.gettransfer.presentation.ui.dialogs

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kg.gettransfer.R


abstract class BaseBottomSheetDialogFragment : BottomSheetDialogFragment() {

	protected abstract val layout: Int

	protected open val style: Int = R.style.DialogStyle

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(DialogFragment.STYLE_NORMAL, style)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return inflater.inflate(layout, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		initUi(savedInstanceState)
		initUx(savedInstanceState)
	}

	override fun onStart() {
		super.onStart()
		(view?.parent as? View)?.let {
			BottomSheetBehavior.from(it)
				.run {
					state = BottomSheetBehavior.STATE_EXPANDED
					peekHeight = it.height
				}
		}
	}

	protected open fun initUi(savedInstanceState: Bundle?) {}

	protected open fun initUx(savedInstanceState: Bundle?) {}

}