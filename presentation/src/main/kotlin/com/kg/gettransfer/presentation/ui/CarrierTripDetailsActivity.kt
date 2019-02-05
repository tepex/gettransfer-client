package com.kg.gettransfer.presentation.ui

import android.content.Context

import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar

import android.view.View
import android.view.WindowManager

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.model.*

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
        presenter.transferId = intent.getLongExtra(CarrierTripDetailsView.EXTRA_TRANSFER_ID, 0)

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
        (toolbar as Toolbar).toolbar_title.text = getString(R.string.LNG_TRIP_DETAILS).plus(" #${presenter.transferId}")

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

    private fun setOnClickListeners() {
        btnCenterRoute.setOnClickListener { presenter.onCenterRouteClick() }
        btnSupport.setOnClickListener { presenter.sendEmail(null, presenter.transferId) }
    }

    override fun setTripInfo(trip: CarrierTripModel) {
        layoutCarrierTripInfo.setInfo(trip.base, trip.totalPrice)
        initMoreInfoLayout(trip)
        initAboutCarLayout(trip.base.vehicle)
        /*if(trip.tripStatus == CarrierTripModel.PAST_TRIP){
            layoutAboutPassenger.isVisible = false
        } else {
           trip.passengerAccount?.profileModel?.let { initAboutPassengerLayout(it) }
        }*/
        initAboutPassengerLayout(trip.passenger.profile)
    }

    private fun initMoreInfoLayout(trip: CarrierTripModel) {
        trip.base.comment?.let {
            tvComment.text = it
            layoutMoreInfoComment.isVisible = true
        }
        trip.flightNumber?.let {
            tvFlightNumber.text = it
            layoutFlightNumber.isVisible = true
        }
        countPassengers.numberIndicator.text = trip.countPassengers.toString()
        if (trip.base.countChild > 0) {
            layoutCountChild.isVisible = true
            childSeats.numberIndicator.text = trip.base.countChild.toString()
        }
    }

    private fun initAboutCarLayout(vehicle: VehicleInfoModel) {
        carName.text = vehicle.name
        carNumber.text = vehicle.registrationNumber
    }

    private fun initAboutPassengerLayout(passenger: ProfileModel) {
        passengerName.text = passenger.name

        val operations = listOf<Pair<CharSequence, String>>(
                Pair(getString(R.string.LNG_COPY), CarrierTripDetailsPresenter.OPERATION_COPY),
                Pair(getString(R.string.LNG_OPEN), CarrierTripDetailsPresenter.OPERATION_OPEN))
        val operationsName: List<CharSequence> = operations.map { it.first }

        passenger.email?.let { email ->
            passengerEmail.text = email
            Utils.setSelectOperationListener(this, layoutPassengerEmail, operationsName, R.string.LNG_DRIVER_EMAIL) {
                presenter.makeFieldOperation(CarrierTripDetailsPresenter.FIELD_EMAIL, operations[it].second, email) }
            btnSendEmailPassenger.setOnClickListener { presenter.sendEmail(email, presenter.transferId) }
            btnChat.setOnClickListener { presenter.sendEmail(email, presenter.transferId) }
        }
        passenger.phone?.let { phone ->
            passengerPhone.text = phone
            Utils.setSelectOperationListener(this, layoutPassengerPhone, operationsName, R.string.LNG_DRIVER_PHONE) {
                presenter.makeFieldOperation(CarrierTripDetailsPresenter.FIELD_PHONE, operations[it].second, phone) }
            btnCallPassenger.setOnClickListener { presenter.callPhone(phone) }
            btnCall.setOnClickListener { presenter.callPhone(phone) }
        }
    }

    override fun setRoute(polyline: PolylineModel, routeModel: RouteModel, isDateChanged: Boolean) =
        setPolyline(polyline, routeModel)

    override fun setPinHourlyTransfer(placeName: String, info: String, point: LatLng, cameraUpdate: CameraUpdate) {
        processGoogleMap(false) {
            setPinForHourlyTransfer(placeName, info, point, cameraUpdate)
        }
    }

    override fun centerRoute(cameraUpdate: CameraUpdate) = showTrack(cameraUpdate)

    override fun copyText(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Copied Text", text)
        clipboard.primaryClip = clip
    }
}
