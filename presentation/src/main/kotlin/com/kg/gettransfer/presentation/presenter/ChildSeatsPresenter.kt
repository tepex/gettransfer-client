package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.presentation.ChildSeatsView
import com.kg.gettransfer.presentation.ChildSeatsView.Companion.INFANT
import com.kg.gettransfer.presentation.ChildSeatsView.Companion.CONVERTIBLE
import com.kg.gettransfer.presentation.ChildSeatsView.Companion.BOOSTER

@InjectViewState
class ChildSeatsPresenter: BasePresenter<ChildSeatsView>() {
    var mPresenter: CreateOrderPresenter? = null

    var infantSeats      = 0
     private set
    var convertibleSeats = 0
    private set
    var boosterSeats     = 0
    private set

    fun increase(seatType: Int) {
        when (seatType) {
            INFANT      -> infantSeats++
            CONVERTIBLE -> convertibleSeats++
            BOOSTER     -> boosterSeats++
        }
    }

    fun decrease(seatType: Int) {
        when (seatType) {
            INFANT      -> infantSeats--
            CONVERTIBLE -> convertibleSeats--
            BOOSTER     -> boosterSeats--
        }
    }

    fun updateSeats() {

    }

}