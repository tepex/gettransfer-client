package com.kg.gettransfer.presentation.ui

import com.kg.gettransfer.R
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_transfer_details_field.*

class TransferDetailsField @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {
    override val containerView: View
        init {
            containerView = LayoutInflater.from(context).inflate(R.layout.view_transfer_details_field, this, true)

            if(attrs != null) {
                val ta           = context.obtainStyledAttributes(attrs, R.styleable.TransferDetailsField)
                field_title.text = ta.getString(R.styleable.TransferDetailsField_title_transfer_details)
                field_text.text  = ta.getString(R.styleable.TransferDetailsField_input_transfer_details)
                field_icon.setImageDrawable(ta.getDrawable(R.styleable.TransferDetailsField_icon_img_transfer_details))
                ta.recycle()
            }
        }
}