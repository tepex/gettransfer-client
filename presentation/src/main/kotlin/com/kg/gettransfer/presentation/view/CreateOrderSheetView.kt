package com.kg.gettransfer.presentation.view

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.model.UserModel

@StateStrategyType(OneExecutionStateStrategy::class)
interface CreateOrderSheetView: BaseView {

    fun setTransportTypes(transportTypes: List<TransportTypeModel>)
    fun setFairPrice(price: String?, time: String?)
    fun setCurrencies(currencies: List<CurrencyModel>)
    fun setUser(user: UserModel, isLoggedIn: Boolean)
    fun setPassengers(count: Int)
    fun setChildren(count: Int)
    fun setCurrency(currency: String)
    fun setDateTimeTransfer(dateTimeString: String, isAfter4Hours: Boolean)
    fun setComment(comment: String)
    fun setGetTransferEnabled(enabled: Boolean)
    fun setPromoResult(discountInfo: String?)
    fun resetPromoView()

}