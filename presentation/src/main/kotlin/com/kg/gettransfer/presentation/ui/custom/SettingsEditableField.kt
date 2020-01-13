package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.content.withStyledAttributes
import com.kg.gettransfer.R
import androidx.core.view.isVisible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_settings_editable_field.view.*

class SettingsEditableField @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr), LayoutContainer {
    override val containerView: View =
        LayoutInflater.from(context).inflate(R.layout.view_settings_editable_field, this, true)

    init {
        context.withStyledAttributes(attrs, R.styleable.SettingsEditableField) {
            input_layout.hint = getString(R.styleable.SettingsEditableField_settingsEditableFieldHint)
            field_chevron.isVisible = getBoolean(R.styleable.SettingsEditableField_settingsEditableFieldChevron, true)
            getBoolean(R.styleable.SettingsEditableField_settingsEditableFieldEnabled, true).let { enabled ->
                field_input.isFocusable = enabled
                if (!enabled) field_input.isClickable = true
            }
            field_input.inputType = getInt(R.styleable.SettingsEditableField_android_inputType, EditorInfo.TYPE_CLASS_TEXT)
        }
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        super.setOnClickListener(listener)
        field_input.setOnClickListener(listener)
    }

    fun setText(text: String) {
        if (text.isNotEmpty()) field_input.setText(text)
    }
}
