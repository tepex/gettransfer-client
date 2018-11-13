package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.google.android.gms.maps.CameraUpdate
import com.kg.gettransfer.presentation.model.*

@StateStrategyType (OneExecutionStateStrategy::class)
interface CreateOrderView: BaseView {
    fun setTransportTypes(transportTypes: List<TransportTypeModel>)
    fun setFairPrice(price: String?, time: String?)
    fun setCurrencies(currencies: List<CurrencyModel>)
    //fun setRoute(routeModel: RouteModel)
    fun setRoute(isDateChanged: Boolean, polyline: PolylineModel, routeModel: RouteModel)
    fun centerRoute(cameraUpdate: CameraUpdate)
    fun setUser(user: UserModel, isLoggedIn: Boolean)
    fun setPassengers(count: Int)
    fun setChildren(count: Int)
    fun setCurrency(currency: String)
    fun setDateTimeTransfer(dateTimeString: String)
    fun setComment(comment: String)
    fun setGetTransferEnabled(enabled: Boolean)
    fun setPromoResult(discountInfo: String?)
    fun resetPromoView()
    fun showEmptyFieldError(invalidField: String)
}
