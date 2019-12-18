package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.kg.gettransfer.R
import androidx.core.view.isGone
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.a_b_orange_view.view.*
import kotlinx.android.synthetic.main.search_form_main.*

class SearchFormMainView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attributeSet, defStyle), LayoutContainer {
    override val containerView: View = LayoutInflater.from(context).inflate(R.layout.search_form_main, this, true)

    private var ivSelectFieldToIsGone: Boolean = false

    init {
        if (attributeSet != null) {
            context.obtainStyledAttributes(attributeSet, R.styleable.SearchFormMainView)
                .apply {
                    ivSelectFieldToIsGone =
                        getBoolean(R.styleable.SearchFormMainView_ivSelectFieldToGone, ivSelectFieldToIsGone)
                    ivSelectFieldTo.isGone = ivSelectFieldToIsGone
                    ivSelectFieldFrom.isGone =
                        getBoolean(R.styleable.SearchFormMainView_ivSelectFieldFromGone, false)
                    setBackgroundResource(
                        when (getBoolean(R.styleable.SearchFormMainView_roundedTopCorners, false)) {
                            true  -> R.drawable.back_top_rounded
                            false -> R.color.colorWhite
                        }
                    )
                    recycle()
                }
        }
    }

    fun setSearchFromClickListener(clickListener: ((View) -> Unit)?) {
        searchFrom.setOnClickListener(clickListener)
    }

    fun setSearchToClickListener(clickListener: ((View) -> Unit)?) {
        searchTo.setOnClickListener(clickListener)
    }

    fun setHourlyClickListener(clickListener: ((View) -> Unit)?) {
        rl_hourly.setOnClickListener(clickListener)
    }

    fun setIvSelectFieldFromClickListener(clickListener: ((View) -> Unit)?) {
        ivSelectFieldFrom.setOnClickListener(clickListener)
    }

    fun hourlyMode(turnOn: Boolean = true) {
        rl_hourly.isGone = !turnOn
        hourly_point.isGone = !turnOn
        tv_b_point.isGone = turnOn
        searchTo.isGone = turnOn
        ivSelectFieldTo.isGone = ivSelectFieldToIsGone || turnOn
        switchLayout.isGone = !turnOn
        if (turnOn) switchPointB(switchPointB.isChecked) else searchTo.isGone = turnOn
    }

    fun switchPointB(checked: Boolean) {
        searchTo.isGone = !checked
    }

    fun setCurrentHoursText(value: String) {
        tvCurrent_hours.text = value
    }

    fun isEmptySearchFrom(): Boolean {
        return searchFrom.text.isEmpty()
    }

    fun isEmptySearchTo(): Boolean {
        return searchTo.text.isEmpty()
    }

    fun setSearchFrom(value: String) {
        if (value != searchFrom.text) {
            searchFrom.text = value
        }
    }

    fun setSearchTo(value: String) {
        if (value != searchTo.text) {
            searchTo.text = value
        }
    }

    fun selectSearchFrom() {
        setSearchFromAlpha(ALPHA_FULL)
        if (!ivSelectFieldToIsGone) {
            ivSelectFieldTo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.btn_pin_location))
        }
    }

    fun selectSearchTo() {
        setSearchFromAlpha(ALPHA_DISABLED)
        if (!ivSelectFieldToIsGone) {
            ivSelectFieldTo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.btn_pin_enabled))
        }
    }

    private fun setSearchFromAlpha(alpha: Float) {
        searchFrom.alpha = alpha
        tv_a_point.alpha = alpha
    }

    companion object {
        const val ALPHA_FULL = 1f
        const val ALPHA_DISABLED = 0.3f
    }
}
