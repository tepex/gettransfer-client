package com.kg.gettransfer.utilities

import android.os.Bundle
import com.facebook.appevents.AppEventsLogger
import com.google.firebase.analytics.FirebaseAnalytics
import com.yandex.metrica.YandexMetrica
import java.math.BigDecimal
import java.util.*
class Analytics(private val firebase: FirebaseAnalytics,
                private val facebook: AppEventsLogger) {

    /** [см. табл.][https://docs.google.com/spreadsheets/d/1RP-96GhITF8j-erfcNXQH5kM6zw17ASmnRZ96qHvkOw/edit#gid=0] */
    companion object {
        @JvmField val EVENT_LOGIN      = "login"
        @JvmField val EVENT_TRANSFER = "create_transfer"
        @JvmField val EVENT_ADD_TO_CART = "add_to_cart"
        @JvmField val EVENT_SELECT_OFFER = "select_offer"
        @JvmField val EVENT_MAKE_PAYMENT = "make_payment"
        @JvmField val EVENT_MENU = "menu"
        @JvmField val EVENT_MAIN = "main"
        @JvmField val EVENT_BUTTONS = "buttons"
        @JvmField val EVENT_SETTINGS = "settings"
        @JvmField val EVENT_ONBOARDING = "onboarding"
        @JvmField val EVENT_TRANSFERS   = "transfers"
        @JvmField val EVENT_OFFERS = "offers"
        @JvmField val EVENT_TRANSFER_SETTINGS = "transfer_settings"
        @JvmField val EVENT_BEGIN_CHECKOUT = "begin_checkout"
        @JvmField val EVENT_ECOMMERCE_PURCHASE = "ecommerce_purchase"
        @JvmField val EVENT_GET_OFFER = "get_offer"
        @JvmField val PASSWORD_RESTORE = "password_restore"
        @JvmField val EVENT_TRANSFER_REVIEW = "transfer_review"
        @JvmField val EVENT_TRANSFER_REVIEW_DETAILED = "transfer_review_detailed"

        @JvmField val STATUS = "status"
        @JvmField val PARAM_KEY_NAME = "name"

        @JvmField val TRIPS_CLICKED      = "trips"
        @JvmField val SETTINGS_CLICKED   = "settings"
        @JvmField val ABOUT_CLICKED      = "about"
        @JvmField val DRIVER_CLICKED     = "driver"
        @JvmField val CUSTOMER_CLICKED   = "customer"
        @JvmField val BEST_PRICE_CLICKED = "best_price"
        @JvmField val SHARE_CLICKED      = "share"
        @JvmField val TRANSFER_CLICKED   = "transfers"
        @JvmField val LOGIN_CLICKED      = "login"

        //other buttons log params
        @JvmField val MY_PLACE_CLICKED      = "my_place"
        @JvmField val SHOW_ROUTE_CLICKED    = "show_route"
        @JvmField val CAR_INFO_CLICKED      = "car_info"
        @JvmField val BACK_CLICKED          = "back"
        @JvmField val POINT_ON_MAP_CLICKED  = "point_on_map"
        @JvmField val PREDEFINED_CLICKED    = "predefined_"
        @JvmField val LAST_PLACE_CLICKED    = "last_place"
        @JvmField val SWAP_CLICKED          = "swap"
        @JvmField val REQUEST_FORM          = "request_form"
        @JvmField val OFFER_DETAILS         = "offer_details"
        @JvmField val OFFER_DETAILS_RATING  = "offer_details_rating"
        @JvmField val OFFER_BOOK            = "offer_book"

        @JvmField val INVALID_EMAIL         = "invalid_email"
        @JvmField val INVALID_PHONE         = "invalid_phone"
        @JvmField val INVALID_NAME          = "invalid_name"
        @JvmField val NO_TRANSPORT_TYPE     = "no_transport_type"
        @JvmField val LICENSE_NOT_ACCEPTED  = "license_not_accepted"
        @JvmField val SERVER_ERROR          = "server_error"

        @JvmField val CURRENCY_PARAM = "currency"
        @JvmField val UNITS_PARAM    = "units"
        @JvmField val LANGUAGE_PARAM = "language"
        @JvmField val LOG_OUT_PARAM  = "logout"

        @JvmField val PARAM_KEY_FIELD  = "field"
        @JvmField val PARAM_KEY_RESULT = "result"

        @JvmField val PARAM_KEY_FILTER  = "filter"

        @JvmField val NUMBER_OF_PASSENGERS = "number_of_passengers"
        @JvmField val ORIGIN = "origin"
        @JvmField val DESTINATION = "destination"
        @JvmField val TRAVEL_CLASS = "travel_class"
        @JvmField val TRANSACTION_ID = "transaction_id"
        @JvmField val BEGIN_IN_HOURS = "begin_in_hours"
        @JvmField val PROMOCODE = "promocode"

        @JvmField val OFFER_TYPE = "offer_type"

        @JvmField val OFFER_PRICE_FOCUSED = "offer_price"
        @JvmField val DATE_TIME_CHANGED   = "date_time"
        @JvmField val PASSENGERS_ADDED    = "pax"
        @JvmField val FLIGHT_NUMBER_ADDED = "flight_number"
        @JvmField val CHILDREN_ADDED      = "children"
        @JvmField val COMMENT_INPUT       = "comment"

        @JvmField val SHARE = "share"

        @JvmField val RATING = "rating"
        @JvmField val VALUE = "value"
        @JvmField val CURRENCY = "currency"

        @JvmField val REVIEW = "review"

        @JvmField val PUNCTUALITY = "punctuality"
        @JvmField val DRIVER = "driver"
        @JvmField val VEHICLE = "vehicle"
        @JvmField val COMMENT = "comment"

        @JvmField val EXIT_STEP = "exit_step"

        @JvmField val EMPTY_VALUE = ""

        @JvmField val RESULT_SUCCESS   = "success"
        @JvmField val RESULT_FAIL      = "fail"
    }

    fun logEvent(event: String, bundle: Bundle, map: Map<String, Any?>) {
        logEventToFirebase(event, bundle)
        logEventToFacebook(event, bundle)
        logEventToYandex(event, map)
    }

    private fun logEventToFirebase(event: String, data: Bundle) {
        firebase.logEvent(event, data)
    }

    private fun logEventToFacebook(event: String, data: Bundle) {
        facebook.logEvent(event, data)
    }

    private fun logEventToYandex(event: String, data: Map<String, Any?>) {
        YandexMetrica.reportEvent(event, data)
    }

    fun logEventEcommercePurchase(event: String, bundle: Bundle, map: Map<String, Any?>,
                                  price: BigDecimal, currency: Currency) {
        logEventToFirebase(event, bundle)
        facebook.logPurchase(price, currency, bundle)
        logEventToYandex(event, map)
    }

    fun logEventBeginCheckout(event: String, bundle: Bundle, map: Map<String, Any?>, price: Double) {
        logEventToFirebase(event, bundle)
        facebook.logEvent(event, price, bundle)
        logEventToYandex(event, map)
    }
}
