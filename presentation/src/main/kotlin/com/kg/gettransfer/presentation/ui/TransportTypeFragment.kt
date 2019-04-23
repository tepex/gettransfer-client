package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.TransportTypeModel
import kotlinx.android.synthetic.main.bottom_sheet_type_transport.*

class TransportTypeFragment: BaseBottomSheetFragment() {

    var transportTypeModel: TransportTypeModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.bottom_sheet_type_transport, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBottomSheetState(view, BottomSheetBehavior.STATE_EXPANDED)

        btnOk.setOnClickListener {
            setBottomSheetState(view, BottomSheetBehavior.STATE_HIDDEN)
        }

        transportTypeModel?.let {
            it.nameId?.let {
                tvTypeTransfer.setText(it)
            }

            it.imageId?.let {
                ivTypeTransfer.setImageResource(it)
            }

            it.price?.min?.let {
                tvPrice.text = it
            }

            it.description?.let {
                tvCars.setText(it)
            }

            tvCountPassengers.text = it.paxMax.toString()
            tvCountLuggage.text = it.luggageMax.toString()
        }
    }
}
