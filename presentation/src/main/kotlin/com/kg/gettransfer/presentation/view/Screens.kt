package com.kg.gettransfer.presentation.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v4.content.FileProvider

import com.google.android.gms.maps.model.LatLngBounds
import com.kg.gettransfer.BuildConfig

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.ui.*

import kotlinx.serialization.json.JSON

import java.io.File
import java.util.Date

import org.jetbrains.anko.toast

import ru.terrakok.cicerone.android.support.SupportAppScreen


object Screens {
    @JvmField val NOT_USED = -1

    @JvmField val MAIN   = "main"
    @JvmField val OFFERS = "offers"

    @JvmField val CARRIER_MODE = "carrier_mode"
    @JvmField val REG_CARRIER = "registration_carrier"
    @JvmField val PASSENGER_MODE = "passenger_mode"

    @JvmField val CARRIER_TRIPS_TYPE_VIEW_CALENDAR = "carrier_trips_type_calendar"
    @JvmField val CARRIER_TRIPS_TYPE_VIEW_LIST     = "carrier_trips_type_list"

    private const val EMAIL_DATA = "mailto:"
    private const val DIAL_SCHEME = "tel"
    private const val NEW_LINE = "\n"
    private const val UNDERSCORE = "_"

    private var canSendEmail: Boolean? = null

    object Main : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, MainActivity::class.java)
    }

    object ShareLogs : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, LogsActivity::class.java)
    }

    object Settings : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, SettingsActivity::class.java)
    }

    object Requests : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, RequestsActivity::class.java)
    }

    object LicenceAgree : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, WebPageActivity()::class.java).apply {
            putExtra(WebPageView.EXTRA_SCREEN, WebPageView.SCREEN_LICENSE)
        }
    }

    object RestorePassword: SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, WebPageActivity()::class.java).apply {
            putExtra(WebPageView.EXTRA_SCREEN, WebPageView.SCREEN_RESTORE_PASS)
        }
    }

    object CreateOrder : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, CreateOrderActivity::class.java)
    }

    open class Login(val nextScreen: String, val email: String?) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            putExtra(LoginView.EXTRA_SCREEN_FOR_RETURN, nextScreen)
            putExtra(LoginView.EXTRA_EMAIL_TO_LOGIN, email)
        }
    }

    data class About(val isOnboardingShowed: Boolean) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, AboutActivity::class.java).apply {
            putExtra(AboutView.EXTRA_OPEN_MAIN, false)
        }
    }

    data class LoginToGetOffers(val id: Long, val email: String?) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            putExtra(LoginView.EXTRA_TRANSFER_ID, id)
            putExtra(LoginView.EXTRA_SCREEN_FOR_RETURN, Screens.OFFERS)
            putExtra(LoginView.EXTRA_EMAIL_TO_LOGIN, email)
        }
    }

    data class ChangeMode(val mode: String) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = when (mode) {
            /*CARRIER_MODE -> Intent(context, WebPageActivity()::class.java).apply {
                putExtra(WebPageView.EXTRA_SCREEN, WebPageView.SCREEN_CARRIER)
            }*/
            CARRIER_MODE -> Intent(context, CarrierTripsMainActivity()::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
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

    data class FindAddress(val from: String, val to: String, val isClickTo: Boolean?, val bounds: LatLngBounds) :
        SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, SearchActivity::class.java).apply {
            putExtra(SearchView.EXTRA_ADDRESS_FROM, from)
            putExtra(SearchView.EXTRA_ADDRESS_TO, to)
            putExtra(SearchView.EXTRA_BOUNDS, bounds)
            isClickTo?.let { putExtra(SearchView.EXTRA_IS_CLICK_TO, it) }
        }
    }

    data class Offers(val transferId: Long) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, OffersActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            putExtra(OffersView.EXTRA_TRANSFER_ID, transferId)
        }
    }

    data class Details(val transferId: Long) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, TransferDetailsActivity::class.java).apply {
            putExtra(TransferDetailsView.EXTRA_TRANSFER_ID, transferId)
        }
    }

    data class Chat(val transferId: Long) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, ChatActivity::class.java).apply {
            putExtra(ChatView.EXTRA_TRANSFER_ID, transferId)
        }
    }

    data class TripDetails(val tripId: Long, val transferId: Long) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) =
            Intent(context, CarrierTripDetailsActivity::class.java).apply {
                putExtra(CarrierTripDetailsView.EXTRA_TRIP_ID, tripId)
                putExtra(CarrierTripDetailsView.EXTRA_TRANSFER_ID, transferId)
            }
    }

    data class Payment(val transferId: Long, val offerId: Long?, val url: String, val percentage: Int,
                       val bookNowTransportId: String?) :
        SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, PaymentActivity::class.java).apply {
            putExtra(PaymentView.EXTRA_TRANSFER_ID, transferId)
            putExtra(PaymentView.EXTRA_OFFER_ID, offerId)
            putExtra(PaymentView.EXTRA_URL, url)
            putExtra(PaymentView.EXTRA_PERCENTAGE, percentage)
            putExtra(PaymentView.EXTRA_BOOK_NOW_TRANSPORT_ID, bookNowTransportId ?: "")
        }
    }

    data class PaymentSettings(val transferId: Long, val offerId: Long?, val dateRefund: Date?, val paymentPercentages: List<Int>, val bookNowTransportId: String?) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, PaymentSettingsActivity::class.java).apply {
            putExtra(
                PaymentSettingsView.EXTRA_PARAMS,
                JSON.stringify(
                    PaymentSettingsView.Params.serializer(),
                    PaymentSettingsView.Params(dateRefund, transferId, offerId, paymentPercentages, bookNowTransportId)
                )
            )
        }
    }

    data class PaymentError(val transferId: Long) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, PaymentErrorActivity::class.java).apply {
            putExtra(PaymentErrorActivity.TRANSFER_ID, transferId)
        }
    }

    data class PaymentSuccess(val transferId: Long, val offerId: Long?) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) =
            Intent(context, PaymentSuccessfulActivity::class.java).apply {
                putExtra(PaymentSuccessfulActivity.TRANSFER_ID, transferId)
                putExtra(PaymentSuccessfulActivity.OFFER_ID, offerId)
            }
    }

    data class SendEmail(val emailCarrier: String?, val logsFile: File?,
                         val transferId: Long?, val userEmail: String?) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?): Intent? {
            val emailSelectorIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(EMAIL_DATA)
            }

            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (emailCarrier != null) putExtra(Intent.EXTRA_EMAIL, arrayOf(emailCarrier))
                else putExtra(Intent.EXTRA_EMAIL, arrayOf(context!!.getString(R.string.email_support)))

                val subject = context?.getString(R.string.LNG_EMAIL_SUBJECT)
                        .plus(if (transferId != null) " #$transferId" else "")
                putExtra(Intent.EXTRA_SUBJECT, subject)

                putExtra(Intent.EXTRA_TEXT, createSignature())

                selector = emailSelectorIntent

                logsFile?.let {
                    putExtra(
                            Intent.EXTRA_STREAM,
                            FileProvider.getUriForFile(context!!, context.getString(R.string.file_provider_authority), it)
                    )
                }
            }
            return if (checkEmailIntent(context!!, emailIntent)) Intent.createChooser(
                emailIntent,
                context.getString(R.string.send_email)
            ) else null
        }

        private fun createSignature(): String =
                NEW_LINE.plus(UNDERSCORE)
                .plus(UNDERSCORE).plus(NEW_LINE)
                .plus(Build.DEVICE).plus(" ")
                .plus(Build.MODEL).plus(NEW_LINE)
                .plus(BuildConfig.VERSION_NAME).plus(NEW_LINE)
                .plus(transferId ?: "")
                .plus(NEW_LINE)
                .plus(userEmail ?: "")
    }


    class Share() : SupportAppScreen() {
        override fun getActivityIntent(context: Context?): Intent {
            return Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, context?.getString(R.string.LNG_SHARE_TEXT,"https://play.google.com/store/apps/details?id=com.gettransfer"))
                type = "text/plain"
            }
        }
    }

    data class CallPhone(val phoneCarrier: String?) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) =
            if (phoneCarrier != null) Intent(Intent.ACTION_DIAL, Uri.fromParts(DIAL_SCHEME, phoneCarrier, null))
            else {
                context!!.toast(context.getString(R.string.driver_not_number))
                null
            }
    }

    private fun checkEmailIntent(context: Context, intent: Intent): Boolean {
        if (canSendEmail == null) canSendEmail =
                context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isNotEmpty()
        if (!canSendEmail!!) context.toast(context.getString(R.string.no_email_apps))
        return canSendEmail!!
    }
}
