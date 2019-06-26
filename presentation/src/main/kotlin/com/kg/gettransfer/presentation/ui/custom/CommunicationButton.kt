package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.kg.gettransfer.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_communication_button.view.*

class CommunicationButton @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        LinearLayout(context, attrs, defStyleAttr), LayoutContainer {

    override val containerView: View
    init {
        containerView = LayoutInflater.from(context).inflate(R.layout.view_communication_button, this, true)

        if(attrs != null) {
            val ta      = context.obtainStyledAttributes(attrs, R.styleable.CommunicationButton)
            btnName.text = ta.getString(R.styleable.CommunicationButton_btn_name)
            val drawableResId = ta.getResourceId(R.styleable.CommunicationButton_btn_img, View.NO_ID)
            btnImg.setImageDrawable(ContextCompat.getDrawable(context, drawableResId))
            btnImg.backgroundTintList = ta.getColorStateList(R.styleable.CommunicationButton_btn_color)
            ta.recycle()
        }
    }
}