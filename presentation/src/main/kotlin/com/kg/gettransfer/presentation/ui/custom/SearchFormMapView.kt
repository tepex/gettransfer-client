package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.kg.gettransfer.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.search_address.view.*
import kotlinx.android.synthetic.main.search_form_map.*

class SearchFormMapView @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        defStyle: Int = 0
) : ConstraintLayout(context, attributeSet, defStyle), LayoutContainer {
    override val containerView: View = LayoutInflater.from(context).inflate(R.layout.search_form_map, this, true)

    fun setSearchClickListener(clickListener: ((View) -> Unit)?) {
        searchField.setOnClickListener(clickListener)
    }

    fun setSearchFieldText(value: String) {
        if (value != searchField.text) {
            searchField.text = value
        }
    }

    fun setFieldMode(isTo: Boolean) {
        tv_point.text = if (isTo) "B" else "A"
        searchField.sub_title.text = context.getString(if (isTo) R.string.LNG_FIELD_DESTINATION else R.string.LNG_FIELD_SOURCE_PICKUP)
    }
}