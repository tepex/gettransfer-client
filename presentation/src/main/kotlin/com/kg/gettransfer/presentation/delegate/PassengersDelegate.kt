package com.kg.gettransfer.presentation.delegate

import java.lang.IllegalArgumentException

class PassengersDelegate {
    var infantSeats = MIN_VALUE
    private set(value) {
        if (value < MIN_VALUE) return
        field = value
    }
    var convertibleSeats = MIN_VALUE
    private set(value) {
        if (value < MIN_VALUE) return
        field = value
    }
    var boosterSeats = MIN_VALUE
    private set(value) {
        if (value < MIN_VALUE) return
        field = value
    }

    fun increase(seatType: Int, seatsCounterView: ChildSeatsView) {
        val current = when (seatType) {
            INFANT      -> ++infantSeats
            CONVERTIBLE -> ++convertibleSeats
            BOOSTER     -> ++boosterSeats
            else        -> throw IllegalArgumentException("Wrong counter constant when increase in ${this.javaClass.name}")
        }
        seatsCounterView.updateView(current, seatType)
    }

    fun decrease(seatType: Int, seatsCounterView: ChildSeatsView) {
        val current = when (seatType) {
            INFANT      -> --infantSeats
            CONVERTIBLE -> --convertibleSeats
            BOOSTER     -> --boosterSeats
            else        -> throw IllegalArgumentException("Wrong counter constant when decrease in ${this.javaClass.name}")
        }
        seatsCounterView.updateView(current, seatType)
    }

    fun getTotalSeats() = infantSeats + convertibleSeats + boosterSeats


    companion object {
        const val INFANT      = 1
        const val CONVERTIBLE = 2
        const val BOOSTER     = 3

        const val MIN_VALUE   = 0
    }
}