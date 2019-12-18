package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.kg.gettransfer.R
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.kg.gettransfer.presentation.ui.SearchFragment
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.a_b_orange_view.view.hourly_point
import kotlinx.android.synthetic.main.a_b_orange_view.view.tv_b_point
import kotlinx.android.synthetic.main.search_address.*
import kotlinx.android.synthetic.main.search_form.*

class SearchFormView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attributeSet, defStyle), LayoutContainer {
    override val containerView: View = LayoutInflater.from(context).inflate(R.layout.search_form, this, true)

    fun changeFocus(isToField: Boolean) {
        if (isToField) searchTo.changeFocus() else searchFrom.changeFocus()
    }

    fun initFromWidget(parent: SearchFragment, title: String) {
        searchFrom.initWidget(parent, false)
        searchFrom.sub_title.text = title
    }

    fun initToWidget(parent: SearchFragment, title: String) {
        searchTo.initWidget(parent, true)
        searchTo.sub_title.text = title
    }

    fun changeViewToHourlyDuration() {
        searchTo.isGone = true
        tv_b_point.isVisible = false
        hourly_point.isVisible = true
        rl_hourly.isVisible = true
    }

    fun findPopularPlace(isToField: Boolean, place: String) {
        val searchField = if (isToField) searchTo else searchFrom
        searchField.initText(place, true, true)
    }
}
