package com.kg.gettransfer.presentation.ui

import android.app.KeyguardManager
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.interactor.SessionInteractor
import com.kg.gettransfer.extensions.internationalExample
import com.kg.gettransfer.presentation.ui.dialogs.BaseBottomSheetDialogFragment
import kotlinx.android.synthetic.main.view_sign_up_error.*
import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent
import ru.terrakok.cicerone.Router

/**
 * The bottomsheet dialog for sign up error
 */
class SignUpBottomSheetError : BaseBottomSheetDialogFragment(), KoinComponent {
    private val sessionInteractor: SessionInteractor by inject()

    override val layout: Int = R.layout.view_sign_up_error

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signUpErrorDetail.text =
            getString(R.string.LNG_ERROR_EMAIL_PHONE, Utils.phoneUtil.internationalExample(sessionInteractor.locale))
        signUpDismissButton.setOnClickListener { dismiss() }
    }

    companion object {
        const val TAG = ".presentation.ui.SignUpBottomSheetError"

        fun newInstance() = SignUpBottomSheetError()
    }
}

/**
 * The bottomsheet dialog for sign up success
 */
class SignUpBottomSheetSuccess : BaseBottomSheetDialogFragment() {
    private val router by inject<Router>()

    override val layout: Int = R.layout.view_sign_up_success
    private var onDismissCallBack: (() -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        signUpDismissButton.setOnClickListener {
            dismiss()
            router.exit()
        }
    }

    fun setOnDissmissCalback(onDismissCallBack: (() -> Unit)): SignUpBottomSheetSuccess {
        this.onDismissCallBack = onDismissCallBack
        return this
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        onDismissCallBack?.invoke()
    }

    companion object {
        const val TAG = ".presentation.ui.SignUpBottomSheetSuccess"

        fun newInstance() = SignUpBottomSheetSuccess()
    }
}
