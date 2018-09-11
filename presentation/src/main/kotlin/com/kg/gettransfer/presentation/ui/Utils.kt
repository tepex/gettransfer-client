package com.kg.gettransfer.presentation.ui

import android.app.Activity

import android.content.Context

import android.os.Build

import android.support.annotation.StringRes

import android.support.v7.app.AlertDialog

import android.text.Editable
import android.text.TextWatcher

import android.view.View
import android.view.inputmethod.InputMethodManager

import android.widget.EditText

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.DistanceUnit
import com.kg.gettransfer.presentation.model.CurrencyModel

import java.util.regex.Pattern

import timber.log.Timber

class Utils {
    companion object {
        private val PHONE_PATTERN = Pattern.compile("^\\+\\d{11}$")
        @JvmField val DATE_TIME_FULL_PATTERN = "yyyy-MM-dd'T'HH:mm:ss"
        @JvmField val DATE_TIME_PATTERN = "dd MMMM yyyy, HH:mm"
        
        fun getAlertDialogBuilder(context: Context): AlertDialog.Builder {
            return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && false)
                AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)
            else AlertDialog.Builder(context)
        }
        
        fun showError(context: Context, finish: Boolean, message: String) {
            getAlertDialogBuilder(context)
                .setTitle(R.string.err_title)
                .setMessage(message)
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
        
        fun showKeyboard(context: Context) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
        }
        
		// Андроид, такой андроид :-) 
		// https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
        fun hideKeyboard(context: Context, view: View?) {
            if(view == null) return
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view.windowToken, 0)
        }
        
        fun checkPhone(phone: String?): Boolean {
            if(phone == null) return false
            return PHONE_PATTERN.matcher(phone.trim()).matches()
        }
        
        fun formatDistance(context: Context, @StringRes stringId: Int, distanceUnit: DistanceUnit, distance: Int?): String {
            if(distance == null) return ""
            var d = distance
            if(distanceUnit == DistanceUnit.Mi) d = DistanceUnit.km2Mi(distance)
            return context.getString(stringId, d)
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
