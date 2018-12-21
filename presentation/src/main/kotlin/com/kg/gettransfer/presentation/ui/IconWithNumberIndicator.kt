package com.kg.gettransfer.presentation.ui

import com.kg.gettransfer.R
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import com.kg.gettransfer.extensions.isVisible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_icon_with_number_indicator.*

class IconWithNumberIndicator @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {

    override val containerView = LayoutInflater.from(context).inflate(R.layout.view_icon_with_number_indicator, this, true)

    init {
        if(attrs != null) {
            context.obtainStyledAttributes(attrs, R.styleable.IconWithNumberIndicator).apply {
                imgIndicator.setImageDrawable(getDrawable(R.styleable.IconWithNumberIndicator_icon_with_number_indicator))
                numberIndicator.isVisible = getBoolean(R.styleable.IconWithNumberIndicator_is_showing_number, true)
                recycle()
            }
        }
    }
}