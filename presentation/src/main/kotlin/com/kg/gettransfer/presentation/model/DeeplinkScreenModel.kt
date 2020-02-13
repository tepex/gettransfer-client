package com.kg.gettransfer.presentation.model

sealed class DeeplinkScreenModel<Long, String> {
    class PaymentOffer(val transferId: Long, val offerId: Long?, val bookNowOfferId: String?) : DeeplinkScreenModel<Long, String>()
    class Chat(val transferId: Long) : DeeplinkScreenModel<Long, String>()
    class Transfer(val transferId: Long) : DeeplinkScreenModel<Long, String>()
    class RateTransfer(val transferId: Long, val rate: Int?) : DeeplinkScreenModel<Long, String>()
    class DownloadVoucher(val transferId: Long) : DeeplinkScreenModel<Long, String>()
    class CreateOrder(val fromPlaceId: String?, val toPlaceId: String?, val promo: String?) : DeeplinkScreenModel<Long, String>()
    class NewPassword(val authKey: String?) : DeeplinkScreenModel<Long, String>()
    class Main : DeeplinkScreenModel<Long, String>()
}