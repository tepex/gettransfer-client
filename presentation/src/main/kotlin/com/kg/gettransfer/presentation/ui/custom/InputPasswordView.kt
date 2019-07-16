package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.view_input_password.view.*

class InputPasswordView@JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        LinearLayout(context, attrs, defStyleAttr), LayoutContainer {

    private var passwordVisible = false

    override val containerView: View
    init {
        containerView = LayoutInflater.from(context).inflate(R.layout.view_input_password, this, true)

        if(attrs != null) {
            val ta = context.obtainStyledAttributes(attrs, R.styleable.InputPasswordView)
            passwordTextInputLayout.hint = ta.getString(R.styleable.InputPasswordView_password_view_hint)
            divider.isVisible = ta.getBoolean(R.styleable.InputPasswordView_password_view_divider_visible, true)
            ta.recycle()
        }

        initFocusChangeListeners()
        ivPasswordToggle.setOnClickListener { togglePassword() }
    }

    private fun initFocusChangeListeners() {
        etPassword.setOnFocusChangeListener { _, hasFocus -> changePasswordToggle(hasFocus) }
    }

    private fun togglePassword() {
        if (passwordVisible) {
            passwordVisible = false
            hidePassword()
        } else {
            passwordVisible = true
            showPassword()
        }
    }

    private fun showPassword() {
        ivPasswordToggle.setImageResource(R.drawable.ic_eye)
        etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        etPassword.text?.length?.let { etPassword.setSelection(it) }
    }

    private fun hidePassword() {
        ivPasswordToggle.setImageResource(R.drawable.ic_eye_off)
        etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        etPassword.text?.length?.let { etPassword.setSelection(it) }
    }

    private fun changePasswordToggle(hasFocus: Boolean) {
        when {
            hasFocus && passwordVisible -> ivPasswordToggle.setImageResource(R.drawable.ic_eye)
            !hasFocus && passwordVisible -> ivPasswordToggle.setImageResource(R.drawable.ic_eye_inactive)
            hasFocus && !passwordVisible -> ivPasswordToggle.setImageResource(R.drawable.ic_eye_off)
            else -> ivPasswordToggle.setImageResource(R.drawable.ic_eye_off_inactive)
        }
    }
}