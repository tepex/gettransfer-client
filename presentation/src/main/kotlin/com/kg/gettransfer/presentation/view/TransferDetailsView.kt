package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.presentation.model.TransportTypeModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface TransferDetailsView: BaseView {
    fun setTransferInfo(transferId: Long, from: String, to: String, dateTimeString: String, distance: String)
    fun setPassengerInfo(countPassengers: Int, personName: String?, countChilds: Int?, flightNumber: String?,
                         comment: String?, transportTypes: List<TransportTypeModel>)
    fun setMapInfo(routeInfo: RouteInfo, from: String, to: String, dateTimeString: String, distance: String)

    fun activateButtonCancel()
    fun setPaymentInfo(price: String, paidSum: String, paidPercentage: Int, remainToPay: String)
    fun setOfferInfo(driverEmail: String, driverPhone: String, driverName: String, transportType: String,
                     transportName: String, transportNumber: String, price: String)
}