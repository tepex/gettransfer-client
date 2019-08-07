package com.kg.gettransfer.presentation.ui

import android.content.Context

import android.support.constraint.ConstraintLayout

import android.util.AttributeSet

import android.view.LayoutInflater
import android.view.View

import com.kg.gettransfer.R

import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.search_address_read_only.view.*

class SearchAddressReadOnly @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {

    override val containerView: View

    var text: String
        get() = addressField.text.toString()
        set(value) { addressField.setText(value) }

    init {
        containerView = LayoutInflater.from(context).inflate(R.layout.search_address_read_only, this, true)
        if (attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.SearchAddressReadOnly)
            addressField.hint = ta.getString(R.styleable.SearchAddressReadOnly_hint)
            sub_title.text = ta.getString(R.styleable.SearchAddressReadOnly_title)
            ta.recycle()
        }
    }

}
