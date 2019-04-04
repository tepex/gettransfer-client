package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.kg.gettransfer.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.create_order_field.view.*

class OrderFieldView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {
    override val containerView: View = LayoutInflater.from(context).inflate(R.layout.create_order_field, this, true)

    init {
        attrs?.let {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.OrderFieldView)
            hint_title.text = ta.getString(R.styleable.OrderFieldView_order_title)
            val drawableResId = ta.getResourceId(R.styleable.OrderFieldView_icon, -1)
            img_icon.setImageDrawable(ContextCompat.getDrawable(context, drawableResId))
            ta.recycle()
        }
    }
}