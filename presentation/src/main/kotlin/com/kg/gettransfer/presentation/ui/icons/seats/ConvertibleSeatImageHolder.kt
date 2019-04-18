package com.kg.gettransfer.presentation.ui.icons.seats

import com.kg.gettransfer.R

object ConvertibleSeatImageHolder: SeatImageHolder() {
    override val selected: Int
        get() = R.drawable.convertible_seat_enabled
    override val empty: Int
        get() = R.drawable.convertible_seat_disabled
}