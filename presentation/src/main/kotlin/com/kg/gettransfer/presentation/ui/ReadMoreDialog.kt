package com.kg.gettransfer.presentation.ui

import android.content.Context

import android.support.v7.app.AppCompatDialog

import android.view.View

import com.kg.gettransfer.R

import timber.log.Timber

/** TODO: Refactor it!! kotlinx.android.synthetic */
class ReadMoreDialog(context: Context): AppCompatDialog(context, R.style.ReadMoreDialog) {
	companion object {
		fun newInstance(context: Context): ReadMoreDialog {
			val dialog = ReadMoreDialog(context)
			dialog.setContentView(R.layout.dialog_read_more)
			dialog.setTitle(R.string.LNG_BESTPRICE_TITLE)
			val closeBtn: View? = dialog.findViewById(R.id.closeBtn)
			closeBtn?.setOnClickListener { dialog.dismiss() }
			return dialog
		}
	}
}
