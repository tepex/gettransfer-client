package com.kg.gettransfer.presentation.ui

import android.app.Activity

import android.os.Build

import android.support.annotation.StringRes

import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

import com.kg.gettransfer.R

class Utils {
	companion object {
		fun showError(activity: Activity, @StringRes errId: Int, finish: Boolean) {
			val builder: AlertDialog.Builder
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) builder = 
				AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert)
			else builder =  AlertDialog.Builder(activity)
			
			builder.setTitle(R.string.err_title)
    			.setMessage(errId)
    			.setPositiveButton(android.R.string.ok, { dialog, _ ->
    			dialog.dismiss()
    			if(finish) activity.finish()
    		})
    		.setIcon(android.R.drawable.ic_dialog_alert)
    		.show()
    	}
	}
}

fun EditText.onTextChanged(cb: (String) -> Unit) {
	this.addTextChangedListener(object : TextWatcher {
		override fun afterTextChanged(s: Editable?) {
		}

		override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
		}

		override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            cb(s.toString())
        }
	})
}
