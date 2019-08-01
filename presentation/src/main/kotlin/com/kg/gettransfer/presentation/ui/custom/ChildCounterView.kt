package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View

import com.kg.gettransfer.R

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_child_seat_type_counter.*

class ChildCounterView @JvmOverloads constructor(
        context: Context,
        attribute: AttributeSet? = null,
        defStyleAttr: Int = 0): ConstraintLayout(context, attribute, defStyleAttr), LayoutContainer {
    override val containerView: View? = LayoutInflater.from(context).inflate(R.layout.view_child_seat_type_counter, this, true)

    init {
        attribute?.let {
            context.obtainStyledAttributes(attribute, R.styleable.ChildCounterView).apply {
                child_seat_title.text = getString(R.styleable.ChildCounterView_counter_title)
                child_seat_description.text = getString(R.styleable.ChildCounterView_counter_description)
                recycle()
            }
        }
    }
}