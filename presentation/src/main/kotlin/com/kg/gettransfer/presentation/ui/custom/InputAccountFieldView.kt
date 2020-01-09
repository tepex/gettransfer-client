package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import com.kg.gettransfer.R
import androidx.core.view.isVisible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_input_account_field.view.*

class InputAccountFieldView@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        LinearLayout(context, attrs, defStyleAttr), LayoutContainer {

    override val containerView: View =
        LayoutInflater.from(context).inflate(R.layout.view_input_account_field, this, true)

    init {
        context.withStyledAttributes(attrs, R.styleable.InputAccountFieldView) {
            fieldLayout.hint = getString(R.styleable.InputAccountFieldView_account_field_view_hint)
            divider.isVisible = getBoolean(R.styleable.InputAccountFieldView_account_field_view_divider_visible, true)
            val drawableResId = getResourceId(R.styleable.InputAccountFieldView_account_field_view_icon, View.NO_ID)
            fieldText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, drawableResId), null, null, null)
            fieldText.inputType = getInt(R.styleable.InputAccountFieldView_android_inputType, EditorInfo.TYPE_CLASS_TEXT)
        }
    }

    fun requestInputFieldFocus() {
        fieldText.requestFocus()
    }

    fun disableInputField() {
        fieldText.isEnabled = false
    }
}