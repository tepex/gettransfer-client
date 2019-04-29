package com.kg.gettransfer.presentation.ui.dialogs

import android.content.Context
import android.os.Bundle
import com.kg.gettransfer.R
import com.kg.gettransfer.utilities.Analytics
import kotlinx.android.synthetic.main.dialog_fragment_store.btnCancel
import kotlinx.android.synthetic.main.dialog_fragment_store.btnContinue
import org.koin.android.ext.android.inject


class StoreDialogFragment : BaseBottomSheetDialogFragment() {
	protected val analytics: Analytics by inject()
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
			logReviewDialogResult(true)
			dismiss()

		}
		btnCancel.setOnClickListener {
			logReviewDialogResult(false)
			dismiss()
		}
	}

	companion object {
		const val STORE_DIALOG_TAG = "store_dialog_tag"

		fun newInstance() = StoreDialogFragment()
	}

	interface OnStoreListener {
		fun onClickGoToStore()
	}

	private fun logReviewDialogResult(positive: Boolean) {
		with(Analytics) {
			val p = Pair(if (positive) REVIEW_APP_ACCEPTED else REVIEW_APP_REJECTED,"")
			analytics.logEvent(EVENT_APP_REVIEW_REQUESTED,
					Bundle(1).apply { putString(p.first, p.second) },
					mapOf(p))
		}
	}

}