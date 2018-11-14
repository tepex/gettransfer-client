package com.kg.gettransfer.presentation.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog

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
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.AnimationUtils

import android.view.inputmethod.EditorInfo

import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView

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
import kotlinx.android.synthetic.main.bottom_sheet_create_order.*
import kotlinx.android.synthetic.main.bottom_sheet_type_transport.*
import kotlinx.android.synthetic.main.layout_popup_comment.*
import kotlinx.android.synthetic.main.layout_popup_comment.view.*

import com.kg.gettransfer.extensions.hideKeyboard
import com.kg.gettransfer.extensions.showKeyboard

import com.kg.gettransfer.presentation.IntentKeys

import kotlinx.android.synthetic.main.amu_info_window.view.*

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
                Screens.LICENCE_AGREE -> return Intent(context, WebPageActivity()::class.java)
                        .putExtra(WebPageActivity.SCREEN, WebPageActivity.SCREEN_LICENSE)
                Screens.OFFERS -> return Intent(context, OffersActivity::class.java)
                Screens.PASSENGER_MODE -> return Intent(context, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                Screens.LOGIN -> return Intent(context, LoginActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                        .putExtra(IntentKeys.SCREEN_FOR_RETURN, Screens.OFFERS )
                        .putExtra(IntentKeys.EMAIL_TO_LOGIN, data as String)
            }
            return null
        }
    }

    override fun getPresenter(): CreateOrderPresenter = presenter

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_order)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = Color.WHITE
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else{
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            viewGradient.visibility = View.GONE
        }

        _mapView = mapView
        initMapView(savedInstanceState)

        rvTransferType.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvTransferType.isNestedScrollingEnabled = false

        etYourPrice.onTextChanged { presenter.cost = it.toDoubleOrNull() }
        etYourPrice.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus) presenter.logTransferSettingsEvent(CreateOrderPresenter.OFFER_PRICE_FOCUSED)
        }
        tvDateTimeTransfer.setOnClickListener {
            showDatePickerDialog()
            presenter.logTransferSettingsEvent(CreateOrderPresenter.DATE_TIME_CHANGED)
        }

        ivPersonsCounterDown.setOnClickListener { presenter.changePassengers(-1) }
        ivPersonsCounterUp.setOnClickListener   { presenter.changePassengers(1) }

        etName.onTextChanged  { presenter.setName(it.trim()) }
        etEmail.onTextChanged { presenter.setEmail(it.trim()) }
        tvPhone.onTextChanged {
            if(it.isEmpty()) {
                tvPhone.setText("+")
                tvPhone.setSelection(1)
            }
            presenter.setPhone("+".plus(it.replace(Regex("\\D"), "")))
        }
        val phoneCode = Utils.getPhoneCodeByCountryIso(this)
        if(phoneCode > 0) tvPhone.setText("+".plus(phoneCode))
        tvPhone.addTextChangedListener(PhoneNumberFormattingTextWatcher())
        
        ivChildCounterDown.setOnClickListener { presenter.changeChildren(-1) }
        ivChildCounterUp.setOnClickListener   { presenter.changeChildren(1) }
        tvFlightOrTrainNumber.onTextChanged   { presenter.setFlightNumber(it.trim()) }

        initPromoSection()
        initKeyBoardListener()

        tvComments.setOnClickListener {
            showPopupWindowComment()
 //           toggleSheetOrder(false)
            presenter.logTransferSettingsEvent(CreateOrderPresenter.COMMENT_INPUT)
        }
        tvAgreement1.setOnClickListener { presenter.showLicenceAgreement() }
        switchAgreement.setOnCheckedChangeListener { _, isChecked -> presenter.setAgreeLicence(isChecked) }

        btnGetOffers.setOnClickListener   { presenter.onGetTransferClick() }
        btnCenterRoute.setOnClickListener { presenter.onCenterRouteClick() }
        btnBack.setOnClickListener { presenter.onBackClick() }

        bsOrder = BottomSheetBehavior.from(sheetOrder)
        sheetOrder.visibility = View.VISIBLE
        bsTransport = BottomSheetBehavior.from(sheetTransport)
        bsTransport.state = BottomSheetBehavior.STATE_HIDDEN
        btnOk.setOnClickListener { hideSheetTransport() }
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
        etPromo.filters = arrayOf(InputFilter.AllCaps())
        etPromo.onTextChanged { presenter.setPromo(etPromo.text.toString()) }
        etPromo.setOnFocusChangeListener { _, hasFocus -> if(!hasFocus) presenter.checkPromoCode()}
        defaultPromoText = tvPromoResult.text.toString()
        constraintLayout_promo.setOnClickListener { showKeyboard(); etPromo.requestFocusFromTouch()}
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
                if(etPromo.isFocused) presenter.checkPromoCode()
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
        Utils.setCurrenciesDialogListener(this, layoutCurrencyType, currencies) { selected ->
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
        layoutPopupView.etPopupComment.setText(tvComments.text)
        layoutPopupView.etPopupComment.setRawInputType(InputType.TYPE_CLASS_TEXT)


        layoutPopupView.etPopupComment.popupWindow = popupWindowComment
        layoutPopupView.etPopupComment.showKeyboard()

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
        calendar.time = presenter.date
        val datePickerDialog = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
            calendar.set(year, monthOfYear, dayOfMonth)
            presenter.date = calendar.time
            showTimePickerDialog()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(this, { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            presenter.changeDate(calendar.time)
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
        timePickerDialog.show()
    }

    override fun setPassengers(count: Int)                   { tvCountPerson.text = count.toString() }
    override fun setChildren(count: Int)                     { tvCountChild.text = count.toString() }
    override fun setCurrency(currency: String)               { tvCurrencyType.text = currency }
    override fun setComment(comment: String)                 { tvComments.text = comment }
    override fun setDateTimeTransfer(dateTimeString: String) { tvDateTimeTransfer.text = dateTimeString }

    override fun setTransportTypes(transportTypes: List<TransportTypeModel>) {
        rvTransferType.adapter = TransferTypeAdapter(transportTypes) { transportType, showInfo ->
            presenter.onTransportChosen()
            if(showInfo) transportTypeClicked(transportType)
        }
    }

    override fun setFairPrice(price: String?, time: String?) {
        if (price == null || time == null) {
            tvRate.text = ""
        } else
        tvRate.text = String.format(getString(R.string.LNG_RIDE_FAIR_PRICE_FORMAT), price, time)
    }

    override fun setUser(user: UserModel, isLoggedIn: Boolean) {
        etName.setText(user.profile.name ?: "")
        tvPhone.setText(user.profile.phone ?: "")
        etEmail.setText(user.profile.email ?: "")
        if(isLoggedIn) etEmail.isEnabled = false
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
        if(discountInfo != null){
            colorRes = R.color.promo_valid
            text = discountInfo
            visibility = View.VISIBLE
        } else {
            colorRes = R.color.color_error
            text = getString(R.string.LNG_RIDE_PROMOCODE_INVALID)
            visibility = View.INVISIBLE
        }
        tvPromoResult.setTextColor(ContextCompat.getColor(this, colorRes))
        tvPromoResult.text = text
        img_okResult.visibility = visibility
    }

    override fun resetPromoView() {
        tvPromoResult.text = defaultPromoText
        tvPromoResult.setTextColor(ContextCompat.getColor(this, R.color.colorTextLightGray))
        img_okResult.visibility = View.INVISIBLE
    }

    override fun showEmptyFieldError(invalidField: String) {
        var messageRes = R.string.LNG_RIDE_CANT_CREATE
        when (invalidField) {
            CreateOrderPresenter.TRANSPORT_FIELD -> messageRes = R.string.LNG_RIDE_CHOOSE_TRANSPORT
            CreateOrderPresenter.EMAIL_FIELD -> messageRes = R.string.LNG_ERROR_EMAIL
            CreateOrderPresenter.PHONE_FIELD -> messageRes = R.string.LNG_RIDE_PHONE
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

    private fun applyDim(parent: ViewGroup, dimAmount: Float){
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
}
