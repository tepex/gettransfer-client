package com.kg.gettransfer.presentation.ui

import android.content.Context

import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.design.widget.CoordinatorLayout
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
import com.kg.gettransfer.presentation.ui.behavior.BottomSheetTripleStatesBehavior
import com.kg.gettransfer.presentation.model.*

import com.kg.gettransfer.presentation.presenter.CarrierTripDetailsPresenter
import com.kg.gettransfer.presentation.ui.custom.CommunicationButton
import com.kg.gettransfer.presentation.ui.custom.TransferDetailsField
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper
import com.kg.gettransfer.presentation.view.CarrierTripDetailsView

import kotlinx.android.synthetic.main.activity_carrier_trip_details.*
import kotlinx.android.synthetic.main.bottom_sheet_carrier_trip_details.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.transfer_details_header.*
import kotlinx.android.synthetic.main.view_about_item.*
import kotlinx.android.synthetic.main.view_communication_buttons.view.*
import kotlinx.android.synthetic.main.view_seats_number.*
import kotlinx.android.synthetic.main.view_transfer_details_about_request.*
import kotlinx.android.synthetic.main.view_transfer_details_comment.view.*
import kotlinx.android.synthetic.main.view_transfer_main_info.*
import kotlinx.android.synthetic.main.view_communication_button.*
import kotlinx.android.synthetic.main.view_transfer_details_field.*

class CarrierTripDetailsActivity : BaseGoogleMapActivity(), CarrierTripDetailsView {
    @InjectPresenter
    internal lateinit var presenter: CarrierTripDetailsPresenter

    @ProvidePresenter
    fun createCarrierTripDetailsPresenter() = CarrierTripDetailsPresenter()

    override fun getPresenter(): CarrierTripDetailsPresenter = presenter

    private lateinit var bsCarrierTripDetails: BottomSheetTripleStatesBehavior<View>

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.tripId = intent.getLongExtra(CarrierTripDetailsView.EXTRA_TRIP_ID, 0)
        presenter.transferId = intent.getLongExtra(CarrierTripDetailsView.EXTRA_TRANSFER_ID, 0)

        setContentView(R.layout.activity_carrier_trip_details)

        passenger_name.field_title.text = getString(R.string.LNG_RIDE_CLIENT_NAME)
        topCommunicationButtons.btnSupport.btnName.text = getString(R.string.LNG_CUSTOMER_SUPPORT).replace(" ", "\n")
        bottomCommunicationButtons.btnSupport.btnName.text = getString(R.string.LNG_CUSTOMER_SUPPORT).replace(" ", "\n")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorWhite)
        }

        _mapView = mapView
        _btnCenter = btnCenterRoute
        initMapView(savedInstanceState)
        setToolbar(toolbar as Toolbar, TOOLBAR_NO_TITLE)
        (toolbar as Toolbar).toolbar_title.text = getString(R.string.LNG_TRIP_DETAILS).plus(" #${presenter.transferId}")
        setViewColor((toolbar as Toolbar), R.color.colorWhite)

        initBottomSheetDetails()
        setOnClickListeners()
    }

    private fun initBottomSheetDetails() {
        bsCarrierTripDetails = BottomSheetTripleStatesBehavior.from(sheetCarrierTripDetails)

        val lp = sheetCarrierTripDetails.layoutParams as CoordinatorLayout.LayoutParams
        lp.height = getHeightForBottomSheetDetails()
        sheetCarrierTripDetails.layoutParams = lp

        bsCarrierTripDetails.state = BottomSheetTripleStatesBehavior.STATE_COLLAPSED
    }

    private fun setOnClickListeners() {
        btnBack.setOnClickListener { presenter.onBackCommandClick() }
        btnCenterRoute.setOnClickListener { presenter.onCenterRouteClick() }
    }

    override fun setTripInfo(trip: CarrierTripModel) {
        initInfoView(trip)
        initAboutTripInfo(trip)
        topCommunicationButtons.btnSupport.setOnClickListener { presenter.sendEmail(null, trip.base.transferId) }
        bottomCommunicationButtons.btnSupport.setOnClickListener { presenter.sendEmail(null, trip.base.transferId) }
    }

    private fun initInfoView(item: CarrierTripModel){
        val transferDateTimePair = Utils.getDateTimeTransferDetails(systemInteractor.locale, item.base.dateLocal, true)
        transfer_details_header.apply {
            tvTransferDate.text = transferDateTimePair.first
            tvTransferTime.text = transferDateTimePair.second
        }
        val transferStatus = when (item.base.tripStatus) {
            CarrierTripBaseModel.FUTURE_TRIP -> getString(R.string.LNG_WILL_START_IN).plus(" ")
                    .plus(Utils.durationToString(this, Utils.convertDuration(item.base.timeToTransfer)))
            CarrierTripBaseModel.IN_PROGRESS_TRIP -> getString(R.string.LNG_IN_PROGRESS)
            CarrierTripBaseModel.PAST_TRIP -> getString(R.string.LNG_RIDE_STATUS_COMPLETED)
            else -> ""
        }
        booking_info.text = getString(R.string.LNG_TRANSFER).plus(" #${item.base.transferId}").plus(" ${transferStatus?: ""}")

        layoutCarrierTripInfo.setInfo(item.base)

        if (item.base.to != null) {
            transfer_details_main.tv_distance.text = SystemUtils.formatDistance(this, item.base.distance, false)
            transfer_details_main.tv_time.text = Utils.durationToString(this, Utils.convertDuration(item.base.time ?: 0))
            transfer_details_main.tv_distance_dash.isVisible = false
        } else {
            transfer_details_main.tv_time.text = HourlyValuesHelper.getValue(item.base.duration ?: 0, this)
        }
        transfer_details_main.tv_price_title.text = getString(R.string.LNG_TOTAL_PRICE).plus(" ${item.base.price}")
        item.totalPrice?.let {
            transfer_details_main.tv_price.text = it.remainsToPay
            transfer_details_main.tv_price_dash.isVisible = false
        }
    }

    private fun initAboutTripInfo(item: CarrierTripModel){
        val operations = listOf<Pair<CharSequence, String>>(
                Pair(getString(R.string.LNG_COPY), CarrierTripDetailsPresenter.OPERATION_COPY),
                Pair(getString(R.string.LNG_OPEN), CarrierTripDetailsPresenter.OPERATION_OPEN))
        val operationsName: List<CharSequence> = operations.map { it.first }
        with(item) {
            layoutPassengersChilds.isVisible = item.countPassengers != null || item.base.countChild > 0
            with(transfer_details_view_seats) {
                item.countPassengers?.let {
                    tv_countPassengers.text = getString(R.string.X_SIGN).plus("$it")
                    imgPassengers.isVisible = true
                    tv_countPassengers.isVisible = true
                }
                if(item.base.countChild > 0) {
                    tvCountChildren.text = getString(R.string.X_SIGN).plus("${item.base.countChild}")
                    imgChildSeats.isVisible = true
                    tvCountChildren.isVisible = true
                }
            }
            base.vehicle.let { initField(car_model_field, it.registrationNumber, it.name) }

            if(showPassengerInfo) {
                base.comment?.let {
                    comment_view.tv_comment_text.text = it
                    comment_view.isVisible = true
                }
                flightNumber?.let { initField(flight_number, it) }
                passenger?.profile?.let { profile ->
                    profile.name?.let { initField(passenger_name, it) }
                    profile.email?.let {email ->
                        initField(passenger_email, email)
                        Utils.setSelectOperationListener(this@CarrierTripDetailsActivity, passenger_email, operationsName, R.string.LNG_DRIVER_EMAIL) {
                            presenter.makeFieldOperation(CarrierTripDetailsPresenter.FIELD_EMAIL, operations[it].second, email) }
                    }
                    profile.phone?.let { phone ->
                        initField(passenger_phone, phone)
                        initCommunicationButton(topCommunicationButtons.btnCall) { presenter.callPhone(phone) }
                        initCommunicationButton(bottomCommunicationButtons.btnCall) { presenter.callPhone(phone) }
                        Utils.setSelectOperationListener(this@CarrierTripDetailsActivity, passenger_phone, operationsName, R.string.LNG_DRIVER_PHONE) {
                            presenter.makeFieldOperation(CarrierTripDetailsPresenter.FIELD_PHONE, operations[it].second, phone) }
                    }
                }
                initCommunicationButton(topCommunicationButtons.btnChat) { presenter.onChatClick() }
                initCommunicationButton(bottomCommunicationButtons.btnChat) { presenter.onChatClick() }
            }
        }
    }

    private fun initCommunicationButton(btn: CommunicationButton, listener: () -> Unit) {
        btn.setOnClickListener { listener() }
        btn.isVisible = true
    }

    private fun initField(field: TransferDetailsField, text: String, title: String? = null){
        title?.let { field.field_title.text = title }
        field.field_text.text = text
        field.isVisible = true
    }

    override fun setRoute(polyline: PolylineModel, routeModel: RouteModel, isDateChanged: Boolean) {
        setPolyline(polyline, routeModel, true)
        btnCenterRoute.isVisible = false
    }

    override fun setPinHourlyTransfer(placeName: String, info: String, point: LatLng, cameraUpdate: CameraUpdate) {
        processGoogleMap(false) { setPinForHourlyTransfer(placeName, info, point, cameraUpdate, true) }
        btnCenterRoute.isVisible = false
    }

    override fun centerRoute(cameraUpdate: CameraUpdate) = showTrack(cameraUpdate)

    override fun copyText(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Copied Text", text)
        clipboard.primaryClip = clip
    }
}
