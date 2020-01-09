package com.kg.gettransfer.presentation.ui.custom

import android.content.Context
import android.os.CountDownTimer
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.ui.onTextChanged
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.activation_code_view.view.*

class ActivationCodeView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attributeSet, defStyle), LayoutContainer {

    override val containerView: View =
        LayoutInflater.from(context).inflate(R.layout.activation_code_view, this, true)

    private var errorIsShowed = false

    private var timerBtnResendCode: CountDownTimer? = null

    var listener: OnActivationCodeListener? = null

    init {
        context.withStyledAttributes(attributeSet, R.styleable.ActivationCodeView) {
            title.text = getString(R.styleable.ActivationCodeView_activation_code_title)
        }

        codeView.filters = arrayOf(InputFilter.AllCaps())
        codeView.onTextChanged {
            if (codeView.length() > CODE_LENGTH) {
                codeView.setText(it.substring(0, CODE_LENGTH))
            }
            btnDone.isEnabled = codeView.length() == CODE_LENGTH
            if (errorIsShowed) {
                codeView.setTextColor(ContextCompat.getColor(context, R.color.color_gtr_green))
                errorText.isVisible = false
                errorIsShowed = false
            }
        }

        btnDone.setOnClickListener { listener?.onDoneClicked(codeView.text.toString()) }
        btnResendCode.setOnClickListener { listener?.onResendCodeClicked() }
    }

    fun setFocus() {
        codeView.requestFocus()
    }

    fun setTimer(resendDelay: Long) {
        btnResendCode.isEnabled = false
        val secInMillis = SEC_IN_MILLIS
        timerBtnResendCode = object : CountDownTimer(resendDelay, secInMillis) {
            override fun onTick(millisUntilFinished: Long) {
                btnResendCode.text =
                    context.getString(R.string.LNG_LOGIN_RESEND_WAIT, (millisUntilFinished / secInMillis).toString())
                        .plus(" ${context.getString(R.string.LNG_SEC)}")
            }

            override fun onFinish() {
                btnResendCode.isEnabled = true
                btnResendCode.text = context.getText(R.string.LNG_LOGIN_RESEND_ALLOW)
            }
        }.start()
    }

    fun cancelTimer() {
        timerBtnResendCode?.cancel()
    }

    fun setWrongCodeError(details: String) {
        codeView.setTextColor(ContextCompat.getColor(context, R.color.color_gtr_red))
        errorText.text = details
        errorText.isVisible = true
        errorIsShowed = true
    }

    interface OnActivationCodeListener {
        fun onDoneClicked(code: String)
        fun onResendCodeClicked()
    }

    companion object {
        const val CODE_LENGTH = 8
        const val SEC_IN_MILLIS = 1_000L
    }
}