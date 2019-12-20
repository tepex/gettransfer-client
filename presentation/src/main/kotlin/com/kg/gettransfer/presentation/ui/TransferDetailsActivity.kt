package com.kg.gettransfer.presentation.ui

import android.os.Build
import android.os.Bundle
import android.os.Handler

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

import androidx.annotation.CallSuper
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat

import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetBehavior

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.ReviewRate
import com.kg.gettransfer.domain.model.Transfer

import androidx.core.view.isVisible
import com.kg.gettransfer.extensions.isNonZero

import com.kg.gettransfer.presentation.delegate.Either
import com.kg.gettransfer.presentation.delegate.OfferItemBindDelegate
import com.kg.gettransfer.presentation.listeners.CancelationReasonListener
import com.kg.gettransfer.presentation.listeners.GoToPlayMarketListener

import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.PolylineModel
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
import com.kg.gettransfer.presentation.ui.helpers.LanguageDrawer
import com.kg.gettransfer.presentation.ui.utils.FragmentUtils
import com.kg.gettransfer.presentation.view.Screens.showSupportScreen
import com.kg.gettransfer.presentation.view.TransferDetailsView

import java.util.Date

import kotlinx.android.synthetic.main.activity_transfer_details.*
import kotlinx.android.synthetic.main.activity_transfer_details.btnBack
import kotlinx.android.synthetic.main.activity_transfer_details.btnCenterRoute
import kotlinx.android.synthetic.main.activity_transfer_details.mapView
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
    EasyPermissions.RationaleCallbacks,
    CancelationReasonListener,
    GoToPlayMarketListener {

    @InjectPresenter
    internal lateinit var presenter: TransferDetailsPresenter

    private lateinit var bsTransferDetails: BottomSheetTripleStatesBehavior<View>
    private lateinit var mapCollapseBehavior: MapCollapseBehavior

    private lateinit var bsSecondarySheet: BottomSheetBehavior<View>

    private val rateAnimation by lazy { RateTripAnimationFragment() }

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

    private fun initBottomSheets() {
        bsTransferDetails = BottomSheetTripleStatesBehavior.from(sheetTransferDetails)
        hideDetailsBottomSheet()

        bsSecondarySheet = BottomSheetBehavior.from(sheetSecondary)
        hideSecondaryBottomSheet()

        tintBackgroundShadow = tintBackground
        bsSecondarySheet.addBottomSheetCallback(bsCallback)
    }

    @CallSuper
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action != MotionEvent.ACTION_DOWN) {
            return super.dispatchTouchEvent(event)
        }
        val ret = when {
            bsSecondarySheet.state == BottomSheetBehavior.STATE_EXPANDED && hideBottomSheet(
                bsSecondarySheet,
                sheetSecondary,
                BottomSheetBehavior.STATE_HIDDEN,
                event
            ) -> true
            else -> false
        }
        return if (ret) ret else super.dispatchTouchEvent(event)
    }

    private val bsCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                tintBackgroundShadow.isVisible = false
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            tintBackgroundShadow.isVisible = true
            tintBackgroundShadow.alpha = slideOffset
        }
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
        topCommunicationButtons.btnSupport.setOnClickListener {
            showSupportScreen(supportFragmentManager, transfer.id)
        }
        bottomCommunicationButtons.btnSupport.setOnClickListener {
            showSupportScreen(supportFragmentManager, transfer.id)
        }
        setBookingInfo(transfer)

        if (transfer.status == Transfer.Status.REJECTED) {
            transfer_details_header.booking_info.setTextColor(ContextCompat.getColor(
                this@TransferDetailsActivity,
                R.color.color_gtr_red
            ))
        }

        val status = transfer.statusCategory
        if (status == Transfer.STATUS_CATEGORY_ACTIVE ||
            status == Transfer.STATUS_CATEGORY_UNFINISHED ||
            status == Transfer.STATUS_CATEGORY_CONFIRMED) {
            initTableLayoutTransportTypes(transfer.transportTypes)
        }

        (status == Transfer.STATUS_CATEGORY_ACTIVE && !transfer.isBookNow()).let { showBtnCancel ->
            topCommunicationButtons.btnCancel.isVisible = showBtnCancel
            bottomCommunicationButtons.btnCancel.isVisible = showBtnCancel
        }

        (status == Transfer.STATUS_CATEGORY_FINISHED ||
            status == Transfer.STATUS_CATEGORY_UNFINISHED).let { showBtnRepeatTransfer ->
            topCommunicationButtons.btnRepeatTransfer.isVisible = showBtnRepeatTransfer
            bottomCommunicationButtons.btnRepeatTransfer.isVisible = showBtnRepeatTransfer
        }
    }

    @Suppress("ComplexMethod")
    private fun setBookingInfo(transfer: TransferModel) {
        booking_info.text = when (transfer.status) {
            Transfer.Status.NEW -> buildString {
                append(getString(R.string.LNG_TRANSFER))
                append(" #${transfer.id} ")
                val isOffersExist = transfer.offersCount > 0 || transfer.bookNowOffers.isNotEmpty()
                if (isOffersExist && transfer.pendingPaymentId == null) {
                    append(getString(R.string.LNG_BOOK_OFFER))
                } else if (transfer.pendingPaymentId != null || transfer.isBookNow()) {
                    append(getMatchedTransferStatusText(transfer.timeToTransfer))
                } else {
                    append(getString(R.string.LNG_WAIT_FOR_OFFERS))
                }
            }
            Transfer.Status.PERFORMED -> buildString {
                append(getString(R.string.LNG_TRANSFER))
                append(" #${transfer.id} ")
                if (transfer.dateTimeTZ.after(Date())) {
                    append(getMatchedTransferStatusText(transfer.timeToTransfer))
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

    private fun getMatchedTransferStatusText(timeToTransfer: Int) =
        getString(R.string.LNG_WILL_START_IN)
            .plus("")
            .plus(Utils.durationToString(
                this@TransferDetailsActivity,
                Utils.convertDuration(timeToTransfer)
            ))

    private fun initInfoView(transfer: TransferModel) {
        transfer_details_header.apply {
            tvTransferDate.text = SystemUtils.formatDate(transfer.dateTime)
            tvTransferTime.text = SystemUtils.formatTime(transfer.dateTime)
        }

        if (transfer.to != null) setDistance(transfer.distance)
        setDuration(transfer.duration, transfer.time)
        setPrices(transfer)

        val isCanDownloadVoucher =
            transfer.statusCategory == Transfer.STATUS_CATEGORY_CONFIRMED ||
            transfer.statusCategory == Transfer.STATUS_CATEGORY_FINISHED
        if (isCanDownloadVoucher) setVoucher()
    }

    private fun setDistance(distance: Int?) {
        if (distance == null || distance == TransferModel.ZERO_VALUE) {
            transfer_details_main.distance_view.showDash()
        } else {
            transfer_details_main.distance_view.setValue(
                SystemUtils.formatDistance(
                    context = this,
                    distance = distance,
                    splitDistance = false,
                    withDistanceText = false))
        }
    }

    private fun setDuration(duration: Int?, time: Int?) {
        val timeValue = duration?.let {
            HourlyValuesHelper.getValue(it, this)
        } ?: time?.let {
            if (it != TransferModel.ZERO_VALUE) Utils.durationToString(this, Utils.convertDuration(it)) else null
        }
        timeValue?.let {
            transfer_details_main.time_view.setValue(it)
        } ?: run {
            transfer_details_main.time_view.showDash()
        }
    }

    private fun setPrices(transfer: TransferModel) {
        when (transfer.statusCategory) {
            Transfer.STATUS_CATEGORY_ACTIVE -> setActiveCategoryPrices(transfer)
            Transfer.STATUS_CATEGORY_CONFIRMED -> setPricesForPaidTransfer(
                transfer.remainsToPay,
                transfer.price,
                transfer.paidPercentage
            )
            Transfer.STATUS_CATEGORY_FINISHED -> setRemainToPayInfo(
                transfer.price ?: "",
                getString(R.string.LNG_RIDE_PAYMENT_COST)
            )
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
                    price_view.setValue(getString(R.string.LNG_RIDE_PAYMENT))
                    price_view.hideTitle()
                }
            }
        }
    }

    private fun setPricesForPaidTransfer(remainsToPay: String?, price: String?, paidPercentage: Int) {
        if (remainsToPay != null && remainsToPay != "0") {
            setRemainToPayInfo(remainsToPay, getString(R.string.LNG_RIDE_PAYMENT_REMAINS))
        } else {
            setRemainToPayInfo(getString(R.string.LNG_RIDE_PAYMENT_PAID))
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
            price_view.setValue(price)
        }
    }

    private fun setRemainToPayInfo(remainsToPay: String, title: String? = null) {
        transfer_details_main.apply {
            price_view.setValue(remainsToPay)
            if (title != null) {
                price_view.setTitle(title)
            } else {
                price_view.hideTitle()
            }
        }
        @Suppress("MagicNumber")
        setFullPrice(remainsToPay, 100)
    }

    private fun setFullPrice(price: String, paymentPercentage: Int) {
        full_price.apply {
            isVisible = true
            field_text.text = price
            field_more_text.isVisible = true
            field_more_text.text = getString(R.string.LNG_RIDE_PAYMENT_PAID_DETAILS, paymentPercentage.toString())
        }
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

    override fun setBookNowOfferInfo(isBookNowOffer: Boolean) {
        tv_bookNow_info.isVisible = isBookNowOffer
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
                ivWheelchair.isVisible  = offerModel.wheelchair
                ivArmor.isVisible       = offerModel.armored

                offerModel.vehicle.color?.let { color ->
                    Utils.setCarColorInTextView(this@TransferDetailsActivity, carColor, color)
                }
            }
        }
        vehiclePhotosView.setPhotos(offerModel.vehicle.transportType.imageId, offerModel.vehicle.photos)
    }

    override fun setRoute(polyline: PolylineModel, routeModel: RouteModel, isDateOrDistanceChanged: Boolean) {
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

    override fun showCancelationReasonsList() {
        FragmentUtils.replaceFragment(
            supportFragmentManager,
            SelectCancelationReasonBottomFragment(),
            R.id.sheetSecondary
        )
    }

    override fun onCancelationReasonSelected(reason: String) {
        Utils.showAlertCancelRequest(this, reason) { if (it) presenter.cancelRequest(reason) }
    }

    override fun showAlertRestoreRequest() {
        Utils.showAlertRestoreRequest(this) { if (it) presenter.restoreRequest() }
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

    override fun showRateAnimation() {
        hideDetailsBottomSheet()
        if (!rateAnimation.isAdded) {
            supportFragmentManager.beginTransaction().apply {
                replace(android.R.id.content, rateAnimation)
                commit()
            }
        }
    }

    override fun askRateInPlayMarket() {
        // dirty hack to show after animation ended
        Handler().postDelayed({
            StoreDialogFragment.newInstance().show(supportFragmentManager, StoreDialogFragment.STORE_DIALOG_TAG)
        }, RateTripAnimationFragment.DURATION_OF_ANIMATION)
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
        hideDetailsBottomSheet()
        presenter.ratingChangeCancelled()
    }

    override fun onClickGoToStore() {
        presenter.redirectToPlayMarket()
    }

    override fun goToGooglePlay() {
        Utils.goToGooglePlay(this, getString(R.string.app_market_package), PLAY_MARKET_RATE)
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

    override fun onClickGoToDriverApp() {
        Utils.goToGooglePlay(this, getString(R.string.driver_app_market_package))
    }

    @CallSuper
    override fun onBackPressed() {
        when {
            bsSecondarySheet.state == BottomSheetBehavior.STATE_EXPANDED -> hideSecondaryBottomSheet()
            else                                                         -> presenter.onBackCommandClick()
        }
    }

    private fun hideDetailsBottomSheet() {
        scrollContent.post { scrollContent.fullScroll(View.FOCUS_UP) }
        bsTransferDetails.state = BottomSheetTripleStatesBehavior.STATE_COLLAPSED
    }

    private fun hideSecondaryBottomSheet() { bsSecondarySheet.state = BottomSheetBehavior.STATE_HIDDEN }

    companion object {
        const val THANKS_DELAY = 3000L
        private const val RC_WRITE_FILE = 111
    }
}
