package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import androidx.core.content.ContextCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import com.kg.gettransfer.R
import androidx.core.view.isVisible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_input_account_field.view.*

class InputAccountFieldView@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        LinearLayout(context, attrs, defStyleAttr), LayoutContainer {

    override val containerView: View
    init {
        containerView = LayoutInflater.from(context).inflate(R.layout.view_input_account_field, this, true)

        if(attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.InputAccountFieldView)
            fieldLayout.hint = ta.getString(R.styleable.InputAccountFieldView_account_field_view_hint)
            divider.isVisible = ta.getBoolean(R.styleable.InputAccountFieldView_account_field_view_divider_visible, true)
            val drawableResId = ta.getResourceId(R.styleable.InputAccountFieldView_account_field_view_icon, View.NO_ID)
            fieldText.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context, drawableResId), null, null, null)
            fieldText.inputType = ta.getInt(R.styleable.InputAccountFieldView_android_inputType, EditorInfo.TYPE_CLASS_TEXT)
            ta.recycle()
        }
    }
}