package com.kg.gettransfer.presentation.ui.dialogs

import android.content.Context
import android.os.Bundle
import com.kg.gettransfer.R
import com.kg.gettransfer.utilities.Analytics
import kotlinx.android.synthetic.main.dialog_fragment_about_driver_app.*
import org.koin.android.ext.android.inject

class AboutDriverAppDialogFragment : BaseBottomSheetDialogFragment() {
	protected val analytics: Analytics by inject()
	override val layout: Int = R.layout.dialog_fragment_about_driver_app
	private var onAboutDriverAppListener: OnAboutDriverAppListener? = null

	override fun onAttach(context: Context?) {
		super.onAttach(context)
		onAboutDriverAppListener = activity as OnAboutDriverAppListener
	}

	override fun initUx(savedInstanceState: Bundle?) {
		super.initUx(savedInstanceState)
		iv_close.setOnClickListener { dismiss() }
		btn_continue.setOnClickListener {
			onAboutDriverAppListener?.onClickGoToDriverApp()
			dismiss()
		}
		btn_cancel.setOnClickListener {
			dismiss()
		}
	}

	companion object {
		const val DIALOG_TAG = "about_driver_app_dialog_tag"

		fun newInstance() = AboutDriverAppDialogFragment()
	}

	interface OnAboutDriverAppListener {
		fun onClickGoToDriverApp()
	}
}