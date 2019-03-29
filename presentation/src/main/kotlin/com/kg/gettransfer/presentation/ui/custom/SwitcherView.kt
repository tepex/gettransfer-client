package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.kg.gettransfer.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_switcher.view.*

class SwitcherView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {

    override val containerView: View = LayoutInflater.from(context).inflate(R.layout.view_switcher, this, true)

    init {
        if (attrs != null) {
            context.obtainStyledAttributes(attrs, R.styleable.SwitcherView).apply {
                switch_title.text = getString(R.styleable.SwitcherView_switcher_title)
                recycle()
            }
        }
    }
}