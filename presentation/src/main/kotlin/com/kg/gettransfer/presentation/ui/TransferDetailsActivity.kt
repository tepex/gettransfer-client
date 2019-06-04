package com.kg.gettransfer.presentation.ui

import android.app.Activity
import android.content.Context
import android.content.Intent

import android.os.Build
import android.os.Bundle
import android.os.Handler

import android.support.annotation.CallSuper
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar

import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager

import android.widget.ImageView

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.bumptech.glide.Glide

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.GoogleMap

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.extensions.*
import com.kg.gettransfer.presentation.mapper.TransportTypeMapper

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.ReviewRateModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.model.TransportTypeModel

import com.kg.gettransfer.presentation.presenter.TransferDetailsPresenter
import com.kg.gettransfer.presentation.ui.behavior.BottomSheetTripleStatesBehavior
import com.kg.gettransfer.presentation.ui.behavior.MapCollapseBehavior
import com.kg.gettransfer.presentation.ui.custom.TransferDetailsField
import com.kg.gettransfer.presentation.ui.dialogs.RatingDetailDialogFragment
import com.kg.gettransfer.presentation.ui.dialogs.StoreDialogFragment
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper
import com.kg.gettransfer.presentation.view.TransferDetailsView

import kotlinx.android.synthetic.main.activity_transfer_details.*
import kotlinx.android.synthetic.main.bottom_sheet_transfer_details.*
import kotlinx.android.synthetic.main.toolbar_nav_back.*
import kotlinx.android.synthetic.main.toolbar_nav_back.view.*
import kotlinx.android.synthetic.main.transfer_details_header.*
import kotlinx.android.synthetic.main.transfer_details_header.view.*
import kotlinx.android.synthetic.main.view_communication_button.*
import kotlinx.android.synthetic.main.view_communication_buttons.view.*

import kotlinx.android.synthetic.main.view_transfer_details_about_driver.*
import kotlinx.android.synthetic.main.view_transfer_details_about_request.*
import kotlinx.android.synthetic.main.view_transfer_details_transport_type_item.view.*


import kotlinx.android.synthetic.main.view_rate_your_transfer.*
import kotlinx.android.synthetic.main.view_seats_number.view.*
import kotlinx.android.synthetic.main.view_transfer_details_about_driver.view.*
import kotlinx.android.synthetic.main.view_transfer_details_about_transport_new.*
import kotlinx.android.synthetic.main.view_transfer_details_about_transport_new.view.*
import kotlinx.android.synthetic.main.view_transfer_details_comment.view.*
import kotlinx.android.synthetic.main.view_transfer_details_driver_languages.view.*
import kotlinx.android.synthetic.main.view_transfer_details_field.view.*
import kotlinx.android.synthetic.main.view_transfer_details_transport_type_item_new.view.*
import kotlinx.android.synthetic.main.view_transfer_main_info.view.*
import kotlinx.android.synthetic.main.view_transport_conveniences.view.*
import kotlinx.android.synthetic.main.view_your_comment.view.tvComment
import kotlinx.android.synthetic.main.view_your_comment.view.tvTitile
import kotlinx.android.synthetic.main.view_your_rate_mark.view.rbYourRateMark
import org.jetbrains.anko.longToast
import java.util.*

class TransferDetailsActivity : BaseGoogleMapActivity(), TransferDetailsView,
        RatingDetailDialogFragment.OnRatingChangeListener,
        StoreDialogFragment.OnStoreListener {

    @InjectPresenter
    internal lateinit var presenter: TransferDetailsPresenter

    private lateinit var bsTransferDetails: BottomSheetTripleStatesBehavior<View>
    private lateinit var mapCollapseBehavior: MapCollapseBehavior<*>

    @ProvidePresenter
    fun createTransferDetailsPresenter() = TransferDetailsPresenter()

    override fun getPresenter(): TransferDetailsPresenter = presenter
    private var mCarMarker: Marker? = null

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.transferId = intent.getLongExtra(TransferDetailsView.EXTRA_TRANSFER_ID, 0)
        setContentView(R.layout.activity_transfer_details)
        setupStatusBar()

        mapCollapseBehavior = (mapView.layoutParams as CoordinatorLayout.LayoutParams).behavior as MapCollapseBehavior

        _mapView = mapView
        _btnCenter = btnCenterRoute
        initBottomSheetDetails()
        setClickListeners()
        initMapView(savedInstanceState)
        setupToolbar()
        initButtonTitles()
        setClickListeners()
    }

    override suspend fun customizeGoogleMaps(gm: GoogleMap) {
        super.customizeGoogleMaps(gm)
        gm.setPadding(0, 0, 0, bsTransferDetails.peekHeight)
        gm.setOnCameraMoveListener {
            if(isMapMovingByUser) {
                mapCollapseBehavior.setLatLngBounds(gm.projection.visibleRegion.latLngBounds)
            }
        }
    }

    private fun setupStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorWhite)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.ivBack.setOnClickListener { presenter.onBackCommandClick() }
        toolbar.toolbar_title.text = getString(R.string.LNG_TRIP_DETAILS)
    }

    private fun initButtonTitles() {
        topCommunicationButtons.btnSupport.btnName.text = getString(R.string.LNG_OFFERS_SUPPORT).replace(" ", "\n")
        bottomCommunicationButtons.btnSupport.btnName.text = getString(R.string.LNG_OFFERS_SUPPORT).replace(" ", "\n")
        topCommunicationButtons.btnRepeatTransfer.btnName.text = getString(R.string.LNG_DETAILS_REPEAT_ROUTE).replace(" ", "\n")
        bottomCommunicationButtons.btnRepeatTransfer.btnName.text = getString(R.string.LNG_DETAILS_REPEAT_ROUTE).replace(" ", "\n")
        topCommunicationButtons.btnCancel.btnName.text = getString(R.string.LNG_CANCEL_REQUEST).replace(" ", "\n")
        bottomCommunicationButtons.btnCancel.btnName.text = getString(R.string.LNG_CANCEL_REQUEST).replace(" ", "\n")
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setupBottomSheetHeight()
    }

    private fun setupBottomSheetHeight() {
        val lp = sheetTransferDetails.layoutParams as CoordinatorLayout.LayoutParams
        lp.height = getHeightForBottomSheetDetails()
        sheetTransferDetails.layoutParams = lp
    }

    override fun onStop() {
        super.onStop()
        clearMarker()
    }

    private fun initBottomSheetDetails() {
        bsTransferDetails = BottomSheetTripleStatesBehavior.from(sheetTransferDetails)
        bsTransferDetails.state = BottomSheetTripleStatesBehavior.STATE_COLLAPSED
    }


    private fun setClickListeners() {
        btnBack.setOnClickListener          { presenter.onBackCommandClick() }
        btnCenterRoute.setOnClickListener   { presenter.onCenterRouteClick() }
//        btnSupportBottom.setOnClickListener { presenter.sendEmail(null, presenter.transferId) }
//        btnCancel.setOnClickListener        { presenter.onCancelRequestClicked() }
        tripRate.setOnRatingChangeListener  { _, fl -> presenter.rateTrip(fl, true) }
        topCommunicationButtons.btnCancel.setOnClickListener { presenter.onCancelRequestClicked() }
        bottomCommunicationButtons.btnCancel.setOnClickListener { presenter.onCancelRequestClicked() }
        topCommunicationButtons.btnRepeatTransfer.setOnClickListener { presenter.onRepeatTransferClicked() }
        bottomCommunicationButtons.btnRepeatTransfer.setOnClickListener { presenter.onRepeatTransferClicked() }
        yourRateMark.setOnClickListener { presenter.rateTrip(yourRateMark.rbYourRateMark.rating , false) }
        yourComment.setOnClickListener { presenter.clickComment(yourComment.tvComment.text.toString()) }
    }

    override fun setTransfer(transfer: TransferModel) {
        initInfoView(transfer)
        initAboutRequestView(transfer)
        topCommunicationButtons.btnSupport.setOnClickListener { presenter.sendEmail(null, transfer.id) }
        bottomCommunicationButtons.btnSupport.setOnClickListener { presenter.sendEmail(null, transfer.id) }

        booking_info.text = when (transfer.status) {
            Transfer.Status.NEW -> {
                val suff = if (transfer.offersCount > 0) R.string.LNG_BOOK_OFFER
                else R.string.LNG_WAIT_FOR_OFFERS
                getString(R.string.LNG_TRANSFER)
                        .plus(" #${transfer.id} ")
                        .plus(getString(suff))
            }
            Transfer.Status.PERFORMED -> {
                if (transfer.dateTimeTZ.after(Date())) getString(R.string.LNG_TRANSFER)
                        .plus(" #${transfer.id} ")
                        .plus(getString(R.string.LNG_WILL_START_IN))
                        .plus(" ")
                        .plus(Utils.durationToString(this, Utils.convertDuration(transfer.timeToTransfer)))
                else getString(R.string.LNG_TRANSFER)
                        .plus(" #${transfer.id} ")
                        .plus(getString(R.string.LNG_IN_PROGRESS))
            }
            else -> transfer.statusName?.let { getString(R.string.LNG_TRANSFER_WAS)
                    .plus(" #${transfer.id} ")
                    .plus(" ")
                    .plus(getString(transfer.statusName).toLowerCase()) }
        }

        if (transfer.status == Transfer.Status.REJECTED)
            transfer_details_header.booking_info.setTextColor(ContextCompat.getColor(this@TransferDetailsActivity, R.color.color_transfer_details_text_red))

        val status = transfer.statusCategory
        if (status == Transfer.STATUS_CATEGORY_ACTIVE || status == Transfer.STATUS_CATEGORY_UNFINISHED || status == Transfer.STATUS_CATEGORY_CONFIRMED) {
            initTableLayoutTransportTypes(transfer.transportTypes)
            layoutTransportTypes.isVisible = true
        }

        if (status == Transfer.STATUS_CATEGORY_ACTIVE) {
            topCommunicationButtons.btnCancel.isVisible = !transfer.isBookNow()
            bottomCommunicationButtons.btnCancel.isVisible = !transfer.isBookNow()
        }
        if (status == Transfer.STATUS_CATEGORY_FINISHED || status == Transfer.STATUS_CATEGORY_UNFINISHED) {
            topCommunicationButtons.btnRepeatTransfer.isVisible = true
            bottomCommunicationButtons.btnRepeatTransfer.isVisible = true
        }
    }

    private fun initInfoView(transfer: TransferModel) {
        val transferDateTimePair = Utils.getDateTimeTransferDetails(sessionInteractor.locale, transfer.dateTime, true)
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
        }
        setPrices(transfer)
        setBookNow(transfer)
    }

    private fun setPrices(transfer: TransferModel) {
        when(transfer.statusCategory) {
            Transfer.STATUS_CATEGORY_ACTIVE -> { setActiveCategoryPrices(transfer) }
            Transfer.STATUS_CATEGORY_CONFIRMED -> { transfer.let { setPricesForPaidTransfer(it.remainsToPay, it.price, it.paidPercentage) } }
            Transfer.STATUS_CATEGORY_FINISHED -> { setRemainToPayInfo(transfer.price ?: "", getString(R.string.LNG_RIDE_PAYMENT_COST)) }
            Transfer.STATUS_CATEGORY_UNFINISHED -> { transfer.passengerOfferedPrice?.let { setPassengerOfferedPrice(it) } }
        }
    }

    private fun setActiveCategoryPrices(transfer: TransferModel) {
        if (transfer.isBookNow()) {
            transfer.let { setPricesForPaidTransfer(it.remainsToPay, it.price, it.paidPercentage) }
        } else {
            transfer.passengerOfferedPrice?.let { setPassengerOfferedPrice(it) }
        }
    }

    private fun setPricesForPaidTransfer(remainsToPay: String?, price: String?, paidPercentage: Int) {
        if(remainsToPay != null && remainsToPay != "0") {
            setRemainToPayInfo(remainsToPay, getString(R.string.LNG_RIDE_PAYMENT_REMAINS))
        } else {
            setRemainToPayInfo(getString(R.string.LNG_RIDE_PAYMENT_PAID))
        }
        setFullPrice(price ?: "", paidPercentage)
    }

    private fun setPassengerOfferedPrice(price: String) {
        transfer_details_main.apply {
            tv_price.text = price
            tv_price_dash.isVisible = false
        }
    }

    private fun setRemainToPayInfo(remainsToPay: String, title: String? = null) {
        transfer_details_main.apply {
            tv_price.text = remainsToPay
            tv_price_dash.isVisible = false
            if (title != null) {
                tv_price_title.text = title
            } else {
                tv_price_title.isVisible = false
            }
        }
        setFullPrice(remainsToPay, 100)
    }

    private fun setFullPrice(price: String, paymentPercentages: Int) {
        full_price.apply {
            isVisible = true
            field_text.text = price
            field_more_text.isVisible = true
            field_more_text.text = getString(R.string.LNG_RIDE_PAYMENT_PAID_DETAILS, paymentPercentages.toString())
        }
    }

    private fun setBookNow(transfer: TransferModel){
        tv_bookNow_info.isVisible = transfer.isBookNow()
        if (layoutAboutDriver.isShown) tv_bookNow_info.isVisible = false
    }

    private fun initAboutRequestView(transfer: TransferModel) {
        transfer.nameSign?.let { initField(passenger_name, it) }
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
            imgPassengers.isVisible = true
            tv_countPassengers.isVisible = true
            transfer.getChildrenCount()
                    .isNonZero()
                    ?.let {
                        tvCountChildren.visibleText = getString(R.string.X_SIGN).plus("$it")
                        imgChildSeats.isVisible =  true
                    }
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
        layoutAboutTransport.isVisible = true
        initAboutTransportView(offer, childSeats)

        initChatButton()

        initCarMarker(offer)
    }

    private fun initChatButton() {
        topCommunicationButtons.btnChat.setOnClickListener { presenter.onChatClick() }
        bottomCommunicationButtons.btnChat.setOnClickListener { presenter.onChatClick() }
        topCommunicationButtons.btnChat.isVisible = true
        bottomCommunicationButtons.btnChat.isVisible = true
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
            }
        }

        offer.carrier.let { carrier ->
            carrier.profile?.name?.let { name ->
                carrier_name.field_text.text = name
                carrier_name.isVisible = true
            }
            carrier.profile?.phone?.let { phone ->
                carrier_phone.field_text.text = phone
                carrier_phone.isVisible = true
                Utils.setSelectOperationListener(this, carrier_phone, operationsName, R.string.LNG_CARRIER_PHONE) {
                    presenter.makeFieldOperation(TransferDetailsPresenter.FIELD_PHONE, operations[it].second, phone) }
            }
            carrier.profile?.email?.let { email ->
                carrier_email.field_text.text = email
                carrier_email.isVisible = true
                Utils.setSelectOperationListener(this, carrier_email, operationsName, R.string.LNG_CARRIER_EMAIL) {
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
                    carColor.setImageDrawable(Utils.getCarColorFormRes(this@TransferDetailsActivity, it))
                }
            }
        }
        if (offerModel.vehicle.photos.isNotEmpty()) {
            Glide.with(this).load(offerModel.vehicle.photos.first()).into(carPhoto)
            carPhoto.scaleType = ImageView.ScaleType.CENTER_CROP
        }
        else carPhoto.setImageDrawable(ContextCompat.getDrawable(this, offerModel.vehicle.transportType.imageId!!))
    }

    override fun setRoute(polyline: PolylineModel, routeModel: RouteModel, isDateChanged: Boolean) {
        setPolyline(polyline, routeModel)
        btnCenterRoute.isVisible = false
        updateMapBehaviorBounds()
    }

    override fun setPinHourlyTransfer(placeName: String, info: String, point: LatLng, cameraUpdate: CameraUpdate) {
        processGoogleMap(false) { setPinForHourlyTransfer(placeName, info, point, cameraUpdate) }
        btnCenterRoute.isVisible = false
        updateMapBehaviorBounds()
    }

    private fun updateMapBehaviorBounds() {
        mapView.getMapAsync {
            mapView.getMapAsync { gm ->
                mapCollapseBehavior.setLatLngBounds(gm.projection.visibleRegion.latLngBounds)
            }
        }
    }

    override fun copyText(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Copied Text", text)
        clipboard.primaryClip = clip
    }

    override fun showAlertCancelRequest() {
        Utils.showAlertCancelRequest(this) { presenter.cancelRequest(it) }
    }

    override fun showCancelRequestToast() {
        longToast(R.string.LNG_TRANSFER_CANCELED)
    }

    override fun centerRoute(cameraUpdate: CameraUpdate) {
        showTrack(cameraUpdate) { updateMapBehaviorBounds() }
    }

    override fun showDetailRate(vehicle: Float, driver: Float, punctuality: Float, offerId: Long, feedback: String) {
        if (supportFragmentManager.fragments.firstOrNull { it.tag == RatingDetailDialogFragment.RATE_DIALOG_TAG} == null) {
            RatingDetailDialogFragment
                .newInstance(vehicle, driver, punctuality, offerId, feedback)
                .show(supportFragmentManager, RatingDetailDialogFragment.RATE_DIALOG_TAG)
        }
    }

    override fun askRateInPlayMarket() {
        StoreDialogFragment.newInstance().show(supportFragmentManager, StoreDialogFragment.STORE_DIALOG_TAG)
    }

    override fun thanksForRate() {
        thanks_for_rate.isVisible = true
        thanks_for_rate.apply {
            isVisible = true
            Handler().postDelayed( { isVisible = false }, THANKS_DELAY)
        }
    }

    override fun closeRateWindow() = closePopUp()

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

    override fun showCommonRating(isShow: Boolean) {
        view_rate_ride.isVisible = isShow
        if (isShow) presenter.logTransferReviewRequested()
    }

    override fun showYourRateMark(isShow: Boolean, averageRate: Float) {
        yourRateMark.rbYourRateMark.setOnRatingChangeListener(null)
        yourRateMark.show(isShow)
        yourRateMark.rbYourRateMark.rating = averageRate
        yourRateMark.rbYourRateMark.setOnRatingChangeListener { _, fl -> presenter.rateTrip(fl , true) }
    }

	override fun showYourComment(isShow: Boolean, comment: String) {
		if (comment.isEmpty())
			yourComment.tvTitile.text = getString(R.string.LNG_PAYMENT_LEAVE_COMMENT)
		else
			yourComment.tvTitile.text = getString(R.string.LNG_RIDE_YOUR_COMMENT)
		yourComment.tvComment.text = comment
		yourComment.show(isShow)

	}

    override fun showCommentEditor(comment: String) {
        Intent(this, TextEditorActivity::class.java).apply {
            putExtra(TextEditorActivity.TEXT_FOR_CORRECTING, comment)
            startActivityForResult(this, TextEditorActivity.REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == TextEditorActivity.REQUEST_CODE) {
            data?.let {
                presenter.commentChanged(it.getStringExtra(TextEditorActivity.CORRECTED_TEXT).orEmpty())
            }
        }
    }

    override fun showYourDataProgress(isShow: Boolean) {
        pbYourData.show(isShow)
        yourComment.show(!isShow)
        yourRateMark.show(!isShow)
    }

    override fun onRatingChanged(list: List<ReviewRateModel>, comment: String) {
        presenter.ratingChanged(list, comment)
    }

    override fun onRatingChangeCancelled() {
        presenter.ratingChangeCancelled()
    }

    override fun onClickGoToStore() = redirectToPlayMarket()

    private fun clearMarker() {
        mCarMarker?.remove()
    }

    companion object {
        const val TRANSPORT_TYPES_COLUMNS = 2

        const val THANKS_DELAY = 3000L
    }
}
