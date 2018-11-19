package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.google.android.gms.maps.CameraUpdate

import com.kg.gettransfer.presentation.model.*

@StateStrategyType (OneExecutionStateStrategy::class)
interface CreateOrderView: BaseView, RouteView {
    fun setTransportTypes(transportTypes: List<TransportTypeModel>)
    fun setFairPrice(price: String?, time: String?)
    fun setCurrencies(currencies: List<CurrencyModel>)
    fun centerRoute(cameraUpdate: CameraUpdate)
    fun setUser(user: UserModel, isLoggedIn: Boolean)
    fun setPassengers(count: Int)
    fun setChildren(count: Int)
    fun setCurrency(currency: String)
    fun setDateTimeTransfer(dateTimeString: String, isAfter4Hours: Boolean)
    fun setComment(comment: String)
    fun setGetTransferEnabled(enabled: Boolean)
    fun setPromoResult(discountInfo: String?)
    fun resetPromoView()
    fun showEmptyFieldError(invalidField: String)
}
