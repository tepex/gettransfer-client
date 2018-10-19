package com.kg.gettransfer.presentation.view

import android.widget.TextView

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.google.android.gms.maps.CameraUpdate

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.model.UserModel

import com.kg.gettransfer.domain.model.Account
import com.kg.gettransfer.domain.model.GTAddress

@StateStrategyType (OneExecutionStateStrategy::class)
interface CreateOrderView: BaseView {
    fun setTransportTypes(transportTypes: List<TransportTypeModel>)
    fun setCurrencies(currencies: List<CurrencyModel>)
    //fun setRoute(routeModel: RouteModel)
    fun setRoute(polyline: PolylineModel, routeModel: RouteModel)
    fun centerRoute(cameraUpdate: CameraUpdate)
    fun setUser(user: UserModel)
    fun setPassengers(count: Int)
    fun setChildren(count: Int)
    fun setCurrency(currency: String)
    fun setDateTimeTransfer(dateTimeString: String)
    fun setComment(comment: String)
    fun setGetTransferEnabled(enabled: Boolean)
    fun setPromoResult(discountInfo: String?)
    fun showPromoButton(show: Boolean)
}
