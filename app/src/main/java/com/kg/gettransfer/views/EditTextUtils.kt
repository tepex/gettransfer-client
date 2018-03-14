package com.kg.gettransfer.views


import android.app.Activity
import android.app.DatePickerDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.kg.gettransfer.R
import java.util.*


/**
 * Created by denisvakulenko on 09/02/2018.
 */


fun EditText.setupClearButtonWithAction() {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            val clearIcon = if (editable?.isNotEmpty() == true) R.drawable.ic_clear_gray_24dp else 0
            setCompoundDrawablesWithIntrinsicBounds(0, 0, clearIcon, 0)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
    })

    setOnTouchListener(View.OnTouchListener { _, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            if (event.rawX >= (this.right - this.compoundPaddingRight)) {
                this.setText("")
                return@OnTouchListener true
            }
        }
        return@OnTouchListener false
    })
}


fun TextView.setupChooseDate(activity: Activity) {
    val clearIcon = R.drawable.ic_calendar_black_24dp
    setCompoundDrawablesWithIntrinsicBounds(0, 0, clearIcon, 0)

    this.setOnTouchListener(null)
    this.setOnLongClickListener(null)
    this.setOnClickListener {
        this@setupChooseDate.requestFocus()

        val dateAndTime = Calendar.getInstance()
        DatePickerDialog(
                activity,
                { v, y, m, d ->
                    this@setupChooseDate.setText("$d.${m + 1}.$y")
                },
                dateAndTime.get(Calendar.YEAR),
                dateAndTime.get(Calendar.MONTH),
                dateAndTime.get(Calendar.DAY_OF_MONTH))
                .show()
    }
}


fun TextView.setupChooseTime(activity: Activity) {
    val clearIcon = R.drawable.ic_access_time_black_24dp
    setCompoundDrawablesWithIntrinsicBounds(0, 0, clearIcon, 0)

    this.setOnTouchListener(null)
    this.setOnLongClickListener(null)
    this.setOnClickListener {
        this@setupChooseTime.requestFocus()

        val dateAndTime = Calendar.getInstance()
        android.app.TimePickerDialog(
                activity,
                { v, h, m ->
                    this@setupChooseTime.text = if (m > 9) "$h:$m" else "$h:0$m"
                },
                dateAndTime.get(Calendar.HOUR),
                dateAndTime.get(Calendar.MONTH), true)
                .show()
    }
}