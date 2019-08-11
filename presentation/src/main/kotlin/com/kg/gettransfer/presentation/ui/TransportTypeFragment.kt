package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.TransportTypeModel
import kotlinx.android.synthetic.main.bottom_sheet_type_transport.*

class TransportTypeFragment: BaseBottomSheetFragment() {
    override val layout = R.layout.bottom_sheet_type_transport

    var transportTypeModel: TransportTypeModel? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBottomSheetState(view, BottomSheetBehavior.STATE_EXPANDED)
        btnOk.setOnClickListener { setBottomSheetState(view, BottomSheetBehavior.STATE_HIDDEN) }

        transportTypeModel?.apply {
            tvTypeTransfer.setText(nameId)
            ivTypeTransfer.setImageResource(imageId)
            price?.min?.let { tvPrice.text = it }
            description?.let { tvCars.setText(it) }
            tvCountPassengers.text = paxMax.toString()
            tvCountLuggage.text = luggageMax.toString()
        }
    }

    companion object { fun getInstance() = TransportTypeFragment() }

}
