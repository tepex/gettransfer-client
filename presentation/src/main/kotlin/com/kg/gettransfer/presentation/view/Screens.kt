package com.kg.gettransfer.presentation.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.ui.*
import com.kg.gettransfer.presentation.ui.dialogs.PaymentErrorDialog
import com.kg.gettransfer.presentation.ui.utils.FragmentUtils
import com.kg.gettransfer.presentation.view.MainNavigateView.Companion.EXTRA_RATE_TRANSFER_ID
import com.kg.gettransfer.presentation.view.MainNavigateView.Companion.EXTRA_RATE_VALUE
import com.kg.gettransfer.presentation.view.MainNavigateView.Companion.SHOW_ABOUT

import kotlinx.serialization.json.JSON

import org.jetbrains.anko.toast

import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {
    const val NOT_USED = -1

    const val OFFERS = "offers"
    const val DETAILS = "details"

    const val CLOSE_AFTER_LOGIN = "close_after_login"

    const val PAYMENT_OFFER    = "payment_offer"
    const val RATE_TRANSFER    = "rate_transfer"
    const val DOWNLOAD_VOUCHER = "download_voucher"
    const val CHAT             = "chat"

    private const val EMAIL_DATA = "mailto:"
    private const val DIAL_SCHEME = "tel"

    private const val NEW_LINE = "\n"
    private const val UNDERSCORE = "_"

    private var canSendEmail: Boolean? = null

    data class MainPassenger(val showAbout: Boolean = false) : SupportAppScreen() {

        override fun getActivityIntent(context: Context?) = Intent(context, MainNavigateActivity::class.java).apply {
            putExtra(SHOW_ABOUT, showAbout)
        }
    }

    data class MainPassengerToRateTransfer(
        val transferId: Long,
        val rate: Int
    ) : SupportAppScreen() {

        override fun getActivityIntent(context: Context?) = Intent(context, MainNavigateActivity::class.java).apply {
            putExtra(EXTRA_RATE_TRANSFER_ID, transferId)
            putExtra(EXTRA_RATE_VALUE, rate)
        }
    }

    object Requests : SupportAppScreen() {

        override fun getActivityIntent(context: Context?) = Intent(context, RequestsPagerActivity::class.java)
    }

    object LicenceAgree : SupportAppScreen() {

        override fun getActivityIntent(context: Context?) = Intent(context, WebPageActivity()::class.java).apply {
            putExtra(WebPageView.EXTRA_SCREEN, WebPageView.SCREEN_LICENSE)
        }
    }

    object CreateOrder : SupportAppScreen() {

        override fun getActivityIntent(context: Context?) = Intent(context, CreateOrderActivity::class.java)
    }

    open class MainLogin(val nextScreen: String, val emailOrPhone: String?) : SupportAppScreen() {

        override fun getActivityIntent(context: Context?) = Intent(context, MainLoginActivity::class.java).apply {
            putExtra(LogInView.EXTRA_PARAMS,
                JSON.stringify(
                    LogInView.Params.serializer(),
                    LogInView.Params(nextScreen, emailOrPhone = emailOrPhone ?: "")
                )
            )
        }
    }

    open class AuthorizationPager(private val params: String) : SupportAppScreen() {

        override fun getFragment(): Fragment = AuthorizationPagerFragment().apply {
            arguments = Bundle().apply { putString(LogInView.EXTRA_PARAMS, params) }
        }
    }

    open class ProfileSettings : SupportAppScreen() {

        override fun getActivityIntent(context: Context?) = Intent(context, ProfileSettingsActivity::class.java)
    }

    open class ChangeEmail : SupportAppScreen() {

        override fun getActivityIntent(context: Context?) = Intent(context, SettingsChangeEmailActivity::class.java)
    }

    open class ChangePhone : SupportAppScreen() {

        override fun getActivityIntent(context: Context?) = Intent(context, SettingsChangePhoneActivity::class.java)
    }

    open class ChangePassword : SupportAppScreen() {

        override fun getActivityIntent(context: Context?) = Intent(context, SettingsChangePasswordActivity::class.java)
    }

    open class SmsCode(
        private val params: LogInView.Params,
        private val isPhone: Boolean
    ) : SupportAppScreen() {

        override fun getFragment() = SmsCodeFragment.newInstance().apply {
            arguments = Bundle().apply {
                putString(LogInView.EXTRA_PARAMS, JSON.stringify(LogInView.Params.serializer(), params))
                putBoolean(SmsCodeView.EXTRA_IS_PHONE, isPhone)
            }
        }
    }

    data class LoginToGetOffers(val transferId: Long, val email: String?) : SupportAppScreen() {

        override fun getActivityIntent(context: Context?) = Intent(context, MainLoginActivity::class.java).apply {
            putExtra(LogInView.EXTRA_PARAMS,
                JSON.stringify(
                    LogInView.Params.serializer(),
                    LogInView.Params(Screens.OFFERS, transferId, emailOrPhone = email ?: "")
                )
            )
        }
    }

    data class LoginToShowDetails(val transferId: Long) : SupportAppScreen() {

        override fun getActivityIntent(context: Context?) = Intent(context, MainLoginActivity::class.java).apply {
            putExtra(LogInView.EXTRA_PARAMS,
                JSON.stringify(
                    LogInView.Params.serializer(),
                    LogInView.Params(Screens.DETAILS, transferId)
                )
            )
        }
    }

    data class LoginToPaymentOffer(
        val transferId: Long,
        val offerId: Long?,
        val bookNowTransportId: String?
    ) : SupportAppScreen() {

        override fun getActivityIntent(context: Context?) = Intent(context, MainLoginActivity::class.java).apply {
            putExtra(LogInView.EXTRA_PARAMS,
                JSON.stringify(
                    LogInView.Params.serializer(),
                    LogInView.Params(
                        Screens.PAYMENT_OFFER,
                        transferId,
                        offerId = offerId ?: 0L,
                        bookNowTransportId = bookNowTransportId ?: ""
                    )
                )
            )
        }
    }

    data class LoginToRateTransfer(val transferId: Long, val rate: Int) : SupportAppScreen() {

        override fun getActivityIntent(context: Context?) = Intent(context, MainLoginActivity::class.java).apply {
            putExtra(LogInView.EXTRA_PARAMS,
                JSON.stringify(
                    LogInView.Params.serializer(),
                    LogInView.Params(Screens.RATE_TRANSFER, transferId, rate = rate)
                )
            )
        }
    }

    data class LoginToDownloadVoucher(val transferId: Long) : SupportAppScreen() {

        override fun getActivityIntent(context: Context?) = Intent(context, MainLoginActivity::class.java).apply {
            putExtra(LogInView.EXTRA_PARAMS,
                JSON.stringify(
                    LogInView.Params.serializer(),
                    LogInView.Params(Screens.DOWNLOAD_VOUCHER, transferId)
                )
            )
        }
    }

    data class LoginToChat(val transferId: Long) : SupportAppScreen() {

        override fun getActivityIntent(context: Context?) = Intent(context, MainLoginActivity::class.java).apply {
            putExtra(LogInView.EXTRA_PARAMS,
                JSON.stringify(
                    LogInView.Params.serializer(),
                    LogInView.Params(Screens.CHAT, transferId)
                )
            )
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

    data class Chat(val transferId: Long) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, ChatActivity::class.java).apply {
            putExtra(ChatView.EXTRA_TRANSFER_ID, transferId)
        }
    }

    data class PlatronPayment(val url: String) : SupportAppScreen() {

        override fun getActivityIntent(context: Context?) =
            Intent(context, PlatronPaymentActivity::class.java).apply {
                putExtra(PlatronPaymentView.EXTRA_URL, url)
            }
    }

    data class CheckoutcomPayment(val paymentId: Long, val amountFormatted: String) : SupportAppScreen() {

        override fun getActivityIntent(context: Context?) =
            Intent(context, CheckoutcomPaymentActivity::class.java).apply {
                putExtra(CheckoutcomPaymentView.EXTRA_PAYMENT_ID, paymentId)
                putExtra(CheckoutcomPaymentView.EXTRA_AMOUNT_FORMATTED, amountFormatted)
            }
    }

    class PaymentOffer : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, PaymentOfferActivity::class.java)
    }

    data class PaymentError(
        val fragmentManager: FragmentManager,
        val transferId: Long,
        val gatewayId: String? = null
    ) {

        fun showDialog() {
            PaymentErrorDialog().apply {
                arguments = Bundle().apply {
                    putLong(PaymentErrorDialog.TRANSFER_ID, transferId)
                    putString(PaymentErrorDialog.GATEWAY_ID, gatewayId)
                }
            }.show(fragmentManager, PaymentErrorDialog.TAG)
        }
    }

    data class PaymentSuccess(val transferId: Long, val offerId: Long?) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) =
            Intent(context, PaymentSuccessfulActivity::class.java).apply {
                putExtra(PaymentSuccessfulView.EXTRA_TRANSFER_ID, transferId)
                putExtra(PaymentSuccessfulView.EXTRA_OFFER_ID, offerId)
            }
    }

    data class SendEmail(
        val emailCarrier: String?,
        val transferId: Long?,
        val userEmail: String?
    ) : SupportAppScreen() {

        @Suppress("UnsafeCallOnNullableType")
        override fun getActivityIntent(context: Context?): Intent? {
            val emailSelectorIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse(EMAIL_DATA)
            }

            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (emailCarrier != null) {
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(emailCarrier))
                } else {
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(context!!.getString(R.string.email_support)))
                }

                val subject = context?.getString(R.string.LNG_EMAIL_SUBJECT).plus(" ${transferId ?: ""}")

                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, createSignature())

                selector = emailSelectorIntent
            }
            return if (checkEmailIntent(context!!, emailIntent)) {
                Intent.createChooser(emailIntent, context.getString(R.string.LNG_SEND_EMAIL))
            } else {
                Intent()
            }
        }

        private fun createSignature() =
            NEW_LINE.plus(UNDERSCORE)
                .plus(UNDERSCORE).plus(NEW_LINE)
                .plus(Build.DEVICE).plus(" ")
                .plus(Build.MODEL).plus(NEW_LINE)
                .plus(BuildConfig.VERSION_NAME).plus(NEW_LINE)
                .plus(transferId ?: "")
                .plus(NEW_LINE)
                .plus(userEmail ?: "")
    }

    class Share : SupportAppScreen() {

        override fun getActivityIntent(context: Context?) = Intent(Intent.ACTION_SEND).apply {
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

    data class CallPhone(val phoneCarrier: String) : SupportAppScreen() {
        override fun getActivityIntent(context: Context?) =
            Intent(Intent.ACTION_DIAL, Uri.fromParts(DIAL_SCHEME, phoneCarrier, null))
    }

    @Suppress("UnsafeCallOnNullableType")
    private fun checkEmailIntent(context: Context, intent: Intent): Boolean {
        if (canSendEmail == null) {
            canSendEmail =
                context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).isNotEmpty()
        }
        if (!canSendEmail!!) {
            context.toast(context.getString(R.string.LNG_NO_EMAIL_APPS))
        }
        return canSendEmail!!
    }

    data class PayPalConnection(
        val paymentId: Long,
        val nonce: String,
        val transferId: Long,
        val offerId: Long?
    ) : SupportAppScreen() {

        override fun getActivityIntent(context: Context?) =
            Intent(context, PaypalConnectionActivity::class.java).apply {
                putExtra(PaypalConnectionView.EXTRA_PAYMENT_ID, paymentId)
                putExtra(PaypalConnectionView.EXTRA_NONCE, nonce)
                putExtra(PaypalConnectionView.EXTRA_TRANSFER_ID, transferId)
                putExtra(PaypalConnectionView.EXTRA_OFFER_ID, offerId)
            }
    }

    fun showSupportScreen(
        fragmentManager: FragmentManager,
        transferId: Long?
    ) {

        FragmentUtils.replaceFragment(
            fragmentManager,
            fragment = SupportFragment.newInstance(transferId, true),
            containerViewId = android.R.id.content,
            tag = SupportFragment.TAG,
            addToBackStack = true)
    }

    /**
     * @param firstLaunch will be true if Onboarding hasn't been shown yet otherwise always be false
     */
    fun showAboutScreen(fragmentManager: FragmentManager, firstLaunch: Boolean = false) {
        FragmentUtils.replaceFragment(
            fragmentManager,
            fragment = AboutFragment.newInstance(firstLaunch),
            containerViewId = android.R.id.content,
            tag = null,
            addToBackStack = true,
            useAnimation = false)
    }
}
