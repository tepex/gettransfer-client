package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_settings_field_picker.*

class SettingsFieldPicker @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {
    override val containerView: View
    init {
        containerView = LayoutInflater.from(context).inflate(R.layout.view_settings_field_picker, this, true)

        if(attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.SettingsFieldPicker)
            field_title.text = ta.getString(R.styleable.SettingsFieldPicker_settingsTitlePicker)
            field_divider.isVisible = ta.getBoolean(R.styleable.SettingsFieldPicker_settingsPickerDivider, true)
            if (ta.getBoolean(R.styleable.SettingsFieldPicker_settingsPickerBlackText, false)) {
                field_title.setTextColor(ContextCompat.getColor(context, R.color.color_gtr_black))
            }
            ta.recycle()
        }
    }
}