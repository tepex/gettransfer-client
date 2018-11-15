package com.kg.gettransfer.presentation.ui

import android.app.DatePickerDialog
import com.kg.gettransfer.common.BoundTimePickerDialog

import android.content.Context
import android.content.Intent

import android.graphics.Color
import android.graphics.drawable.ColorDrawable

import android.os.Build
import android.os.Bundle
import android.os.Handler

import android.support.annotation.CallSuper

import android.support.design.widget.BottomSheetBehavior
import android.support.v4.content.ContextCompat

import android.support.v7.widget.LinearLayoutManager
import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.InputFilter

import android.text.InputType
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.AnimationUtils

import android.view.inputmethod.EditorInfo
import android.widget.*

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.model.MapStyleOptions

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.interactor.OfferInteractor
import com.kg.gettransfer.domain.interactor.PromoInteractor

import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.adapter.TransferTypeAdapter

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.PolylineModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.model.UserModel

import com.kg.gettransfer.presentation.presenter.CreateOrderPresenter
import com.kg.gettransfer.presentation.view.CreateOrderView

import kotlinx.android.synthetic.main.activity_create_order.*
//import kotlinx.android.synthetic.main.bottom_sheet_create_order.*
import kotlinx.android.synthetic.main.bottom_sheet_type_transport.*
import kotlinx.android.synthetic.main.layout_popup_comment.*
import kotlinx.android.synthetic.main.layout_popup_comment.view.*

import kotlinx.android.synthetic.main.amu_info_window.view.*


import com.kg.gettransfer.extensions.hideKeyboard
import com.kg.gettransfer.extensions.showKeyboard


import com.kg.gettransfer.presentation.IntentKeys

import kotlinx.android.synthetic.main.bottom_sheet_create_order_new.*
import kotlinx.android.synthetic.main.view_create_order_field.view.*
import kotlinx.android.synthetic.main.view_seats.view.*

import org.koin.android.ext.android.inject

import java.util.Calendar

class CreateOrderActivity: BaseGoogleMapActivity(), CreateOrderView {

    @InjectPresenter
    internal lateinit var presenter: CreateOrderPresenter

    private val routeInteractor: RouteInteractor by inject()
    private val transferInteractor: TransferInteractor by inject()
    private val promoInteractor: PromoInteractor by inject()
    private val offerInteractor: OfferInteractor by inject()
    private val calendar = Calendar.getInstance()
    private lateinit var bsOrder: BottomSheetBehavior<View>
    private lateinit var bsTransport: BottomSheetBehavior<View>
    private lateinit var popupWindowComment: PopupWindow

    private var defaultPromoText: String? = null
    private var isKeyBoardOpened = false

    companion object {
        const val DIM_AMOUNT = 0.5f

        const val KEYBOARD_WAIT_DELAY = 300L
    }

    @ProvidePresenter
    fun createCreateOrderPresenter(): CreateOrderPresenter = CreateOrderPresenter(coroutineContexts,
                                                                                  router,
                                                                                  systemInteractor,
                                                                                  routeInteractor,
                                                                                  transferInteractor,
                                                                                  promoInteractor,
                                                                                  offerInteractor)

    protected override var navigator = object: BaseNavigator(this) {
        @CallSuper
        protected override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
            val intent = super.createActivityIntent(context, screenKey, data)
            if(intent != null) return intent
                
            when(screenKey) {
                Screens.LICENCE_AGREE -> return Intent(context, WebPageActivity()::class.java).apply {
                    putExtra(WebPageActivity.SCREEN, WebPageActivity.SCREEN_LICENSE)
                }
                Screens.PASSENGER_MODE -> return Intent(context, MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                }
            }
            return null
        }
    }

    override fun getPresenter(): CreateOrderPresenter = presenter

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_order)

        Utils.initPhoneNumberUtil(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = Color.WHITE
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else{
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            viewGradient.visibility = View.GONE
        }

        _mapView = mapView
        initMapView(savedInstanceState)

        initFieldsViews()

        rvTransferType.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvTransferType.isNestedScrollingEnabled = false

        initChangeTextListeners()
        initClickListeners()
        initPromoSection()
        initKeyBoardListener()

        bsOrder = BottomSheetBehavior.from(sheetOrder)
        sheetOrder.visibility = View.VISIBLE
        bsTransport = BottomSheetBehavior.from(sheetTransport)
        bsTransport.state = BottomSheetBehavior.STATE_HIDDEN

    private fun initFieldsViews() {
        scrollContent.setOnTouchListener(onTouchListener)
    }




    private fun hideSheetTransport() {
        bsTransport.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun toggleSheetOrder() {
        if(bsOrder.state != BottomSheetBehavior.STATE_EXPANDED) {
            bsOrder.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            bsOrder.state = BottomSheetBehavior.STATE_COLLAPSED
            scrollContent.fullScroll(View.FOCUS_UP)
        }
    }

    private fun initPromoSection() {

        promo_field.field_input.filters = arrayOf(InputFilter.AllCaps())
        promo_field.field_input.setOnFocusChangeListener { _, hasFocus -> if(!hasFocus) presenter.checkPromoCode() }
        defaultPromoText = promo_field.field_title.text.toString()
    }
    
    private fun initKeyBoardListener() {
        addKeyBoardDismissListener { closed ->
            if (!closed && !isKeyBoardOpened) {
                isKeyBoardOpened = true
                btnGetOffers.visibility = View.GONE
            }
            else if (closed && isKeyBoardOpened) {
                isKeyBoardOpened = false
                Handler().postDelayed({   // postDelayed нужен, чтобы кнопка не морагала посередине экрана
                    btnGetOffers.visibility = View.VISIBLE
                }, 100)
                if(promo_field.field_input.isFocused) presenter.checkPromoCode()
            }
        }
    }

    protected suspend override fun customizeGoogleMaps() {
        super.customizeGoogleMaps()
        googleMap.uiSettings.setRotateGesturesEnabled(false)
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
    }

    protected override fun initMap() {
        super.initMap()
        presenter.initMapAndPrices()
    }

    override fun setCurrencies(currencies: List<CurrencyModel>) {
        Utils.setCurrenciesDialogListener(this, fl_currency, currencies) { selected ->
            presenter.changeCurrency(selected)
        }
    }

    private fun showPopupWindowComment() {
        val screenHeight = getScreenHeight()

        val layoutPopupView = LayoutInflater.from(applicationContext).inflate(R.layout.layout_popup_comment, layoutPopup)

        applyDim(window.decorView.rootView as  ViewGroup, DIM_AMOUNT)
        popupWindowComment = PopupWindow(layoutPopupView,
                LinearLayout.LayoutParams.MATCH_PARENT,
                screenHeight / 3,
                true)
        popupWindowComment.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        popupWindowComment.inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
        popupWindowComment.isOutsideTouchable = true
        layoutPopupView.etPopupComment.setText(comment_field.field_input.text)
        layoutPopupView.etPopupComment.setRawInputType(InputType.TYPE_CLASS_TEXT)
        layoutPopupView.etPopupComment.popupWindow = popupWindowComment
        if(!isKeyBoardOpened) layoutPopupView.etPopupComment.showKeyboard()

        Handler().postDelayed({
            popupWindowComment.showAtLocation(mainLayoutActivityTransfer, Gravity.CENTER, 0, 0)
            val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.show_popup)
            layoutPopupView.startAnimation(animation)
        }, CreateOrderActivity.KEYBOARD_WAIT_DELAY)

        layoutPopupView.btnClearPopupComment.setOnClickListener { layoutPopupView.etPopupComment.setText("") }
        layoutPopupView.etPopupComment.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                presenter.setComment(layoutPopupView.etPopupComment.text.toString().trim())
                popupWindowComment.dismiss()
                return@OnEditorActionListener true
            }
            false
        })
        popupWindowComment.setOnDismissListener {
            layoutPopupView.etPopupComment.hideKeyboard()
//            toggleSheetOrder()
            val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.hide_popup)
            layoutPopupView.startAnimation(animation)
            btnGetOffers.requestFocus()
            clearDim(window.decorView.rootView as  ViewGroup)
        }
        layoutPopupView.setOnClickListener { layoutPopupView.etPopupComment.requestFocus() }
        layoutPopupView.etPopupComment.setSelection(layoutPopupView.etPopupComment.text.length)
    }

    private fun getScreenHeight(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.heightPixels
    }

    private fun showDatePickerDialog() {
        val currentDate = presenter.currentDate
        calendar.time = presenter.date
        val datePickerDialog = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            calendar.set(year, monthOfYear, dayOfMonth)
            presenter.date = calendar.time

            val calendarWithoutTime: Calendar = Calendar.getInstance()
            calendarWithoutTime.set(year, monthOfYear, dayOfMonth, 0, 0)
            when {
                calendarWithoutTime.time.after(currentDate.time) -> {
                    showTimePickerDialog(-1, 24, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
                }
                calendar.time.after(currentDate.time) -> {
                    showTimePickerDialog(currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE),
                                         calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE))
                }
                else -> showTimePickerDialog(currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE),
                                             currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE))
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.datePicker.minDate = currentDate.timeInMillis
        datePickerDialog.show()
    }

    private fun showTimePickerDialog(minHour: Int, minMinute: Int, setHour: Int, setMinute: Int) {
        /*val timePickerDialog = TimePickerDialog(this, { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            presenter.changeDate(calendar.time)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
        timePickerDialog.show()*/

        val boundTimePickerDialog = BoundTimePickerDialog(this, { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            presenter.changeDate(calendar.time)
        }, setHour, setMinute, true)
        boundTimePickerDialog.setMin(minHour, minMinute)
        boundTimePickerDialog.show()
    }

    override fun setPassengers(count: Int)                   { tvCountPerson.text = count.toString() }
    override fun setChildren(count: Int)                     { tvCountChild.text = count.toString() }
    override fun setCurrency(currency: String)               { tvCurrencyType.text = currency }
    override fun setComment(comment: String)                 { tvComments.text = comment }
    override fun setDateTimeTransfer(dateTimeString: String, isAfter4Hours: Boolean) {
        if(isAfter4Hours) tvDateTimeTransfer.text = getString(R.string.LNG_DATE_IN_HOURS).plus(" ")
                                                    .plus(CreateOrderPresenter.FUTURE_HOUR).plus(" ")
                                                    .plus(getString(R.string.LNG_HOUR_FEW))
        else tvDateTimeTransfer.text = dateTimeString
    }

    private fun checkMinusButton(count: Int, minimum: Int, view: ImageView) {
        val imgRes = if (count == minimum) R.drawable.ic_circle_minus else R.drawable.ic_minus_enabled
        view.setImageDrawable(getDrawable(imgRes))
    }

    override fun setPassengers(count: Int) {
        passengers_seats.person_count.text = count.toString()
        checkMinusButton(count, 1, passengers_seats.img_minus_seat)
    }
    override fun setChildren(count: Int) {
        child_seats.person_count.text = count.toString()
        checkMinusButton(count, 0, child_seats.img_minus_seat)
    }
    override fun setCurrency(currency: String)               { tv_currency.text = currency }
    override fun setComment(comment: String)                 { comment_field.field_input.setText(comment) }
    override fun setDateTimeTransfer(dateTimeString: String) { transfer_date_time_field.field_input.setText(dateTimeString) }

    override fun setTransportTypes(transportTypes: List<TransportTypeModel>) {
        rvTransferType.adapter = TransferTypeAdapter(transportTypes) { transportType, showInfo ->
            presenter.onTransportChosen()
            if(showInfo) transportTypeClicked(transportType)
        }
    }

    override fun setFairPrice(price: String?, time: String?) {
        if (price == null || time == null) {
            tvRate.text = ""
        } else tvRate.text = String.format(getString(R.string.LNG_RIDE_FAIR_PRICE_FORMAT), price, time)
    }

    override fun setUser(user: UserModel, isLoggedIn: Boolean) {
        user_name_field.field_input.setText(user.profile.name ?: "")
        if(user.profile.phone != null) user_name_field.field_input.setText(user.profile.phone)
        else {
            val phoneCode = Utils.getPhoneCodeByCountryIso(this)
            if(phoneCode > 0) user_name_field.field_input.setText("+".plus(phoneCode))
            else user_name_field.field_input.setText("+")
        }
        email_field.field_input.setText(user.profile.email ?: "")
        if(isLoggedIn) email_field.field_input.isEnabled = false
        switchAgreement.isChecked = user.termsAccepted
    }

    //TODO сделать подсветку не заполненных полей
    override fun setGetTransferEnabled(enabled: Boolean) {}

    override fun setRoute(isDateChanged: Boolean, polyline: PolylineModel, routeModel: RouteModel) {
        if(isDateChanged) clearMarkersAndPolylines()
        setPolyline(polyline, routeModel)
    }

    override fun centerRoute(cameraUpdate: CameraUpdate) { showTrack(cameraUpdate) }

    override fun setPromoResult(discountInfo: String?) {
        val colorRes: Int
        val text: String
        val visibility: Int
        if(discountInfo != null) {
            colorRes = R.color.promo_valid
            text = discountInfo
            visibility = View.VISIBLE
        } else {
            colorRes = R.color.color_error
            text = getString(R.string.LNG_RIDE_PROMOCODE_INVALID)
            visibility = View.INVISIBLE
        }
        promo_field.field_title.setTextColor(ContextCompat.getColor(this, colorRes))
        promo_field.field_title.text = text
        img_okResult.visibility = visibility
    }

    override fun resetPromoView() {
        promo_field.field_title.text = defaultPromoText
        promo_field.field_title.setTextColor(ContextCompat.getColor(this, R.color.colorTextLightGray))
        img_okResult.visibility = View.INVISIBLE
    }

    override fun showEmptyFieldError(invalidField: String) {
        var messageRes = R.string.LNG_RIDE_CANT_CREATE
        when (invalidField) {
            CreateOrderPresenter.EMAIL_FIELD -> messageRes = R.string.LNG_ERROR_EMAIL
            CreateOrderPresenter.NAME_FIELD -> messageRes = R.string.LNG_RIDE_NAME
            CreateOrderPresenter.PHONE_FIELD -> messageRes = R.string.LNG_RIDE_PHONE
            CreateOrderPresenter.TRANSPORT_FIELD -> messageRes = R.string.LNG_RIDE_CHOOSE_TRANSPORT
            CreateOrderPresenter.TERMS_ACCEPTED_FIELD -> messageRes = R.string.LNG_RIDE_OFFERT_ERROR
        }

        Utils.showEmptyFieldsForTransferRequest(this, getString(messageRes))
    }

    private fun transportTypeClicked(transportType: TransportTypeModel) {
        sheetTransport.visibility = View.VISIBLE
        bsTransport.state = BottomSheetBehavior.STATE_EXPANDED
        showTransportInfo(transportType)
        presenter.logEventMain(CreateOrderPresenter.CAR_INFO_CLICKED)
    }

    private fun showTransportInfo(transportType: TransportTypeModel) {
        tvTypeTransfer.setText(transportType.nameId!!)
        ivTypeTransfer.setImageResource(transportType.imageId!!)
        tvPrice.text = transportType.price?.min
        tvCountPassengers.text = transportType.paxMax.toString()
        tvCountLuggage.text = transportType.luggageMax.toString()
    }

    @CallSuper
    override fun onBackPressed() {
        if(bsTransport.state == BottomSheetBehavior.STATE_EXPANDED) hideSheetTransport()
        else if(bsOrder.state == BottomSheetBehavior.STATE_EXPANDED) toggleSheetOrder()
        else super.onBackPressed()
    }

    private fun applyDim(parent: ViewGroup, dimAmount: Float) {
        val dim = ColorDrawable(Color.BLACK)
        dim.setBounds(0, 0, parent.width, parent.height)
        dim.alpha = (dimAmount * 255).toInt()

        val overLay = parent.overlay
        overLay.add(dim)
    }

    private fun clearDim(parent: ViewGroup) {
        val overLay = parent.overlay
        overLay.clear()
    }

    //TODO create custom view for new bottom sheet
    private fun initFieldsViews() {

        /* icons */
        transfer_date_time_field.field_icon.setImageDrawable(getDrawable(R.drawable.ic_calendar_triangle))
        user_name_field.field_icon.setImageDrawable(getDrawable(R.drawable.ic_passport))
        email_field.field_icon.setImageDrawable(getDrawable(R.drawable.ic_mail))
        phone_field.field_icon.setImageDrawable(getDrawable(R.drawable.ic_phone))
        flight_number_field.field_icon.setImageDrawable(getDrawable(R.drawable.ic_flight))
        promo_field.field_icon.setImageDrawable(getDrawable(R.drawable.sale_icon))
        comment_field.field_icon.setImageDrawable(getDrawable(R.drawable.ic_comment))

        passengers_seats.seat_icon.setImageDrawable(getDrawable(R.drawable.ic_passenger_small))
        child_seats.seat_icon.setImageDrawable(getDrawable(R.drawable.ic_child_seat))

        /* titles */
        price_field_title.text = getString(R.string.LNG_RIDE_PRICE)
        transfer_date_time_field.field_title.text = getString(R.string.LNG_RIDE_DATE)
        user_name_field.field_title.text = getString(R.string.LNG_RIDE_NAME_PLACEHOLDER)
        email_field.field_title.text = getString(R.string.LNG_RIDE_EMAIL_PLACEHOLDER)
        phone_field.field_title.text = getString(R.string.LNG_RIDE_PHONE_PLACEHOLDER)
        flight_number_field.field_title.text = getString(R.string.LNG_RIDE_FLIGHT_PLACEHOLDER)
        promo_field.field_title.text = getString(R.string.LNG_RIDE_PROMOCODE_PLACEHOLDER)
        comment_field.field_title.text = getString(R.string.LNG_RIDE_COMMENT)
        passengers_seats.seat_title.text = getString(R.string.LNG_RIDE_PASSENGERS)
        child_seats.seat_title.text = getString(R.string.LNG_RIDE_CHILDREN)

        /* editable fields */
        price_field_input.hint = getString(R.string.LNG_RIDE_PRICE_YOUR)
        price_field_input.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
 //       transfer_date_time_field.field_input.hint = getString(R.string)
        user_name_field.field_input.hint = getString(R.string.LNG_RIDE_NAME)
        email_field.field_input.hint = getString(R.string.LNG_RIDE_EMAIL)
        phone_field.field_input.hint = getString(R.string.LNG_RIDE_PHONE)
        phone_field.field_input.inputType = InputType.TYPE_CLASS_PHONE
        flight_number_field.field_input.hint = getString(R.string.LNG_RIDE_FLIGHT)
        promo_field.field_input.hint = getString(R.string.LNG_RIDE_PROMOCODE)
        comment_field.field_input.hint = getString(R.string.LNG_COMMENT_PLACEHOLDER)

        passengers_seats.person_count.text = getString(R.string.passenger_number_default)
        child_seats.person_count.text = getString(R.string.child_number_default)

        comment_field.field_input.isFocusable = false
        comment_field.field_input.setOnClickListener { showPopupWindowComment() }
    }

    private fun initChangeTextListeners() {
        price_field_input.onTextChanged             { presenter.cost = it.toDoubleOrNull() }
        price_field_input.setOnFocusChangeListener  { _, hasFocus ->
            if(hasFocus) presenter.logTransferSettingsEvent(CreateOrderPresenter.OFFER_PRICE_FOCUSED)
        }
        user_name_field.field_input.onTextChanged        { presenter.setName(it.trim()) }
        email_field.field_input.onTextChanged            { presenter.setEmail(it.trim()) }
        phone_field.field_input.onTextChanged            {
            if(it.isEmpty()) {
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
        transfer_date_time_field.setOnClickListener {
            showDatePickerDialog()
            presenter.logTransferSettingsEvent(CreateOrderPresenter.DATE_TIME_CHANGED)
        }
        passengers_seats.img_plus_seat.setOnClickListener   { presenter.changePassengers(1) }
        passengers_seats.img_minus_seat.setOnClickListener  { presenter.changePassengers(-1) }
        child_seats.img_minus_seat.setOnClickListener       { presenter.changeChildren(-1) }
        child_seats.img_plus_seat.setOnClickListener        { presenter.changeChildren(1) }

        cl_offer_price.setOnClickListener                   { fieldTouched(price_field_input)  }
        transfer_date_time_field.setOnClickListener         { fieldTouched(transfer_date_time_field.field_input)  }
        user_name_field.setOnClickListener                  { fieldTouched(user_name_field.field_input) }
        email_field.setOnClickListener                      { fieldTouched(email_field.field_input) }
        phone_field.setOnClickListener                      { fieldTouched(phone_field.field_input)}
        flight_number_field.setOnClickListener              { fieldTouched(flight_number_field.field_input) }
        promo_field.setOnClickListener                      { fieldTouched(promo_field.field_input) }
        comment_field.setOnClickListener                    { showPopupWindowComment()
            presenter.logTransferSettingsEvent(CreateOrderPresenter.COMMENT_INPUT)
        }

        tvAgreement1.setOnClickListener                     { presenter.showLicenceAgreement() }
        switchAgreement.setOnCheckedChangeListener          { _, isChecked -> presenter.setAgreeLicence(isChecked) }

        btnGetOffers.setOnClickListener                     { presenter.onGetTransferClick() }
        btnCenterRoute.setOnClickListener                   { presenter.onCenterRouteClick() }
        btnBack.setOnClickListener                          { presenter.onBackClick() }
        btnOk.setOnClickListener                            { hideSheetTransport() }
    }

    private fun fieldTouched(viewForFocus: EditText) {
        if(!isKeyBoardOpened) showKeyboard()
        viewForFocus.requestFocus()
    }
}
