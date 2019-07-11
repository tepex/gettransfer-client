package com.kg.gettransfer.utilities

import android.content.Context
import android.os.Bundle

import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerLib

import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger

import com.google.firebase.analytics.FirebaseAnalytics

import com.kg.gettransfer.domain.model.ReviewRate
import com.kg.gettransfer.presentation.model.PaymentRequestModel

import com.yandex.metrica.Revenue
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.profile.Attribute
import com.yandex.metrica.profile.UserProfile

import java.util.Currency

import kotlin.math.roundToLong

class Analytics(
    private val context: Context,
    private val firebase: FirebaseAnalytics,
    private val facebook: AppEventsLogger
) {

    /**
     * log single value for event
     */
    fun logEvent(event: String, key: String, value: Any?) {
        val map = mutableMapOf(key to value)
        val bundle = createBundleFromMap(map)
        logEvent(event, bundle, map)
    }

    /**
     * log event without keys and values
     */
    fun logSingleEvent(event: String) = logEvent(event, null, null)

    /**
     * log some values for one event
     * @param pairs list of Pair for multiple event values
     */
    fun logEvent(event: String, pairs: List<Pair<String, Any?>>) {
        val map = mutableMapOf<String, Any?>()
        pairs.forEach { map[it.first] = it.second }
        val bundle = createBundleFromMap(map)
        logEvent(event, bundle, map)
    }

    private fun logEvent(event: String, bundle: Bundle?, map: Map<String, Any?>?) {
        logEventToFirebase(event, bundle)
        logEventToYandex(event, map)
    }

    private fun logEventToFirebase(event: String, data: Bundle?) =
        firebase.logEvent(event, data)

    private fun logEventToFacebook(event: String, data: Bundle) =
        facebook.logEvent(event, data)

    private fun logEventToYandex(event: String, data: Map<String, Any?>?) =
        YandexMetrica.reportEvent(event, data)

    private fun logEventToAppsFlyer(event: String, data: Map<String, Any?>?) =
        AppsFlyerLib.getInstance().trackEvent(context, event, data)

    fun reviewDetailKey(value: String) = when (value) {
        ReviewRate.RateType.COMMUNICATION.name -> "punctuality"
        else -> value.toLowerCase()
    }

    private fun createBundleFromMap(map: MutableMap<String, Any?>): Bundle {
        return Bundle().apply {
            map.forEach { (k, v) ->
                when (v) {
                    is String -> putString(k, v)
                    is Int -> putInt(k, v)
                    is Double -> putDouble(k, v)
                }
            }
        }
    }

    inner class EcommercePurchase(
        private val transactionId: String,
        private val promocode: String?,
        private val hours: Int?,
        private var paymentType: String,
        private val offerType: String,
        private val requestType: String,
        private val currency: Currency,
        private val currencyCode: String,
        private val price: Double) {

        fun sendAnalytics() {
            paymentType = if (paymentType == PaymentRequestModel.PLATRON) CARD else PAYPAL
            sendToFirebase()
            sendToFacebook()
            sendToYandex()
            sendToAppsFlyer()
            logEvent(EVENT_MAKE_PAYMENT, STATUS, RESULT_SUCCESS)
        }

        private fun sendToAppsFlyer() {
            val map = mutableMapOf<String, Any?>()
            map[TRANSACTION_ID] = transactionId
            map[PROMOCODE] = promocode
            hours?.let { map[HOURS] = it }
            map[PAYMENT_TYPE] = paymentType
            map[OFFER_TYPE] = offerType
            map[TRIP_TYPE] = requestType
            map[AFInAppEventParameterName.CURRENCY] = currencyCode
            map[AFInAppEventParameterName.REVENUE] = price
            logEventToAppsFlyer(AFInAppEventType.PURCHASE, map)
        }

        private fun sendToYandex() {
            val map = mutableMapOf<String, Any?>()
            map[TRANSACTION_ID] = transactionId
            map[PROMOCODE] = promocode
            hours?.let { map[HOURS] = it }
            map[PAYMENT_TYPE] = paymentType
            map[OFFER_TYPE] = offerType
            map[TRIP_TYPE] = requestType
            map[CURRENCY] = currencyCode
            map[VALUE] = price
            logEventToYandex(EVENT_ECOMMERCE_PURCHASE, map)

            sendRevenue()
        }

        private fun sendRevenue() {
            val priceMicros = price.roundToLong() * 1000000L // priceMicros = price × 10^6
            val revenue = Revenue.newBuilderWithMicros(priceMicros, currency)
                    .withProductID(requestType)
                    .withQuantity(1)
                    .build()
            YandexMetrica.reportRevenue(revenue)
        }

        private fun sendToFacebook() {
            val bundle = Bundle()
            bundle.putString(TRANSACTION_ID, transactionId)
            bundle.putString(PROMOCODE, promocode)
            hours?.let { bundle.putInt(Analytics.HOURS, it) }
            bundle.putString(PAYMENT_TYPE, paymentType)
            bundle.putString(OFFER_TYPE, offerType)
            bundle.putString(TRIP_TYPE, requestType)
            facebook.logPurchase(price.toBigDecimal(), currency, bundle)
        }

        private fun sendToFirebase() {
            val bundle = Bundle()
            bundle.putString(TRANSACTION_ID, transactionId)
            bundle.putString(PROMOCODE, promocode)
            hours?.let { bundle.putInt(Analytics.HOURS, it) }
            bundle.putString(PAYMENT_TYPE, paymentType)
            bundle.putString(OFFER_TYPE, offerType)
            bundle.putString(TRIP_TYPE, requestType)
            bundle.putString(CURRENCY, currencyCode)
            bundle.putDouble(VALUE, price)
            logEventToFirebase(EVENT_ECOMMERCE_PURCHASE, bundle)
        }
    }

    inner class BeginCheckout(
        private val share: Int,
        private val promocode: String?,
        private val hours: Int?,
        private var paymentType: String,
        private val offerType: String,
        private val requestType: String,
        private val currencyCode: String,
        private val price: Double
    ) {

        fun sendAnalytics() {
            paymentType = if (paymentType == PaymentRequestModel.PLATRON) CARD else PAYPAL
            sendToFirebase()
            sendToFacebook()
            sendToYandex()
            sendToAppsFlyer()
        }

        private fun sendToAppsFlyer() {
            val map = mutableMapOf<String, Any?>()
            map[SHARE] = share
            map[PROMOCODE] = promocode
            hours?.let { map[HOURS] = it }
            map[PAYMENT_TYPE] = paymentType
            map[OFFER_TYPE] = offerType
            map[TRIP_TYPE] = requestType
            map[AFInAppEventParameterName.CURRENCY] = currencyCode
            map[AFInAppEventParameterName.PRICE] = price
            logEventToAppsFlyer(AFInAppEventType.INITIATED_CHECKOUT, map)
        }

        private fun sendToYandex() {
            val map = mutableMapOf<String, Any?>()
            map[SHARE] = share
            map[PROMOCODE] = promocode
            hours?.let { map[HOURS] = it }
            map[PAYMENT_TYPE] = paymentType
            map[OFFER_TYPE] = offerType
            map[TRIP_TYPE] = requestType
            map[CURRENCY] = currencyCode
            map[VALUE] = price
            logEventToYandex(EVENT_BEGIN_CHECKOUT, map)
        }

        private fun sendToFacebook() {
            val bundle = Bundle()
            bundle.putInt(SHARE, share)
            bundle.putString(PROMOCODE, promocode)
            hours?.let { bundle.putInt(Analytics.HOURS, it) }
            bundle.putString(PAYMENT_TYPE, paymentType)
            bundle.putString(OFFER_TYPE, offerType)
            bundle.putString(TRIP_TYPE, requestType)
            bundle.putString(CURRENCY, currencyCode)
            facebook.logEvent(AppEventsConstants.EVENT_NAME_INITIATED_CHECKOUT, price, bundle)
        }

        private fun sendToFirebase() {
            val bundle = Bundle()
            bundle.putInt(SHARE, share)
            bundle.putString(PROMOCODE, promocode)
            hours?.let { bundle.putInt(Analytics.HOURS, it) }
            bundle.putString(PAYMENT_TYPE, paymentType)
            bundle.putString(OFFER_TYPE, offerType)
            bundle.putString(TRIP_TYPE, requestType)
            bundle.putString(CURRENCY, currencyCode)
            bundle.putDouble(VALUE, price)
            logEventToFirebase(EVENT_BEGIN_CHECKOUT, bundle)
        }
    }

    fun logEventAddToCart(
            numberOfPassengers: Int,
            origin: String?,
            destination: String?,
            travelClass: String?,
            hours: Int?,
            tripType: String,
            value: String?,
            currency: String?) {

        val fbBundle = Bundle()
        val map = mutableMapOf<String, Any?>()
        val afMap = mutableMapOf<String, Any?>()

        map[Analytics.NUMBER_OF_PASSENGERS] = numberOfPassengers
        map[Analytics.ORIGIN] = origin
        map[Analytics.DESTINATION] = destination
        map[Analytics.TRAVEL_CLASS] = travelClass
        map[Analytics.HOURS] = hours
        map[Analytics.TRIP_TYPE] = tripType

        val bundle = createBundleFromMap(map)
        fbBundle.putAll(bundle)
        afMap.putAll(map)

        bundle.putString(Analytics.VALUE, value)
        map[Analytics.VALUE] = value

        currency?.let {
            bundle.putString(Analytics.CURRENCY, currency)
            fbBundle.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency)
            map[Analytics.CURRENCY] = currency
            afMap[AFInAppEventParameterName.CURRENCY] = currency
        }

        logEventToFirebase(Analytics.EVENT_ADD_TO_CART, bundle)
        logEventToFacebook(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, fbBundle)
        logEventToYandex(Analytics.EVENT_ADD_TO_CART, map)
        logEventToAppsFlyer(AFInAppEventType.ADD_TO_CART, afMap)
    }

    fun logCreateTransfer(
            result: String,
            value: String?,
            currency: String?,
            hours: Int?) {

        val map = mutableMapOf<String, Any?>()

        map[Analytics.PARAM_KEY_RESULT] = result
        map[Analytics.VALUE] = value
        currency?.let { map[Analytics.CURRENCY] = it }
        map[Analytics.HOURS] = hours

        val bundle = createBundleFromMap(map)

        logEvent(Analytics.EVENT_TRANSFER, bundle, map)
    }

    fun logProfile(attribute: String) {
        val userProfile = UserProfile.newBuilder()
            .apply(Attribute.customString(USER_TYPE).withValue(attribute))
            .build()
        YandexMetrica.reportUserProfile(userProfile)
    }

    /** [см. табл.][https://docs.google.com/spreadsheets/d/1RP-96GhITF8j-erfcNXQH5kM6zw17ASmnRZ96qHvkOw/edit#gid=0] */
    companion object {
        const val EVENT_LOGIN_PASS = "login_pass"
        const val EVENT_LOGIN_CODE = "login_code"
        const val EVENT_GET_CODE = "get_code"
        const val EVENT_RESEND_CODE = "resend_code"
        const val EVENT_SIGN_UP = "signup"
        const val EVENT_TRANSFER = "create_transfer"
        const val EVENT_ADD_TO_CART = "add_to_cart"
        const val EVENT_SELECT_OFFER = "select_offer"
        const val EVENT_MAKE_PAYMENT = "make_payment"
        const val EVENT_MENU = "menu"
        const val EVENT_MAIN = "main"
        const val EVENT_SETTINGS = "settings"
        const val EVENT_ONBOARDING = "onboarding"
        const val EVENT_TRANSFERS = "transfers"
        const val EVENT_OFFERS = "offers"
        const val EVENT_TRANSFER_SETTINGS = "transfer_settings"
        const val EVENT_BEGIN_CHECKOUT = "begin_checkout"
        const val EVENT_ECOMMERCE_PURCHASE = "ecommerce_purchase"
        const val EVENT_GET_OFFER = "get_offer"
        const val PASSWORD_RESTORE = "password_restore"
        const val EVENT_TRANSFER_REVIEW_REQUESTED = "transfer_review_requested"
        const val EVENT_TRANSFER_REVIEW_DETAILED = "transfer_review_detailed"
        const val EVENT_APP_REVIEW_REQUESTED = "app_review_requested"
        const val EVENT_IPAPI_REQUEST = "ipapi_request"

        const val STATUS = "status"
        const val PARAM_KEY_NAME = "name"

        const val TRIPS_CLICKED = "trips"
        const val SETTINGS_CLICKED = "settings"
        const val ABOUT_CLICKED = "about"
        const val DRIVER_CLICKED = "driver"
        const val CUSTOMER_CLICKED = "customer"
        const val BEST_PRICE_CLICKED = "best_price"
        const val TRANSFER_CLICKED = "transfers"
        const val LOGIN_CLICKED = "login"

        const val GET_CODE_CLICKED = "get_code"
        const val RESEND_CODE_CLICKED = "resend_code"
        const val VERIFY_CODE_CLICKED = "verify_code"
        const val VERIFY_PASSWORD_CLICKED = "verify_password"
        const val MY_PLACE_CLICKED = "my_place"
        const val SHOW_ROUTE_CLICKED = "show_route"
        const val CAR_INFO_CLICKED = "car_info"
        const val BACK_CLICKED = "back"
        const val BACK_TO_MAP = "back_to_map"
        const val POINT_ON_MAP_CLICKED = "point_on_map"
        const val PREDEFINED_CLICKED = "predefined_"
        const val LAST_PLACE_CLICKED = "last_place"
        const val SWAP_CLICKED = "swap"
        const val REQUEST_FORM = "request_form"
        const val OFFER_DETAILS = "offer_details"
        const val OFFER_DETAILS_RATING = "offer_details_rating"
        const val OFFER_BOOK = "offer_book"
        const val CANCEL_TRANSFER_BTN = "cancel_transfer_btn"

        const val INVALID_EMAIL = "invalid_email"
        const val INVALID_PHONE = "invalid_phone"
        const val INVALID_NAME = "ic_hourly_driver"
        const val NO_TRANSPORT_TYPE = "no_transport_type"
        const val LICENSE_NOT_ACCEPTED = "license_not_accepted"
        const val PASSENGERS_NOT_CHOSEN = "passengers"
        const val SERVER_ERROR = "server_error"

        const val CURRENCY_PARAM = "currency"
        const val UNITS_PARAM = "units"
        const val LANGUAGE_PARAM = "language"
        const val LOG_OUT_PARAM = "logout"

        const val PARAM_KEY_FIELD = "field"
        const val PARAM_KEY_RESULT = "result"

        const val PARAM_KEY_FILTER = "filter"

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
        const val REGULAR = "regular"
        const val NOW = "now"

        const val OFFER_PRICE_FOCUSED = "offer_price"
        const val DATE_TIME_CHANGED = "date_time"
        const val PASSENGERS_ADDED = "pax"
        const val FLIGHT_NUMBER_ADDED = "flight_number"
        const val CHILDREN_ADDED = "children"
        const val COMMENT_INPUT = "comment"

        const val SHARE = "share"

        const val RATING = "rating"
        const val VALUE = "value"
        const val CURRENCY = "currency"
        const val HOURS = "hours"

        const val REVIEW = "review"
        const val EVENT_REVIEW_AVERAGE = "transfer_review"
        const val REVIEW_COMMENT = "comment"
        const val REVIEW_APP_REJECTED = "rejected"
        const val REVIEW_APP_ACCEPTED = "accepted"

        const val PUNCTUALITY = "punctuality"
        const val DRIVER = "driver"
        const val VEHICLE = "vehicle"
        const val COMMENT = "comment"

        const val EXIT_STEP = "exit_step"

        const val EMPTY_VALUE = ""

        const val RESULT_SUCCESS = "success"
        const val RESULT_FAIL = "fail"

        const val PAYMENT_TYPE = "payment_type"
        const val CARD = "card"
        const val PAYPAL = "paypal"

        const val USER_TYPE = "usertype"
        const val DRIVER_TYPE = "driver"
        const val PASSENGER_TYPE = "passenger"
        const val CARRIER_TYPE = "carrier"

        const val ORDER_CREATED_FROM = "create_from"
        const val FROM_MAP = "map"
        const val FROM_FORM = "form"

        const val MESSAGE_IN  = "message_in"
        const val MESSAGE_OUT = "message_out"
    }
}
