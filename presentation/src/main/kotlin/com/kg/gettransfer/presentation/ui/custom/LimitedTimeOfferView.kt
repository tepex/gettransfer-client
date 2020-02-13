package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import com.kg.gettransfer.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_limited_time_offer.view.*

class LimitedTimeOfferView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {

    override val containerView: View? =
        LayoutInflater.from(context).inflate(R.layout.view_limited_time_offer, this, true)

    init {
        context.withStyledAttributes(attrs, R.styleable.LimitedTimeOfferView) {
            tvLimitOffer.text = getString(R.styleable.LimitedTimeOfferView_text_limit)
        }
    }
}