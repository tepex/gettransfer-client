package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransferModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface TransferDetailsView: BaseView {
    fun setTransfer(transferModel: TransferModel)
    fun setOffer(offerModel: OfferModel)
    //fun setRoute(routeModel: RouteModel)
    fun setRoute(polyline: PolylineModel, routeModel: RouteModel)
    fun setButtonCancelVisible(visible: Boolean)
}
