package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_settings_editable_field.view.*

class SettingsEditableField @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {
    override val containerView: View
    init {
        containerView = LayoutInflater.from(context).inflate(R.layout.view_settings_editable_field, this, true)

        if(attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.SettingsEditableField)
            input_layout.hint = ta.getString(R.styleable.SettingsEditableField_settingsEditableFieldHint)
            field_chevron.isVisible = ta.getBoolean(R.styleable.SettingsEditableField_settingsEditableFieldChevron, true)
            ta.getBoolean(R.styleable.SettingsEditableField_settingsEditableFieldEnabled, true).let {
                field_input.isFocusable = it
                if (!it) field_input.isClickable = true
            }
            field_input.inputType = ta.getInt(R.styleable.SettingsEditableField_android_inputType, EditorInfo.TYPE_CLASS_TEXT)
            ta.recycle()
        }
    }
}