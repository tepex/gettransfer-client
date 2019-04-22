package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior

import android.support.v4.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.model.TransportTypeModel
import kotlinx.android.synthetic.main.bottom_sheet_type_transport.*
import java.lang.Exception

class TransportTypeFragment: Fragment() {

    var transportTypeModel: TransportTypeModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.bottom_sheet_type_transport, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = view.parent
        if(parent is ViewGroup){
            try {
                val bottomSheet = BottomSheetBehavior.from(parent)
                bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED

                btnOk.setOnClickListener {
                    bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
                }

            } catch (e: Exception){ }
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
