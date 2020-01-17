package com.kg.gettransfer.presentation.view

import androidx.annotation.StringRes

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

import com.kg.gettransfer.R

import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.model.UserModel

import com.kg.gettransfer.utilities.Analytics

@StateStrategyType (OneExecutionStateStrategy::class)
interface CreateOrderView : BaseView, RouteView {
    fun setTransportTypes(transportTypes: List<TransportTypeModel>)
    fun setTripType(withReturnWay: Boolean)
    fun setUser(user: UserModel, isLoggedIn: Boolean)
    fun setEditableFields(offeredPrice: Double?, flightNumber: String?, flightNumberReturn: String?, promo: String)
    fun setPassengers(count: Int)
    fun setChildSeats(setOf: Set<ChildSeatItem>, total: Int)
    fun setCurrency(currency: String, hideCurrencies: Boolean)
    fun setDateTimeTransfer(dateTimeString: String, startField: Boolean)
    fun showHourlyDurationDialog(durationValue: Int?)
    fun setHourlyDuration(durationValue: Int?)
    fun setHintForDateTimeTransfer(withReturnWay: Boolean)
    fun disablePromoCodeField()
    fun setPromoResult(discountInfo: String?)
    fun resetPromoView()
    fun showEmptyFieldError(@StringRes stringId: Int)
    fun showNotLoggedAlert(withOfferId: Long)
    fun highLightErrorField(errorField: FieldError)
    fun showCommentDialog(comment: String, hintsToComments: List<String>?)
    fun showCurrencies()

    enum class FieldError(val value: String?, @StringRes val stringId: Int) {
        EMAIL_FIELD(Analytics.INVALID_EMAIL, R.string.LNG_RIDE_EMAIL),
        PHONE_FIELD(Analytics.INVALID_PHONE, R.string.LNG_RIDE_PHONE),
        INVALID_EMAIL(Analytics.INVALID_EMAIL, R.string.LNG_ERROR_EMAIL),
        INVALID_PHONE(Analytics.INVALID_PHONE, R.string.LNG_ERROR_PHONE),
        TRANSPORT_FIELD(Analytics.NO_TRANSPORT_TYPE, R.string.LNG_RIDE_CHOOSE_TRANSPORT),
        TERMS_ACCEPTED_FIELD(Analytics.LICENSE_NOT_ACCEPTED, R.string.LNG_RIDE_OFFERT_ERROR),
        PASSENGERS_COUNT(Analytics.PASSENGERS_NOT_CHOSEN, R.string.LNG_ERROR_PASSENGERS),
        TIME_NOT_SELECTED(Analytics.PASSENGERS_NOT_CHOSEN, R.string.LNG_ERROR_DATE),
        RETURN_TIME(Analytics.PASSENGERS_NOT_CHOSEN, R.string.LNG_ERROR_RETURN_DATE),
        OFFERED_PRICE_MIN(null, R.string.LNG_ERROR_OFFERED_PRICE_MIN),
        OFFERED_PRICE_MAX(null, R.string.LNG_ERROR_OFFERED_PRICE_MAX),
        UNKNOWN("no_param", R.string.LNG_RIDE_CANT_CREATE);
    }

    enum class ChildSeatItem(var count: Int, @StringRes val stringId: Int) {
        INFANT(0, R.string.LNG_SEAT_INFANT_SHORT),
        CONVERTIBLE(0, R.string.LNG_SEAT_CONVERTIBLE_SHORT),
        BOOSTER(0, R.string.LNG_SEAT_BOOSTER_SHORT)
    }
}
