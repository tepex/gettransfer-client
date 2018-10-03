package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.widget.EditText
import android.widget.PopupWindow

class CustomEditText(context: Context?, attrs: AttributeSet?) : EditText(context, attrs) {

    var popupWindow: PopupWindow? = null

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            popupWindow!!.dismiss()
        }
        return false
    }
}