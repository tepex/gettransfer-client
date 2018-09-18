package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.widget.Toolbar
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.CarrierTripDetailsPresenter
import com.kg.gettransfer.presentation.view.CarrierTripDetailsView
import kotlinx.android.synthetic.main.activity_carrier_transfer_details.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_transfer_request_info.view.*

class CarrierTripDetailsActivity: BaseActivity(), CarrierTripDetailsView{
    @InjectPresenter
    internal lateinit var presenter: CarrierTripDetailsPresenter

    @ProvidePresenter
    fun createCarrierTripDetailsPresenter(): CarrierTripDetailsPresenter = CarrierTripDetailsPresenter(coroutineContexts, router, apiInteractor)

    override fun getPresenter(): CarrierTripDetailsPresenter = presenter

    protected override var navigator = object: BaseNavigator(this){}

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_carrier_transfer_details)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).toolbar_title.setText(R.string.activity_carrier_transfer_details_title)
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }

        layoutTransferInfo.chevron.visibility = View.GONE
        layoutTransferInfo.divider.visibility = View.GONE
    }

    override fun setTripInfo(transferId: Long, from: String, to: String, dateTime: String, distance: Int, countPassengers: Int?, passengerName: String?, countChild: Int, flightNumber: String?, comment: String?, pay: String?) {
        layoutTransferInfo.tvTransferRequestNumber.text = transferId.toString()
        layoutTransferInfo.tvFrom.text = from
        layoutTransferInfo.tvTo.text = to
        layoutTransferInfo.tvOrderDateTime.text = dateTime
        layoutTransferInfo.tvDistance.text = distance.toString()
        tvCountPassengers.text = countPassengers.toString()
        if (passengerName != null) tvPassengerName.text = passengerName else tvPassengerName.text = "-//-"
        if (countChild > 0) tvCountChildSeats.text = countChild.toString() else tvCountChildSeats.text = "-//-"
        if (flightNumber != null) tvFlightOrTrainNumber.text = flightNumber else tvFlightOrTrainNumber.text ="-//-"
        if (comment != null) tvComment.text = comment else tvComment.text = "-//-"
        if (tvPay != null) tvPay.text = pay else tvPay.text = "-//-"
    }
}