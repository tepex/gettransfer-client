package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.util.AttributeSet
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.onTextChanged
import com.kg.gettransfer.utilities.PhoneNumberFormatter
import kotlinx.android.synthetic.main.view_input_account_field.view.*

class InputAccountPhoneFieldView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : InputAccountFieldView(context, attrs, defStyleAttr) {

    init {
        fieldText.addTextChangedListener(PhoneNumberFormatter())
    }

    fun setOnTextChanged(onTextChanged: ((text: String) -> Unit)? = null) {
        with(fieldText) {
            onTextChanged { text ->
                if (text.isEmpty() && isFocused) {
                    setText("+")
                    setSelection(1)
                }
                onTextChanged?.invoke(text)
            }
        }
    }

    fun setOnFocusChangeListener(onFocusChanged: ((hasFocus: Boolean) -> Unit)? = null) {
        with(fieldText) {
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    if (text.toString().isEmpty()) {
                        val phoneCode = Utils.getPhoneCodeByCountryIso(context)
                        setText(if (phoneCode > 0) "+$phoneCode" else "+")
                    }
                    post { setSelection(this.text.toString().length) }
                }
                onFocusChanged?.invoke(hasFocus)
            }
        }
    }
}
