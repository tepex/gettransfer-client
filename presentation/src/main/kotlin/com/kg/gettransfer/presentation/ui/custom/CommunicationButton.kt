package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible

import com.kg.gettransfer.R

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_communication_button.view.*

class CommunicationButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes), LayoutContainer {

    override val containerView: View =
        LayoutInflater.from(context).inflate(R.layout.view_communication_button, this, true)

    init {
        attrs?.let {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.CommunicationButton)
            btnName.text = ta.getString(R.styleable.CommunicationButton_btn_name)
            val drawableResId = ta.getResourceId(R.styleable.CommunicationButton_btn_img, View.NO_ID)
            btnImg.setImageDrawable(ContextCompat.getDrawable(context, drawableResId))
            btnImg.backgroundTintList = ta.getColorStateList(R.styleable.CommunicationButton_btn_color)
                ?: ContextCompat.getColorStateList(context, R.color.colorWhite)
            ta.recycle()
        }
    }

    fun setCounter(count: Int) {
        if (count > 0) {
            tvEventsCounter.text = count.toString()
            tvEventsCounter.isVisible = true
        } else {
            tvEventsCounter.isVisible = false
        }
    }
}
