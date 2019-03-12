package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.kg.gettransfer.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_seats_number.view.*

class SeatsView @JvmOverloads constructor(
        context: Context,
        attribute: AttributeSet? = null,
        defStyleAttr: Int = 0):
        ConstraintLayout(context, attribute, defStyleAttr), LayoutContainer {
    override val containerView: View?
        init {
            containerView = LayoutInflater.from(context).inflate(R.layout.view_seats_number, this, true)

            context.obtainStyledAttributes(attribute, R.styleable.SeatsView).apply {
                tv_countPassengers.text = getString(R.styleable.SeatsView_passenger_seats)
                tvCountChildren.text = getString(R.styleable.SeatsView_child_seats)
                recycle()
            }
        }
}
