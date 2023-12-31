package com.kg.gettransfer.presentation.ui.custom

import com.kg.gettransfer.R
import android.content.Context
import androidx.core.content.ContextCompat
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.textfield.TextInputLayout
import com.kg.gettransfer.presentation.ui.Utils
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_create_order_field.*

class CreateOrderField @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    TextInputLayout(context, attrs, defStyleAttr), LayoutContainer {

    override val containerView: View =
        LayoutInflater.from(context).inflate(R.layout.view_create_order_field, this, true)

    var text: String
        get() = field_input.text.toString()
        set(value) { field_input.setText(value) }

    var hint: String
        get() = input_layout.hint.toString()
        set(value) { input_layout.hint = value }

    init {
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.CreateOrderField)
            input_layout.hint = ta.getString(R.styleable.CreateOrderField_hint)
            field_input.isFocusable = ta.getBoolean(R.styleable.CreateOrderField_isFocusable, true)
            field_input.inputType =
                ta.getInteger(R.styleable.CreateOrderField_android_inputType, InputType.TYPE_CLASS_TEXT)
            val drawableResId = ta.getResourceId(R.styleable.CreateOrderField_icon_img, View.NO_ID)
            val showChevron = ta.getBoolean(R.styleable.CreateOrderField_showChevron, false)
            Utils.setDrawables(
                field_input,
                if (drawableResId != View.NO_ID) ContextCompat.getDrawable(context, drawableResId) else null,
                null,
                if (showChevron) ContextCompat.getDrawable(context, R.drawable.ic_arrow_right) else null,
                null
            )
            ta.recycle()
        }
    }
}
