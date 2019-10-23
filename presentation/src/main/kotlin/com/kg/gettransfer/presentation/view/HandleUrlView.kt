package com.kg.gettransfer.presentation.view

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(OneExecutionStateStrategy::class)
interface HandleUrlView : BaseView {
    companion object {
        const val RC_WRITE_FILE = 111
        const val PASSENGER_CABINET = "/passenger/cabinet"
        const val PASSENGER_RATE = "/passenger/rate"
        const val CARRIER_CABINET = "/carrier/cabinet"
        const val VOUCHER = "/transfers/voucher"
        const val CHOOSE_OFFER_ID = "choose_offer_id"
        const val OPEN_CHAT = "open_chat"
        const val TRANSFERS = "transfers"
        const val SLASH = "/"
        const val EQUAL = "="
        const val QUESTION = "?"
        const val RATE = "rate_val"

        const val NEW_TRANSFER = "/transfers/new"
        const val FROM_PLACE_ID = "from_place_id"
        const val TO_PLACE_ID = "to_place_id"
        const val PROMO_CODE = "promo_code"
    }
}
