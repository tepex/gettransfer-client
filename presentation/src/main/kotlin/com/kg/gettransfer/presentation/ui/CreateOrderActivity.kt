package com.kg.gettransfer.presentation.ui

import android.app.DatePickerDialog

import android.graphics.Color

import android.os.Build
import android.os.Bundle
import android.os.Handler

import android.support.annotation.CallSuper
import android.support.annotation.ColorRes
import android.support.annotation.StringRes

import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.ContextCompat

import android.support.v7.widget.LinearLayoutManager

import android.telephony.PhoneNumberFormattingTextWatcher

import android.text.InputFilter
import android.text.InputType //don't delete
import android.text.TextUtils //don't delete



import android.view.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo //don't delete

import android.widget.*

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R
import com.kg.gettransfer.common.BoundDatePickerDialog

import com.kg.gettransfer.common.BoundTimePickerDialog

import com.kg.gettransfer.extensions.*

import com.kg.gettransfer.presentation.adapter.TransferTypeAdapter
import com.kg.gettransfer.presentation.model.*

import com.kg.gettransfer.presentation.presenter.CreateOrderPresenter

import com.kg.gettransfer.presentation.view.CreateOrderView

import com.kg.gettransfer.utilities.Analytics.Companion.CAR_INFO_CLICKED
import com.kg.gettransfer.utilities.Analytics.Companion.COMMENT_INPUT
import com.kg.gettransfer.utilities.Analytics.Companion.DATE_TIME_CHANGED
import com.kg.gettransfer.utilities.Analytics.Companion.OFFER_PRICE_FOCUSED

import java.util.Calendar

import kotlinx.android.synthetic.main.activity_create_order.*
import kotlinx.android.synthetic.main.bottom_sheet_create_order_new.*
import kotlinx.android.synthetic.main.bottom_sheet_type_transport.*
import kotlinx.android.synthetic.main.layout_popup_comment.*
import kotlinx.android.synthetic.main.layout_popup_comment.view.* //don't delete
import kotlinx.android.synthetic.main.view_create_order_field.view.*
import kotlinx.android.synthetic.main.view_seats.view.*

class CreateOrderActivity : BaseGoogleMapActivity(), CreateOrderView {
    @InjectPresenter
    internal lateinit var presenter: CreateOrderPresenter

    private val calendar = Calendar.getInstance()

    private lateinit var bsOrder: BottomSheetBehavior<View>
    private lateinit var bsTransport: BottomSheetBehavior<View>
    private lateinit var popupWindowComment: PopupWindow

    private var defaultPromoText: String? = null


    companion object {

        const val KEYBOARD_WAIT_DELAY = 300L

        const val FIELD_START  = true
        const val FIELD_RETURN = false
    }

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

        _mapView = mapView
        initMapView(savedInstanceState)

        initFieldsViews()
        scrollContent.setOnTouchListener(onTouchListener)

        rvTransferType.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvTransferType.isNestedScrollingEnabled = false

        initChangeTextListeners()
        initClickListeners()
        initPromoSection()
        initKeyBoardListener()
        initBottomSheets()
    }

    private fun initBottomSheets() {
        bsOrder = BottomSheetBehavior.from(sheetOrder)
        bsTransport = BottomSheetBehavior.from(sheetTransport)
        bsTransport.state = BottomSheetBehavior.STATE_HIDDEN

        _tintBackground = tintBackground
        bsOrder.setBottomSheetCallback(bottomSheetCallback)
        bsTransport.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN && bsOrder.state == BottomSheetBehavior.STATE_HIDDEN)
                    _tintBackground.isVisible = false
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (bsOrder.state == BottomSheetBehavior.STATE_COLLAPSED) {
                    _tintBackground.isVisible = true
                    _tintBackground.alpha = slideOffset
                }
            }
        })
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (bsTransport.state == BottomSheetBehavior.STATE_EXPANDED) {
                if(hideBottomSheet(bsTransport, sheetTransport, BottomSheetBehavior.STATE_HIDDEN, event)) return true
            } else if (bsOrder.state == BottomSheetBehavior.STATE_EXPANDED) {
                if(hideBottomSheet(bsOrder, sheetOrder, BottomSheetBehavior.STATE_COLLAPSED, event)) return true
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun hideSheetTransport() { bsTransport.state = BottomSheetBehavior.STATE_HIDDEN }

    private fun toggleSheetOrder() {
        if (bsOrder.state != BottomSheetBehavior.STATE_EXPANDED) bsOrder.state = BottomSheetBehavior.STATE_EXPANDED
        else {
            bsOrder.state = BottomSheetBehavior.STATE_COLLAPSED
            scrollContent.fullScroll(View.FOCUS_UP)
        }
    }

    private fun initPromoSection() {
        promo_field.field_input.filters = arrayOf(InputFilter.AllCaps())
        promo_field.field_input.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) presenter.checkPromoCode() }
        defaultPromoText = promo_field.field_title.text.toString()
    }

    private fun initKeyBoardListener() {
        addKeyBoardDismissListener { closed ->
            if (!closed) btnGetOffers.isVisible = !closed
            else {
                // postDelayed нужен, чтобы кнопка не морагала посередине экрана
                Handler().postDelayed({ btnGetOffers.isVisible = closed }, 100)
                if (promo_field.field_input.isFocused) presenter.checkPromoCode()
            }
        }
    }

    /*
    protected suspend override fun customizeGoogleMaps(gm: GoogleMap) {
        super.customizeGoogleMaps(gm)
        gm.uiSettings.setRotateGesturesEnabled(false)
        gm.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
    }
    */

    protected override fun initMap() {
        super.initMap()
        presenter.initMapAndPrices()
    }

    override fun setCurrencies(currencies: List<CurrencyModel>) =
        Utils.setCurrenciesDialogListener(this, fl_currency, currencies) { presenter.changeCurrency(it) }


    private fun showPopupWindowComment() {
        val screenHeight = getScreenSide(true)
        applyDim(window.decorView.rootView as  ViewGroup, DIM_AMOUNT)

        val layoutPopupView = LayoutInflater.from(applicationContext).inflate(R.layout.layout_popup_comment, layoutPopup).apply {
            btnClearPopupComment.setOnClickListener { etPopupComment.setText("") }
            setOnClickListener { etPopupComment.requestFocus() }
            etPopupComment.setSelection(etPopupComment.text.length)
        }

        popupWindowComment = PopupWindow(layoutPopupView, LinearLayout.LayoutParams.MATCH_PARENT, screenHeight / 3, true).apply {
            softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
            inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
            isOutsideTouchable = true
            setOnDismissListener {
                layoutPopupView.etPopupComment.hideKeyboard()
                layoutPopupView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.hide_popup))
                btnGetOffers.requestFocus()
                clearDim(window.decorView.rootView as  ViewGroup)
            }
        }

        with (layoutPopupView.etPopupComment) {
            setText(comment_field.field_input.text)
            setRawInputType(InputType.TYPE_CLASS_TEXT)
            popupWindow = popupWindowComment
            if (!isKeyBoardOpened) showKeyboard()
            setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    presenter.setComment(layoutPopupView.etPopupComment.text.toString().trim())
                    popupWindowComment.dismiss()
                    return@OnEditorActionListener true
                }
                false
            })
        }

        Handler().postDelayed({
            popupWindowComment.showAtLocation(mainLayoutActivityTransfer, Gravity.CENTER, 0, 0)
            layoutPopupView.startAnimation(AnimationUtils.loadAnimation(applicationContext, R.anim.show_popup))
        }, CreateOrderActivity.KEYBOARD_WAIT_DELAY)
    }



    private fun showDatePickerDialog(field: Boolean) {
        val tripDate = if (field) presenter.startDate else presenter.returnDate
        val currentDate = presenter.currentDate
        calendar.time = presenter.startDate.date

        val boundDatePickerDialog = BoundDatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                presenter.startDate.date = calendar.time

                val calendarWithoutTime = Calendar.getInstance()
                calendarWithoutTime.set(year, monthOfYear, dayOfMonth, 0, 0)
                when {

                    calendarWithoutTime.time.after(currentDate.time) -> showTimePickerDialog(
                        -1,
                        24,
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        field
                    )

                    calendar.time.after(currentDate.time) -> showTimePickerDialog(
                        currentDate.get(Calendar.HOUR_OF_DAY),
                        currentDate.get(Calendar.MINUTE),
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        field
                    )

                    else -> showTimePickerDialog(
                        currentDate.get(Calendar.HOUR_OF_DAY),
                        currentDate.get(Calendar.MINUTE),
                        currentDate.get(Calendar.HOUR_OF_DAY),
                        currentDate.get(Calendar.MINUTE),
                        field
                    )
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        )

        if (Build.VERSION.SDK_INT < 21) {
            boundDatePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
            boundDatePickerDialog.setMin(
                currentDate.get(Calendar.YEAR),
                currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH)
            )
        } else {
            boundDatePickerDialog.datePicker.minDate = currentDate.timeInMillis
        }
        boundDatePickerDialog.show()
    }

    private fun showTimePickerDialog(minHour: Int, minMinute: Int, setHour: Int, setMinute: Int, field: Boolean) {
        /*val timePickerDialog = TimePickerDialog(this, { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            presenter.changeDate(calendar.time)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
        timePickerDialog.show() */

        BoundTimePickerDialog(
            this,
            { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                presenter.changeDate(calendar.time, field)
            },
            setHour,
            setMinute,
            true
        ).apply {
            setMin(minHour, minMinute)
            show()
        }
    }

    override fun setDateTimeTransfer(dateTimeString: String, isAfterMinHours: Boolean, startField: Boolean) {
        if (isAfterMinHours) transfer_date_time_field.field_input.setText(getTextForMinDate())
        else transfer_date_time_field.field_input.setText(dateTimeString)
    }

    override fun setHintForDateTimeTransfer() {
        transfer_date_time_field.field_input.hint = getTextForMinDate()
    }

    private fun getTextForMinDate() = getString(R.string.LNG_DATE_IN_HOURS)
            .plus(" ")
            .plus(presenter.futureHour)
            .plus(" ")
            .plus(getString(R.string.LNG_HOUR_FEW))

            private fun checkMinusButton(count: Int, minimum: Int, view: ImageView) {
        val imgRes = if (count == minimum) R.drawable.ic_circle_minus else R.drawable.ic_minus_enabled
        view.setImageDrawable(ContextCompat.getDrawable(this, imgRes))
    }

    override fun setPassengers(count: Int) {
        passengers_seats.person_count.text = count.toString()
        checkMinusButton(count, 0, passengers_seats.img_minus_seat)
    }

    override fun setChildren(count: Int) {
        child_seats.person_count.text = count.toString()
        checkMinusButton(count, 0, child_seats.img_minus_seat)
    }

    override fun setCurrency(currency: String) { tv_currency.text = currency }
    override fun setComment(comment: String)   { comment_field.field_input.setText(comment) }

    override fun setTransportTypes(transportTypes: List<TransportTypeModel>) {
        rvTransferType.adapter = TransferTypeAdapter(transportTypes) { transportType, showInfo ->
            presenter.onTransportChosen()
            if (showInfo) transportTypeClicked(transportType)
        }
    }

    override fun setFairPrice(price: String?, time: String?) {
        if (price == null || time == null) tvRate.text = ""
        else tvRate.text = String.format(getString(R.string.LNG_RIDE_FAIR_PRICE_FORMAT), price, time)
    }

    override fun setUser(user: UserModel, isLoggedIn: Boolean) {
        user_name_field.field_input.setText(user.profile.name ?: "")
        if (user.profile.phone != null) phone_field.field_input.setText(user.profile.phone)
        else {
            val phoneCode = Utils.getPhoneCodeByCountryIso(this)
            phone_field.field_input.setText(if (phoneCode > 0) "+".plus(phoneCode) else "+")
        }
        email_field.field_input.setText(user.profile.email ?: "")

        if (isLoggedIn) email_field.field_input.isEnabled = false
        layoutAgreement.isGone = user.termsAccepted
    }

    //TODO сделать подсветку не заполненных полей
    override fun setGetTransferEnabled(enabled: Boolean) {}

    override fun setRoute(polyline: PolylineModel, routeModel: RouteModel, isDateChanged: Boolean) {
        if (isDateChanged) clearMarkersAndPolylines()
        setPolyline(polyline, routeModel)
    }

    override fun setPinHourlyTransfer(placeName: String, info: String, point: LatLng, cameraUpdate: CameraUpdate) =
        processGoogleMap(false) { setPinForHourlyTransfer(placeName, info, point, cameraUpdate) }

    override fun centerRoute(cameraUpdate: CameraUpdate) = showTrack(cameraUpdate)

    override fun setPromoResult(discountInfo: String?) {
        @ColorRes var colorRes = R.color.color_error
        var text = getString(R.string.LNG_RIDE_PROMOCODE_INVALID)
        discountInfo?.let {
            colorRes = R.color.promo_valid
            text = it
        }
        promo_field.field_title.setTextColor(ContextCompat.getColor(this, colorRes))
        promo_field.field_title.text = text
        img_okResult.isVisible = discountInfo != null
    }

    override fun resetPromoView() {
        promo_field.field_title.text = defaultPromoText
        promo_field.field_title.setTextColor(ContextCompat.getColor(this, R.color.colorTextLightGray))
        img_okResult.isVisible = false
    }

    override fun showEmptyFieldError(@StringRes stringId: Int) {
        Utils.getAlertDialogBuilder(this).apply {
            setTitle(getString(R.string.LNG_RIDE_CANT_CREATE))
            setMessage(getString(stringId))
            setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

    private fun transportTypeClicked(transportType: TransportTypeModel) {
        bsTransport.state = BottomSheetBehavior.STATE_EXPANDED
        showTransportInfo(transportType)
        presenter.logEventMain(CAR_INFO_CLICKED)
    }

    private fun showTransportInfo(transportType: TransportTypeModel) {
        tvTypeTransfer.setText(transportType.nameId!!)
        ivTypeTransfer.setImageResource(transportType.imageId!!)
        tvPrice.text            = transportType.price?.min
        tvCountPassengers.text  = transportType.paxMax.toString()
        tvCountLuggage.text     = transportType.luggageMax.toString()
        tvCars.setText(transportType.description!!)
    }

    @CallSuper
    override fun onBackPressed() {
        when {
            isKeyBoardOpened                                        -> hideKeyboard()
            bsTransport.state == BottomSheetBehavior.STATE_EXPANDED -> hideSheetTransport()
            bsOrder.state     == BottomSheetBehavior.STATE_EXPANDED -> toggleSheetOrder()
            else                                                    -> super.onBackPressed()
        }
    }

    //TODO create custom view for new bottom sheet
    private fun initFieldsViews() {

        /* icons */

        passengers_seats.seat_icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_passenger_small))
        child_seats.seat_icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_child_seat))

        /* titles */
        passengers_seats.seat_title.text = getString(R.string.LNG_RIDE_PASSENGERS)
        child_seats.seat_title.text = getString(R.string.LNG_RIDE_CHILDREN)

        /* editable fields */
        passengers_seats.person_count.text = getString(R.string.passenger_number_default)
        child_seats.person_count.text      = getString(R.string.child_number_default)
    }

    private fun initChangeTextListeners() {
        price_field_input.onTextChanged             { presenter.cost = it.toDoubleOrNull() }
        price_field_input.setOnFocusChangeListener  { _, hasFocus ->
            if (hasFocus) presenter.logTransferSettingsEvent(OFFER_PRICE_FOCUSED)
        }
        user_name_field.field_input.onTextChanged        { presenter.setName(it.trim()) }
        email_field.field_input.onTextChanged            { presenter.setEmail(it.trim()) }
        phone_field.field_input.onTextChanged            {
            if (it.isEmpty()) {
                phone_field.field_input.setText("+")
                phone_field.field_input.setSelection(1)
            }
            presenter.setPhone("+".plus(it.replace(Regex("\\D"), "")))
        }
        phone_field.field_input.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        flight_number_field.field_input.onTextChanged    { presenter.setFlightNumber(it.trim()) }
        promo_field.field_input.onTextChanged            { presenter.setPromo(promo_field.field_input.text.toString()) }
    }

    private fun initClickListeners() {
        View.OnClickListener {
            showDatePickerDialog(FIELD_START)
            presenter.logTransferSettingsEvent(DATE_TIME_CHANGED)
        }.let {
            transfer_date_time_field.setOnClickListener(it)
            transfer_date_time_field.field_input.setOnClickListener(it)
        }

        View.OnClickListener {
            showDatePickerDialog(FIELD_RETURN)
        }.let {
            transfer_return_date_field.setOnClickListener(it)
            transfer_return_date_field.field_input.setOnClickListener(it)
        }


        passengers_seats.img_plus_seat.setOnClickListener   { presenter.changePassengers(1) }
        passengers_seats.img_minus_seat.setOnClickListener  { presenter.changePassengers(-1) }
        child_seats.img_minus_seat.setOnClickListener       { presenter.changeChildren(-1) }
        child_seats.img_plus_seat.setOnClickListener        { presenter.changeChildren(1) }

        cl_offer_price.setOnClickListener                   { fieldTouched(price_field_input)  }
        user_name_field.setOnClickListener                  { fieldTouched(user_name_field.field_input) }
        email_field.setOnClickListener                      { fieldTouched(email_field.field_input) }
        phone_field.setOnClickListener                      { fieldTouched(phone_field.field_input)}
        flight_number_field.setOnClickListener              { fieldTouched(flight_number_field.field_input) }
        promo_field.setOnClickListener                      { fieldTouched(promo_field.field_input) }
        comment_field.setOnClickListener                    { showPopupWindowComment()
            presenter.logTransferSettingsEvent(COMMENT_INPUT)
        }
        comment_field.field_input.setOnClickListener        { showPopupWindowComment() }

        tvAgreement1.setOnClickListener                     { presenter.showLicenceAgreement() }
        switchAgreement.setOnCheckedChangeListener          { _, isChecked -> presenter.setAgreeLicence(isChecked) }

        btnGetOffers.setOnClickListener                     { presenter.onGetTransferClick() }
        btnCenterRoute.setOnClickListener                   { presenter.onCenterRouteClick() }
        btnBack.setOnClickListener                          { presenter.onBackClick() }
        btnOk.setOnClickListener                            { hideSheetTransport() }
    }

    private fun fieldTouched(viewForFocus: EditText) {
        if (!isKeyBoardOpened) showKeyboard()
        viewForFocus.apply {
            requestFocus()
            setSelection(text.length)
        }
    }

    override fun showNotLoggedAlert(withOfferId: Long) =
        Utils.showScreenRedirectingAlert(this, getString(R.string.log_in_requirement_error_title),
                    getString(R.string.log_in_to_see_transfers_and_offers)) { presenter.redirectToLogin(withOfferId) }
}
