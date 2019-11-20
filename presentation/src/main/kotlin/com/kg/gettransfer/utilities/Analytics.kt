package com.kg.gettransfer.utilities

import android.content.Context
import android.os.Bundle

import com.appsflyer.AFInAppEventParameterName
import com.appsflyer.AFInAppEventType
import com.appsflyer.AppsFlyerLib

import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger

import com.google.firebase.analytics.FirebaseAnalytics

import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.interactor.*
import com.kg.gettransfer.domain.model.*
import com.kg.gettransfer.presentation.delegate.AccountManager

import com.kg.gettransfer.presentation.model.PaymentRequestModel
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.model.map
import com.kg.gettransfer.sys.presentation.ConfigsManager

import com.yandex.metrica.Revenue
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.profile.Attribute
import com.yandex.metrica.profile.UserProfile

import io.sentry.Sentry

import java.util.Currency

import kotlin.math.roundToLong
import kotlinx.coroutines.Job

import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject

@Suppress("TooManyFunctions")
class Analytics(
    private val context: Context,
    private val firebase: FirebaseAnalytics,
    private val facebook: AppEventsLogger
) : KoinComponent {

    private val paymentInteractor: PaymentInteractor by inject()
    private val sessionInteractor: SessionInteractor by inject()
    private val orderInteractor: OrderInteractor by inject()
    private val transferInteractor: TransferInteractor by inject()
    private val offerInteractor: OfferInteractor by inject()
    private val accountManager: AccountManager by inject()
    private val configsManager: ConfigsManager by inject()

    private val compositeDisposable = Job()
    private val utils = AsyncUtils(get(), compositeDisposable)

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
     *
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

    open inner class EcommercePurchase {

        protected var event: String = EVENT_ECOMMERCE_PURCHASE

        private lateinit var transactionId: String
        private var promocode: String? = null
        private var duration: Int? = null
        private lateinit var offerType: String
        private lateinit var requestType: String
        private lateinit var currency: Currency
        private lateinit var currencyCode: String
        private var price: Double = -1.0
        private var campaign: String? = null
        private var rubPrice: Double? = Double.NaN
        private var offerId: String? = null
        private var origin: String? = null
        private var destination: String? = null
        private var transportType: List<String>? = null
        private var passengersCount: Int? = null
        private var beginInHours: Int? = null // time to transfer
        protected var paymentType: String? = null

        private var offer: Offer? = null
        private var bookNowOffer: BookNowOffer? = null
        private var transfer: Transfer? = null
        private var transferModel: TransferModel? = null
        private var offers: List<OfferItem> = emptyList()

        /**
         * Send analytics for single transfer
         */
        suspend fun sendAnalytics() {
            getTransferAndOffer()
            transfer?.let { sendAnalytics(it) }
        }

        /**
         * Send analytics for single transfer
         */
        private fun sendAnalytics(transfer: Transfer) {
            if (transfer.rubPrice != null && !transfer.analyticsSent) {
                prepareData()
                sendToFirebase()
                sendToFacebook()
                sendToYandex()
                sendToAppsFlyer()
                utils.launchSuspend {
                    val role = if (accountManager.remoteAccount.isBusinessAccount) {
                        Transfer.Role.PARTNER
                    } else {
                        Transfer.Role.PASSENGER
                    }
                    utils.asyncAwait {
                        transferInteractor.sendAnalytics(transfer.id, role.toString())
                    }
                }
            }
        }

        /**
         * Send analytics for list of transfers
         */
        suspend fun sendAnalytics(transfers: List<Transfer>) {
            transfers.forEach { tr ->
                transfer = tr
                transferModel = tr.map(configsManager.getConfigs().transportTypes.map { it.map() })
                getOffer(tr)
                sendAnalytics(tr)
            }
        }

        private suspend fun getOffer(tr: Transfer) {
            utils.asyncAwait { offerInteractor.getOffers(tr.id) }.also { result ->
                if (!result.isError()) {
                    offers = mutableListOf<OfferItem>().apply {
                        addAll(result.model)
                        addAll(tr.bookNowOffers)
                    }
                    offers.firstOrNull()?.let { getOfferType(it) }
                }
            }
        }

        protected fun prepareData() {
            transactionId = transfer?.id.toString()
            promocode = transfer?.promoCode
            duration = orderInteractor.duration
            offerType = if (offer != null) REGULAR else NOW
            requestType = when {
                transfer?.duration != null        -> TRIP_HOURLY
                transfer?.dateReturnLocal != null -> TRIP_ROUND
                else                              -> TRIP_DESTINATION
            }
            currencyCode = sessionInteractor.currency.code
            currency = Currency.getInstance(currencyCode)
            price = offer?.price?.amount ?: bookNowOffer?.amount ?: (-1.0).also {
                Sentry.capture(
                        """when try to get offer for analytics of payment - server return invalid value:
                            |offer is null  - ${offer == null}
                            |offer.price is null  - ${offer?.price == null}
                            |offer.price.amount is null  - ${offer?.price?.amount == null}
                            |bookNowOffer is null  - ${bookNowOffer == null}
                            |bookNowOffer.amount is null  - ${bookNowOffer?.amount == null}
                         """.trimMargin()
                )
            }
            campaign = transfer?.campaign
            rubPrice = transfer?.rubPrice
            offerId = offer?.id.toString()
            origin = orderInteractor.from?.variants?.first
            destination = orderInteractor.to?.variants?.first
            passengersCount = transfer?.pax
            beginInHours = transferModel?.timeToTransfer?.div(MIN_PER_HOUR)
            transportType = transfer?.transportTypeIds?.map { it.toString() }
        }

        protected suspend fun getTransferAndOffer() = paymentInteractor.selectedTransfer?.let { st ->
            paymentInteractor.selectedOffer?.let { so ->
                transfer = st
                transferModel = st.map(configsManager.getConfigs().transportTypes.map { it.map() })
                getOfferType(so)
            }
        }

        private fun getOfferType(offerItem: OfferItem) {
            when (offerItem) {
                is Offer        -> offer = offerItem
                is BookNowOffer -> bookNowOffer = offerItem
            }
        }

        private fun sendToAppsFlyer() {
            val map = mutableMapOf<String, Any?>()
            map[TRANSACTION_ID] = transactionId
            map[PROMOCODE] = promocode
            duration?.let { map[DURATION] = it }
            map[OFFER_TYPE] = offerType
            map[TRIP_TYPE] = requestType
            map[AFInAppEventParameterName.CURRENCY] = currencyCode
            map[AFInAppEventParameterName.REVENUE] = price
            logEventToAppsFlyer(AFInAppEventType.PURCHASE, map)
        }

        protected fun sendToYandex() {
            val map = mutableMapOf<String, Any?>()
            map[TRANSACTION_ID] = transactionId
            map[PROMOCODE] = promocode
            duration?.let { map[DURATION] = it }
            map[OFFER_TYPE] = offerType
            map[TRIP_TYPE] = requestType
            map[CURRENCY] = currencyCode
            map[VALUE] = price
            map[CAMPAIGN] = campaign
            rubPrice?.let { map[RUB_PRICE] = it }
            map[OFFER_ID] = offerId
            map[ORIGIN] = origin
            map[DESTINATION] = destination
            map[NUMBER_OF_PASSENGERS] = passengersCount
            map[TRAVEL_CLASS] = transportType
            map[BEGIN_IN_HOURS] = beginInHours
            paymentType?.let { map[PAYMENT_TYPE] = it }
            logEventToYandex(event, map)

            sendRevenue()
        }

        private fun sendRevenue() {
            @Suppress("MagicNumber")
            val priceMicros = price.roundToLong() * 1_000_000L // priceMicros = price × 10^6
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
            duration?.let { bundle.putInt(DURATION, it) }
            bundle.putString(OFFER_TYPE, offerType)
            bundle.putString(TRIP_TYPE, requestType)
            bundle.putString(OFFER_ID, offerId)
            bundle.putString(ORIGIN, origin)
            bundle.putString(DESTINATION, destination)
            passengersCount?.let { bundle.putInt(NUMBER_OF_PASSENGERS, it) }
            bundle.putStringArray(TRAVEL_CLASS, transportType?.toTypedArray())
            beginInHours?.let { bundle.putInt(BEGIN_IN_HOURS, it) }
            facebook.logPurchase(price.toBigDecimal(), currency, bundle)
        }

        protected fun sendToFirebase() {
            val bundle = Bundle()
            bundle.putString(TRANSACTION_ID, transactionId)
            bundle.putString(PROMOCODE, promocode)
            duration?.let { bundle.putInt(DURATION, it) }
            bundle.putString(OFFER_TYPE, offerType)
            bundle.putString(TRIP_TYPE, requestType)
            bundle.putString(CURRENCY, currencyCode)
            bundle.putDouble(VALUE, price)
            bundle.putString(CAMPAIGN, campaign)
            rubPrice?.let { bundle.putDouble(RUB_PRICE, it) }
            bundle.putString(OFFER_ID, offerId)
            bundle.putString(ORIGIN, origin)
            bundle.putString(DESTINATION, destination)
            passengersCount?.let { bundle.putInt(NUMBER_OF_PASSENGERS, it) }
            bundle.putStringArray(TRAVEL_CLASS, transportType?.toTypedArray())
            beginInHours?.let { bundle.putInt(BEGIN_IN_HOURS, it) }
            paymentType?.let { bundle.putString(PAYMENT_TYPE, paymentType) }
            logEventToFirebase(event, bundle)
        }
    }

    /**
     * This class is child class of EcommercePurchase because it has almost the same parameters
     * for analytics
     */
    inner class PaymentStatus(private var mPaymentType: String) : EcommercePurchase() {

        suspend fun sendAnalytics(event: String) {
            super.event = event
            super.paymentType = when (mPaymentType) {
                PaymentRequestModel.PLATRON -> CARD
                PaymentRequestModel.PAYPAL  -> PAYPAL
                else                        -> BALANCE
            }
            getTransferAndOffer()
            prepareData()
            sendToFirebase()
            sendToYandex()
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
            paymentType = when (paymentType) {
                PaymentRequestModel.PLATRON -> CARD
                PaymentRequestModel.PAYPAL  -> PAYPAL
                else                        -> BALANCE
            }
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
            hours?.let { bundle.putInt(HOURS, it) }
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
            hours?.let { bundle.putInt(HOURS, it) }
            bundle.putString(PAYMENT_TYPE, paymentType)
            bundle.putString(OFFER_TYPE, offerType)
            bundle.putString(TRIP_TYPE, requestType)
            bundle.putString(CURRENCY, currencyCode)
            bundle.putDouble(VALUE, price)
            logEventToFirebase(EVENT_BEGIN_CHECKOUT, bundle)
        }
    }

    @Suppress("LongParameterList")
    fun logEventAddToCart(
        travelClass: String?,
        hours: Int?,
        tripType: String,
        value: String?,
        currency: String?
    ) {

        val fbBundle = Bundle()
        val map = mutableMapOf<String, Any?>()
        val afMap = mutableMapOf<String, Any?>()

        map[NUMBER_OF_PASSENGERS] = orderInteractor.passengers
        map[ORIGIN] = orderInteractor.from?.variants?.first
        map[DESTINATION] = orderInteractor.to?.variants?.first
        map[TRAVEL_CLASS] = travelClass
        map[HOURS] = hours
        map[TRIP_TYPE] = tripType

        val bundle = createBundleFromMap(map)
        fbBundle.putAll(bundle)
        afMap.putAll(map)

        bundle.putString(VALUE, value)
        map[VALUE] = value

        currency?.let {
            bundle.putString(CURRENCY, currency)
            fbBundle.putString(AppEventsConstants.EVENT_PARAM_CURRENCY, currency)
            map[CURRENCY] = currency
            afMap[AFInAppEventParameterName.CURRENCY] = currency
        }

        logEventToFirebase(EVENT_ADD_TO_CART, bundle)
        logEventToFacebook(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, fbBundle)
        logEventToYandex(EVENT_ADD_TO_CART, map)
        logEventToAppsFlyer(AFInAppEventType.ADD_TO_CART, afMap)
    }

    fun logCreateTransfer(
        result: String,
        value: String?,
        currency: String?,
        hours: Int?
    ) {

        val map = mutableMapOf<String, Any?>()

        map[PARAM_KEY_RESULT] = result
        map[VALUE] = value
        currency?.let { map[CURRENCY] = it }
        map[HOURS] = hours

        val bundle = createBundleFromMap(map)

        logEvent(EVENT_TRANSFER, bundle, map)
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
        const val EVENT_PAYMENT_DONE = "payment_done"
        const val EVENT_PAYMENT_FAILED = "payment_failed"
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

        const val EVENT_BECOME_CARRIER = "become_carrier"
        const val EVENT_NEW_CARRIER_APP_DIALOG = "new_carrier_app_dialog"
        const val OPEN_SCREEN = "open_screen"
        const val GO_TO_MARKET = "go_to_market"

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
        const val DURATION = "duration"
        const val OFFER_ID = "offer_id"

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
        const val BALANCE = "balance"

        const val USER_TYPE = "usertype"
        const val DRIVER_TYPE = "driver"
        const val PASSENGER_TYPE = "passenger"
        const val CARRIER_TYPE = "carrier"

        const val ORDER_CREATED_FROM = "create_from"
        const val FROM_MAP = "map"
        const val FROM_FORM = "form"

        const val MESSAGE_IN  = "message_in"
        const val MESSAGE_OUT = "message_out"

        const val CAMPAIGN = "campaign"
        const val RUB_PRICE = "rub_price"

        const val MIN_PER_HOUR = 60
    }
}
