package com.kg.gettransfer.presentation.ui

import com.kg.gettransfer.R
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_about_item.*

class AboutItem @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {
    override val containerView: View
    init {
        containerView = LayoutInflater.from(context).inflate(R.layout.view_about_item, this, true)

        if(attrs != null) {
            val ta           = context.obtainStyledAttributes(attrs, R.styleable.AboutItem)
            field_title.text = ta.getString(R.styleable.AboutItem_title_about_item)
            field_text.text  = ta.getString(R.styleable.AboutItem_text_about_item)
            field_img.setImageDrawable(ta.getDrawable(R.styleable.AboutItem_img_about_item))
            ta.recycle()
        }
    }
}