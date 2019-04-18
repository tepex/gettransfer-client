package com.kg.gettransfer.presentation.ui.icons.seats

import com.kg.gettransfer.presentation.delegate.PassengersDelegate

object SeatImageProvider {

    fun getImage(type: Int, selected: Boolean) =
            getImageHolder(type).getImage(selected)

    private fun getImageHolder(type: Int) =
            when (type) {
                PassengersDelegate.INFANT      -> InfantSeatImageHolder
                PassengersDelegate.CONVERTIBLE -> ConvertibleSeatImageHolder
                PassengersDelegate.BOOSTER     -> BoosterSeatImageHolder
                else                           -> InfantSeatImageHolder
            }
}

abstract class SeatImageHolder {
    abstract val selected: Int
    abstract val empty:    Int

    fun getImage(selected: Boolean) =
            if (selected) this.selected
            else empty
}