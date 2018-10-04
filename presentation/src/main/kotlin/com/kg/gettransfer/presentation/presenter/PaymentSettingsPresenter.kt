package com.kg.gettransfer.presentation.presenter

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.presentation.view.PaymentSettingsView
import ru.terrakok.cicerone.Router

class PaymentSettingsPresenter(cc: CoroutineContexts,
                               router: Router,
                               systemInteractor: SystemInteractor): BasePresenter<PaymentSettingsView>(cc, router, systemInteractor) {
}