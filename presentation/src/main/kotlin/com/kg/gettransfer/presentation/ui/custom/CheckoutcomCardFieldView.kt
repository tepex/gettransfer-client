package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import androidx.core.content.withStyledAttributes
import com.google.android.material.textfield.TextInputLayout
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.ui.onTextChanged
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_checkoutcom_card_field.*

class CheckoutcomCardFieldView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextInputLayout(context, attrs, defStyleAttr), LayoutContainer {

    override val containerView: View =
        LayoutInflater.from(context).inflate(R.layout.view_checkoutcom_card_field, this, true)

    var text: String
        get() = field_input.text.toString()
        set(value) { field_input.setText(value) }

    var hint: String
        get() = input_layout.hint.toString()
        set(value) { input_layout.hint = value }

    private var errorIsShowed = false

    init {
        context.withStyledAttributes(attrs, R.styleable.CheckoutcomCardFieldView) {
            input_layout.hint = getString(R.styleable.CheckoutcomCardFieldView_hint)
            field_input.inputType =
                getInteger(R.styleable.CheckoutcomCardFieldView_android_inputType, InputType.TYPE_CLASS_TEXT)
            getString(R.styleable.CheckoutcomCardFieldView_android_digits)?.let { digits ->
                field_input.keyListener = DigitsKeyListener.getInstance(digits)
            }
        }

        input_layout.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                if (input_layout.height > 0) {
                    input_layout.viewTreeObserver.removeOnPreDrawListener(this)
                    updateHintPosition(field_input.hasFocus(), !field_input.text.isNullOrEmpty())
                    return false
                }
                return true
            }
        })

        field_input.setOnFocusChangeListener { _, hasFocus ->
            updateHintPosition(hasFocus, !field_input.text.isNullOrEmpty())
        }

        field_input.onTextChanged { if (errorIsShowed) hideError() }
    }

    private fun updateHintPosition(hasFocus: Boolean, hasText: Boolean) {
        if (hasFocus || hasText) {
            val topPadding = context.resources.getDimensionPixelSize(R.dimen.checkoutcom_card_field_hint_padding)
            field_input.setPadding(0, topPadding, 0, 0)
        } else {
            field_input.setPadding(0, 0, 0, getTextInputLayoutTopSpace())
        }
    }

    private fun getTextInputLayoutTopSpace(): Int {
        var currentView: View = field_input
        var space = 0
        do {
            space += currentView.top
            (currentView.parent as? View)?.let { currentView = it }
        } while (currentView.id != input_layout.id)
        return space
    }

    fun setMaxLength(length: Int) {
        field_input.filters = arrayOf(InputFilter.LengthFilter(length))
    }

    fun showError() {
        errorIsShowed = true
        field.setBackgroundResource(R.drawable.back_checkoutcom_card_field_error)
    }

    private fun hideError() {
        errorIsShowed = false
        field.setBackgroundResource(R.drawable.back_checkoutcom_card_field)
    }
}
