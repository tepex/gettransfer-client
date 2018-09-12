package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.domain.model.TransportType

@StateStrategyType(OneExecutionStateStrategy::class)
interface TransferDetailsView: BaseView {
    fun setTransferInfo(transferId: Long, from: String, to: String, dateTimeString: String, distance: Int, distanceUnit: String)
    fun setPassengerInfo(countPassengers: Int, personName: String?, countChilds: Int?, flightNumber: String?, comment: String?, transportTypes: List<TransportType>)
    fun setMapInfo(routeInfo: RouteInfo, from: String, to: String, dateTimeString: String, distanceUnit: String)

    fun activateButtonCancel()
    fun setPaymentInfo()
    fun setOfferInfo()
}