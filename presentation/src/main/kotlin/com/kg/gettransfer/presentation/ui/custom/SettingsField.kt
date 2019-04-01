package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_settings_field.*

class SettingsField @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {
    override val containerView: View
    init {
        containerView = LayoutInflater.from(context).inflate(R.layout.view_settings_field, this, true)

        if(attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.SettingsField)
            field_title.text = ta.getString(R.styleable.SettingsField_settingsTitle)
            ta.getBoolean(R.styleable.SettingsField_isSwitchField, false).let {
                field_chevron.isVisible = !it
                switch_button.isVisible = it
            }
            ta.recycle()
        }
    }
}