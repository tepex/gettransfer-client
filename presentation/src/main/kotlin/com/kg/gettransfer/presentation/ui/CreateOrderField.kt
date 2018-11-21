package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_create_order_field.view.*

class CreateOrderField @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {
    override val containerView: View
        init {
            containerView = LayoutInflater.from(context).inflate(R.layout.view_create_order_field, this, true)

            if(attrs != null) {
                val ta = context.obtainStyledAttributes(attrs, R.styleable.CreateOrderField)
                field_title.text = ta.getString(R.styleable.CreateOrderField_title)
                field_input.hint = ta.getString(R.styleable.CreateOrderField_hint)
            }
        }
}