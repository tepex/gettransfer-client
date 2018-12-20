package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.graphics.Color

import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.constraint.ConstraintLayout
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.ContextCompat

import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.bumptech.glide.Glide

import com.google.android.gms.maps.CameraUpdate

import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.extensions.*
import com.kg.gettransfer.presentation.model.*

import com.kg.gettransfer.presentation.presenter.TransferDetailsPresenter
import com.kg.gettransfer.presentation.view.TransferDetailsView

import kotlinx.android.synthetic.main.activity_transfer_details.*
import kotlinx.android.synthetic.main.bottom_sheet_transfer_details.*

import kotlinx.android.synthetic.main.view_transfer_details_about_driver.*
import kotlinx.android.synthetic.main.view_transfer_details_about_request.*
import kotlinx.android.synthetic.main.view_transfer_details_about_transport.*
import kotlinx.android.synthetic.main.view_transfer_details_communicate_buttons.*
import kotlinx.android.synthetic.main.view_transfer_details_field.*
import kotlinx.android.synthetic.main.view_transfer_details_info.*
import kotlinx.android.synthetic.main.view_transfer_details_transport_type_item.*
import kotlinx.android.synthetic.main.view_transfer_details_transport_type_item.view.* //Don't delete
import android.view.MotionEvent
import android.widget.ImageView

class TransferDetailsActivity : BaseGoogleMapActivity(), TransferDetailsView {

    @InjectPresenter
    internal lateinit var presenter: TransferDetailsPresenter

    private lateinit var bsTransferDetails: BottomSheetBehavior<View>

    @ProvidePresenter
    fun createTransferDetailsPresenter() = TransferDetailsPresenter()

    override fun getPresenter(): TransferDetailsPresenter = presenter

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.transferId = intent.getLongExtra(TransferDetailsView.EXTRA_TRANSFER_ID, 0)

        setContentView(R.layout.activity_transfer_details)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = Color.WHITE
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            viewGradient.isVisible = false
        }

        _mapView = mapView
        initMapView(savedInstanceState)

        _tintBackground = tintBackground
        bsTransferDetails = BottomSheetBehavior.from(sheetTransferDetails)
        bsTransferDetails.setBottomSheetCallback(bottomSheetCallback)

        initTextFields()
        setClickListeners()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (bsTransferDetails.state == BottomSheetBehavior.STATE_EXPANDED) {
                if(hideBottomSheet(bsTransferDetails, sheetTransferDetails, BottomSheetBehavior.STATE_COLLAPSED, event)) return true
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun initTextFields() {
        tvTransferCancelled.text = getString(R.string.LNG_RIDE_REQUEST_FOR).plus(" ").plus(getString(R.string.LNG_RIDE_TRANSFER_CANCELLED))
        textTransferWillStartTime.text = getString(R.string.LNG_TRANSFER_START).plus(":")
        textRequestSentOrCompletedDate.text = getString(R.string.LNG_RIDE_REQUEST_WAS_SENT).plus(":")
        textYourPrice.text = getString(R.string.LNG_RIDE_PRICE_YOUR).plus(":")
        textNotPaid.text = getString(R.string.LNG_RIDE_NOT_PAID).plus(":")
        textPrice.text = getString(R.string.LNG_RIDE_PAYMENT_COST).plus(":")
        textDistance.text = getString(R.string.LNG_RIDE_DISTANCE).plus(":")
        textDuration.text = getString(R.string.LNG_RIDE_TIME).plus(":")
        textRequestSentOrCompletedDate.text = getString(R.string.LNG_RIDE_REQUEST_WAS_SENT).plus(":")
    }

    private fun setClickListeners() {
        btnBack.setOnClickListener          { presenter.onBackCommandClick() }
        btnCenterRoute.setOnClickListener   { presenter.onCenterRouteClick() }
        btnSupportTop.setOnClickListener    { presenter.sendEmail(null) }
        btnSupportBottom.setOnClickListener { presenter.sendEmail(null) }
        btnCancel.setOnClickListener        { presenter.onCancelRequestClicked() }
    }

    override fun setTransfer(transfer: TransferModel, userProfile: ProfileModel) {
        initInfoView(transfer)
        initAboutRequestView(transfer, userProfile)
        val status = transfer.statusCategory
        if(status == Transfer.STATUS_CATEGORY_ACTIVE || status == Transfer.STATUS_CATEGORY_UNFINISHED) {
            initTableLayoutTransportTypes(transfer.transportTypes)
            flexboxLayoutTransportTypes.isVisible = true
        }
        layoutButtonSupportTop.isVisible   = status == Transfer.STATUS_CATEGORY_FINISHED
        layoutCommunicateButtons.isVisible = status == Transfer.STATUS_CATEGORY_CONFIRMED

        btnsLayoutBottom.isVisible = status == Transfer.STATUS_CATEGORY_ACTIVE || status == Transfer.STATUS_CATEGORY_CONFIRMED
        btnSupportBottom.isVisible = status == Transfer.STATUS_CATEGORY_ACTIVE || status == Transfer.STATUS_CATEGORY_CONFIRMED
        btnCancel.isVisible        = status == Transfer.STATUS_CATEGORY_ACTIVE
    }

    private fun initInfoView(transfer: TransferModel) {
        //top left
        val transferDateTimePair = Utils.getDateTimeTransferDetails(systemInteractor.locale, transfer.dateTime, true)
        tvTransferDate.text = transferDateTimePair.first
        tvTransferTime.text = getString(R.string.LNG_TRANSFER_AT).plus(" ").plus(transferDateTimePair.second)

        //top right
        if (transfer.to != null) {
            tvDistance.text = SystemUtils.formatDistance(this, transfer.distance, false)
            tvDuration.text = Utils.durationToString(this, Utils.convertDuration(transfer.time ?: 0))
        } else {
            textDistance.text = getString(R.string.LNG_TIME_RIDE)
            tvDuration.text = Utils.formatDuration(this, transfer.duration!!)

            tvTransferWillStartTime.text = Utils.durationToString(this, Utils.convertDuration(transfer.timeToTransfer))
        }

        //bottom left
        if (transfer.statusCategory == Transfer.STATUS_CATEGORY_ACTIVE || transfer.statusCategory == Transfer.STATUS_CATEGORY_UNFINISHED) {
            if (transfer.price != null) {
                layoutYourPrice.isVisible = true
                tvYourPrice.text = transfer.price
            }
            else {
                verticalDivider2.isVisible = false
                val lp = bottomRightLayouts.layoutParams
                lp.width = ConstraintLayout.LayoutParams.MATCH_PARENT
                bottomRightLayouts.layoutParams = lp
            }
        } else {
            layoutPrices.isVisible = true
            tvPrice.text = transfer.price
            if (transfer.remainsToPay != null) tvNotPaid.text = transfer.remainsToPay
            else {
                textNotPaid.isVisible = false
                tvNotPaid.isVisible = false
            }
        }

        //bottom right
        when (transfer.statusCategory) {
            Transfer.STATUS_CATEGORY_UNFINISHED -> {
                tvTransferCancelled.isVisible = true
            }
            Transfer.STATUS_CATEGORY_ACTIVE -> {
                layoutRequestSentOrCompletedDate.isVisible = true
                textRequestSentOrCompletedDate.text = getString(R.string.LNG_RIDE_REQUEST_WAS_SENT)
                val transferCreateDateTimePair = Utils.getDateTimeTransferDetails(systemInteractor.locale, transfer.createdAt, false)
                tvRequestSentOrCompletedDate.text = transferCreateDateTimePair.first
                        .plus(" ${getString(R.string.LNG_TRANSFER_AT)} ")
                        .plus(transferCreateDateTimePair.second)
            }
            Transfer.STATUS_CATEGORY_FINISHED -> {
                layoutRequestSentOrCompletedDate.isVisible = true
                textRequestSentOrCompletedDate.text = getString(R.string.LNG_RIDE_REQUEST_WAS_SENT)
                val transferCreateDateTimePair = Utils.getDateTimeTransferDetails(systemInteractor.locale, transfer.createdAt, false)
                tvRequestSentOrCompletedDate.text = transferCreateDateTimePair.first
                        .plus(" ${getString(R.string.LNG_TRANSFER_AT)} ")
                        .plus(transferCreateDateTimePair.second)
            }
            Transfer.STATUS_CATEGORY_CONFIRMED -> {
                layoutTransferWillStartTime.isVisible = true
                tvTransferWillStartTime.text = Utils.durationToString(this, Utils.convertDuration(transfer.timeToTransfer))
            }
        }
    }

    private fun initAboutRequestView(transfer: TransferModel, userProfile: ProfileModel) {
        booking_number.field_text.text = transfer.id.toString()
        with (userProfile) {
            name?.let {
                passenger_name.field_text.text = it
                passenger_name.isVisible = true
            }
            email?.let {
                passenger_email.field_text.text = it
                passenger_email.isVisible = true
            }
            phone?.let {
                passenger_phone.field_text.text = it
                passenger_phone.isVisible = true
            }
        }
        transfer.flightNumber?.let {
            flight_number.field_text.text = it
            flight_number.isVisible = true
        }
        transfer.promoCode?.let {
            promo_code.field_text.text = it
            promo_code.isVisible = true
        }
        transfer.comment?.let {
            comment.field_text.text = it
            comment.isVisible = true
        }
    }

    private fun initTableLayoutTransportTypes(transportTypes: List<TransportTypeModel>) {
        flexboxLayoutTransportTypes.removeAllViews()
        transportTypes.forEach {
            flexboxLayoutTransportTypes.addView(LayoutInflater.from(this).inflate(R.layout.view_transfer_details_transport_type_item, null, false).apply {

                transportTypeItemName.text            = getString(it.nameId!!).plus(":")
                transportTypeItemCountPassengers.text = Utils.formatPersons(this@TransferDetailsActivity, it.paxMax)
                transportTypeItemCountBaggage.text    = Utils.formatLuggage(this@TransferDetailsActivity, it.luggageMax)
            })
        }
    }

    override fun setOffer(offer: OfferModel, childSeats: Int) {
        initAboutDriverView(offer)
        initAboutTransportView(offer, childSeats)

        layoutAboutRequestTitle.isVisible = true
        layoutAboutTransport.isVisible = true
    }

    private fun initAboutDriverView(offer: OfferModel) {
        offer.phoneToCall?.let { phone ->
            layoutCommunicateButtons.isVisible = true
            btnCall.setOnClickListener { presenter.callPhone(phone) }
        }

        val operations = listOf<Pair<CharSequence, String>>(
                Pair(getString(R.string.LNG_COPY), TransferDetailsPresenter.OPERATION_COPY),
                Pair(getString(R.string.LNG_OPEN), TransferDetailsPresenter.OPERATION_OPEN))
        val operationsName: List<CharSequence> = operations.map { it.first }

        offer.driver?.let { driver ->
            driver.phone?.let { phone ->
                driver_phone.field_text.text = phone
                driver_phone.isVisible = true
                Utils.setSelectOperationListener(this, driver_phone, operationsName, R.string.LNG_DRIVER_PHONE) {
                    presenter.makeFieldOperation(TransferDetailsPresenter.FIELD_PHONE, operations[it].second, phone) }
            }
            driver.email?.let { email ->
                driver_email.field_text.text = email
                driver_email.isVisible = true
                Utils.setSelectOperationListener(this, driver_email, operationsName, R.string.LNG_DRIVER_EMAIL) {
                    presenter.makeFieldOperation(TransferDetailsPresenter.FIELD_EMAIL, operations[it].second, email) }
            }
        }

        offer.carrier.let { carrier ->
            carrier_id.field_title.text = getString(R.string.LNG_DRIVER).plus(" №${carrier.id}")
            carrier_id.field_text.text = carrier.completedTransfers.toString().plus(" ").plus(getString(R.string.LNG_RIDES))

            carrier.profile?.name?.let { name -> carrier_name.text = name }
            carrier.profile?.phone?.let { phone ->
                carrier_phone.field_text.text = phone
                carrier_phone.isVisible = true
                Utils.setSelectOperationListener(this, carrier_phone, operationsName, R.string.LNG_DRIVER_PHONE) {
                    presenter.makeFieldOperation(TransferDetailsPresenter.FIELD_PHONE, operations[it].second, phone) }
            }
            carrier.profile?.email?.let { email ->
                carrier_email.field_text.text = email
                carrier_email.isVisible = true
                Utils.setSelectOperationListener(this, carrier_email, operationsName, R.string.LNG_DRIVER_EMAIL) {
                    presenter.makeFieldOperation(TransferDetailsPresenter.FIELD_EMAIL, operations[it].second, email) }
            }

            Utils.initCarrierLanguages(layoutCarrierLanguages, offer.carrier.languages)
            layoutAboutDriver.isVisible = true
        }
    }

    private fun initAboutTransportView(offerModel: OfferModel, childSeats: Int) {
        carName.text = offerModel.vehicle.name.plus(", ${offerModel.vehicle.year}")
        carType.text = getString(offerModel.vehicle.transportType.nameId!!).plus(":")
        carLicensePlate.text = offerModel.vehicle.registrationNumber
        offerModel.carrier.ratings.average?.let { ratingBar.rating = it }
        if (offerModel.carrier.approved) ivLike.isVisible = true
        tvCountPassengers.text = Utils.formatPersons(this, offerModel.vehicle.transportType.paxMax)
        tvCountBaggage.text = Utils.formatLuggage(this, offerModel.vehicle.transportType.luggageMax)

        if (childSeats > 0) {
            child_seats_field.field_text.text = childSeats.toString()
            child_seats_field.isVisible = true
        }

        imgFreeWater.isVisible = offerModel.refreshments
        imgFreeWiFi.isVisible = offerModel.wifi
        ivManyPhotos.isVisible = offerModel.vehicle.photos.size > 1

        offerModel.vehicle.color?.let { carColor.setImageDrawable(Utils.getVehicleColorFormRes(this, it)) }
        carColor.isVisible = offerModel.vehicle.color != null

        if (offerModel.vehicle.photos.isNotEmpty()) {
            Glide.with(this).load(offerModel.vehicle.photos.first()).into(carPhoto)
            carPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
        }
        else carPhoto.setImageDrawable(ContextCompat.getDrawable(this, offerModel.vehicle.transportType.imageId!!))
    }

    override fun setRoute(polyline: PolylineModel, routeModel: RouteModel, isDateChanged: Boolean) =
        setPolyline(polyline, routeModel)

    override fun setPinHourlyTransfer(placeName: String, info: String, point: LatLng, cameraUpdate: CameraUpdate) =
        processGoogleMap(false) { setPinForHourlyTransfer(placeName, info, point, cameraUpdate) }

    override fun copyText(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Copied Text", text)
        clipboard.primaryClip = clip
    }

    override fun showAlertCancelRequest() {
        Utils.showAlertCancelRequest(this) { presenter.cancelRequest(it) }
    }

    override fun recreateActivity() { recreate() }

    override fun centerRoute(cameraUpdate: CameraUpdate) = showTrack(cameraUpdate)

    companion object {
        const val TRANSPORT_TYPES_COLUMNS = 2
    }
}
