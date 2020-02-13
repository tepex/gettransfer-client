package com.kg.gettransfer.utilities

import android.net.Uri
import com.kg.gettransfer.presentation.model.DeeplinkScreenModel
import org.koin.core.KoinComponent

class DeeplinkManager : KoinComponent {

    fun getScreenForLink(appLinkData: Uri): DeeplinkScreenModel<Long, String> {
        return appLinkData.path?.let { path ->
            when {
                path == PASSENGER_CABINET || path == PARTNER_CABINET             -> checkCabinetUrl(appLinkData)
                path.startsWith(PASSENGER_RATE) || path.startsWith(PARTNER_RATE) -> checkRateUrl(appLinkData)
                path.contains(VOUCHER)        -> checkVoucherUrl(appLinkData)
                path.contains(NEW_TRANSFER)   -> checkCreateOrderUrl(appLinkData)
                path.startsWith(NEW_PASSWORD) -> checkNewPasswordUrl(appLinkData)
                else                          -> null
            } ?: DeeplinkScreenModel.Main()
        } ?: DeeplinkScreenModel.Main()
    }

    private fun checkCabinetUrl(appLinkData: Uri): DeeplinkScreenModel<Long, String>? {
        val fragment = appLinkData.fragment
        if (fragment?.startsWith(TRANSFERS) != true) return null
        return when {
            fragment.contains(CHOOSE_OFFER_ID) -> checkPaymentOfferUrl(fragment)
            fragment.contains(OPEN_CHAT)       -> checkChatUrl(fragment)
            else                               -> checkTransferUrl(fragment)
        }
    }

    private fun checkPaymentOfferUrl(fragment: String): DeeplinkScreenModel.PaymentOffer? {
        val transferId = fragment.substring(fragment.indexOf(SLASH) + 1, fragment.indexOf(QUESTION)).toLongOrNull()
        val offerId = fragment.substring(fragment.lastIndexOf(EQUAL) + 1, fragment.length).toLongOrNull()
        val bookNowTransportId =
            if (offerId == null) {
                fragment.substring(fragment.lastIndexOf(EQUAL) + 1, fragment.length)
            } else {
                null
            }
        return transferId?.let { DeeplinkScreenModel.PaymentOffer(it, offerId, bookNowTransportId) }
    }

    private fun checkChatUrl(fragment: String): DeeplinkScreenModel.Chat? {
        val transferId = fragment.substring(fragment.indexOf(SLASH) + 1, fragment.indexOf(QUESTION)).toLongOrNull()
        return transferId?.let { DeeplinkScreenModel.Chat(it) }
    }

    private fun checkTransferUrl(fragment: String): DeeplinkScreenModel.Transfer? {
        val transferId = fragment.substring(fragment.indexOf(SLASH) + 1).toLongOrNull()
        return transferId?.let { DeeplinkScreenModel.Transfer(it) }
    }

    private fun checkRateUrl(appLinkData: Uri): DeeplinkScreenModel.RateTransfer? {
        val transferId = appLinkData.lastPathSegment?.toLongOrNull()
        val rate = appLinkData.getQueryParameter(RATE)?.toIntOrNull()
        return transferId?.let { DeeplinkScreenModel.RateTransfer(it, rate) }
    }

    private fun checkVoucherUrl(appLinkData: Uri): DeeplinkScreenModel.DownloadVoucher? {
        val transferId = appLinkData.lastPathSegment?.toLongOrNull()
        return transferId?.let { DeeplinkScreenModel.DownloadVoucher(it) }
    }

    private fun checkCreateOrderUrl(appLinkData: Uri): DeeplinkScreenModel.CreateOrder {
        val fromPlaceId = appLinkData.getQueryParameter(FROM_PLACE_ID)
        val toPlaceId = appLinkData.getQueryParameter(TO_PLACE_ID)
        val promo = appLinkData.getQueryParameter(PROMO_CODE)
        return DeeplinkScreenModel.CreateOrder(fromPlaceId, toPlaceId, promo)
    }

    private fun checkNewPasswordUrl(appLinkData: Uri): DeeplinkScreenModel.NewPassword {
        return DeeplinkScreenModel.NewPassword(appLinkData.getQueryParameter(AUTH_KEY))
    }

    companion object {
        const val PASSENGER_CABINET = "/passenger/cabinet"
        const val PARTNER_CABINET = "/partner/cabinet"
        const val PASSENGER_RATE = "/passenger/rate"
        const val PARTNER_RATE = "/partner/rate"
        const val VOUCHER = "/transfers/voucher"
        const val NEW_TRANSFER = "/transfers/new"
        const val NEW_PASSWORD = "/new_password"
        const val CHOOSE_OFFER_ID = "choose_offer_id"
        const val OPEN_CHAT = "open_chat"
        const val TRANSFERS = "transfers"
        const val SLASH = "/"
        const val EQUAL = "="
        const val QUESTION = "?"
        const val RATE = "rate_val"

        // Params
        const val FROM_PLACE_ID = "from_place_id"
        const val TO_PLACE_ID = "to_place_id"
        const val PROMO_CODE = "promo_code"
        const val AUTH_KEY = "auth_key"
    }
}