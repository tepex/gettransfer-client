package com.kg.gettransfer.presentation

import android.content.Context
import android.content.Intent

import com.kg.gettransfer.presentation.ui.LoginActivity
import com.kg.gettransfer.presentation.ui.PaymentSettingsActivity

import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {
	@JvmField val BUNDLE_KEY       = "bundle_key"
	
	@JvmField val MAIN  		   = "main"
	@JvmField val FIND_ADDRESS     = "find_address"
	@JvmField val ABOUT            = "about"
	@JvmField val READ_MORE        = "read_more"
	@JvmField val SHARE_LOGS	   = "logs"

	@JvmField val REQUESTS         = "requests"
	@JvmField val CARRIER_TRIPS    = "carrier_trips"
	@JvmField val SETTINGS         = "settings"
	@JvmField val CREATE_ORDER     = "create_order"
	@JvmField val CARRIER_MODE     = "carrier_mode"
	@JvmField val PASSENGER_MODE   = "passenger_mode"
	@JvmField val SELECT_FINISH    = "select_finish_on_map"

	@JvmField val TRIP_DETAILS     = "carrier_trip_details"

	@JvmField val LICENCE_AGREE    = "licence_agree"
	@JvmField val REG_CARRIER      = "registration_carrier"

	@JvmField val OFFERS           = "offers"
	@JvmField val DETAILS          = "details"

	@JvmField val PAYMENT          = "payment"
	
	@JvmField val NOT_USED 		   = -1

	@JvmField val CLOSE_ACTIVITY   = "close_activity"

    data class Login(val nextScreen: String, val email: String): SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            putExtra(LoginActivity.SCREEN_FOR_RETURN, nextScreen)
            putExtra(LoginActivity.EMAIL_TO_LOGIN, email)
        }
    }

    data class PaymentSettings(val dateRefund: Date?, val transferId: Long, val offerId: Long): SupportAppScreen() {
        override fun getActivityIntent(context: Context?) = Intent(context, PaymentSettingsActivity::class.java).apply {
            putExtra(PaymentSettingsActivity.EXTRA_DATE_REFUND, dateRefund)
            putExtra(PaymentSettingsActivity.EXTRA_TRANSFER_ID, transferId)
            putExtra(PaymentSettingsActivity.EXTRA_OFFER_ID, offerId)
        }
}
