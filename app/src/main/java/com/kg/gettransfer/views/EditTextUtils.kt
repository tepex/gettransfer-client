package com.kg.gettransfer.views


import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.ContextWrapper
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.*
import android.widget.EditText
import com.kg.gettransfer.R
import java.text.DateFormat
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


private fun EditText.clearListenersFixFocus() {
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


private fun getActivity(context: Context): Activity? {
    var context: Context? = context
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

class DateField : EditText {
    constructor(c: Context) : super(c)
    constructor(c: Context, attrs: AttributeSet) : super(c, attrs)
    constructor(c: Context, attrs: AttributeSet, defStyle: Int) : super(c, attrs, defStyle)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.actionMasked == MotionEvent.ACTION_UP) {
            requestFocus()
            performClick()
        }

        return true
    }

    init {
        val icon = R.drawable.ic_calendar_black_24dp
        setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0)

        clearListenersFixFocus()

        setOnClickListener {
            requestFocus()

            val c = Calendar.getInstance()
            try {
                c.time = DateFormat
                        .getDateInstance()
                        .parse(text.toString())
            } catch (e: Exception) {
            }

            DatePickerDialog(
                    getActivity(context),
                    { _, y, m, d ->
                        val c = Calendar.getInstance()
                        c.set(Calendar.YEAR, y)
                        c.set(Calendar.MONTH, m)
                        c.set(Calendar.DAY_OF_MONTH, d)
                        setText(DateFormat
                                .getDateInstance()
                                .format(c.time))
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH))
                    .show()
        }
    }
}

class TimeField : EditText {
    constructor(c: Context) : super(c)
    constructor(c: Context, attrs: AttributeSet) : super(c, attrs)
    constructor(c: Context, attrs: AttributeSet, defStyle: Int) : super(c, attrs, defStyle)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.actionMasked == MotionEvent.ACTION_UP) {
            requestFocus()
            performClick()
        }

        return true
    }

    init {
        val icon = R.drawable.ic_access_time_black_24dp
        setCompoundDrawablesWithIntrinsicBounds(0, 0, icon, 0)

        clearListenersFixFocus()

        setOnClickListener {
            requestFocus()

            val c = Calendar.getInstance()

            try {
                c.time = DateFormat
                        .getTimeInstance(DateFormat.SHORT)
                        .parse(text.toString())
            } catch (e: Exception) {
            }
            android.app.TimePickerDialog(
                    getActivity(context),
                    { _, h, m ->
                        val c = Calendar.getInstance()
                        c.set(Calendar.HOUR_OF_DAY, h)
                        c.set(Calendar.MINUTE, m)
                        setText(DateFormat
                                .getTimeInstance(DateFormat.SHORT)
                                .format(c.time))
                    },
                    c.get(Calendar.HOUR_OF_DAY),
                    c.get(Calendar.MINUTE),
                    android.text.format.DateFormat.is24HourFormat(context))
                    .show()
        }
    }
}