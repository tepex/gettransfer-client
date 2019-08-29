package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_settings_field_switch.*

class SettingsFieldSwitch @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {
    override val containerView: View = LayoutInflater.from(context).inflate(R.layout.view_settings_field_switch, this, true)

    init {
        if(attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.SettingsFieldSwitch)
            field_title.text = ta.getString(R.styleable.SettingsFieldSwitch_settingsTitleSwitch)
            field_divider.isVisible = ta.getBoolean(R.styleable.SettingsFieldSwitch_settingsShowDivider, true)
            ta.recycle()
        }
    }

    fun hideDivider() {
        field_divider.isVisible = false
    }

    fun showDivider() {
        field_divider.isVisible = true
    }
}