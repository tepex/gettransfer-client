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
import kotlinx.android.synthetic.main.view_settings_field_horizontal_picker.*

class SettingsFieldHorizontalPicker @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {
    override val containerView: View = LayoutInflater.from(context).inflate(R.layout.view_settings_field_horizontal_picker, this, true)

    init {
        if(attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.SettingsFieldHorizontalPicker)
            field_title.text = ta.getString(R.styleable.SettingsFieldHorizontalPicker_settingsTitlePicker)
            field_divider.isVisible = ta.getBoolean(R.styleable.SettingsFieldHorizontalPicker_settingsPickerDivider, true)
            if (ta.getBoolean(R.styleable.SettingsFieldHorizontalPicker_settingsPickerBlackText, false)) {
                field_title.setTextColor(ContextCompat.getColor(context, R.color.color_gtr_black))
            }
            ta.recycle()
        }
    }
}