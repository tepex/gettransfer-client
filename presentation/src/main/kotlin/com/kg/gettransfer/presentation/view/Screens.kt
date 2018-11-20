package com.kg.gettransfer.presentation.view

import android.content.Context
import android.content.Intent

import com.google.android.gms.maps.model.LatLngBounds

import com.kg.gettransfer.presentation.ui.*

import kotlinx.serialization.json.JSON

import java.util.Date

import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {
	@JvmField val BUNDLE_KEY       = "bundle_key"
	
	@JvmField val CARRIER_TRIPS    = "carrier_trips"
	
	
	@JvmField val MAIN             = "main"
	@JvmField val CARRIER_MODE     = "carrier_mode"
	@JvmField val REG_CARRIER      = "registration_carrier"
	@JvmField val PASSENGER_MODE   = "passenger_mode"

	@JvmField val OFFERS           = "offers"
	@JvmField val CLOSE_ACTIVITY   = "close_activity"
	
	
	@JvmField val SELECT_FINISH    = "select_finish_on_map"

	
	@JvmField val NOT_USED 		   = -1

    object Main: SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, MainActivity::class.java)
    }

    object ShareLogs: SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, LogsActivity::class.java)
    }
    
    object About: SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, AboutActivity::class.java)
    }

    object Settings: SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, SettingsActivity::class.java)
    }

    object Requests: SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, RequestsActivity::class.java)
    }

    object LicenceAgree: SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, WebPageActivity()::class.java).apply {
            putExtra(WebPageView.EXTRA_SCREEN, WebPageView.SCREEN_LICENSE)
        }
    }

    object CreateOrder: SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, CreateOrderActivity::class.java)
    }

    open class Login(val nextScreen: String, val email: String?): SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            putExtra(LoginView.EXTRA_SCREEN_FOR_RETURN, nextScreen)
            putExtra(LoginView.EXTRA_EMAIL_TO_LOGIN, email)
        }
    }

    data class LoginToGetOffers(val id: Long, val email: String?): SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            putExtra(LoginView.EXTRA_TRANSFER_ID, id)
            putExtra(LoginView.EXTRA_SCREEN_FOR_RETURN, Screens.OFFERS)
            putExtra(LoginView.EXTRA_EMAIL_TO_LOGIN, email)
        }
    }

    data class ChangeMode(val mode: String): SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = when(mode) {
            CARRIER_MODE -> Intent(context, WebPageActivity()::class.java).apply {
                putExtra(WebPageView.EXTRA_SCREEN, WebPageView.SCREEN_CARRIER)
            }
            REG_CARRIER -> Intent(context, WebPageActivity()::class.java).apply {
                putExtra(WebPageView.EXTRA_SCREEN, WebPageView.SCREEN_REG_CARRIER)
            }
            PASSENGER_MODE -> Intent(context, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            else -> null
        }
    }

    data class FindAddress(val from: String, val to: String, val isClickTo: Boolean?,val bounds: LatLngBounds): SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, SearchActivity::class.java).apply {
            putExtra(SearchView.EXTRA_ADDRESS_FROM, from)
            putExtra(SearchView.EXTRA_ADDRESS_TO, to)
            putExtra(SearchView.EXTRA_BOUNDS, bounds)
            isClickTo?.let { putExtra(SearchView.EXTRA_IS_CLICK_TO, it) }
        }
    }

    data class Offers(val transferId: Long): SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, OffersActivity::class.java).apply {
            putExtra(OffersView.EXTRA_TRANSFER_ID, transferId)
        }
    }

    data class Details(val transferId: Long): SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, TransferDetailsActivity::class.java).apply {
            putExtra(TransferDetailsView.EXTRA_TRANSFER_ID, transferId)
        }
    }

    data class TripDetails(val tripId: Long): SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, CarrierTripDetailsActivity::class.java).apply {
            putExtra(CarrierTripDetailsView.EXTRA_TRIP_ID, tripId)
        }
    }

    data class Payment(val transferId: Long, val offerId: Long, val url: String, val percentage: Int): SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, PaymentActivity::class.java).apply {
            putExtra(PaymentView.EXTRA_TRANSFER_ID, transferId)
            putExtra(PaymentView.EXTRA_OFFER_ID, offerId)
            putExtra(PaymentView.EXTRA_URL, url)
            putExtra(PaymentView.EXTRA_PERCENTAGE, percentage)
        }
    }

    data class PaymentSettings(val transferId: Long, val offerId: Long, val dateRefund: Date?): SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, PaymentSettingsActivity::class.java).apply {
            putExtra(PaymentSettingsView.EXTRA_PARAMS,
                     JSON.stringify(PaymentSettingsView.Params.serializer(),
                                    PaymentSettingsView.Params(dateRefund, transferId, offerId)))
        }
    }
}
