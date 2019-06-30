package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.delegate.ChildSeatsView
import com.kg.gettransfer.presentation.delegate.PassengersDelegate
import com.kg.gettransfer.presentation.delegate.PassengersDelegate.Companion.BOOSTER
import com.kg.gettransfer.presentation.delegate.PassengersDelegate.Companion.CONVERTIBLE
import com.kg.gettransfer.presentation.delegate.PassengersDelegate.Companion.INFANT
import com.kg.gettransfer.presentation.ui.icons.seats.SeatImageProvider
import kotlinx.android.synthetic.main.bottom_sheet_child_seats.*
import kotlinx.android.synthetic.main.view_child_seat_type_counter.view.*
import kotlinx.android.synthetic.main.view_count_controller.view.*
import org.koin.core.KoinComponent
import org.koin.core.inject

class ChildSeatsFragment: BaseBottomSheetFragment(), ChildSeatsView, KoinComponent{
    override val layout = R.layout.bottom_sheet_child_seats
    override val minusEnabled = R.drawable.ic_minus
    override val minusDisabled = R.drawable.ic_minus_disabled

    private val delegate: PassengersDelegate by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()
        initCounters()

        setBottomSheetState(view, BottomSheetBehavior.STATE_EXPANDED)
    }

    private fun initCounters() {
        delegate.initSeats(this)
    }

    private fun initClickListeners() {
        view_infant_seat.view_counter_btns.img_minus_seat.setOnClickListener      { performMinus(INFANT) }
        view_infant_seat.view_counter_btns.img_plus_seat.setOnClickListener       { performPlus(INFANT) }
        view_convertible_seat.view_counter_btns.img_minus_seat.setOnClickListener { performMinus(CONVERTIBLE) }
        view_convertible_seat.view_counter_btns.img_plus_seat.setOnClickListener  { performPlus(CONVERTIBLE) }
        view_booster_seat.view_counter_btns.img_minus_seat.setOnClickListener     { performMinus(BOOSTER) }
        view_booster_seat.view_counter_btns.img_plus_seat.setOnClickListener      { performPlus(BOOSTER) }
        btnOkChildSeats.setOnClickListener                                        {
            setBottomSheetState( this@ChildSeatsFragment.view!!, BottomSheetBehavior.STATE_HIDDEN)   //force unwrap because fragment already has view with clicked button
        }
    }

    private fun performPlus(type: Int) =
            delegate.increase(type, this)

    private fun performMinus(type: Int) =
            delegate.decrease(type, this)

    override fun updateView(count: Int, type: Int) {
        val res = if (count == 0) minusDisabled else minusEnabled
        context?.let {
            getMinusImageView(type).setImageDrawable(ContextCompat.getDrawable(it, res))
            val seatImageRes = SeatImageProvider.getImage(type, count > 0)
            getSeatImage(type).setImageDrawable(ContextCompat.getDrawable(it, seatImageRes))
        }
        getTextView(type).text = "$count"
    }

    private fun getMinusImageView (type: Int) =
            when (type) {
                INFANT      -> view_infant_seat.view_counter_btns.img_minus_seat
                CONVERTIBLE -> view_convertible_seat.view_counter_btns.img_minus_seat
                BOOSTER     -> view_booster_seat.view_counter_btns.img_minus_seat
                else        -> throw IllegalArgumentException()
        }

    private fun getTextView(type: Int) =
            when (type) {
                INFANT      -> view_infant_seat.view_counter_btns.person_count
                CONVERTIBLE -> view_convertible_seat.view_counter_btns.person_count
                BOOSTER     -> view_booster_seat.view_counter_btns.person_count
                else        -> throw IllegalArgumentException()
            }
    private fun getSeatImage(type: Int) =
            when (type) {
                INFANT      -> iv_infant_child_seat
                CONVERTIBLE -> iv_convertible_child_seat
                BOOSTER     -> iv_booster_child_seat
                else        -> throw IllegalArgumentException()
            }
}