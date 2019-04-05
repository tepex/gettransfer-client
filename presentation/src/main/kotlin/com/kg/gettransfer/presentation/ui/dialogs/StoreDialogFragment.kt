package com.kg.gettransfer.presentation.ui.dialogs

import android.content.Context
import android.os.Bundle
import com.kg.gettransfer.R
import kotlinx.android.synthetic.main.dialog_fragment_store.btnCancel
import kotlinx.android.synthetic.main.dialog_fragment_store.btnContinue


class StoreDialogFragment : BaseBottomSheetDialogFragment() {

	override val layout: Int = R.layout.dialog_fragment_store
	private var onStoreListener: OnStoreListener? = null

	override fun onAttach(context: Context?) {
		super.onAttach(context)
		onStoreListener = activity as OnStoreListener
	}

	override fun initUx(savedInstanceState: Bundle?) {
		super.initUx(savedInstanceState)
		btnContinue.setOnClickListener {
			onStoreListener?.onClickGoToStore()
			dismiss()
		}
		btnCancel.setOnClickListener {
			dismiss()
		}
	}

	companion object {
		fun newInstance() = StoreDialogFragment()
	}

	interface OnStoreListener {
		fun onClickGoToStore()
	}

}