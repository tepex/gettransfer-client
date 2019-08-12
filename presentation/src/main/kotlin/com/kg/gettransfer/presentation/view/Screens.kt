package com.kg.gettransfer.presentation.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment

import com.kg.gettransfer.BuildConfig

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.ui.*
import com.kg.gettransfer.presentation.view.MainNavigateView.Companion.EXTRA_RATE_TRANSFER_ID
import com.kg.gettransfer.presentation.view.MainNavigateView.Companion.EXTRA_RATE_VALUE
import kotlinx.serialization.json.JSON

import org.jetbrains.anko.toast

import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {
    @JvmField val NOT_USED = -1

    @JvmField val MAIN = "main"
    @JvmField val OFFERS = "offers"
    @JvmField val DETAILS = "details"

    @JvmField val CARRIER_MODE = "carrier_mode"
    @JvmField val PASSENGER_MODE = "passenger_mode"

    @JvmField val REG_CARRIER = "registration_carrier"
    @JvmField val CLOSE_AFTER_LOGIN = "close_after_login"

    @JvmField val MAIN_WITH_MAP = "main_with_map"
    @JvmField val MAIN_WITHOUT_MAP = "main_without_map"

    @JvmField val CARRIER_TRIPS_TYPE_VIEW_CALENDAR = "carrier_trips_type_calendar"
    @JvmField val CARRIER_TRIPS_TYPE_VIEW_LIST = "carrier_trips_type_list"

    @JvmField val PAYMENT_OFFER = "payment_offer"
    @JvmField val RATE_TRANSFER = "rate_transfer"
    const val RETURN_MAIN = "return"

    private const val EMAIL_DATA = "mailto:"
    private const val DIAL_SCHEME = "tel"

    private const val NEW_LINE = "\n"
    private const val UNDERSCORE = "_"

    private var canSendEmail: Boolean? = null

    data class MainPassenger(
        val showDrawer: Boolean = false  //TODO remove
    ) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, MainNavigateActivity::class.java)
    }

    data class MainPassengerToRateTransfer(
            val transferId: Long,
            val rate: Int
    ) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, MainNavigateActivity::class.java)
            .apply {
                putExtra(EXTRA_RATE_TRANSFER_ID, transferId)
                putExtra(EXTRA_RATE_VALUE, rate)
            }
    }

    object CarrierMode : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, CarrierTripsMainActivity()::class.java)
    }

    object CarrierRegister : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, WebPageActivity()::class.java).apply {
            putExtra(WebPageView.EXTRA_SCREEN, WebPageView.SCREEN_REG_CARRIER)
        }
    }

    object DriverModeNotSupport : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, DriverModeNotSupportedActivity()::class.java)
    }

    object Requests : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, RequestsActivity::class.java)
    }

    object LicenceAgree : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, WebPageActivity()::class.java).apply {
            putExtra(WebPageView.EXTRA_SCREEN, WebPageView.SCREEN_LICENSE)
        }
    }

    object RestorePassword : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, WebPageActivity()::class.java).apply {
            putExtra(WebPageView.EXTRA_SCREEN, WebPageView.SCREEN_RESTORE_PASS)
        }
    }

    object CarrierTransfers : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, WebPageActivity()::class.java).apply {
            putExtra(WebPageView.EXTRA_SCREEN, WebPageView.SCREEN_TRANSFERS)
        }
    }

    object CreateOrder : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, CreateOrderActivity::class.java)
    }

    data class About(val isOnboardingShowed: Boolean) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, AboutActivity::class.java).apply {
            putExtra(AboutView.EXTRA_OPEN_MAIN, isOnboardingShowed)
        }
    }

    open class MainLogin(val nextScreen: String, val emailOrPhone: String?) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, MainLoginActivity::class.java).apply {
            putExtra(LogInView.EXTRA_PARAMS,
                JSON.stringify(
                    LogInView.Params.serializer(),
                    LogInView.Params(
                        nextScreen,
                        emailOrPhone = emailOrPhone ?: "")
                )
            )
        }
    }

    /*open class Login(val nextScreen: String, val emailOrPhone: String?) : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return LogInFragment().apply {
                arguments = Bundle().apply {
                    putString(LogInView.EXTRA_NEXT_SCREEN, nextScreen)
                    putString(LogInView.EXTRA_EMAIL_TO_LOGIN, emailOrPhone)
                }
            }
        }
    }

    open class SignUp() : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return SignUpFragment()
        }
    }*/

    open class AuthorizationPager(private val params: String) :
        SupportAppScreen() {
        override fun getFragment(): Fragment = AuthorizationPagerFragment().apply {
            arguments = Bundle().apply {
                putString(LogInView.EXTRA_PARAMS, params)
            }
        }
    }

    open class ProfileSettings() : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, ProfileSettingsActivity::class.java)
    }

    open class ChangeEmail() : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, SettingsChangeEmailActivity::class.java)
    }

    open class ChangePassword() : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, SettingsChangePasswordActivity::class.java)
    }

    open class SmsCode(
        private val params: LogInView.Params,
        private val isPhone: Boolean
    ) : SupportAppScreen() {
        override fun getFragment() = SmsCodeFragment.newInstance().apply {
            arguments = Bundle().apply {
                putString(
                    LogInView.EXTRA_PARAMS,
                    JSON.stringify(LogInView.Params.serializer(), params)
                )
                putBoolean(SmsCodeFragment.EXTERNAL_IS_PHONE, isPhone)
            }
        }
    }

    data class LoginToGetOffers(val transferId: Long, val email: String?) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, MainLoginActivity::class.java).apply {
            putExtra(LogInView.EXTRA_PARAMS,
                JSON.stringify(
                    LogInView.Params.serializer(),
                    LogInView.Params(
                        Screens.OFFERS,
                        transferId,
                        emailOrPhone = email ?: "")
                )
            )
        }
    }

    data class LoginToShowDetails(val transferId: Long) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, MainLoginActivity::class.java).apply {
            putExtra(LogInView.EXTRA_PARAMS,
                JSON.stringify(
                    LogInView.Params.serializer(),
                    LogInView.Params(
                        Screens.DETAILS,
                        transferId)
                )
            )
        }
    }

    data class LoginToPaymentOffer(val transferId: Long, val offerId: Long?) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, MainLoginActivity::class.java).apply {
            putExtra(LogInView.EXTRA_PARAMS,
                JSON.stringify(
                    LogInView.Params.serializer(),
                    LogInView.Params(
                        Screens.PAYMENT_OFFER,
                        transferId,
                        offerId = offerId ?: 0L)
                )
            )
        }
    }

    data class LoginToRateTransfer(val transferId: Long, val rate: Int) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, MainLoginActivity::class.java).apply {
            putExtra(LogInView.EXTRA_PARAMS,
                JSON.stringify(
                    LogInView.Params.serializer(),
                    LogInView.Params(
                        Screens.RATE_TRANSFER,
                        transferId,
                        rate = rate)
                )
            )
        }
    }

    data class FindAddress(
        val from: String,
        val to: String,
        val isClickTo: Boolean?,
        val returnMain: Boolean = false
    ) :
        SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, SearchActivity::class.java).apply {
            putExtra(SearchView.EXTRA_ADDRESS_FROM, from)
            putExtra(SearchView.EXTRA_ADDRESS_TO, to)
            putExtra(RETURN_MAIN, returnMain)
            isClickTo?.let { putExtra(SearchView.EXTRA_IS_CLICK_TO, it) }
        }
    }

    data class Offers(val transferId: Long) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, OffersActivity::class.java).apply {
            putExtra(OffersView.EXTRA_TRANSFER_ID, transferId)
        }
    }

    data class Details(val transferId: Long) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, TransferDetailsActivity::class.java).apply {
            putExtra(TransferDetailsView.EXTRA_TRANSFER_ID, transferId)
        }
    }

    data class Chat(val transferId: Long, val tripId: Long? = null) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, ChatActivity::class.java).apply {
            putExtra(ChatView.EXTRA_TRANSFER_ID, transferId)
            tripId?.let { putExtra(ChatView.EXTRA_TRIP_ID, it) }
        }
    }

    data class TripDetails(val tripId: Long, val transferId: Long) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) =
            Intent(context, CarrierTripDetailsActivity::class.java).apply {
                putExtra(CarrierTripDetailsView.EXTRA_TRIP_ID, tripId)
                putExtra(CarrierTripDetailsView.EXTRA_TRANSFER_ID, transferId)
            }
    }

    data class Payment(
        val url: String?,
        val percentage: Int,
        val paymentType: String
    ) :
        SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, PaymentActivity::class.java).apply {
            putExtra(PaymentView.EXTRA_URL, url)
            putExtra(PaymentView.EXTRA_PERCENTAGE, percentage)
            putExtra(PaymentView.EXTRA_PAYMENT_TYPE, paymentType)
        }
    }

    class PaymentOffer : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, PaymentOfferActivity::class.java).apply {
            /*putExtra(
                PaymentOfferView.EXTRA_PARAMS,
                JSON.stringify(
                    PaymentOfferView.Params.serializer(),
                    PaymentOfferView.Params(dateRefund, transferId, paymentPercentages)
                )
            )*/
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

    data class SendEmail(
        val emailCarrier: String?,
        val transferId: Long?,
        val userEmail: String?
    ) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?): Intent? {
            val emailSelectorIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(EMAIL_DATA)
            }

            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (emailCarrier != null)
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(emailCarrier))
                else putExtra(Intent.EXTRA_EMAIL, arrayOf(context!!.getString(R.string.email_support)))

                val subject = context?.getString(R.string.LNG_EMAIL_SUBJECT)
                    .plus(" ${transferId ?: ""}")

                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, createSignature())

                selector = emailSelectorIntent
            }
            return if (checkEmailIntent(context!!, emailIntent)) Intent.createChooser(
                emailIntent,
                context.getString(R.string.LNG_SEND_EMAIL)
            )
            else Intent()
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
                putExtra(
                    Intent.EXTRA_TEXT,
                    context?.getString(
                        R.string.LNG_SHARE_TEXT,
                        "https://play.google.com/store/apps/details?id=com.gettransfer"
                    )
                )
                type = "text/plain"
            }
        }
    }

    data class CallPhone(val phoneCarrier: String) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) =
            Intent(Intent.ACTION_DIAL, Uri.fromParts(DIAL_SCHEME, phoneCarrier, null))
    }

    private fun checkEmailIntent(context: Context, intent: Intent): Boolean {
        if (canSendEmail == null) canSendEmail =
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isNotEmpty()
        if (!canSendEmail!!) context.toast(context.getString(R.string.LNG_NO_EMAIL_APPS))
        return canSendEmail!!
    }

    data class PayPalConnection(
        val paymentId: Long,
        val nonce: String,
        val transferId: Long,
        val offerId: Long?,
        val percentage: Int,
        val bookNowTransportId: String?
    ) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) =
            Intent(context, PaypalConnectionActivity::class.java).apply {
                putExtra(PaypalConnectionView.EXTRA_PAYMENT_ID, paymentId)
                putExtra(PaypalConnectionView.EXTRA_NONCE, nonce)
                putExtra(PaypalConnectionView.EXTRA_TRANSFER_ID, transferId)
                putExtra(PaypalConnectionView.EXTRA_OFFER_ID, offerId)
                putExtra(PaypalConnectionView.EXTRA_PERCENTAGE, percentage)
                putExtra(PaypalConnectionView.EXTRA_BOOK_NOW_TRANSPORT_ID, bookNowTransportId)
            }
    }
}
