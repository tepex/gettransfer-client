package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.v4.app.Fragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.kg.gettransfer.R
import kotlinx.android.synthetic.main.bottom_sheet_type_transport.*

class TransportTypeFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.bottom_sheet_type_transport, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ctx = context
        if(ctx is CreateOrderActivity) {
            val model = ctx.getTransportTypeModel()
            model?.let {
                model.nameId?.let {
                    tvTypeTransfer.setText(it)
                }

                model.imageId?.let {
                    ivTypeTransfer.setImageResource(it)
                }

                model.price?.min?.let {
                    tvPrice.text = it
                }

                model.description?.let {
                    tvCars.setText(it)
                }

                tvCountPassengers.text = model.paxMax.toString()
                tvCountLuggage.text = model.luggageMax.toString()
            }

            ctx.expandBottomSheet()
        }

        btnOk.setOnClickListener {
            if(ctx is CreateOrderActivity) {
                ctx.hideBottomSheet()
            }
        }
    }
}
