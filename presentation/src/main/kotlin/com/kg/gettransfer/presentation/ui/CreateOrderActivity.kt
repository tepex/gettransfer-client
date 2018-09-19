package com.kg.gettransfer.presentation.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog

import android.content.Context
import android.content.Intent

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.app.AppCompatDelegate
import android.support.annotation.StringRes

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar

import android.text.InputType
import android.util.DisplayMetrics

import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo

import android.widget.TextView

import android.widget.LinearLayout
import android.widget.PopupWindow

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MapStyleOptions
import com.kg.gettransfer.R
import com.kg.gettransfer.R.id.*

import com.kg.gettransfer.domain.AsyncUtils

import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.domain.model.Account

import com.kg.gettransfer.extensions.hideKeyboard

import com.kg.gettransfer.presentation.Screens

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransportTypeModel

import com.kg.gettransfer.presentation.adapter.TransferTypeAdapter
import com.kg.gettransfer.presentation.presenter.CreateOrderPresenter

import com.kg.gettransfer.presentation.view.CreateOrderView
import kotlinx.android.synthetic.main.activity_create_order.*
import kotlinx.android.synthetic.main.bottom_sheet_create_order.*

import java.util.Calendar

import kotlinx.android.synthetic.main.layout_popup_comment.*
import kotlinx.android.synthetic.main.layout_popup_comment.view.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch

import org.koin.android.ext.android.inject
import java.util.*
import kotlin.coroutines.experimental.suspendCoroutine

class CreateOrderActivity: BaseGoogleMapActivity(), CreateOrderView {
    @InjectPresenter
    internal lateinit var presenter: CreateOrderPresenter

    private val routeInteractor: RouteInteractor by inject()
	private val transferInteractor: TransferInteractor by inject()
    private val calendar = Calendar.getInstance()
    private lateinit var sheetBehavior: BottomSheetBehavior<View>

    @ProvidePresenter
    fun createCreateOrderPresenter(): CreateOrderPresenter = CreateOrderPresenter(coroutineContexts,
                                                                                  router,
                                                                                  systemInteractor,
                                                                                  routeInteractor,
                                                                                  transferInteractor)

    protected override var navigator = object: BaseNavigator(this) {
        @CallSuper
        protected override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
            val intent = super.createActivityIntent(context, screenKey, data)
            if(intent != null) return intent
                
            when(screenKey) {
                Screens.LICENCE_AGREE -> return Intent(context, LicenceAgreementActivity::class.java)
                Screens.OFFERS -> return Intent(context, OffersActivity::class.java)
            }
            return null
        }
    }

    override fun getPresenter(): CreateOrderPresenter = presenter

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_create_order)
		_mapView = mapView
		initGoogleMap(savedInstanceState)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }

        rvTransferType.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        etYourPrice.onTextChanged { presenter.cost = it.toIntOrNull() }
        tvDateTimeTransfer.setOnClickListener { showDatePickerDialog() }

        ivPersonsCounterDown.setOnClickListener { presenter.changePassengers(-1) }
        ivPersonsCounterUp.setOnClickListener { presenter.changePassengers(1) }

        tvName.onTextChanged { presenter.setName(it.trim()) }
        etEmail.onTextChanged { presenter.setEmail(it.trim()) }
        tvPhone.onTextChanged { presenter.setPhone(it.trim()) }
        ivChildCounterDown.setOnClickListener { presenter.changeChildren(-1) }
        ivChildCounterUp.setOnClickListener { presenter.changeChildren(1) }
        tvFlightOrTrainNumber.onTextChanged { presenter.setFlightNumber(it.trim()) }

        tvComments.setOnClickListener { showPopupWindowComment() }
        layoutAgreement.setOnClickListener { presenter.showLicenceAgreement() }
        cbAgreement.setOnClickListener { presenter.setAgreeLicence(cbAgreement.isChecked()) }

        btnGetOffers.setOnClickListener { presenter.onGetTransferClick() }

        val mapViewBundle = savedInstanceState?.getBundle(MainActivity.MAP_VIEW_BUNDLE_KEY)
        initGoogleMap(mapViewBundle)

        sheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheet.setOnClickListener { toggleBottomSheet() }
    }

    private fun toggleBottomSheet() {
        if (sheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    protected override fun customizeGoogleMaps() {
        super.customizeGoogleMaps()
        googleMap.uiSettings.setRotateGesturesEnabled(false)
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
    }

    override fun setCurrencies(currencies: List<CurrencyModel>) {
        Utils.setCurrenciesDialogListener(this, ivChangeCurrency, currencies) {
            selected -> presenter.changeCurrency(selected) 
        }
    }
    
    private fun showPopupWindowComment(){
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels

        val layoutPopup = LayoutInflater.from(applicationContext).inflate(R.layout.layout_popup_comment, layoutPopup)
        val popupWindowComment = PopupWindow(layoutPopup, LinearLayout.LayoutParams.MATCH_PARENT, screenHeight / 3, true)
        layoutPopup.etPopupComment.setText(tvComments.text)
        layoutPopup.etPopupComment.setRawInputType(InputType.TYPE_CLASS_TEXT)
        popupWindowComment.showAtLocation(mainLayoutActivityTransfer, Gravity.CENTER, 0, 0)
        layoutShadow.visibility = View.VISIBLE

        layoutPopup.btnClearPopupComment.setOnClickListener { layoutPopup.etPopupComment.setText("") }
        layoutPopup.etPopupComment.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                presenter.setComment(layoutPopup.etPopupComment.text.toString().trim())
                popupWindowComment.dismiss()
                return@OnEditorActionListener true
            }
            false
        })
        popupWindowComment.setOnDismissListener {
			val view = currentFocus
			view?.hideKeyboard()
			view?.clearFocus()
            layoutShadow.visibility = View.GONE
        }
        layoutPopup.setOnClickListener{ layoutPopup.etPopupComment.requestFocus()}
        layoutPopup.etPopupComment.setSelection(layoutPopup.etPopupComment.text.length)
    }

    private fun showDatePickerDialog() {
        calendar.setTime(presenter.date)
        val datePickerDialog = DatePickerDialog(this, { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                presenter.date = calendar.getTime()
                showTimePickerDialog()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
        
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(this, { _, hour, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                presenter.date = calendar.getTime()
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true)
        timePickerDialog.show()
    }

    override fun setPassengers(count: Int) {
        tvCountPerson.text = count.toString()
    }

    override fun setChildren(count: Int) {
        tvCountChild.text = count.toString()
    }
    
    override fun setCurrency(currency: String) { tvCurrencyType.text = currency }

    override fun setDateTimeTransfer(dateTimeString: String) {
        tvDateTimeTransfer.text = dateTimeString
    }

    override fun setComment(comment: String) {
        tvComments.text = comment
    }

    override fun setTransportTypes(transportTypes: List<TransportTypeModel>) {
        rvTransferType.adapter = TransferTypeAdapter(transportTypes, { presenter.checkFields() })
    }
    
    override fun setAccount(account: Account) {
        tvName.setText(account.fullName ?: "")
        tvPhone.setText(account.phone ?: "")
        if(account.loggedIn) {
            etEmail.setText(account.email)
            etEmail.isEnabled = false
            tvEmail.visibility = View.GONE
        }
    }
    
    override fun setGetTransferEnabled(enabled: Boolean) {

    }

    override fun setRoute(routeModel: RouteModel) {
    	Utils.setPins(this, googleMap, routeModel)
    }
}
