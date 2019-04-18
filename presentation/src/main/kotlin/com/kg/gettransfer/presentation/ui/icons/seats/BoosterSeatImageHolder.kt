package com.kg.gettransfer.presentation.ui.icons.seats

import com.kg.gettransfer.R

object BoosterSeatImageHolder: SeatImageHolder() {
    override val selected: Int
        get() = R.drawable.booster_seat_enabled
    override val empty: Int
        get() = R.drawable.booster_seat_disabled
}