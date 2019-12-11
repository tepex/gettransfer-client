package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isInvisible
import com.kg.gettransfer.extensions.isVisible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_transfer_main_info_item.view.*

class TransferMainInfoItemView @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attributes, defStyleAttr), LayoutContainer {

    override val containerView: View =
        LayoutInflater.from(context).inflate(R.layout.view_transfer_main_info_item, this, true)

    init {
        if (attributes != null) {
            val ta = context.obtainStyledAttributes(attributes, R.styleable.TransferMainInfoItemView)
            tv_title.text = ta.getString(R.styleable.TransferMainInfoItemView_transfer_main_info_title)
            ta.recycle()
        }
    }

    fun setValue(value: String) {
        tv_value.text = value
        hideDash()
        showTitle()
    }

    fun setTitle(title: String) {
        tv_title.text = title
        showTitle()
    }

    private fun showTitle() {
        tv_title.isVisible = true
    }

    fun hideTitle() {
        tv_title.isVisible = false
    }

    fun showDash() {
        dash.isVisible = true
        tv_value.isInvisible = true
    }

    private fun hideDash() {
        dash.isVisible = false
        tv_value.isInvisible = false
    }
}
