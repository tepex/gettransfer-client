package com.kg.gettransfer.presentation.ui

import android.app.Activity

import android.content.Context

import android.os.Build

import android.support.annotation.StringRes

import android.support.v7.app.AlertDialog

import android.text.Editable
import android.text.TextWatcher

import android.view.View

import android.widget.EditText

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.CurrencyModel

import timber.log.Timber

class Utils {
    companion object {
        fun getAlertDialogBuilder(context: Context): AlertDialog.Builder {
            return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && false)
                AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)
            else AlertDialog.Builder(context)
        }
        
        fun showError(context: Context, @StringRes errId: Int, finish: Boolean) {
            getAlertDialogBuilder(context)
                .setTitle(R.string.err_title)
                .setMessage(errId)
                .setPositiveButton(android.R.string.ok, { dialog, _ ->
                   dialog.dismiss()
                   if(finish) (context as Activity).finish()
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
        
        fun setCurrenciesDialogListener(context: Context, view: View, items: List<CharSequence>,
            listener: (Int) -> Unit) { setModelsDialogListener(context, view, R.string.currency, items, listener) }
        fun setLocalesDialogListener(context: Context, view: View, items: List<CharSequence>,
            listener: (Int) -> Unit) { setModelsDialogListener(context, view, R.string.application_language, items, listener) }
        fun setDistanceUnitsDialogListener(context: Context, view: View, items: List<CharSequence>,
            listener: (Int) -> Unit) { setModelsDialogListener(context, view, R.string.distance_units, items, listener) }

        fun setModelsDialogListener(context: Context, view: View, @StringRes titleId: Int, items: List<CharSequence>, 
                                    listener: (Int) -> Unit) {
            view.setOnClickListener {
                getAlertDialogBuilder(context)
                    .setTitle(titleId)
                    .setItems(items.toTypedArray()) { _, which -> listener(which) }
                    .setNegativeButton(android.R.string.cancel, null)
                    .show()
            }
        }
	}
}

fun EditText.onTextChanged(cb: (String) -> Unit) {
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = cb(s.toString())
    })
}
