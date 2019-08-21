package com.kg.gettransfer.presentation.ui

import android.os.Build
import android.os.Bundle
import android.os.Handler

import androidx.annotation.CallSuper
import androidx.annotation.ColorRes
import androidx.annotation.NonNull
import androidx.annotation.StringRes
import com.google.android.material.bottomsheet.BottomSheetBehavior
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.Toolbar

import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.GoogleMap

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.ReviewRate
import com.kg.gettransfer.domain.model.Transfer

import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.extensions.isNonZero

import com.kg.gettransfer.presentation.delegate.Either
import com.kg.gettransfer.presentation.delegate.OfferItemBindDelegate

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.model.LocaleModel

import com.kg.gettransfer.presentation.presenter.TransferDetailsPresenter
import com.kg.gettransfer.presentation.ui.behavior.BottomSheetTripleStatesBehavior
import com.kg.gettransfer.presentation.ui.behavior.MapCollapseBehavior
import com.kg.gettransfer.presentation.ui.custom.TransferDetailsField
import com.kg.gettransfer.presentation.ui.dialogs.RatingDetailDialogFragment
import com.kg.gettransfer.presentation.ui.dialogs.StoreDialogFragment
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper
import com.kg.gettransfer.presentation.ui.helpers.LanguageDrawer
import com.kg.gettransfer.presentation.view.TransferDetailsView

import java.util.Date

import kotlinx.android.synthetic.main.activity_transfer_details.*
import kotlinx.android.synthetic.main.bottom_sheet_transfer_details.*
import kotlinx.android.synthetic.main.layout_passengers_seats.view.*
import kotlinx.android.synthetic.main.toolbar_nav_back.*
import kotlinx.android.synthetic.main.toolbar_nav_back.view.*
import kotlinx.android.synthetic.main.transfer_details_header.*
import kotlinx.android.synthetic.main.transfer_details_header.view.*
import kotlinx.android.synthetic.main.view_communication_button.*
import kotlinx.android.synthetic.main.view_communication_buttons.view.*

import kotlinx.android.synthetic.main.view_transfer_details_about_driver.*
import kotlinx.android.synthetic.main.view_transfer_details_about_request.*

import kotlinx.android.synthetic.main.view_rate_your_transfer.*
import kotlinx.android.synthetic.main.view_transfer_details_about_driver.view.*
import kotlinx.android.synthetic.main.view_transfer_details_about_transport.*
import kotlinx.android.synthetic.main.view_transfer_details_about_transport.view.*
import kotlinx.android.synthetic.main.view_transfer_details_comment.view.*
import kotlinx.android.synthetic.main.view_transfer_details_driver_languages.view.*
import kotlinx.android.synthetic.main.view_transfer_details_field.view.*
import kotlinx.android.synthetic.main.view_transfer_details_transport_type_item_new.view.*
import kotlinx.android.synthetic.main.view_transfer_main_info.view.*
import kotlinx.android.synthetic.main.view_transport_conveniences.view.*

import kotlinx.android.synthetic.main.view_your_rate_mark.view.rbYourRateMark

import org.jetbrains.anko.longToast

import pub.devrel.easypermissions.EasyPermissions

@Suppress("TooManyFunctions")
class TransferDetailsActivity : BaseGoogleMapActivity(),
    TransferDetailsView,
    RatingDetailDialogFragment.OnRatingChangeListener,
    StoreDialogFragment.OnStoreListener,
    EasyPermissions.PermissionCallbacks,
    EasyPermissions.RationaleCallbacks {

    @InjectPresenter
    internal lateinit var presenter: TransferDetailsPresenter

    private lateinit var bsTransferDetails: BottomSheetTripleStatesBehavior<View>
    private lateinit var bsSecondarySheet: BottomSheetBehavior<View>
    private lateinit var mapCollapseBehavior: MapCollapseBehavior<*>

    @ProvidePresenter
    fun createTransferDetailsPresenter() = TransferDetailsPresenter()

    override fun getPresenter(): TransferDetailsPresenter = presenter
    private var mCarMarker: Marker? = null

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(null)
        presenter.transferId = intent.getLongExtra(TransferDetailsView.EXTRA_TRANSFER_ID, 0)
        setContentView(R.layout.activity_transfer_details)
        setupStatusBar()

        @Suppress("UnsafeCast")
        mapCollapseBehavior = (mapView.layoutParams as CoordinatorLayout.LayoutParams).behavior as MapCollapseBehavior

        baseMapView = mapView
        baseBtnCenter = btnCenterRoute
        initBottomSheets()
        setClickListeners()
        initMapView(savedInstanceState)
        setupToolbar()
        initButtonTitles()
        setClickListeners()
    }

    @CallSuper
    override suspend fun customizeGoogleMaps(gm: GoogleMap) {
        super.customizeGoogleMaps(gm)
        gm.setPadding(0, 0, 0, bsTransferDetails.peekHeight)
        gm.setOnCameraMoveListener {
            if (isMapMovingByUser) {
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
        @Suppress("UnsafeCast")
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.ivBack.setOnClickListener { presenter.onBackCommandClick() }
        toolbar.toolbar_title.text = getString(R.string.LNG_TRIP_DETAILS)
    }

    private fun initButtonTitles() {
        topCommunicationButtons.btnSupport.btnName.text = getString(R.string.LNG_OFFERS_SUPPORT).replace(" ", "\n")
        bottomCommunicationButtons.btnSupport.btnName.text = getString(R.string.LNG_OFFERS_SUPPORT).replace(" ", "\n")
        topCommunicationButtons.btnRepeatTransfer.btnName.text =
            getString(R.string.LNG_DETAILS_REPEAT_ROUTE).replace(" ", "\n")
        bottomCommunicationButtons.btnRepeatTransfer.btnName.text =
            getString(R.string.LNG_DETAILS_REPEAT_ROUTE).replace(" ", "\n")
        topCommunicationButtons.btnCancel.btnName.text = getString(R.string.LNG_CANCEL_REQUEST).replace(" ", "\n")
        bottomCommunicationButtons.btnCancel.btnName.text = getString(R.string.LNG_CANCEL_REQUEST).replace(" ", "\n")
    }

    @CallSuper
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setupBottomSheetHeight()
    }

    private fun setupBottomSheetHeight() {
        val lp = sheetTransferDetails.layoutParams
        if (lp is CoordinatorLayout.LayoutParams) {
            lp.height = getHeightForBottomSheetDetails()
            sheetTransferDetails.layoutParams = lp
        }
    }

    @CallSuper
    override fun onStop() {
        clearMarker()
        super.onStop()
    }

    private val bsCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                tintBackgroundShadow.isVisible = false
                hideKeyboard()
            }
        }

        override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {
            tintBackgroundShadow.isVisible = true
            tintBackgroundShadow.alpha = slideOffset
        }
    }

    private fun initBottomSheets() {
        bsTransferDetails = BottomSheetTripleStatesBehavior.from(sheetTransferDetails)
        bsSecondarySheet = BottomSheetBehavior.from(secondary_bottom_sheet)

        bsSecondarySheet.state = BottomSheetBehavior.STATE_HIDDEN
        bsTransferDetails.state = BottomSheetTripleStatesBehavior.STATE_COLLAPSED

        tintBackgroundShadow = tintBackground
        bsSecondarySheet.setBottomSheetCallback(bsCallback)
    }

    private fun setClickListeners() {
        btnBack.setOnClickListener          { presenter.onBackCommandClick() }
        btnCenterRoute.setOnClickListener   { presenter.onCenterRouteClick() }
        tripRate.setOnRatingBarChangeListener { _, rating, _ -> presenter.rateTrip(rating, true) }
        topCommunicationButtons.btnCancel.setOnClickListener { presenter.onCancelRequestClicked() }
        bottomCommunicationButtons.btnCancel.setOnClickListener { presenter.onCancelRequestClicked() }
        topCommunicationButtons.btnRepeatTransfer.setOnClickListener { presenter.onRepeatTransferClicked() }
        bottomCommunicationButtons.btnRepeatTransfer.setOnClickListener { presenter.onRepeatTransferClicked() }
        yourRateMark.setOnClickListener { presenter.rateTrip(yourRateMark.rbYourRateMark.rating, false) }
    }

    override fun setTransfer(transfer: TransferModel) {
        initInfoView(transfer)
        initAboutRequestView(transfer)
        topCommunicationButtons.btnSupport.setOnClickListener { presenter.sendEmail(null, transfer.id) }
        bottomCommunicationButtons.btnSupport.setOnClickListener { presenter.sendEmail(null, transfer.id) }

        setBookingInfo(transfer)

        if (transfer.status == Transfer.Status.REJECTED) {
            transfer_details_header.booking_info.setTextColor(ContextCompat.getColor(
                this@TransferDetailsActivity,
                R.color.color_transfer_details_text_red
            ))
        }

        val status = transfer.statusCategory
        if (status == Transfer.STATUS_CATEGORY_ACTIVE ||
            status == Transfer.STATUS_CATEGORY_UNFINISHED ||
            status == Transfer.STATUS_CATEGORY_CONFIRMED) {
            initTableLayoutTransportTypes(transfer.transportTypes)
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

    @Suppress("ComplexMethod")
    private fun setBookingInfo(transfer: TransferModel) {
        booking_info.text = when (transfer.status) {
            Transfer.Status.NEW -> buildString {
                append(getString(R.string.LNG_TRANSFER))
                append(" #${transfer.id} ")
                if (transfer.offersCount > 0 && !transfer.isBookNow() && transfer.pendingPaymentId == null) {
                    append(getString(R.string.LNG_BOOK_OFFER))
                } else if (transfer.pendingPaymentId != null) {
                    append(getString(R.string.LNG_WILL_START_IN))
                    append(" ")
                    append(Utils.durationToString(
                        this@TransferDetailsActivity,
                        Utils.convertDuration(transfer.timeToTransfer)
                    ))
                } else {
                    append(getString(R.string.LNG_WAIT_FOR_OFFERS))
                }
            }
            Transfer.Status.PERFORMED -> buildString {
                append(getString(R.string.LNG_TRANSFER))
                append(" #${transfer.id} ")
                if (transfer.dateTimeTZ.after(Date())) {
                    append(getString(R.string.LNG_WILL_START_IN))
                    append(" ")
                    append(Utils.durationToString(
                        this@TransferDetailsActivity,
                        Utils.convertDuration(transfer.timeToTransfer)
                    ))
                } else {
                    append(getString(R.string.LNG_IN_PROGRESS))
                }
            }
            else -> transfer.statusName?.let { statusName ->
                buildString {
                    append(getString(R.string.LNG_TRANSFER_WAS))
                    append(" #${transfer.id} ")
                    append(" ")
                    append(getString(statusName).toLowerCase())
                }
            }
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
        when (transfer.statusCategory) {
            Transfer.STATUS_CATEGORY_ACTIVE -> setActiveCategoryPrices(transfer)
            Transfer.STATUS_CATEGORY_CONFIRMED -> setPricesForPaidTransfer(
                transfer.remainsToPay,
                transfer.price,
                transfer.paidPercentage
            )
            Transfer.STATUS_CATEGORY_FINISHED -> {
                setRemainToPayInfo(transfer.price ?: "", getString(R.string.LNG_RIDE_PAYMENT_COST))
                setVoucher()
            }
            Transfer.STATUS_CATEGORY_UNFINISHED -> transfer.passengerOfferedPrice?.let { setPassengerOfferedPrice(it) }
        }
    }

    private fun setActiveCategoryPrices(transfer: TransferModel) {
        if (transfer.isBookNow()) {
            transfer.let { setPricesForPaidTransfer(it.remainsToPay, it.price, it.paidPercentage) }
        } else {
            transfer.passengerOfferedPrice?.let { setPassengerOfferedPrice(it) }
            transfer.pendingPaymentId?.let {
                transfer_details_main.apply {
                    tv_price.text = getString(R.string.LNG_RIDE_PAYMENT)
                    tv_price_dash.isVisible = false
                    tv_price_title.isVisible = false
                }
            }
        }
    }

    private fun setPricesForPaidTransfer(remainsToPay: String?, price: String?, paidPercentage: Int) {
        if (remainsToPay != null && remainsToPay != "0") {
            setRemainToPayInfo(remainsToPay, getString(R.string.LNG_RIDE_PAYMENT_REMAINS))
        } else {
            setRemainToPayInfo(getString(R.string.LNG_RIDE_PAYMENT_PAID))
            setVoucher()
        }
        setFullPrice(price ?: "", paidPercentage)
    }

    private fun setVoucher() {
        layoutVoucher.isVisible = true
        layoutVoucher.setOnClickListener { checkPermissionForWrite() }
    }

    private fun checkPermissionForWrite() {
        val perms = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            presenter.onDownloadVoucherClick()
        } else EasyPermissions.requestPermissions(
                this,
                getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER_QUESTION),
                RC_WRITE_FILE,
                *perms)
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
        @Suppress("MagicNumber")
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

    private fun setBookNow(transfer: TransferModel) {
        tv_bookNow_info.isVisible = transfer.isBookNow()
        if (layoutAboutDriver.isShown) tv_bookNow_info.isVisible = false
    }

    @Suppress("ComplexMethod")
    private fun initAboutRequestView(transfer: TransferModel) {
        transfer.nameSign?.let { initField(passenger_name, it) }
        with(transfer) {
            flightNumber?.let { initField(flight_number, it) }
            dateTimeReturn?.let { initField(back_trip, SystemUtils.formatDateTime(it)) }
            flightNumberReturn?.let { initField(back_flight_number, it) }
            promoCode?.let { promoCode ->
                promo_code.field_text.text = Utils.getSpannedStringFromHtmlString(promoCode)
                promo_code.isVisible = true
            }
            comment?.let { comment ->
                comment_view.tv_comment_text.text = comment
                comment_view.isVisible = true
            }
        }
        setPassengersAndSeats(transfer)
    }

    private fun setPassengersAndSeats(transfer: TransferModel) {
        with(passengersAndSeats) {
            passengers.field_text.text = transfer.countPassengers.toString()
            transfer.childSeatsInfant.isNonZero()?.let {
                setCountSeats(infantSeat, it)
            }
            transfer.childSeatsConvertible.isNonZero()?.let {
                setCountSeats(convertibleSeat, it)
            }
            transfer.childSeatsBooster.isNonZero()?.let {
                setCountSeats(boosterSeat, it)
            }
        }
    }

    private fun setCountSeats(view: TransferDetailsField, countSeats: Int) {
        view.apply {
            isVisible = true
            field_text.text = countSeats.toString()
        }
    }

    private fun initField(field: TransferDetailsField, text: String, title: String? = null) {
        title?.let { field.field_title.text = title }
        field.field_text.text = text
        field.isVisible = true
    }

    private fun initTableLayoutTransportTypes(transportTypes: List<TransportTypeModel>) {
        flexboxTransportTypes.isVisible = true
        flexboxTransportTypes.removeAllViews()
        transportTypes.forEach { transportType ->
            flexboxTransportTypes.addView(
                LayoutInflater
                    .from(this)
                    .inflate(R.layout.view_transfer_details_transport_type_item_new, null, false)
                    .apply {
                        transportType_Name.text = getString(transportType.nameId)
                        transportType_Img.setImageDrawable(ContextCompat.getDrawable(
                            this@TransferDetailsActivity,
                            transportType.imageId
                        ))
                        transportType_сountPassengers.text = Utils.formatPersons(
                            this@TransferDetailsActivity,
                            transportType.paxMax
                        )
                        transportType_сountBaggage.text = Utils.formatPersons(
                            this@TransferDetailsActivity,
                            transportType.luggageMax
                        )
                    }
                )
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
        layoutAboutDriver.isVisible = true
        offer.phoneToCall?.let { phone ->
            topCommunicationButtons.btnCall.setOnClickListener { presenter.callPhone(phone) }
            bottomCommunicationButtons.btnCall.setOnClickListener { presenter.callPhone(phone) }
            topCommunicationButtons.btnCall.isVisible = true
            bottomCommunicationButtons.btnCall.isVisible = true
        }

        val operations = listOf<Pair<CharSequence, String>>(
            getString(R.string.LNG_COPY) to TransferDetailsPresenter.OPERATION_COPY,
            getString(R.string.LNG_OPEN) to TransferDetailsPresenter.OPERATION_OPEN
        )
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

            OfferItemBindDelegate.bindLanguages(
                Either.Single(layoutAboutDriver.view_driver_languages.layoutCarrierLanguages),
                carrier.languages,
                layoutParamsRes = LanguageDrawer.LanguageLayoutParamsRes.TRANSFER_DETAILS
            )
        }
    }

    @Suppress("UNUSED_PARAMETER", "NestedBlockDepth")
    private fun initAboutTransportView(offerModel: OfferModel, childSeats: Int) {
        with(layoutAboutTransport) {
            car_model_field.field_title.text = offerModel.vehicle.name
            car_model_field.field_text.text  = offerModel.vehicle.registrationNumber
            car_model_field.isVisible = true

            with(view_conveniences) {
                conveniences_field.text = getString(offerModel.vehicle.transportType.nameId)
                imgFreeWater.isVisible  = offerModel.refreshments
                imgFreeWiFi.isVisible   = offerModel.wifi
                imgCharge.isVisible     = offerModel.charger

                offerModel.vehicle.color?.let { Utils.setCarColorInTextView(this@TransferDetailsActivity, carColor, it) }
            }
        }
        vehiclePhotosView.setPhotos(offerModel.vehicle.transportType.imageId, offerModel.vehicle.photos)
    }

    override fun setRoute(polyline: PolylineModel, routeModel: RouteModel, isDateChanged: Boolean) {
        setPolyline(polyline, routeModel)
        btnCenterRoute.isVisible = false
        updateMapBehaviorBounds()
    }

    override fun setMapBottomPadding() {
        @Suppress("MagicNumber")
        mapView.setPadding(0, 0, 0, 150)
    }

    override fun setPinHourlyTransfer(
        placeName: String,
        info: String,
        point: LatLng,
        cameraUpdate: CameraUpdate,
        isDateChanged: Boolean
    ) {
        processGoogleMap(false) { setPinForHourlyTransfer(placeName, info, point, cameraUpdate) }
        btnCenterRoute.isVisible = false
        updateMapBehaviorBounds()
    }

    private fun updateMapBehaviorBounds() {
        mapView.getMapAsync { mapCollapseBehavior.setLatLngBounds(it.projection.visibleRegion.latLngBounds) }
    }

    override fun copyField(text: String) {
        copyText(text)
    }

    override fun showAlertCancelRequest() {
        Utils.showAlertCancelRequest(this, presenter::cancelRequest)
    }

    override fun showCancelRequestToast() {
        longToast(R.string.LNG_TRANSFER_CANCELED)
    }

    override fun centerRoute(cameraUpdate: CameraUpdate) {
        showTrack(cameraUpdate) { updateMapBehaviorBounds() }
    }

    override fun showDetailRate() {
        supportFragmentManager.fragments.firstOrNull { fragment ->
            fragment.tag == RatingDetailDialogFragment.RATE_DIALOG_TAG
        } ?: RatingDetailDialogFragment.newInstance().show(
            supportFragmentManager,
            RatingDetailDialogFragment.RATE_DIALOG_TAG
        )
    }

    override fun askRateInPlayMarket() {
        StoreDialogFragment.newInstance().show(supportFragmentManager, StoreDialogFragment.STORE_DIALOG_TAG)
    }

    override fun thanksForRate() {
        thanks_for_rate.isVisible = true
        thanks_for_rate.apply {
            isVisible = true
            Handler().postDelayed(
                { isVisible = false },
                THANKS_DELAY
            )
        }
    }

    override fun closeRateWindow() = closePopUp()

    private fun initCarMarker(offer: OfferModel) {
        processGoogleMap(false) {
            mCarMarker = addCarToMap(presenter.getMarkerIcon(offer))
            presenter.initCoordinates()
        }
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

    override fun showYourRateMark(isShow: Boolean, averageRate: Double) {
        yourRateMark.rbYourRateMark.onRatingBarChangeListener = null
        yourRateMark.isVisible = isShow
        yourRateMark.rbYourRateMark.rating = averageRate.toFloat()
        yourRateMark.rbYourRateMark.setOnRatingBarChangeListener { _, rating, _ -> presenter.rateTrip(rating, true) }
    }

    override fun showYourDataProgress(isShow: Boolean) {
        pbYourData.isVisible = isShow
        yourRateMark.isVisible = !isShow
    }

    override fun onRatingChanged(list: List<ReviewRate>, comment: String) {
        presenter.ratingChanged(list, comment)
    }

    override fun onRatingChangeCancelled() {
        presenter.ratingChangeCancelled()
    }

    override fun onClickGoToStore() {
        presenter.redirectToPlayMarket()
    }

    override fun goToGooglePlay() {
        Utils.goToGooglePlay(this, getString(R.string.app_market_package), BaseActivity.PLAY_MARKET_RATE)
    }

    private fun clearMarker() {
        mCarMarker?.remove()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        longToast(getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER_ACCESS))
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        presenter.onDownloadVoucherClick()
    }

    override fun onRationaleDenied(requestCode: Int) {
        longToast(getString(R.string.LNG_DOWNLOAD_BOOKING_VOUCHER_ACCESS))
    }

    override fun onRationaleAccepted(requestCode: Int) {}

    @CallSuper
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    companion object {
        const val THANKS_DELAY = 3000L
        private const val RC_WRITE_FILE = 111
    }
}
