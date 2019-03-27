package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_region.view.*

class RegionView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {

    override val containerView: View =
            LayoutInflater.from(context).inflate(R.layout.view_region,this, true)
    init {
        attrs?.let {
            context.obtainStyledAttributes(attrs, R.styleable.RegionView).apply {
                tvRegion.text = getString(R.styleable.RegionView_title_region)
                recycle()
            }
        }
    }
}