package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.v7.widget.Toolbar

import android.view.MotionEvent
import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.google.android.gms.maps.GoogleMap

import com.kg.gettransfer.R

import com.kg.gettransfer.presentation.model.CarrierTripModel
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel

import com.kg.gettransfer.presentation.presenter.CarrierTripDetailsPresenter

import com.kg.gettransfer.presentation.view.CarrierTripDetailsView

import kotlinx.android.synthetic.main.activity_carrier_transfer_details.*
import kotlinx.android.synthetic.main.view_transfer_request_info.view.*

class CarrierTripDetailsActivity: BaseGoogleMapActivity(), CarrierTripDetailsView {
    @InjectPresenter
    internal lateinit var presenter: CarrierTripDetailsPresenter

    @ProvidePresenter
    fun createCarrierTripDetailsPresenter() = CarrierTripDetailsPresenter()

    override fun getPresenter(): CarrierTripDetailsPresenter = presenter

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.tripId = intent.getLongExtra(CarrierTripDetailsView.EXTRA_TRIP_ID, 0)

        setContentView(R.layout.activity_carrier_transfer_details)
        _mapView = mapView
        initMapView(savedInstanceState)

        setToolbar(toolbar as Toolbar, R.string.activity_carrier_transfer_details_title)

        layoutTransferInfo.chevron.visibility = View.GONE
        layoutTransferInfo.divider.visibility = View.GONE

        btnCall.setOnClickListener { presenter.onCallClick() }
    }

    protected suspend override fun customizeGoogleMaps() {
        super.customizeGoogleMaps()
        // https://stackoverflow.com/questions/16974983/google-maps-api-v2-supportmapfragment-inside-scrollview-users-cannot-scroll-th
        transparentImage.setOnTouchListener(View.OnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    svTransfer.requestDisallowInterceptTouchEvent(true)
                    return@OnTouchListener false
                }
                MotionEvent.ACTION_UP -> {
                    svTransfer.requestDisallowInterceptTouchEvent(false)
                    return@OnTouchListener true
                }
                MotionEvent.ACTION_MOVE -> {
                    svTransfer.requestDisallowInterceptTouchEvent(true)
                    return@OnTouchListener false
                }
            }
            return@OnTouchListener true
        })
    }

    /*override fun setRoute(routeModel: RouteModel) {
        Utils.setPins(this, googleMap, routeModel)
    }*/

    override fun setRoute(polyline: PolylineModel, routeModel: RouteModel) {
        setPolyline(polyline, routeModel)
    }

    override fun setTripInfo(trip: CarrierTripModel) {
        layoutTransferInfo.tvTransferRequestNumber.text = getString(R.string.LNG_RIDE_NUMBER).plus(trip.transferId)
        layoutTransferInfo.tvFrom.text = trip.from
        layoutTransferInfo.tvTo.text = trip.to
        //layoutTransferInfo.tvOrderDateTime.text = getString(R.string.transfer_date_local, trip.dateTime)
        layoutTransferInfo.tvOrderDateTime.text = trip.dateTime
        layoutTransferInfo.tvDistance.text = Utils.formatDistance(this, trip.distance, trip.distanceUnit)
        tvCountPassengers.text = trip.countPassengers.toString()
        
        if(trip.nameSign != null) tvPassengerName.text = trip.nameSign else layoutName.visibility = View.GONE
        if(trip.countChild > 0) tvCountChildSeats.text = trip.countChild.toString() else layoutChildSeat.visibility = View.GONE
        if(trip.flightNumber != null) tvFlightOrTrainNumber.text = trip.flightNumber else layoutFlightNumber.visibility = View.GONE
        if(trip.comment != null) tvComment.text = trip.comment else layoutComment.visibility = View.GONE
        if(trip.remainsToPay != null) tvPay.text = trip.remainsToPay else layoutRemainsToPay.visibility = View.GONE
    }
}
