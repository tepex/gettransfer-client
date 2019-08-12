package com.kg.gettransfer.presentation.ui

import android.content.Context

import androidx.core.content.ContextCompat

import android.util.AttributeSet

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout

import com.kg.gettransfer.R

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_about_item.*

class AboutItem @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), LayoutContainer {
    override val containerView: View =
            LayoutInflater.from(context).inflate(R.layout.view_about_item, this, true)

    init {

        if(attrs != null) {
            val ta           = context.obtainStyledAttributes(attrs, R.styleable.AboutItem)
            tvTitle.text = ta.getString(R.styleable.AboutItem_title_about_item)
            val drawableResId = ta.getResourceId(R.styleable.AboutItem_img_about_item, View.NO_ID)
            ivOnboard.setImageDrawable(ContextCompat.getDrawable(context, drawableResId))
            ta.recycle()
        }
    }
}