package com.kg.gettransfer.presentation.presenter

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.view.SelectCancelationReasonView
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
class SelectCancelationReasonPresenter : MvpPresenter<SelectCancelationReasonView>() {

    override fun attachView(view: SelectCancelationReasonView) {
        super.attachView(view)
        viewState.setCancelationReasonsList(getCancelationReasons())
        viewState.showBottomSheet()
    }

    private fun getCancelationReasons() =
        listOf(
            R.string.LNG_CANCELATION_REASON_CHANGED_REQUIREMENTS,
            R.string.LNG_CANCELATION_REASON_WANTED_KNOW_THE_PRICE,
            R.string.LNG_CANCELATION_REASON_FOUND_CHEAPER,
            R.string.LNG_CANCELATION_REASON_GO_TO_THE_BUS_OR_TRAIN,
            R.string.LNG_CANCELATION_REASON_RENTED_CAR,
            R.string.LNG_CANCELATION_REASON_RELATIVES_OR_FRIENDS_WILL_THROW,
            R.string.LNG_CANCELATION_REASON_DIDNT_EXPECT_TO_GET_A_FEW_PRICES,
            R.string.LNG_CANCELATION_REASON_TOOK_THE_TOUR_WITH_TRANSFER,
            R.string.LNG_CANCELATION_REASON_NO_OFFERS_RECEIVED,
            R.string.LNG_CANCELATION_REASON_ANOTHER_REASON
        )
}
