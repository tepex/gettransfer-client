package com.kg.gettransfer.presentation.view

import android.support.annotation.StringRes

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import com.google.android.gms.maps.CameraUpdate

import com.kg.gettransfer.R

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.model.UserModel

import com.kg.gettransfer.utilities.Analytics

@StateStrategyType (OneExecutionStateStrategy::class)
interface CreateOrderView : BaseView, RouteView {
    fun setTransportTypes(transportTypes: List<TransportTypeModel>)
    fun setFairPrice(price: String?, time: String?)
    fun setCurrencies(currencies: List<CurrencyModel>)
    fun setUser(user: UserModel, isLoggedIn: Boolean)
    fun setPassengers(count: Int)
    fun setChildren(count: Int)
    fun setCurrency(currency: String)
    fun setDateTimeTransfer(dateTimeString: String, isAfterMinHours: Boolean, startField: Boolean)
    fun setHintForDateTimeTransfer(withReturnWay: Boolean)
    fun setComment(comment: String)
    fun setGetTransferEnabled(enabled: Boolean)
    fun setPromoResult(discountInfo: String?)
    fun resetPromoView()
    fun showEmptyFieldError(@StringRes stringId: Int)
    fun showNotLoggedAlert(withOfferId: Long)

    enum class FieldError(val value: String, @StringRes val stringId: Int) {
        EMAIL_FIELD(Analytics.INVALID_EMAIL, R.string.LNG_ERROR_EMAIL),
        PHONE_FIELD(Analytics.INVALID_PHONE, R.string.LNG_RIDE_PHONE),
        TRANSPORT_FIELD(Analytics.NO_TRANSPORT_TYPE, R.string.LNG_RIDE_CHOOSE_TRANSPORT),
        TERMS_ACCEPTED_FIELD(Analytics.LICENSE_NOT_ACCEPTED, R.string.LNG_RIDE_OFFERT_ERROR),
        PASSENGERS_COUNT(Analytics.PASSENGERS_NOT_CHOSEN, R.string.LNG_ERROR_PASSENGERS),
        TIME_NOT_SELECTED(Analytics.PASSENGERS_NOT_CHOSEN, R.string.LNG_ERROR_DATE),
        RETURN_TIME(Analytics.PASSENGERS_NOT_CHOSEN, R.string.LNG_ERROR_RETURN_DATE),
        UNKNOWN("no_param", R.string.LNG_RIDE_CANT_CREATE);
    }

    companion object {
        /* Пока сервевер не присылает минимальный временной промежуток до заказа */
        const val FUTURE_MINUTE = 5
    }
}
