package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import com.kg.gettransfer.R
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_transfer_main_info.*

class TransferMainInfoView @JvmOverloads constructor(
        context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0):
        ConstraintLayout(context, attributes, defStyleAttr), LayoutContainer {
    override val containerView: View

    init {
        containerView = LayoutInflater.from(context).inflate(R.layout.view_transfer_main_info, this, true)

        if(attributes != null) {
            val ta = context.obtainStyledAttributes(attributes, R.styleable.TransferMainInfoView)
            tv_distance.text = ta.getString(R.styleable.TransferMainInfoView_distance)
            tv_distance_title.text = ta.getString(R.styleable.TransferMainInfoView_distance_title)
            tv_time.text = ta.getString(R.styleable.TransferMainInfoView_time)
            tv_time_title.text = ta.getString(R.styleable.TransferMainInfoView_time_title)
            tv_price.text = ta.getString(R.styleable.TransferMainInfoView_price)
            tv_price_title.text = ta.getString(R.styleable.TransferMainInfoView_price_title)
            ta.recycle()
        }
    }
}
