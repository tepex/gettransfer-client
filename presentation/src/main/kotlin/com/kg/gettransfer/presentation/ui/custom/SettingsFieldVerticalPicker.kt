package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.kg.gettransfer.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_settings_field_vertical_picker.view.*

class SettingsFieldVerticalPicker @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {
    override val containerView: View = LayoutInflater.from(context).inflate(R.layout.view_settings_field_vertical_picker, this, true)

    init {
        if(attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.SettingsFieldVerticalPicker)
            titleText.text = ta.getString(R.styleable.SettingsFieldVerticalPicker_settingsTitlePicker)
            ta.recycle()
        }
    }
}