package com.kg.gettransfer.presentation.ui

import android.content.Context

import android.os.Build
import android.os.Bundle
import android.os.Handler

import android.support.annotation.CallSuper
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager

import android.widget.ImageView

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.bumptech.glide.Glide

import com.google.android.gms.maps.CameraUpdate

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.extensions.*

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.ProfileModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.model.TransportTypeModel

import com.kg.gettransfer.presentation.presenter.TransferDetailsPresenter
import com.kg.gettransfer.presentation.ui.behavior.BottomSheetTripleStatesBehavior
import com.kg.gettransfer.presentation.ui.custom.TransferDetailsField
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper
import com.kg.gettransfer.presentation.view.TransferDetailsView

import kotlinx.android.synthetic.main.activity_transfer_details.*
import kotlinx.android.synthetic.main.bottom_sheet_transfer_details.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.transfer_details_header.*
import kotlinx.android.synthetic.main.transfer_details_header.view.*
import kotlinx.android.synthetic.main.view_about_item.*
import kotlinx.android.synthetic.main.view_communication_button.*
import kotlinx.android.synthetic.main.view_communication_buttons.view.*

import kotlinx.android.synthetic.main.view_transfer_details_about_driver.*
import kotlinx.android.synthetic.main.view_transfer_details_about_request.*
import kotlinx.android.synthetic.main.view_transfer_details_about_transport.*
import kotlinx.android.synthetic.main.view_transfer_details_transport_type_item.view.* //don't delete


import kotlinx.android.synthetic.main.view_rate_dialog.view.*
import kotlinx.android.synthetic.main.view_rate_field.*
import kotlinx.android.synthetic.main.view_rate_in_store.view.*
import kotlinx.android.synthetic.main.view_rate_your_transfer.*
import kotlinx.android.synthetic.main.view_seats_number.view.*
import kotlinx.android.synthetic.main.view_transfer_details_about_driver.view.*
import kotlinx.android.synthetic.main.view_transfer_details_about_transport_new.view.*
import kotlinx.android.synthetic.main.view_transfer_details_comment.view.*
import kotlinx.android.synthetic.main.view_transfer_details_driver_languages.view.*
import kotlinx.android.synthetic.main.view_transfer_details_transport_type_item_new.view.*
import kotlinx.android.synthetic.main.view_transfer_main_info.view.*
import kotlinx.android.synthetic.main.view_transport_conveniences.view.*
import java.util.Calendar

class TransferDetailsActivity : BaseGoogleMapActivity(), TransferDetailsView {

    @InjectPresenter
    internal lateinit var presenter: TransferDetailsPresenter

    private lateinit var bsTransferDetails: BottomSheetTripleStatesBehavior<View>

    @ProvidePresenter
    fun createTransferDetailsPresenter() = TransferDetailsPresenter()

    override fun getPresenter(): TransferDetailsPresenter = presenter
    private var mCarMarker: Marker? = null

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.transferId = intent.getLongExtra(TransferDetailsView.EXTRA_TRANSFER_ID, 0)

        setContentView(R.layout.activity_transfer_details)

        topCommunicationButtons.btnSupport.btnName.text = getString(R.string.LNG_CUSTOMER_SUPPORT).replace(" ", "\n")
        bottomCommunicationButtons.btnSupport.btnName.text = getString(R.string.LNG_CUSTOMER_SUPPORT).replace(" ", "\n")
        topCommunicationButtons.btnRepeatTransfer.btnName.text = getString(R.string.LNG_DETAILS_REPEAT_ROUTE).replace(" ", "\n")
        bottomCommunicationButtons.btnRepeatTransfer.btnName.text = getString(R.string.LNG_DETAILS_REPEAT_ROUTE).replace(" ", "\n")
        topCommunicationButtons.btnCancel.btnName.text = getString(R.string.LNG_CANCEL_REQUEST).replace(" ", "\n")
        bottomCommunicationButtons.btnCancel.btnName.text = getString(R.string.LNG_CANCEL_REQUEST).replace(" ", "\n")

        topCommunicationButtons.btnSupport.btnImg.backgroundTintList = ContextCompat.getColorStateList(this, R.color.color_gtr_yellow)
        bottomCommunicationButtons.btnSupport.btnImg.backgroundTintList = ContextCompat.getColorStateList(this, R.color.color_gtr_yellow)

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = Color.WHITE
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            viewGradient.isVisible = false
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }

        _mapView = mapView
        initMapView(savedInstanceState)
        setToolbar(toolbar as Toolbar, TOOLBAR_NO_TITLE)
        (toolbar as Toolbar).toolbar_title.text = getString(R.string.LNG_TRIP_DETAILS).plus(" #${presenter.transferId}")

        bsTransferDetails = BottomSheetTripleStatesBehavior.from(sheetTransferDetails)
        bsTransferDetails.state = BottomSheetTripleStatesBehavior.STATE_COLLAPSED

        //initTextFields()
        setClickListeners()
    }

    override fun onStop() {
        super.onStop()
        clearMarker()
    }

    /*override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (bsTransferDetails.state == BottomSheetBehavior.STATE_EXPANDED) {
                if(hideBottomSheet(bsTransferDetails, sheetTransferDetails, BottomSheetBehavior.STATE_COLLAPSED, event)) return true
            }
        }
        return super.dispatchTouchEvent(event)
    }*/

    /*private fun initTextFields() {
        tvTransferCancelledOrInProgress.text = getString(R.string.LNG_RIDE_REQUEST_FOR).plus(" ").plus(getString(R.string.LNG_RIDE_TRANSFER_CANCELLED))
        textTransferWillStartTime.text = getString(R.string.LNG_TRANSFER_START).plus(":")
        textRequestSentOrCompletedDate.text = getString(R.string.LNG_RIDE_REQUEST_WAS_SENT).plus(":")
        textYourPrice.text = getString(R.string.LNG_RIDE_PRICE_YOUR).plus(":")
        textNotPaid.text = getString(R.string.LNG_RIDE_NOT_PAID).plus(":")
        textPrice.text = getString(R.string.LNG_RIDE_PAYMENT_COST).plus(":")
        textDistance.text = getString(R.string.LNG_RIDE_DISTANCE).plus(":")
        textDuration.text = getString(R.string.LNG_RIDE_TIME).plus(":")
        textRequestSentOrCompletedDate.text = getString(R.string.LNG_RIDE_REQUEST_WAS_SENT).plus(":")
    }*/

    private fun setClickListeners() {
        btnBack.setOnClickListener          { presenter.onBackCommandClick() }
        btnCenterRoute.setOnClickListener   { presenter.onCenterRouteClick() }
//        btnSupportBottom.setOnClickListener { presenter.sendEmail(null, presenter.transferId) }
//        btnCancel.setOnClickListener        { presenter.onCancelRequestClicked() }
        tripRate.setOnRatingChangeListener  { _, fl -> disableRate(); presenter.rateTrip(fl) }
        topCommunicationButtons.btnCancel.setOnClickListener { presenter.onCancelRequestClicked() }
        bottomCommunicationButtons.btnCancel.setOnClickListener { presenter.onCancelRequestClicked() }
        topCommunicationButtons.btnRepeatTransfer.setOnClickListener { presenter.onRepeatTransferClicked() }
        bottomCommunicationButtons.btnRepeatTransfer.setOnClickListener { presenter.onRepeatTransferClicked() }
    }

    override fun setTransfer(transfer: TransferModel, userProfile: ProfileModel, showRate: Boolean) {
        initInfoView(transfer)
        initAboutRequestView(transfer, userProfile)
        topCommunicationButtons.btnSupport.setOnClickListener { presenter.sendEmail(null, transfer.id) }
        bottomCommunicationButtons.btnSupport.setOnClickListener { presenter.sendEmail(null, transfer.id) }

        booking_info.text = when (transfer.status) {
            Transfer.Status.NEW -> {
                if (transfer.offersCount > 0) getString(R.string.LNG_BOOK_OFFER)
                else getString(R.string.LNG_WAIT_FOR_OFFERS)
            }
            Transfer.Status.PERFORMED -> {
                if (transfer.dateTime.after(Calendar.getInstance().time)) getString(R.string.LNG_TRANSFER)
                        .plus(" #${transfer.id} ")
                        .plus(getString(R.string.LNG_WILL_START_IN))
                        .plus(" ")
                        .plus(Utils.durationToString(this, Utils.convertDuration(transfer.timeToTransfer)))
                else getString(R.string.LNG_TRANSFER_IN_PROGRESS)
            }
            else -> transfer.statusName?.let { getString(R.string.LNG_TRANSFER_WAS)
                    .plus(" ")
                    .plus(getString(transfer.statusName).toLowerCase()) }
        }

        if (transfer.status == Transfer.Status.REJECTED)
            transfer_details_header.booking_info.setTextColor(ContextCompat.getColor(this@TransferDetailsActivity, R.color.color_transfer_details_text_red))

        val status = transfer.statusCategory
        if (status == Transfer.STATUS_CATEGORY_ACTIVE || status == Transfer.STATUS_CATEGORY_UNFINISHED) {
            initTableLayoutTransportTypes(transfer.transportTypes)
            layoutTransportTypes.isVisible = true
        }
        (status == Transfer.STATUS_CATEGORY_FINISHED && showRate).let {
            view_rate_ride.isVisible = it
            if (it) presenter.logTransferReviewRequested()
        }

        if (status == Transfer.STATUS_CATEGORY_ACTIVE) {
            topCommunicationButtons.btnCancel.isVisible = true
            bottomCommunicationButtons.btnCancel.isVisible = true
        }
        if (status == Transfer.STATUS_CATEGORY_FINISHED || status == Transfer.STATUS_CATEGORY_UNFINISHED) {
            topCommunicationButtons.btnRepeatTransfer.isVisible = true
            bottomCommunicationButtons.btnRepeatTransfer.isVisible = true
        }
    }

    private fun initInfoView(transfer: TransferModel) {
        val transferDateTimePair = Utils.getDateTimeTransferDetails(systemInteractor.locale, transfer.dateTime, true)
        transfer_details_header.apply {
            tvTransferDate.text = transferDateTimePair.first
            tvTransferTime.text = transferDateTimePair.second
        }

        if (transfer.to != null) {
            transfer_details_main.tv_distance.text = SystemUtils.formatDistance(this, transfer.distance, false)
            transfer_details_main.tv_time.text = Utils.durationToString(this, Utils.convertDuration(transfer.time ?: 0))
            transfer_details_main.tv_distance_dash.isVisible = false
        } else {
            transfer_details_main.tv_time.text = HourlyValuesHelper.getValue(transfer.duration ?: 0, this)
          //  textDistance.text = getString(R.string.LNG_TIME_RIDE)
          //  tvDuration.text = Utils.formatDuration(this, transfer.duration!!)
          //  tvTransferWillStartTime.text = Utils.durationToString(this, Utils.convertDuration(transfer.timeToTransfer))
        }
 //       if (transfer.statusCategory == Transfer.STATUS_CATEGORY_ACTIVE || transfer.statusCategory == Transfer.STATUS_CATEGORY_UNFINISHED)
        transfer_details_main.tv_price_title.text = getString(R.string.LNG_TOTAL_PRICE).plus(" ${transfer.price?:""}")
        transfer.remainsToPay?.let {
            transfer_details_main.tv_price.text = transfer.remainsToPay
            transfer_details_main.tv_price_dash.isVisible = false
        }
        setBookNow(transfer)
        //setTransferStatus(transfer)

//        when (transfer.statusCategory) {
//            Transfer.STATUS_CATEGORY_UNFINISHED -> {
//                transfer_details_header.booking_info.text = transfer.statusName?.let {
//                    getString(R.string.LNG_TRANSFER_WAS)
//                            .plus(" ")
//                            .plus(getString(transfer.statusName).toLowerCase())
//                }
//                transfer_details_header.booking_info.setTextColor(ContextCompat.getColor(this, R.color.color_transfer_details_text_red))
//               // tvTransferCancelledOrInProgress.isVisible = true
//            }
//            Transfer.STATUS_CATEGORY_ACTIVE -> {
//                layoutRequestSentOrCompletedDate.isVisible = true
//                textRequestSentOrCompletedDate.text = getString(R.string.LNG_RIDE_REQUEST_WAS_SENT)
//                val transferCreateDateTimePair = Utils.getDateTimeTransferDetails(systemInteractor.locale, transfer.createdAt, false)
//                tvRequestSentOrCompletedDate.text = transferCreateDateTimePair.first
//                        .plus(" ${getString(R.string.LNG_TRANSFER_AT)} ")
//                        .plus(transferCreateDateTimePair.second)
//            }
//            Transfer.STATUS_CATEGORY_FINISHED -> {
//                layoutRequestSentOrCompletedDate.isVisible = true
//                textRequestSentOrCompletedDate.text = getString(R.string.LNG_RIDE_REQUEST_WAS_SENT)
//                val transferCreateDateTimePair = Utils.getDateTimeTransferDetails(systemInteractor.locale, transfer.createdAt, false)
//                tvRequestSentOrCompletedDate.text = transferCreateDateTimePair.first
//                        .plus(" ${getString(R.string.LNG_TRANSFER_AT)} ")
//                        .plus(transferCreateDateTimePair.second)
//            }
//            Transfer.STATUS_CATEGORY_CONFIRMED -> {
//                if(transfer.dateTime.after(Calendar.getInstance().time)){
//                    layoutTransferWillStartTime.isVisible = true
//                    tvTransferWillStartTime.text = Utils.durationToString(this, Utils.convertDuration(transfer.timeToTransfer))
//                } else {
//                    tvTransferCancelledOrInProgress.text = getString(R.string.LNG_TRANSFER_IN_PROGRESS)
//                    tvTransferCancelledOrInProgress.setTextColor(ContextCompat.getColor(this, R.color.color_transfer_details_text_green))
//                    tvTransferCancelledOrInProgress.isVisible = true
//                }
//            }
//        }
    }

    private fun setBookNow(transfer: TransferModel){
        if (transfer.paidPercentage != 0 && transfer.bookNow != null) {
            tv_bookNow_info.isVisible = true
        }
    }

    /*private fun setTransferStatus(transfer: TransferModel) {
        with(transfer_details_header.booking_info) {
            when (transfer.status) {
                Transfer.Status.NEW -> {
                    text = "Заявка #"
                            .plus(transfer.id)
                            .plus(" выберите перевозчика")
                }
                Transfer.Status.REJECTED -> {
                    setTextColor(ContextCompat.getColor(this@TransferDetailsActivity, R.color.color_transfer_details_text_red))
                    text = "Заявка #"
                            .plus(transfer.id)
                            .plus(" отменена")
                }
                Transfer.Status.PERFORMED -> {
                    text = "Трансфер #"
                            .plus(transfer.id)
                            .plus(" начнется через ")
                            .plus(Utils.durationToString(this@TransferDetailsActivity, Utils.convertDuration(transfer.timeToTransfer)))
                }
                Transfer.Status.PENDING_CONFIRMATION -> {
                    text = "Трансфер #"
                            .plus(transfer.id)
                            .plus(" выполняется")
                }
                Transfer.Status.CANCELED -> {
                    text = "Трансфер #"
                            .plus(transfer.id)
                            .plus(" завершен")
                }
                else -> return
            }
        }
    }*/

    private fun initAboutRequestView(transfer: TransferModel, userProfile: ProfileModel) {
   //     booking_number.field_text.text = transfer.id.toString()
        with (userProfile) {
            name?.let { initField(passenger_name, it) }
            email?.let { initField(passenger_email, it) }
            phone?.let { initField(passenger_phone, it) }
        }
        with(transfer) {
            flightNumber?.let { initField(flight_number, it) }
            dateTimeReturn?.let { initField(back_trip, SystemUtils.formatDateTime(it)) }
            flightNumberReturn?.let { initField(back_flight_number, it) }
            promoCode?.let {
                promo_code.field_text.text = Utils.getSpannedStringFromHtmlString(it)
                promo_code.isVisible = true
            }
            comment?.let {
                comment_view.tv_comment_text.text = it
                comment_view.isVisible = true
            }
        }

        with(transfer_details_view_seats) {
            tv_countPassengers.text = getString(R.string.X_SIGN).plus("${transfer.countPassengers}")
            tvCountChildren.text = getString(R.string.X_SIGN).plus("${transfer.countChilds}")
            imgChildSeats.isVisible =  transfer.countChilds > 0
            tvCountChildren.isVisible = transfer.countChilds > 0
        }
    }

    private fun initField(field: TransferDetailsField, text: String, title: String? = null){
        title?.let { field.field_title.text = title }
        field.field_text.text = text
        field.isVisible = true
    }

    private fun initTableLayoutTransportTypes(transportTypes: List<TransportTypeModel>) {
        flexboxTransportTypes.removeAllViews()
        transportTypes.forEach {
            flexboxTransportTypes.addView(LayoutInflater.from(this).inflate(R.layout.view_transfer_details_transport_type_item_new, null, false).apply {
                transportType_Name.text = getString(it.nameId!!)
                transportType_Img.setImageDrawable(ContextCompat.getDrawable(this@TransferDetailsActivity, it.imageId!!))
                view_seats_and_lugg_count.transportType_сountPassengers.text = Utils.formatPersons(this@TransferDetailsActivity, it.paxMax)
                view_seats_and_lugg_count.transportType_сountBaggage.text = Utils.formatPersons(this@TransferDetailsActivity, it.luggageMax)
            })
        }
    }

    override fun setOffer(offer: OfferModel, childSeats: Int) {
        initAboutDriverView(offer)
        initAboutTransportView(offer, childSeats)
        layoutAboutTransport.isVisible = true

        initCarMarker(offer)
    }

    private fun initAboutDriverView(offer: OfferModel) {
        offer.phoneToCall?.let { phone ->
            topCommunicationButtons.btnCall.setOnClickListener { presenter.callPhone(phone) }
            bottomCommunicationButtons.btnCall.setOnClickListener { presenter.callPhone(phone) }
            topCommunicationButtons.btnCall.isVisible = true
            bottomCommunicationButtons.btnCall.isVisible = true
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
                topCommunicationButtons.btnChat.setOnClickListener { /*presenter.sendEmail(email, null)*/ presenter.onChatClick() }
                bottomCommunicationButtons.btnChat.setOnClickListener { /*presenter.sendEmail(email, null)*/ presenter.onChatClick() }
                topCommunicationButtons.btnChat.isVisible = true
                bottomCommunicationButtons.btnChat.isVisible = true
            }
        }

        offer.carrier.let { carrier ->
            carrier.profile?.name?.let { name -> carrier_name.field_text.text = name }
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

            Utils.initCarrierLanguages(layoutAboutDriver.view_driver_languages.layoutCarrierLanguages, offer.carrier.languages)
            layoutAboutDriver.isVisible = true
        }
    }

    private fun initAboutTransportView(offerModel: OfferModel, childSeats: Int) {
        layoutAboutTransport .apply {
            car_model_field.field_title.text = offerModel.vehicle.name
            car_model_field.field_text.text  = offerModel.vehicle.registrationNumber
            car_model_field.isVisible = true

            view_conveniences.apply {
                conveniences_field.field_title.text = getString(offerModel.vehicle.transportType.nameId!!)
                imgFreeWater.isVisible = offerModel.refreshments
                imgFreeWiFi.isVisible  = offerModel.wifi
                imgCharge.isVisible    = offerModel.charger

                offerModel.vehicle.color?.let{
                    carColor.isVisible = true
                    carColor.setImageDrawable(Utils.getVehicleColorFormRes(this@TransferDetailsActivity, it))
                }
            }
        }


//        if (childSeats > 0) {
//            child_seats_field.field_text.text = childSeats.toString()
//            child_seats_field.isVisible = true
//        }

  //      ivManyPhotos.isVisible = offerModel.vehicle.photos.size > 1


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

    override fun showDetailRate(tappedRate: Float) {
        val popUpView = showPopUpWindow(R.layout.view_rate_dialog, transferDetailsParent)
        popUpView.main_rate.isScrollable = false
        popUpView.tvCancelRate.setOnClickListener { presenter.onReviewCanceled() }
        popUpView.send_feedBack.setOnClickListener {
            closePopUp()
            presenter.sendReview(Utils.createListOfDetailedRates(popUpView), popUpView.et_reviewComment.text.toString())
        }
        setupDetailRatings(tappedRate, popUpView)
    }

    override fun askRateInPlayMarket() {
        view_rate_ride.isGone = true
        showPopUpWindow(R.layout.view_rate_in_store, transferDetailsParent).apply {
            tv_reject_store.setOnClickListener { closePopUp() }
            tv_agree_store.setOnClickListener { presenter.onRateInStore() }
        }

    }

    override fun thanksForRate() {
        thanks_for_rate.isVisible = true
        thanks_for_rate.apply {
            isVisible = true
            Handler().postDelayed( { isVisible = false }, THANKS_DELAY)
        }

    }

    override fun showRateInPlayMarket() = redirectToPlayMarket()

    override fun closeRateWindow() = closePopUp()

    private fun disableRate() {
        view_rate_ride.isGone = true
        tripRate.setOnRatingChangeListener(null)
    }

    private fun setupDetailRatings(rateForFill: Float, v: View){
        rateForFill.let {
            v.apply {
                main_rate.rating                 = it
                driver_rate.rate_bar.rating      = it
                punctuality_rate.rate_bar.rating = it
                vehicle_rate.rate_bar.rating     = it
            }
        }
    }

    private fun initCarMarker(offer: OfferModel) {
        processGoogleMap(false) {
            mCarMarker = addCarToMap(presenter.getMarkerIcon(offer))
            presenter.initCoordinates() }
    }

    override fun moveCarMarker(bearing: Float, latLon: LatLng, show: Boolean) {
        mCarMarker?.apply {
            position = latLon
            rotation = bearing
            isVisible = show
            isFlat = true
        }
    }

    override fun updateCamera(latLngList: List<LatLng>) {
        runOnUiThread {
            moveCameraWithDriverCoordinate(Utils.getCameraUpdate(latLngList))
        }
    }

    private fun clearMarker() {
        mCarMarker?.remove()
    }

    companion object {
        const val TRANSPORT_TYPES_COLUMNS = 2

        const val THANKS_DELAY = 3000L
    }
}
