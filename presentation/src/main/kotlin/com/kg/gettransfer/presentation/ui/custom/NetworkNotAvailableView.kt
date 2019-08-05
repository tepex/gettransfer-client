package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_network_not_available.view.*

class NetworkNotAvailableView @JvmOverloads constructor(
    context: Context,
    attribute: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attribute, defStyleAttr), LayoutContainer {

    override val containerView: View? =
        LayoutInflater.from(context).inflate(R.layout.view_network_not_available, this, true)

    init {
        attribute?.let {
            context.obtainStyledAttributes(attribute, R.styleable.NetworkNotAvailableView).apply {
                textNetworkNotAvailable.text = getString(R.styleable.NetworkNotAvailableView_text_no_internet)
                recycle()
            }
        }
    }

    fun changeViewVisibility(isShow: Boolean) {
        textNetworkNotAvailable.isVisible = isShow
    }

    fun isShowing() = textNetworkNotAvailable.isVisible
}
