package com.kg.gettransfer.presentation.ui

import android.annotation.SuppressLint

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

import android.text.InputFilter
import android.text.InputType //don't delete
import android.text.TextUtils //don't delete
import android.util.Log

import android.view.*
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo //don't delete

import android.widget.*

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.LatLng

import com.kg.gettransfer.R

import com.kg.gettransfer.extensions.*

import com.kg.gettransfer.presentation.adapter.TransferTypeAdapter
import com.kg.gettransfer.presentation.delegate.DateTimeDelegate
import com.kg.gettransfer.presentation.delegate.DateTimeDelegate.Companion.START_DATE
import com.kg.gettransfer.presentation.model.*

import com.kg.gettransfer.presentation.presenter.CreateOrderPresenter
import com.kg.gettransfer.presentation.ui.helpers.DateTimeScreen

import com.kg.gettransfer.presentation.view.CreateOrderView

import com.kg.gettransfer.utilities.Analytics.Companion.CAR_INFO_CLICKED
import com.kg.gettransfer.utilities.Analytics.Companion.COMMENT_INPUT
import com.kg.gettransfer.utilities.Analytics.Companion.DATE_TIME_CHANGED
import com.kg.gettransfer.utilities.Analytics.Companion.OFFER_PRICE_FOCUSED
import com.kg.gettransfer.utilities.PhoneNumberFormatter
import com.kg.gettransfer.presentation.view.CreateOrderView.FieldError

import kotlinx.android.synthetic.main.activity_create_order.*
import kotlinx.android.synthetic.main.bottom_sheet_create_order_new.*
import kotlinx.android.synthetic.main.bottom_sheet_type_transport.*
import kotlinx.android.synthetic.main.layout_popup_comment.*
import kotlinx.android.synthetic.main.layout_popup_comment.view.* //don't delete
import kotlinx.android.synthetic.main.view_count_controller.view.*
import kotlinx.android.synthetic.main.view_create_order_field.view.*
import org.koin.android.ext.android.inject

class CreateOrderActivity : BaseGoogleMapActivity(), CreateOrderView, DateTimeScreen {
    @InjectPresenter
    internal lateinit var presenter: CreateOrderPresenter
    private val dateDelegate: DateTimeDelegate by inject()

    private lateinit var bsOrder: BottomSheetBehavior<View>
    private lateinit var bsTransport: BottomSheetBehavior<View>
    private lateinit var bsCurrencies: BottomSheetBehavior<View>
    private lateinit var bsChildSeats: BottomSheetBehavior<View>
    private lateinit var popupWindowComment: PopupWindow

    private var defaultPromoText: String? = null

    private var phoneCode: Int = 0

    private var hasErrorFields = false
    private var errorFieldView: View? = null

    companion object {

        const val KEYBOARD_WAIT_DELAY = 300L

        const val FIELD_START  = true
        const val FIELD_RETURN = false

        const val SHOW = true
        const val HIDE = false
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
        _btnCenter = btnCenterRoute
        initMapView(savedInstanceState)

        initFieldsViews()
        scrollContent.setOnTouchListener(onTouchListener)

        rvTransferType.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvTransferType.isNestedScrollingEnabled = false

        setCurrenciesFragment()

        initChangeTextListeners()
        initClickListeners()
        initPromoSection()
        initKeyBoardListener()
        initBottomSheets()
    }

    @SuppressLint("CommitTransaction")
    private fun setCurrenciesFragment() {
        supportFragmentManager.beginTransaction().add(R.id.currenciesFragment, SelectCurrencyFragment()).commit()
    }

    val bsCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN && bsOrder.state == BottomSheetBehavior.STATE_HIDDEN)
                _tintBackground.isVisible = false
            if (bottomSheet == sheetChildSeats && newState == BottomSheetBehavior.STATE_HIDDEN) {
                presenter.updateChildSeatsInfo()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            if (bsOrder.state == BottomSheetBehavior.STATE_COLLAPSED) {
                _tintBackground.isVisible = true
                _tintBackground.alpha = slideOffset
            }
        }
    }

    private fun initBottomSheets() {
        bsOrder = BottomSheetBehavior.from(sheetOrder)
        bsTransport = BottomSheetBehavior.from(sheetTransport)
        bsCurrencies = BottomSheetBehavior.from(sheetCurrencies)
        bsChildSeats = BottomSheetBehavior.from(sheetChildSeats)
        bsTransport.state = BottomSheetBehavior.STATE_HIDDEN
        bsCurrencies.state = BottomSheetBehavior.STATE_HIDDEN
        bsChildSeats.state = BottomSheetBehavior.STATE_HIDDEN

        _tintBackground = tintBackground
        bsOrder.setBottomSheetCallback(bottomSheetCallback)
        bsTransport.setBottomSheetCallback(bsCallback)
        bsCurrencies.setBottomSheetCallback(bsCallback)
        bsChildSeats.setBottomSheetCallback(bsCallback)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            when {
                bsTransport.state == BottomSheetBehavior.STATE_EXPANDED ->
                    if(hideBottomSheet(bsTransport, sheetTransport, BottomSheetBehavior.STATE_HIDDEN, event)) return true

                bsCurrencies.state == BottomSheetBehavior.STATE_EXPANDED ->
                    if(hideBottomSheet(bsCurrencies, sheetCurrencies, BottomSheetBehavior.STATE_HIDDEN, event)) return true

                bsChildSeats.state == BottomSheetBehavior.STATE_EXPANDED ->
                    if (hideBottomSheet(bsChildSeats, sheetChildSeats, BottomSheetBehavior.STATE_HIDDEN, event)) return true

                bsOrder.state == BottomSheetBehavior.STATE_EXPANDED ->
                    if(hideBottomSheet(bsOrder, sheetOrder, BottomSheetBehavior.STATE_COLLAPSED, event)) return true

                bsChildSeats.state == BottomSheetBehavior.STATE_EXPANDED ->
                    if(hideBottomSheet(bsOrder, sheetOrder, BottomSheetBehavior.STATE_COLLAPSED, event)) return true
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun hideBottomSheet(bottomSheet: BottomSheetBehavior<View>) { bottomSheet.state = BottomSheetBehavior.STATE_HIDDEN }

    private fun toggleSheetOrder() {
        if (bsOrder.state != BottomSheetBehavior.STATE_EXPANDED) bsOrder.state = BottomSheetBehavior.STATE_EXPANDED
        else {
            bsOrder.state = BottomSheetBehavior.STATE_COLLAPSED
            scrollContent.fullScroll(View.FOCUS_UP)
        }
    }

    private fun initPromoSection() {
        with(promo_field) {
            field_input.filters = arrayOf(InputFilter.AllCaps())
            field_input.setOnFocusChangeListener { _, hasFocus -> if (!hasFocus) presenter.checkPromoCode() }
            defaultPromoText = input_layout.hint?.toString()
        }
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

    /*override fun setCurrencies(currencies: List<CurrencyModel>) =
        Utils.setCurrenciesDialogListener(this, fl_currency, currencies) { presenter.changeCurrency(it) }*/

    /*override fun setCurrencies(all: List<CurrencyModel>, popular: List<CurrencyModel>, selected: CurrencyModel) {
        currenciesFragment.setCurrencies(all, popular, selected) {
            bsCurrencies.state = BottomSheetBehavior.STATE_HIDDEN
            presenter.changeCurrency(it)
        }
        bsCurrencies.state = BottomSheetBehavior.STATE_EXPANDED
    }*/


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
            text = comment_field.field_input.text
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
        dateDelegate.chooseOrderTime(this, field, this)
    }

    //DateTimeScreen interface method
    override fun setFieldDate(date: String, field: Boolean) {
        presenter.changeDate(field)
        (if (field == START_DATE) transfer_date_time_field
        else transfer_return_date_field.also { showReturnFlight(SHOW) })
                .apply { field_input.setText(date) }
    }

    override fun setDateTimeTransfer(dateTimeString: String, startField: Boolean) {
        (if (startField) transfer_date_time_field
        else transfer_return_date_field.also { showReturnFlight(SHOW) })
                .also { it.field_input.setText(dateTimeString) }
    }

    override fun setTripType(withReturnWay: Boolean) {
        rl_returnWayTime.isVisible = withReturnWay
    }

    override fun setHintForDateTimeTransfer(withReturnWay: Boolean) {
        transfer_date_time_field.input_layout.hint = getString(R.string.LNG_RIDE_DATE)
    }

    private fun checkMinusButton(count: Int, minimum: Int, view: ImageView) {
        val imgRes = if (count == minimum) R.drawable.ic_minus_disabled else R.drawable.ic_minus
        view.setImageDrawable(ContextCompat.getDrawable(this, imgRes))
    }

    override fun setPassengers(count: Int) {
        passengers_count.person_count.text = "$count"
        checkMinusButton(count, 0, passengers_count.img_minus_seat)
    }

    override fun setChildSeats(setOf: Set<CreateOrderView.ChildSeatItem>, total: Int) {
        if (total == 0) {
            children_seat_field.field_input.setText("")
            return
        }
        with(StringBuilder()) {
            if (total > 1) append("$total ")
            append("(")
            setOf.forEach {
                if (it.count > 1) {
                    append(it.count)
                    append("x ")
                }
                append(getString(it.stringId))
                append(", ")
            }
            trimEnd()
            delete(length - 2, length)
            append(")")
        }.also {
            children_seat_field.field_input.setText(it)
        }
    }

    override fun setCurrency(currency: String, hideCurrencies: Boolean) {
        tv_currency.text = currency
        if(hideCurrencies) hideBottomSheet(bsCurrencies)
    }
    override fun setComment(comment: String)   { comment_field.field_input.setText(comment) }

    override fun setTransportTypes(transportTypes: List<TransportTypeModel>) {
        rvTransferType.adapter = TransferTypeAdapter(transportTypes) { transportType, showInfo ->
            presenter.onTransportChosen()
            checkErrorField(rvTransferType)
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
        else phoneCode = Utils.getPhoneCodeByCountryIso(this)

        email_field.field_input.setText(user.profile.email ?: "")

        if (isLoggedIn) email_field.field_input.isEnabled = false
        layoutAgreement.isGone = user.termsAccepted
    }

    override fun setRoute(polyline: PolylineModel, routeModel: RouteModel, isDateChanged: Boolean) {
        if (isDateChanged) clearMarkersAndPolylines()
        setPolyline(polyline, routeModel)
        btnCenterRoute.isVisible = false
    }

    override fun setPinHourlyTransfer(placeName: String, info: String, point: LatLng, cameraUpdate: CameraUpdate) {
        processGoogleMap(false) { setPinForHourlyTransfer(placeName, info, point, cameraUpdate) }
        btnCenterRoute.isVisible = false
    }

    override fun centerRoute(cameraUpdate: CameraUpdate) = showTrack(cameraUpdate)

    override fun setPromoResult(discountInfo: String?) {
        @ColorRes var colorText = R.color.color_error
        var text = getString(R.string.LNG_RIDE_PROMOCODE_INVALID)

        discountInfo?.let {
            colorText = R.color.colorGreen
            text = it
        }
        promo_field.field_input.setTextColor(ContextCompat.getColor(this, colorText))
        promo_field.input_layout.hint = text
    }

    override fun resetPromoView() {
        promo_field.input_layout.hint = defaultPromoText
        promo_field.field_input.setTextColor(ContextCompat.getColor(this, R.color.colorTextLightGray))
    }

    override fun showEmptyFieldError(@StringRes stringId: Int) {
        Utils.getAlertDialogBuilder(this).apply {
            setTitle(getString(R.string.LNG_RIDE_CANT_CREATE))
            setMessage(getString(stringId))
            setPositiveButton(R.string.LNG_OK) { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

    override fun highLightErrorField(errorField: FieldError) {
        hasErrorFields = true
        when (errorField) {
            FieldError.TRANSPORT_FIELD -> {
                highLightErrorField(rvTransferType)
                errorFieldView = rvTransferType
            }
            FieldError.TIME_NOT_SELECTED -> {
                highLightErrorField(transfer_date_time_field)
                errorFieldView = transfer_date_time_field
            }
            FieldError.RETURN_TIME -> {
                highLightErrorField(transfer_return_date_field)
                errorFieldView = transfer_return_date_field
            }
            FieldError.PASSENGERS_COUNT -> highLightErrorField(passengers_count_field)
            FieldError.PHONE_FIELD -> {
                highLightErrorField(phone_field)
                errorFieldView = phone_field
            }
            FieldError.EMAIL_FIELD -> {
                highLightErrorField(email_field)
                errorFieldView = email_field
            }
            FieldError.TERMS_ACCEPTED_FIELD -> highLightErrorField(layoutAgreement)
            else -> return
        }
    }

    private fun highLightErrorField(view: View) {
        view.setBackgroundResource(R.drawable.background_create_order_error)
        scrollContent.smoothScrollTo(0, view.bottom)
    }

    private fun checkErrorField(view: View) {
        if (hasErrorFields) {
            hasErrorFields = false
            clearHighLightErrorField(view)
        }
    }

    private fun clearHighLightErrorField(view: View?) = view?.setBackgroundResource(0)

    private fun transportTypeClicked(transportType: TransportTypeModel) {
        bsTransport.state = BottomSheetBehavior.STATE_EXPANDED
        showTransportInfo(transportType)
        presenter.logButtons(CAR_INFO_CLICKED)
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
            isKeyBoardOpened                                          -> hideKeyboard()
            bsTransport.state  == BottomSheetBehavior.STATE_EXPANDED  -> hideBottomSheet(bsTransport)
            bsCurrencies.state == BottomSheetBehavior.STATE_EXPANDED  -> hideBottomSheet(bsCurrencies)
            bsChildSeats.state == BottomSheetBehavior.STATE_EXPANDED  -> hideBottomSheet(bsChildSeats)
            bsOrder.state      == BottomSheetBehavior.STATE_EXPANDED  -> toggleSheetOrder()
            else                                                      -> presenter.onBackClick()
        }
    }

    //TODO create custom view for new bottom sheet
    private fun initFieldsViews() {
     //   passengers_seats.seat_icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_passenger_small))
     //   child_seats.seat_icon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_child_seat))
        Utils.setDrawables(price_field_input.field_input, 0, 0, R.drawable.ic_arrow_right, 0)
        price_field_input.field_input.compoundDrawablePadding = 0
        passengers_count.person_count.text = getString(R.string.passenger_number_default)
    }

    private fun initChangeTextListeners() {
        price_field_input.field_input.onTextChanged             { presenter.cost = it.toDoubleOrNull() }
        price_field_input.field_input.setOnFocusChangeListener  { _, hasFocus ->
            if (hasFocus) presenter.logTransferSettingsEvent(OFFER_PRICE_FOCUSED)
        }
        user_name_field.field_input.onTextChanged        { presenter.setName(it.trim()) }
        email_field.field_input.onTextChanged            { presenter.setEmail(it.trim()) }
        phone_field.field_input.onTextChanged            {
            if (it.isEmpty() && phone_field.field_input.isFocused) {
                phone_field.field_input.setText("+")
                phone_field.field_input.setSelection(1)
            }
            presenter.setPhone("+".plus(it.replace(Regex("\\D"), "")))
        }
        phone_field.field_input.addTextChangedListener(PhoneNumberFormatter())
        flight_number_field.field_input.onTextChanged         { presenter.setFlightNumber(it.trim(), false) }
        flight_numberReturn_field.field_input.onTextChanged   { presenter.setFlightNumber(it.trim(), true) }
        promo_field.field_input.onTextChanged                 { presenter.setPromo(promo_field.field_input.text.toString()) }
    }

    private fun initClickListeners() {
        View.OnClickListener {
            showDatePickerDialog(FIELD_START)
            presenter.logTransferSettingsEvent(DATE_TIME_CHANGED)
        }.let {
            transfer_date_time_field.setOnClickListener(it)
            transfer_date_time_field.field_input.setOnClickListener(it)
        }

        fl_DeleteReturnDate.setOnClickListener              { presenter.clearReturnDate()
            showReturnFlight(HIDE)
        }
        passengers_count.img_plus_seat.setOnClickListener   {
            checkErrorField(passengers_count_field)
            presenter.changePassengers(1)
        }
        passengers_count.img_minus_seat.setOnClickListener  {
            presenter.changePassengers(-1)
        }

        View.OnClickListener {
            bsChildSeats.state = BottomSheetBehavior.STATE_EXPANDED
        }.let {
            children_seat_field.setOnClickListener(it)
            children_seat_field.field_input.setOnClickListener(it)
        }

        cl_offer_price.setOnClickListener                   { fieldTouched(price_field_input.field_input)  }
        user_name_field.setOnClickListener                  { fieldTouched(user_name_field.field_input) }
        email_field.setOnClickListener                      { fieldTouched(email_field.field_input) }
        phone_field.field_input.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) setPhoneCode()
            else {
                val phone = phone_field.field_input.text?.trim()
                phone?.let {
                    if (phone.length == 1)
                        phone_field.field_input.text?.clear()
                }
            }
        }
        flight_number_field.setOnClickListener              { fieldTouched(flight_number_field.field_input) }
        comment_field.field_input.setOnClickListener        { showPopupWindowComment()
            presenter.logTransferSettingsEvent(COMMENT_INPUT)
        }

        tvAgreement1.setOnClickListener                     { presenter.showLicenceAgreement() }
        switchAgreement.setOnCheckedChangeListener          {
            _, isChecked -> presenter.setAgreeLicence(isChecked)
            checkErrorField(layoutAgreement)
        }

        btnGetOffers.setOnClickListener                     {
            clearHighLightErrorField(errorFieldView)
            presenter.onGetTransferClick()
        }
        btnCenterRoute.setOnClickListener                   { presenter.onCenterRouteClick() }
        btnBack.setOnClickListener                          { presenter.onBackClick() }
        btnOk.setOnClickListener                            { hideBottomSheet(bsTransport) }

        fl_currency.setOnClickListener { bsCurrencies.state = BottomSheetBehavior.STATE_EXPANDED }
    }

    override fun enableReturnTimeChoose() {
        View.OnClickListener {
            showDatePickerDialog(FIELD_RETURN)
        }.let {
            transfer_return_date_field.setOnClickListener(it)
            transfer_return_date_field.field_input.setOnClickListener(it)
        }
    }

    private fun setPhoneCode() {
        val phone = phone_field.field_input.text?.trim()
        phone?.let {
            if (phone.isEmpty())
                phone_field.field_input.setText(if (phoneCode > 0) "+".plus(phoneCode) else "+")
        }
    }

    private fun fieldTouched(viewForFocus: EditText) {
        if (!isKeyBoardOpened) showKeyboard()
        viewForFocus.apply {
            requestFocus()
            setSelection(text.length)
        }
    }

    override fun showNotLoggedAlert(withOfferId: Long) =
        Utils.showScreenRedirectingAlert(this, getString(R.string.LNG_LOGIN_NOT_LOGGED_IN),
                    getString(R.string.LNG_LOGIN_LOGIN_TO_CONTINUE)) { presenter.redirectToLogin(withOfferId) }

    val showReturnFlight: (show: Boolean) -> Unit = { show ->
        flight_numberReturn_field.isVisible = show
        fl_DeleteReturnDate.isVisible       = show
        if (!show) {
            transfer_return_date_field.field_input.text?.clear()
            flight_numberReturn_field.field_input.text?.clear()
            transfer_return_date_field.input_layout.hint = getString(R.string.LNG_RIDE_DATE_RETURN_PLACEHOLDER)
        } else transfer_return_date_field.input_layout.hint = getString(R.string.LNG_RIDE_RETURN_TRANSFER)
        changeReturnTransferIcon(show)
    }

    private fun changeReturnTransferIcon(show: Boolean) {
        if (show)
            Utils.setDrawables(
                    transfer_return_date_field.field_input,
                    R.drawable.ic_calendar, 0, 0, 0)

        else Utils.setDrawables(
                transfer_return_date_field.field_input,
                R.drawable.ic_plus, 0, R.drawable.ic_arrow_right, 0)
    }
}
