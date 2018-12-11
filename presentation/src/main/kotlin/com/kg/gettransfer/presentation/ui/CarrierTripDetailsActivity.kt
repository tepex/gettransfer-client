package com.kg.gettransfer.presentation.ui

import android.content.Context

import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar

import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.google.android.gms.maps.GoogleMap

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isGone

import com.kg.gettransfer.presentation.model.CarrierTripModel
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel

import com.kg.gettransfer.presentation.presenter.CarrierTripDetailsPresenter
import com.kg.gettransfer.presentation.view.CarrierTripDetailsView

import kotlinx.android.synthetic.main.activity_carrier_trip_details.*
import kotlinx.android.synthetic.main.bottom_sheet_carrier_trip_details.*
import kotlinx.android.synthetic.main.toolbar.view.*

import kotlinx.android.synthetic.main.view_carrier_trip_details_layout_about_car.*
import kotlinx.android.synthetic.main.view_carrier_trip_details_layout_about_passenger.*
import kotlinx.android.synthetic.main.view_carrier_trip_details_layout_more_info.*
import kotlinx.android.synthetic.main.view_icon_with_number_indicator.view.*

class CarrierTripDetailsActivity : BaseGoogleMapActivity(), CarrierTripDetailsView {
    @InjectPresenter
    internal lateinit var presenter: CarrierTripDetailsPresenter

    @ProvidePresenter
    fun createCarrierTripDetailsPresenter() = CarrierTripDetailsPresenter()

    override fun getPresenter(): CarrierTripDetailsPresenter = presenter

    private lateinit var bsCarrierTripDetails: BottomSheetBehavior<View>

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.tripId = intent.getLongExtra(CarrierTripDetailsView.EXTRA_TRIP_ID, 0)
        val transferId = intent.getLongExtra(CarrierTripDetailsView.EXTRA_TRANSFER_ID, 0)

        setContentView(R.layout.activity_carrier_trip_details)

        titleComment.text = getString(R.string.LNG_RIDE_SETTINGS_OPTIONAL_COMMENT).substring(0, 1).toUpperCase()
                .plus(getString(R.string.LNG_RIDE_SETTINGS_OPTIONAL_COMMENT).substring(1))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }

        _mapView = mapView
        initMapView(savedInstanceState)
        setToolbar(toolbar as Toolbar, TOOLBAR_NO_TITLE)
        //(toolbar as Toolbar).toolbar_title.text = getString(R.string.LNG_TRANSFER).plus(" #$transferId")
        (toolbar as Toolbar).toolbar_title.text = getString(R.string.LNG_TRIP_DETAILS).plus(" #$transferId")

        //_tintBackground = tintBackground
        bsCarrierTripDetails = BottomSheetBehavior.from(sheetCarrierTripDetails)
        //bsCarrierTripDetails.setBottomSheetCallback(bottomSheetCallback)
        bsCarrierTripDetails.state = BottomSheetBehavior.STATE_EXPANDED
        setOnClickListeners()
    }

    /*override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (bsCarrierTripDetails.state == BottomSheetBehavior.STATE_EXPANDED) {
                if(hideBottomSheet(bsCarrierTripDetails, sheetCarrierTripDetails, BottomSheetBehavior.STATE_COLLAPSED, event)) return true
            }
        }
        return super.dispatchTouchEvent(event)
    }*/

        layoutTransferInfo.chevron.isGone = true
        layoutTransferInfo.divider.isGone = true

    override fun setTripInfo(trip: CarrierTripModel) {
        layoutCarrierTripInfo.setInfo(trip, true)
        initMoreInfoLayout(trip)
        initAboutCarLayout(trip.vehicleName, trip.vehicleNumber)
        /*if(trip.tripStatus == CarrierTripModel.PAST_TRIP){
            layoutAboutPassenger.isVisible = false
        } else {
           trip.passengerAccount?.profileModel?.let { initAboutPassengerLayout(it) }
        }*/
        trip.passengerAccount?.profileModel?.let { initAboutPassengerLayout(it) }
    }

    private fun initMoreInfoLayout(trip: CarrierTripModel){
        trip.comment?.let {
            tvComment.text = it
            layoutMoreInfoComment.isVisible = true
        }
        trip.flightNumber?.let {
            tvFlightNumber.text = it
            layoutFlightNumber.isVisible = true
        }
        countPassengers.numberIndicator.text = trip.countPassengers.toString()
        if(trip.countChild > 0){
            layoutCountChild.isVisible = true
            childSeats.numberIndicator.text = trip.countChild.toString()
        }
    }

    private fun initAboutCarLayout(vehicleName: String, vehicleNumber: String){
        carName.text = vehicleName
        carNumber.text = vehicleNumber
    }

    private fun initAboutPassengerLayout(passengerProfile: ProfileModel){
        passengerName.text = passengerProfile.name

        val operations = listOf<Pair<CharSequence, String>>(
                Pair(getString(R.string.LNG_COPY), CarrierTripDetailsPresenter.OPERATION_COPY),
                Pair(getString(R.string.LNG_OPEN), CarrierTripDetailsPresenter.OPERATION_OPEN))
        val operationsName: List<CharSequence> = operations.map { it.first }

        passengerProfile.email?.let { email ->
            passengerEmail.text = email
            Utils.setSelectOperationListener(this, layoutPassengerEmail, operationsName, R.string.LNG_DRIVER_EMAIL) {
                presenter.makeFieldOperation(CarrierTripDetailsPresenter.FIELD_EMAIL, operations[it].second, email) }
            btnSendEmailPassenger.setOnClickListener { presenter.sendEmail(email) }
            btnChat.setOnClickListener { presenter.sendEmail(email) }
        }
        passengerProfile.phone?.let{ phone ->
            passengerPhone.text = phone
            Utils.setSelectOperationListener(this, layoutPassengerPhone, operationsName, R.string.LNG_DRIVER_PHONE) {
                presenter.makeFieldOperation(CarrierTripDetailsPresenter.FIELD_PHONE, operations[it].second, phone) }
            btnCallPassenger.setOnClickListener { presenter.callPhone(phone) }
            btnCall.setOnClickListener { presenter.callPhone(phone) }
        }
    }

    override fun setTripInfo(trip: CarrierTripModel) {
        layoutTransferInfo.tvTransferRequestNumber.text = getString(R.string.LNG_RIDE_NUMBER).plus(trip.base.transferId)
        layoutTransferInfo.tvFrom.text = trip.base.from
        layoutTransferInfo.tvTo.text = trip.base.to
        //layoutTransferInfo.tvOrderDateTime.text = getString(R.string.transfer_date_local, trip.dateTime)
        layoutTransferInfo.tvOrderDateTime.text = trip.base.dateTime
        layoutTransferInfo.tvDistance.text = SystemUtils.formatDistance(this, trip.base.distance, true)
        tvCountPassengers.text = trip.countPassengers.toString()

        if (trip.nameSign != null) tvPassengerName.text = trip.nameSign else layoutName.isGone = true
        if (trip.base.countChild > 0) tvCountChildSeats.text = trip.base.countChild.toString() else layoutChildSeat.isGone = true
        if (trip.flightNumber != null) tvFlightOrTrainNumber.text = trip.flightNumber else layoutFlightNumber.isGone = true
        if (trip.base.comment != null) tvComment.text = trip.base.comment else layoutComment.isGone = true
        tvPay.text = trip.remainsToPay
    }
}