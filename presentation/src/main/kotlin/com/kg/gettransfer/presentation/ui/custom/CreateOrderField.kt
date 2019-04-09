package com.kg.gettransfer.presentation.ui.custom

import com.kg.gettransfer.R
import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_create_order_field_new.*

class CreateOrderField @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        LinearLayout(context, attrs, defStyleAttr), LayoutContainer {
    override val containerView: View =
            LayoutInflater.from(context)
                    .inflate(R.layout.view_create_order_field_new, this, true)

    init {
        if(attrs != null) {
                val ta      = context.obtainStyledAttributes(attrs, R.styleable.CreateOrderField)
                input_layout.hint        = ta.getString(R.styleable.CreateOrderField_hint)
                field_input.isFocusable = ta.getBoolean(R.styleable.CreateOrderField_isFocusable, true)
                field_input.inputType   = ta.getInteger(R.styleable.CreateOrderField_inputType, InputType.TYPE_CLASS_TEXT)
                val drawableResId = ta.getResourceId(R.styleable.CreateOrderField_icon_img, -1)
                field_input.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        ContextCompat.getDrawable(context, drawableResId), null,
                        ContextCompat.getDrawable(context, R.drawable.ic_arrow_right), null)
                ta.recycle()
            }
        }
}