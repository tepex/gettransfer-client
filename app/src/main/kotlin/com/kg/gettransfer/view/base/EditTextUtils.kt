package com.kg.gettransfer.view.base


import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.AttributeSet
import android.view.*
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
            val clearIcon = if (editable?.isNotEmpty() == true) R.drawable.ic_clear_gray_20dp else 0
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


fun EditText.clearListenersFixFocus() {
    setOnTouchListener(null)

    setOnLongClickListener(null)

    setOnGenericMotionListener(null)

    isLongClickable = false

    customSelectionActionModeCallback = object : ActionMode.Callback {
        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = false
        override fun onDestroyActionMode(mode: ActionMode) = Unit
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean = false
        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean = false
    }

    setTextIsSelectable(false)

    isFocusableInTouchMode = true
}


fun TextView.clearListenersFixFocus() {
    setOnTouchListener(null)

    setOnLongClickListener(null)

    setOnGenericMotionListener(null)

    isLongClickable = false

    customSelectionActionModeCallback = object : ActionMode.Callback {
        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean = false
        override fun onDestroyActionMode(mode: ActionMode) = Unit
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean = false
        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean = false
    }

    setTextIsSelectable(false)

    isFocusableInTouchMode = true
}

/* @TODO: УБИВАТЬ ЗА ТАКОЕ!!! */
fun getActivity(c: Context): Activity? {
    var context: Context? = c
    while(context is ContextWrapper) {
        if(context is Activity) return context
        context = context.baseContext
    }
    return null
}

class DateField: TextView {
    constructor(c: Context) : super(c)
    constructor(c: Context, attrs: AttributeSet) : super(c, attrs)
    constructor(c: Context, attrs: AttributeSet, defStyle: Int) : super(c, attrs, defStyle)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.actionMasked == MotionEvent.ACTION_UP) {
            requestFocus()
            performClick()
        }

        return true
    }

    init {
        clearListenersFixFocus()

        setOnClickListener {
            requestFocus()

            val c = Calendar.getInstance()
            try {
                c.time = DateFormat
                        .getMediumDateFormat(context)
                        .parse(text.toString())
            } catch (e: Exception) {
            }

            DatePickerDialog(
                    getActivity(context),
                    { _, y, m, d ->
                        val cal = Calendar.getInstance()
                        cal.set(Calendar.YEAR, y)
                        cal.set(Calendar.MONTH, m)
                        cal.set(Calendar.DAY_OF_MONTH, d)
                        text = DateFormat
                                .getMediumDateFormat(context)
                                .format(cal.time)
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH))
                    .show()
        }
    }
}


class TimeField : TextView {
    constructor(c: Context) : super(c)
    constructor(c: Context, attrs: AttributeSet) : super(c, attrs)
    constructor(c: Context, attrs: AttributeSet, defStyle: Int) : super(c, attrs, defStyle)

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.actionMasked == MotionEvent.ACTION_UP) {
            requestFocus()
            performClick()
        }

        return true
    }

    init {
        clearListenersFixFocus()

        setOnClickListener {
            requestFocus()

            val c = Calendar.getInstance()

            try {
                c.time = DateFormat
                        .getTimeFormat(context)
                        .parse(text.toString())
            } catch (e: Exception) {
            }
            android.app.TimePickerDialog(
                    getActivity(context),
                    { _, h, m ->
                        val cal = Calendar.getInstance()
                        cal.set(Calendar.HOUR_OF_DAY, h)
                        cal.set(Calendar.MINUTE, m)
                        text = DateFormat.getTimeFormat(context).format(cal.time)
                    },
                    c.get(Calendar.HOUR_OF_DAY),
                    c.get(Calendar.MINUTE),
                    android.text.format.DateFormat.is24HourFormat(context))
                    .show()
        }
    }
}