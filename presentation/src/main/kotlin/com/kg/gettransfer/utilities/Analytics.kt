package com.kg.gettransfer.utilities

import android.os.Bundle

import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger

import com.google.firebase.analytics.FirebaseAnalytics

import com.kg.gettransfer.domain.model.ReviewRate

import com.yandex.metrica.YandexMetrica

import java.math.BigDecimal

import java.util.Currency

class Analytics(
    private val firebase: FirebaseAnalytics,
    private val facebook: AppEventsLogger
) {

    /** [см. табл.][https://docs.google.com/spreadsheets/d/1RP-96GhITF8j-erfcNXQH5kM6zw17ASmnRZ96qHvkOw/edit#gid=0] */
    companion object {
        const val EVENT_LOGIN      = "login"
        const val EVENT_TRANSFER = "create_transfer"
        const val EVENT_ADD_TO_CART = "add_to_cart"
        const val EVENT_SELECT_OFFER = "select_offer"
        const val EVENT_MAKE_PAYMENT = "make_payment"
        const val EVENT_MENU         = "menu"
        const val EVENT_MAIN         = "main"
        const val EVENT_BUTTONS      = "buttons"
        const val EVENT_SETTINGS     = "settings"
        const val EVENT_ONBOARDING   = "onboarding"
        const val EVENT_TRANSFERS    = "transfers"
        const val EVENT_OFFERS       = "offers"
        const val EVENT_TRANSFER_SETTINGS  = "transfer_settings"
        const val EVENT_BEGIN_CHECKOUT     = "begin_checkout"
        const val EVENT_ECOMMERCE_PURCHASE = "ecommerce_purchase"
        const val EVENT_GET_OFFER          = "get_offer"
        const val PASSWORD_RESTORE         = "password_restore"
        const val EVENT_TRANSFER_REVIEW_REQUESTED = "transfer_review_requested"
        const val EVENT_TRANSFER_REVIEW_DETAILED  = "transfer_review_detailed"
        const val EVENT_APP_REVIEW_REQUESTED      = "app_review_requested"

        const val STATUS = "status"
        const val PARAM_KEY_NAME = "name"

        const val TRIPS_CLICKED      = "trips"
        const val SETTINGS_CLICKED   = "settings"
        const val ABOUT_CLICKED      = "about"
        const val DRIVER_CLICKED     = "driver"
        const val CUSTOMER_CLICKED   = "customer"
        const val BEST_PRICE_CLICKED = "best_price"
        const val SHARE_CLICKED      = "share"
        const val TRANSFER_CLICKED   = "transfers"
        const val LOGIN_CLICKED      = "login"

        const val MY_PLACE_CLICKED      = "my_place"
        const val SHOW_ROUTE_CLICKED    = "show_route"
        const val CAR_INFO_CLICKED      = "car_info"
        const val BACK_CLICKED          = "back"
        const val POINT_ON_MAP_CLICKED  = "point_on_map"
        const val PREDEFINED_CLICKED    = "predefined_"
        const val LAST_PLACE_CLICKED    = "last_place"
        const val SWAP_CLICKED          = "swap"
        const val REQUEST_FORM          = "request_form"
        const val OFFER_DETAILS         = "offer_details"
        const val OFFER_DETAILS_RATING  = "offer_details_rating"
        const val OFFER_BOOK            = "offer_book"
        const val CANCEL_TRANSFER_BTN   = "cancel_transfer_btn"

        const val INVALID_EMAIL         = "invalid_email"
        const val INVALID_PHONE         = "invalid_phone"
        const val INVALID_NAME          = "invalid_name"
        const val NO_TRANSPORT_TYPE     = "no_transport_type"
        const val LICENSE_NOT_ACCEPTED  = "license_not_accepted"
        const val PASSENGERS_NOT_CHOSEN = "passengers"
        const val SERVER_ERROR          = "server_error"

        const val CURRENCY_PARAM = "currency"
        const val UNITS_PARAM    = "units"
        const val LANGUAGE_PARAM = "language"
        const val LOG_OUT_PARAM  = "logout"

        const val PARAM_KEY_FIELD  = "field"
        const val PARAM_KEY_RESULT = "result"

        const val PARAM_KEY_FILTER  = "filter"

        const val NUMBER_OF_PASSENGERS = "number_of_passengers"
        const val ORIGIN = "origin"
        const val DESTINATION = "destination"
        const val TRAVEL_CLASS = "travel_class"
        const val TRIP_TYPE = "request_type"
        const val TRANSACTION_ID = "transaction_id"
        const val BEGIN_IN_HOURS = "begin_in_hours"
        const val PROMOCODE = "promocode"

        const val TRIP_HOURLY = "hourly"
        const val TRIP_DESTINATION = "destination"
        const val TRIP_ROUND = "roundtrip"

        const val OFFER_TYPE = "offer_type"

        const val OFFER_PRICE_FOCUSED = "offer_price"
        const val DATE_TIME_CHANGED   = "date_time"
        const val PASSENGERS_ADDED    = "pax"
        const val FLIGHT_NUMBER_ADDED = "flight_number"
        const val CHILDREN_ADDED      = "children"
        const val COMMENT_INPUT       = "comment"

        const val SHARE = "share"

        const val RATING = "rating"
        const val VALUE = "value"
        const val CURRENCY = "currency"

        const val REVIEW = "review"
        const val REVIEW_AVERAGE = "transfer_review"
        const val REVIEW_COMMENT = "comment"
        const val REVIEW_APP_REJECTED = "rejected"
        const val REVIEW_APP_ACCEPTED = "accepted"

        const val PUNCTUALITY = "punctuality"
        const val DRIVER = "driver"
        const val VEHICLE = "vehicle"
        const val COMMENT = "comment"

        const val EXIT_STEP = "exit_step"

        const val EMPTY_VALUE = ""

        const val RESULT_SUCCESS   = "success"
        const val RESULT_FAIL      = "fail"
    }

    fun logEvent(event: String, bundle: Bundle, map: Map<String, Any?>) {
        logEventToFirebase(event, bundle)
        logEventToFacebook(event, bundle)
        logEventToYandex(event, map)
    }

    fun logEventToFirebase(event: String, data: Bundle) {
        firebase.logEvent(event, data)
    }

    fun logEventToFacebook(event: String, data: Bundle) {
        facebook.logEvent(event, data)
    }

    fun logEventToYandex(event: String, data: Map<String, Any?>) {
        YandexMetrica.reportEvent(event, data)
    }

    fun logEventEcommerce(event: String, bundle: Bundle, map: Map<String, Any?>) {
        logEventToFirebase(event, bundle)
        logEventToYandex(event, map)
    }

    fun logEventEcommercePurchaseFB(bundle: Bundle, price: BigDecimal, currency: Currency)
            = facebook.logPurchase(price, currency, bundle)

    fun logEventBeginCheckoutFB(bundle: Bundle, price: Double) =
        facebook.logEvent(AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT, price, bundle)

    fun requestResult(positive: Boolean) =
        if (positive) REVIEW_APP_ACCEPTED else REVIEW_APP_REJECTED

    fun reviewDetailKey(value: String) = when (value) {
        ReviewRate.RateType.PUNCTUALITY.type -> "punctuality"
        ReviewRate.RateType.VEHICLE.type     -> "vehicle"
        else                                 -> "driver"
    }
}
