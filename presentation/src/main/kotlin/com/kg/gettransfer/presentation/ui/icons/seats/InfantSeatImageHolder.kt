package com.kg.gettransfer.presentation.ui.icons.seats

import com.kg.gettransfer.R

object InfantSeatImageHolder: SeatImageHolder() {
    override val selected: Int
        get() = R.drawable.infant_seat_enabled
    override val empty: Int
        get() = R.drawable.infant_seat_disabled
}