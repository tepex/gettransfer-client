package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater

import com.kg.gettransfer.R

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_transfer_details_carrier_info_field.*

class TransferDetailsCarrierInfoField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {

    override val containerView =
        LayoutInflater.from(context).inflate(R.layout.view_transfer_details_carrier_info_field, this, true)

    init {
        if (attrs != null) {
            context.obtainStyledAttributes(attrs, R.styleable.TransferDetailsCarrierInfoField).apply {
                field_title.text = getString(R.styleable.TransferDetailsCarrierInfoField_carrier_field_title)
                field_text.text = getString(R.styleable.TransferDetailsCarrierInfoField_carrier_field_text)
                recycle()
            }
        }
    }
}
