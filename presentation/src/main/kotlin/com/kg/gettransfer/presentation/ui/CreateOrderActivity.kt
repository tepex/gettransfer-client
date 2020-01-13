package com.kg.gettransfer.presentation.ui

import android.graphics.Color

import android.os.Build
import android.os.Bundle
import android.os.Handler

import android.view.MotionEvent
import android.view.View
import android.view.WindowManager

import androidx.annotation.CallSuper
import androidx.annotation.StringRes

import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior

import com.kg.gettransfer.R

import androidx.core.view.isVisible

import com.kg.gettransfer.presentation.delegate.DateTimeDelegate

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.model.UserModel

import com.kg.gettransfer.presentation.presenter.CreateOrderPresenter
import com.kg.gettransfer.presentation.presenter.CurrencyChangedListener

import com.kg.gettransfer.presentation.ui.custom.BottomSheetCreateOrderNewView
import com.kg.gettransfer.presentation.ui.dialogs.CommentDialogFragment
import com.kg.gettransfer.presentation.ui.dialogs.HourlyDurationDialogFragment
import com.kg.gettransfer.presentation.ui.helpers.DateTimeScreen
import com.kg.gettransfer.presentation.ui.utils.FragmentUtils
import com.kg.gettransfer.presentation.view.CreateOrderView

import com.kg.gettransfer.utilities.Analytics.Companion.COMMENT_INPUT
import com.kg.gettransfer.utilities.Analytics.Companion.DATE_TIME_CHANGED
import com.kg.gettransfer.utilities.Analytics.Companion.OFFER_PRICE_FOCUSED

import kotlinx.android.synthetic.main.activity_create_order.*
import kotlinx.android.synthetic.main.bottom_sheet_create_order.*

import org.koin.android.ext.android.inject

@Suppress("TooManyFunctions")
class CreateOrderActivity : BaseGoogleMapActivity(),
    CreateOrderView,
    DateTimeScreen,
    BottomSheetCreateOrderNewView.OnCreateOrderListener,
    CommentDialogFragment.OnCommentListener,
    CurrencyChangedListener {

    @InjectPresenter
    internal lateinit var presenter: CreateOrderPresenter
    private val dateDelegate: DateTimeDelegate by inject()

    private lateinit var bsOrder: BottomSheetBehavior<View>
    private lateinit var bsSecondarySheet: BottomSheetBehavior<View>

    @ProvidePresenter
    fun createCreateOrderPresenter() = CreateOrderPresenter()

    override fun getPresenter(): CreateOrderPresenter = presenter

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_order)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = Color.WHITE
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            viewGradient.visibility = View.GONE
        }

        baseMapView = mapView
        baseBtnCenter = btnCenterRoute

        initMapView(savedInstanceState)
    }

    @CallSuper
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        sheetOrder.listener = this
        btnBack.setOnClickListener { presenter.onBackClick() }
        btnCenterRoute.setOnClickListener { presenter.onCenterRouteClick() }
        initKeyBoardListener()
        initBottomSheets()
    }

    @CallSuper
    override fun onPostResume() {
        super.onPostResume()
        presenter.init()
    }

    private val bsCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN && bsOrder.state == BottomSheetBehavior.STATE_HIDDEN) {
                tintBackgroundShadow.isVisible = false
            }
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                presenter.updateChildSeatsInfo()
                hideKeyboard()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (bsOrder.state == BottomSheetBehavior.STATE_COLLAPSED) {
                tintBackgroundShadow.isVisible = true
                tintBackgroundShadow.alpha = slideOffset
            }
        }
    }

    private fun initBottomSheets() {
        bsOrder = BottomSheetBehavior.from(sheetOrder)
        bsSecondarySheet = BottomSheetBehavior.from(secondary_bottom_sheet)
        bsSecondarySheet.state = BottomSheetBehavior.STATE_HIDDEN

        tintBackgroundShadow = tintBackground
        bsOrder.addBottomSheetCallback(bottomSheetCallback)
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
                secondary_bottom_sheet,
                BottomSheetBehavior.STATE_HIDDEN,
                event
            ) -> true
            bsOrder.state == BottomSheetBehavior.STATE_EXPANDED && hideBottomSheet(
                bsOrder,
                sheetOrder,
                BottomSheetBehavior.STATE_COLLAPSED,
                event
            ) -> true
            else -> false
        }
        return if (ret) ret else super.dispatchTouchEvent(event)
    }

    private fun hideSecondaryBottomSheet() { bsSecondarySheet.state = BottomSheetBehavior.STATE_HIDDEN }

    private fun toggleSheetOrder() {
        if (bsOrder.state != BottomSheetBehavior.STATE_EXPANDED) {
            bsOrder.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            bsOrder.state = BottomSheetBehavior.STATE_COLLAPSED
//            TODO scrollContent.fullScroll(View.FOCUS_UP)
        }
    }

    private fun initKeyBoardListener() {
        addKeyBoardDismissListener { closed ->
            if (!closed) {
                btnGetOffers.isVisible = !closed
            } else {
                // Suppress button flashing
                Handler().postDelayed({ btnGetOffers.isVisible = closed }, DELAY)
// TODO NOT USED                if (promo_field.field_input.isFocused) {
//                    presenter.checkPromoCode()
//                }
            }
        }
    }

    @CallSuper
    protected override fun initMap() {
        super.initMap()
        presenter.mapInitialized()
    }

    override fun checkPromoCode() {
        presenter.checkPromoCode()
    }

    override fun onCurrencyClick() {
        presenter.onChangeCurrencyClick()
    }

    override fun onPriceChanged(value: Double?) {
        presenter.setOfferedPrice(value)
    }

    override fun onPriceFocused() {
        presenter.logTransferSettingsEvent(OFFER_PRICE_FOCUSED)
    }

    override fun onNameChanged(value: String) {
        presenter.setName(value)
    }

    override fun onFlightNumberChanged(value: String) {
        presenter.setFlightNumber(value, false)
    }

    override fun onFlightNumberReturnChanged(value: String) {
        presenter.setFlightNumber(value, true)
    }

    override fun onPromoCodeChanged(value: String) {
        presenter.setPromo(value)
    }

    override fun onHourlyDurationClick() {
        presenter.showHourlyDurationDialog()
    }

    override fun onTransferDateTimeClick() {
        showDatePickerDialog(FIELD_START)
        presenter.logTransferSettingsEvent(DATE_TIME_CHANGED)
    }

    override fun onTransferReturnDateTimeClick() {
        showDatePickerDialog(FIELD_RETURN)
    }

    override fun onDeleteReturnDateTimeClick() {
        presenter.clearReturnDate()
    }

    override fun onPassengersCountInc() {
        presenter.changePassengers(1)
    }

    override fun onPassengersCountDec() {
        presenter.changePassengers(-1)
    }

    override fun onChildrenSeatClick() {
        hideKeyboard()
        FragmentUtils.replaceFragment(supportFragmentManager, ChildSeatsFragment(), R.id.secondary_bottom_sheet)
    }

    override fun onCommentClick(value: String) {
        presenter.commentClick(value)
        presenter.logTransferSettingsEvent(COMMENT_INPUT)
    }

    override fun onAgreementClick() {
        presenter.showLicenceAgreement()
    }

    override fun onAgreementChecked(value: Boolean) {
        presenter.setAgreeLicence(value)
    }

    override fun onTransportTypeClicked(type: TransportTypeModel, showInfo: Boolean) {
        if (showInfo) {
            val fragment = TransportTypeFragment()
            fragment.transportTypeModel = type
            FragmentUtils.replaceFragment(supportFragmentManager, fragment, R.id.secondary_bottom_sheet)
            presenter.onTransportTypeClicked()
        } else {
            presenter.onSelectedTransportTypesChanged()
        }
    }

    override fun onGetOffersClick() {
        presenter.onGetTransferClick()
    }

    override fun showHourlyDurationDialog(durationValue: Int?) {
        HourlyDurationDialogFragment
            .newInstance(durationValue, object : HourlyDurationDialogFragment.OnHourlyDurationListener {
                override fun onDone(durationValue: Int) {
                    presenter.updateDuration(durationValue)
                }
            })
            .show(supportFragmentManager, HourlyDurationDialogFragment.DIALOG_TAG)
    }

    override fun currencyChanged(currency: CurrencyModel) {
        presenter.currencyChanged(currency)
    }

    private fun showDatePickerDialog(field: Boolean) {
        dateDelegate.chooseOrderTime(this, field, this)
    }

    // DateTimeScreen interface method
    override fun setFieldDate(date: String, field: Boolean) {
        presenter.changeDate(field)
        sheetOrder.setTransferDateTime(date, field)
    }

    override fun setCurrency(currency: String, hideCurrencies: Boolean) {
        sheetOrder.currency = currency
        if (hideCurrencies) {
            hideSecondaryBottomSheet()
        }
    }

    override fun setTransportTypes(transportTypes: List<TransportTypeModel>) {
        sheetOrder.updateTypes(transportTypes)
    }

    override fun setUser(user: UserModel, isLoggedIn: Boolean) {
        if (isLoggedIn && user.termsAccepted) {
            sheetOrder.showAgreement = false
        } else {
            sheetOrder.isAcceptedAgreement = user.termsAccepted
        }
    }

    override fun setRoute(polyline: PolylineModel, routeModel: RouteModel, isDateOrDistanceChanged: Boolean) {
        if (isDateOrDistanceChanged) {
            clearMarkersAndPolylines()
        }
        setPolyline(polyline, routeModel)
        btnCenterRoute.isVisible = false
    }

    @Suppress("EmptyFunctionBlock")
    override fun setMapBottomPadding() {}

    override fun setPinHourlyTransfer(
        placeName: String,
        info: String,
        point: LatLng,
        cameraUpdate: CameraUpdate,
        isDateChanged: Boolean
    ) {
        if (isDateChanged) {
            clearMarkersAndPolylines()
        }
        processGoogleMap(false) { setPinForHourlyTransfer(placeName, info, point, cameraUpdate) }
        btnCenterRoute.isVisible = false
    }

    override fun centerRoute(cameraUpdate: CameraUpdate) = showTrack(cameraUpdate)

    override fun showEmptyFieldError(@StringRes stringId: Int) {
        Utils.getAlertDialogBuilder(this).apply {
            setTitle(getString(stringId))
            setPositiveButton(R.string.LNG_OK) { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

    override fun setPassengers(count: Int) {
        sheetOrder.setPassengers(count)
    }

    override fun setChildSeats(setOf: Set<CreateOrderView.ChildSeatItem>, total: Int) {
        sheetOrder.setChildSeats(setOf, total)
    }

    override fun disablePromoCodeField() {
        sheetOrder.disablePromoView()
    }

    override fun setPromoResult(discountInfo: String?) {
        sheetOrder.setPromoResult(discountInfo)
    }

    override fun resetPromoView() {
        sheetOrder.resetPromoView()
    }

    override fun setEditableFields(
        offeredPrice: Double?,
        flightNumber: String?,
        flightNumberReturn: String?,
        promo: String
    ) {
        sheetOrder.price = offeredPrice
        sheetOrder.flightNumber = flightNumber
        sheetOrder.flightNumberReturn = flightNumberReturn
        sheetOrder.promoCode = promo
    }

    override fun setHourlyDuration(durationValue: Int?) {
        sheetOrder.setHourlyDuration(durationValue)
    }

    override fun setDateTimeTransfer(dateTimeString: String, startField: Boolean) {
        sheetOrder.setTransferDateTime(dateTimeString, startField)
    }

    override fun highLightErrorField(errorField: CreateOrderView.FieldError) {
        sheetOrder.highLightErrorField(errorField)
    }

    @CallSuper
    override fun onBackPressed() {
        when {
            isKeyBoardOpened                                             -> hideKeyboard()
            bsSecondarySheet.state == BottomSheetBehavior.STATE_EXPANDED -> hideSecondaryBottomSheet()
            bsOrder.state == BottomSheetBehavior.STATE_EXPANDED          -> toggleSheetOrder()
            else                                                         -> presenter.onBackClick()
        }
    }

/* TODO
    override fun enableReturnTimeChoose() {
        val listener = View.OnClickListener { showDatePickerDialog(FIELD_RETURN) }
        transfer_return_date_field.setOnClickListener(listener)
        transfer_return_date_field.field_input.setOnClickListener(listener)
    }
*/

    override fun showNotLoggedAlert(withOfferId: Long) =
        Utils.showScreenRedirectingAlert(this, getString(R.string.LNG_LOGIN_LOGIN_TO_CONTINUE)) {
            presenter.redirectToLogin(withOfferId)
        }

    override fun setTripType(withReturnWay: Boolean) {
        sheetOrder.withReturnWay = withReturnWay
    }

    @Suppress("EmptyFunctionBlock")
    override fun setHintForDateTimeTransfer(withReturnWay: Boolean) {
//      TODO transfer_date_time_field.input_layout.hint = context.getString(R.string.LNG_RIDE_DATE)
    }

    override fun onSetComment(comment: String) {
        sheetOrder.comment = comment
        presenter.setComment(comment)
    }

    override fun showCommentDialog(comment: String, hintsToComments: List<String>?) =
        CommentDialogFragment.newInstance(comment, hintsToComments?.toTypedArray())
            .show(supportFragmentManager, CommentDialogFragment.COMMENT_DIALOG_TAG)

    override fun showCurrencies() {
        hideKeyboard()
        FragmentUtils.replaceFragment(
            supportFragmentManager,
            SelectCurrencyBottomFragment(),
            R.id.secondary_bottom_sheet
        )
    }

    companion object {
        const val FIELD_START  = true
        const val FIELD_RETURN = false

        const val SHOW = true
        const val HIDE = false

        const val SIGN_NAME_FIELD_MAX_LENGTH = 20
        const val DELAY = 100L
    }
}
