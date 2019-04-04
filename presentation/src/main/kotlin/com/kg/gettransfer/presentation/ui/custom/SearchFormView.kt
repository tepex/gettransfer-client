package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isInvisible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.search_form.*

class SearchFormView @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyle: Int = 0
) : ConstraintLayout(context, attributeSet, defStyle), LayoutContainer {
    override val containerView: View = LayoutInflater.from(context).inflate(R.layout.search_form, this, true)

    init {
        if (attributeSet != null)
            context.obtainStyledAttributes(attributeSet, R.styleable.SearchFormView)
                    .apply {
                        fl_inverse.isInvisible = getBoolean(R.styleable.SearchFormView_isInverseInvisible, false)
                        recycle()
                    }
    }
}