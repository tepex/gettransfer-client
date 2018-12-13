package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.support.constraint.ConstraintLayout
import com.kg.gettransfer.R
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_rate_field.*

class RateField @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {
    override val containerView: View
    init {
        containerView = LayoutInflater.from(context).inflate(R.layout.view_rate_field, this, true)

        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.RateField)
            tv_rate_title.text = ta.getString(R.styleable.RateField_rate_title)
            tv_rate_sub_title.text = ta.getString(R.styleable.RateField_rate_sub_title)
            ta.recycle()
        }
    }

}