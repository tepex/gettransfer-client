package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v7.widget.Toolbar

import android.view.MotionEvent
import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.presenter.TransferDetailsPresenter
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper
import com.kg.gettransfer.presentation.view.TransferDetailsView
import com.kg.gettransfer.utilities.Analytics.Companion.TRAVEL_CLASS
import com.kg.gettransfer.utilities.Analytics.Companion.VALUE

import kotlinx.android.synthetic.main.activity_transfer_details.*
import kotlinx.android.synthetic.main.view_transfer_request_info.*
import kotlinx.android.synthetic.main.view_transfer_request_info.view.*
import kotlinx.android.synthetic.main.view_transport_type_transfer_details.view.* //don't delete

class TransferDetailsActivity: BaseGoogleMapActivity(), TransferDetailsView {
    @InjectPresenter
    internal lateinit var presenter: TransferDetailsPresenter
    
    @ProvidePresenter
    fun createTransferDetailsPresenter() = TransferDetailsPresenter()

    override fun getPresenter(): TransferDetailsPresenter = presenter
    
    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.transferId = intent.getLongExtra(TransferDetailsView.EXTRA_TRANSFER_ID, 0)
        
        setContentView(R.layout.activity_transfer_details)

        setToolbar(toolbar as Toolbar, R.string.LNG_RIDE_DETAILS)
        layoutTransferInfo.chevron.visibility = View.GONE

        _mapView = mapView
        initMapView(savedInstanceState)
    }
    
    protected suspend override fun customizeGoogleMaps(gm: GoogleMap) {
        super.customizeGoogleMaps(gm)

        // https://stackoverflow.com/questions/16974983/google-maps-api-v2-supportmapfragment-inside-scrollview-users-cannot-scroll-th
        transparentImage.setOnTouchListener(View.OnTouchListener { _, motionEvent ->
            when(motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    svTransferDetails.requestDisallowInterceptTouchEvent(true)
                    return@OnTouchListener false
                }
                MotionEvent.ACTION_UP -> {
                    svTransferDetails.requestDisallowInterceptTouchEvent(false)
                    return@OnTouchListener true
                }
                MotionEvent.ACTION_MOVE -> {
                    svTransferDetails.requestDisallowInterceptTouchEvent(true)
                    return@OnTouchListener false
                }
            }
            return@OnTouchListener true
        })
    }

    override fun setTransfer(transferModel: TransferModel) {
        layoutTransferInfo.tvTransferRequestNumber.text = getString(R.string.LNG_RIDE_NUMBER).plus(transferModel.id)
        layoutTransferInfo.tvFrom.text = transferModel.from
        layoutTransferInfo.tvOrderDateTime.text = transferModel.dateTime
        if(transferModel.to != null) {
            layoutTransferInfo.tvTo.text = transferModel.to
            layoutTransferInfo.tvDistance.text = Utils.formatDistance(this, transferModel.distance, transferModel.distanceUnit)
        } else if(transferModel.duration != null) {

            rl_hourly_info.visibility = View.VISIBLE
            tvMarkerTo.visibility = View.GONE
            tv_duration.text = HourlyValuesHelper.getValue(transferModel.duration, this)

        }
        //layoutTransferInfo.tvTo.text = transferModel.to
        //layoutTransferInfo.tvDistance.text = Utils.formatDistance(this, transferModel.distance, transferModel.distanceUnit)

        tvCountPassengers.text = transferModel.countPassengers.toString()
        if(transferModel.nameSign != null) {
            tvPassengerName.text = transferModel.nameSign
            layoutName.visibility = View.VISIBLE
        }
        if(transferModel.countChilds > 0) {
            tvCountChilds.text = transferModel.countChilds.toString()
            layoutChilds.visibility = View.VISIBLE
        }
        if(transferModel.flightNumber != null) {
            tvFlightNumber.text = transferModel.flightNumber
            layoutFlightNumber.visibility = View.VISIBLE
        }
        if(transferModel.comment != null) {
            tvComment.text = transferModel.comment
            layoutComment.visibility = View.VISIBLE
        }

        layoutTransportTypesList.removeAllViews()
        transferModel.transportTypes.forEach {
            var viewTransportType = layoutInflater.inflate(R.layout.view_transport_type_transfer_details, null, false)
            viewTransportType.tvNameTransportType.setText(it.nameId!!)
            viewTransportType.tvCountPersons.text = Utils.formatPersons(this, it.paxMax)
            viewTransportType.tvCountBaggage.text = Utils.formatLuggage(this, it.luggageMax)
            layoutTransportTypesList.addView(viewTransportType)
            presenter.logEventGetOffer(TRAVEL_CLASS, viewTransportType.tvNameTransportType.text.toString())
        }

        if(transferModel.price != null) {
            paymentInfoPaid.text = getString(R.string.activity_transfer_details_paid_sum,
                                             transferModel.paidSum,
                                             transferModel.paidPercentage)
            paymentInfoPay.text = transferModel.remainToPay
            paymentInfoSum.text = transferModel.price
            presenter.logEventGetOffer(VALUE, transferModel.price)
            layoutPaymentInfo.visibility = View.VISIBLE
        }
    }

    override fun setButtonCancelVisible(visible: Boolean) =
        if(visible) btnCancel.visibility = View.VISIBLE else btnCancel.visibility = View.GONE

    override fun setOffer(offerModel: OfferModel) {
        offerModel.driver?.let {
            offerDriverInfoEmail.text = it.email
            offerDriverInfoPhone.text = it.phone
            offerDriverInfoName.text = it.name
            layoutOfferDriverInfo.visibility = View.VISIBLE
        }

        offerTransportInfoCarType.text   = getString(offerModel.vehicle.transportType.nameId!!)
        offerTransportInfoCarName.text   = offerModel.vehicle.vehicleBase.name
        offerTransportInfoCarNumber.text = offerModel.vehicle.vehicleBase.registrationNumber
        offerTransportInfoPrice.text     = offerModel.price.base.default
        
        layoutOfferTransportInfo.visibility = View.VISIBLE
    }

    override fun setRoute(polyline: PolylineModel, routeModel: RouteModel, isDateChanged: Boolean) =
        setPolyline(polyline, routeModel)

    override fun setPinHourlyTransfer(placeName: String, info: String, point: LatLng) =
        processGoogleMap(false) { setPinForHourlyTransfer(placeName, info, point) }
}
