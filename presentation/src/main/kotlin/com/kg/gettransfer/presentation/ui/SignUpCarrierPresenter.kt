package com.kg.gettransfer.presentation.ui

import com.kg.gettransfer.presentation.presenter.BasePresenter
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SignUpCarrierView

/**
 * Presenter for the [SignUpCarrierFragment]
 *
 * @author П. Густокашин (Diwixis)
 */
class SignUpCarrierPresenter : BasePresenter<SignUpCarrierView>() {
    fun registration() {
        router.navigateTo(Screens.Carrier(Screens.REG_CARRIER))
    }
}