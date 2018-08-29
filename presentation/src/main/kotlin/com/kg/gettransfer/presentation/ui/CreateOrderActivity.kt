package com.kg.gettransfer.presentation.ui

import android.app.DatePickerDialog
import android.app.TimePickerDialog

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color

import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v4.app.Fragment

import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar

import android.widget.TextView

import android.text.InputType
import android.util.DisplayMetrics
import android.view.*

import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager

import android.widget.LinearLayout
import android.widget.PopupWindow

import android.widget.RelativeLayout

import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.domain.interactor.AddressInteractor
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.domain.model.TransportTypePrice

import com.kg.gettransfer.presentation.Screens

import com.kg.gettransfer.presentation.model.CurrencyModel
import com.kg.gettransfer.presentation.model.TransportTypeModel

import com.kg.gettransfer.presentation.presenter.CreateOrderPresenter

import com.kg.gettransfer.presentation.view.CreateOrderView

import kotlinx.android.synthetic.main.activity_transfer.*
import kotlinx.android.synthetic.main.layout_popup_comment.*
import kotlinx.android.synthetic.main.layout_popup_comment.view.*
import kotlinx.android.synthetic.main.view_maps_pin.view.*
import kotlinx.coroutines.experimental.Job

import org.koin.android.ext.android.inject

import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportAppNavigator

import java.util.Calendar
import kotlin.coroutines.experimental.suspendCoroutine

class CreateOrderActivity: MvpAppCompatActivity(), CreateOrderView {

    @InjectPresenter
    internal lateinit var presenter: CreateOrderPresenter

    private val navigatorHolder: NavigatorHolder by inject()
    private val router: Router by inject()
	private val addressInteractor: AddressInteractor by inject()
	private val apiInteractor: ApiInteractor by inject()
	private val coroutineContexts: CoroutineContexts by inject()

    private val compositeDisposable = Job()
    private val utils = AsyncUtils(coroutineContexts)
    private lateinit var googleMap: GoogleMap

    var mYear = 0
    var mMonth = 0
    var mDay = 0
    var mHour = 0
    var mMinute = 0
    
    private val clickListenerCounterButtons = View.OnClickListener { view ->
        when (view.id) {
            R.id.tvPersonsCounterDown -> presenter.changeCounter(tvCountPerson, -1)
            R.id.tvPersonsCounterUp -> presenter.changeCounter(tvCountPerson, 1)
            R.id.tvChildCounterDown -> presenter.changeCounter(tvCountChild, -1)
            R.id.tvChildCounterUp -> presenter.changeCounter(tvCountChild, 1)
        }
    }

    @ProvidePresenter
    fun createCreateOrderPresenter(): CreateOrderPresenter = CreateOrderPresenter(resources,
                                                                                  coroutineContexts,
                                                                                  router,
                                                                                  addressInteractor,
                                                                                  apiInteractor)

    private val navigator: Navigator = object: SupportAppNavigator(this, Screens.NOT_USED) {
        protected override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
            when(screenKey) {
                Screens.LICENCE_AGREE -> return Intent(this@CreateOrderActivity, LicenceAgreementActivity::class.java)
            }
            return null
        }
        protected override fun createFragment(screenKey: String, data: Any?): Fragment? = null
    }

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_transfer)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }

        rvTransferType.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        changeDateTime(false)

        setOnClickListeners()

        val mapViewBundle = savedInstanceState?.getBundle(MainActivity.MAP_VIEW_BUNDLE_KEY)
        initGoogleMap(mapViewBundle)
    }

    @CallSuper
    protected override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    @CallSuper
    protected override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
        mapView.onResume()
    }

    @CallSuper
    protected override fun onPause() {
        navigatorHolder.removeNavigator()
        mapView.onPause()
        super.onPause()
    }

    @CallSuper
    protected override fun onStop() {
        mapView.onStop()
        super.onStop()
    }

    @CallSuper
    protected override fun onDestroy() {
        mapView.onDestroy()
        compositeDisposable.cancel()
        super.onDestroy()
    }

    @CallSuper
    override fun onLowMemory() {
        mapView.onLowMemory()
        super.onLowMemory()
    }

    private fun initGoogleMap(mapViewBundle: Bundle?) {
        mapView.onCreate(mapViewBundle)

        utils.launchAsync(compositeDisposable) {
            googleMap = getGoogleMapAsync()
            customizeGoogleMaps()
        }
    }

    private suspend fun getGoogleMapAsync(): GoogleMap = suspendCoroutine { cont ->
        mapView.getMapAsync { cont.resume(it) }
    }

    private fun customizeGoogleMaps() {
        googleMap.uiSettings.setRotateGesturesEnabled(false)
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))

        // https://stackoverflow.com/questions/16974983/google-maps-api-v2-supportmapfragment-inside-scrollview-users-cannot-scroll-th
        transparentImage.setOnTouchListener(View.OnTouchListener { view, motionEvent ->
            when (motionEvent.action){
                MotionEvent.ACTION_DOWN -> {
                    svTransfer.requestDisallowInterceptTouchEvent(true)
                    return@OnTouchListener false
                }
                MotionEvent.ACTION_UP -> {
                    svTransfer.requestDisallowInterceptTouchEvent(false)
                    return@OnTouchListener true
                }
                MotionEvent.ACTION_MOVE -> {
                    svTransfer.requestDisallowInterceptTouchEvent(true)
                    return@OnTouchListener false
                }
            }
            return@OnTouchListener true
        })
    }

    override fun setCurrencies(currencies: List<CurrencyModel>) {
        Utils.setCurrenciesDialogListener(this, btnChangeCurrencyType, currencies) { 
            selected -> presenter.changeCurrency(selected) 
        }
    }
    
    private fun setOnClickListeners() {
        tvPersonsCounterDown.setOnClickListener(clickListenerCounterButtons)
        tvPersonsCounterUp.setOnClickListener(clickListenerCounterButtons)
        tvChildCounterDown.setOnClickListener(clickListenerCounterButtons)
        tvChildCounterUp.setOnClickListener(clickListenerCounterButtons)
        
        layoutDateTimeTransfer.setOnClickListener { changeDateTime(true) }
        tvComments.setOnClickListener { showPopupWindowComment() }
        layoutAgreement.setOnClickListener { presenter.showLicenceAgreement() }
    }

    private fun showPopupWindowComment(){
        val displaymetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displaymetrics)
        val screenHeight = displaymetrics.heightPixels

        val layoutPopup = LayoutInflater.from(applicationContext).inflate(R.layout.layout_popup_comment, layoutPopup)
        val popupWindowComment = PopupWindow(layoutPopup, LinearLayout.LayoutParams.MATCH_PARENT, screenHeight / 3, true)
        layoutPopup.etPopupComment.setText(tvComments.text)
        layoutPopup.etPopupComment.setRawInputType(InputType.TYPE_CLASS_TEXT)
        popupWindowComment.showAtLocation(mainLayoutActivityTransfer, Gravity.CENTER, 0, 0)
        layoutShadow.visibility = View.VISIBLE

        layoutPopup.btnClearPopupComment.setOnClickListener { layoutPopup.etPopupComment.setText("") }
        layoutPopup.etPopupComment.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                presenter.setComment(layoutPopup.etPopupComment.text.toString())
                popupWindowComment.dismiss()
                return@OnEditorActionListener true
            }
            false
        })
        popupWindowComment.setOnDismissListener {
            hideKeyboard()
            layoutShadow.visibility = View.GONE
        }
        layoutPopup.setOnClickListener{ layoutPopup.etPopupComment.requestFocus()}
        layoutPopup.etPopupComment.setSelection(layoutPopup.etPopupComment.text.length)
        showKeyboard()
    }

    private fun changeDateTime(showDialog: Boolean){
        val calendar = Calendar.getInstance()
        mYear = calendar.get(Calendar.YEAR)
        mMonth = calendar.get(Calendar.MONTH)
        mDay = calendar.get(Calendar.DAY_OF_MONTH)
        mHour = calendar.get(Calendar.HOUR_OF_DAY)
        mMinute = calendar.get(Calendar.MINUTE)
        if(showDialog) showDatePickerDialog() else presenter.changeDateTimeTransfer(mYear, mMonth, mDay, mHour, mMinute)
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            run {
                mYear = year
                mMonth = monthOfYear
                mDay = dayOfMonth
                showTimePickerDialog()
            }
        }, mYear, mMonth, mDay)
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(this, TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            run {
                mHour = hour
                mMinute = minute
                presenter.changeDateTimeTransfer(mYear, mMonth, mDay, mHour, mMinute)
            }
        }, mHour, mMinute, true)
        timePickerDialog.show()
    }

    private fun showKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0)
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if(view != null)
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onBackPressed() {
        presenter.onBackCommandClick()
    }

    override fun setCounters(textViewCounter: TextView, count: Int) {
        textViewCounter.text = count.toString()
    }

    override fun setCurrency(currency: String) { tvCurrencyType.text = currency }

    override fun setDateTimeTransfer(dateTimeString: String) {
        tvDateTimeTransfer.text = dateTimeString
        tvOrderDateTime.text = dateTimeString
    }

    override fun setComment(comment: String) {
        tvComments.text = comment
    }

    override fun setTransportTypes(transportTypes: List<TransportTypeModel>, transportTypePrice: List<TransportTypePrice>) {
        rvTransferType.adapter = TransferTypeAdapter(transportTypes, transportTypePrice)
    }
    
    override fun setRoute(route: Pair<GTAddress, GTAddress>) {
    	tvFrom.setText(route.first.name)
    	tvTo.setText(route.second.name)
    }

    override fun setMapInfo(routeInfo: RouteInfo, route: Pair<GTAddress, GTAddress>, distanceUnit: String) {

        val distance = String.format(getString(R.string.distance), routeInfo.distance, distanceUnit)
        tvDistance.text = distance

        //Создание пинов с информацией
        val ltInflater = layoutInflater
        val pinLayout = ltInflater.inflate(R.layout.view_maps_pin, null)

        pinLayout.tvPlace.text = route.first.name
        pinLayout.tvInfo.text = tvDateTimeTransfer.text
        pinLayout.tvPlaceMirror.text = route.first.name
        pinLayout.tvInfoMirror.text = tvDateTimeTransfer.text
        pinLayout.imgPin.setImageResource(R.drawable.map_label_a)
        val bmPinA = createBitmapFromView(pinLayout)

        pinLayout.tvPlace.text = route.second.primary
        pinLayout.tvInfo.text = distance
        pinLayout.tvPlaceMirror.text = route.second.primary
        pinLayout.tvInfoMirror.text = distance
        pinLayout.imgPin.setImageResource(R.drawable.map_label_b)
        val bmPinB = createBitmapFromView(pinLayout)

        //Создание polyline

        // Для построения подробного маршрута
        val mPoints = arrayListOf<LatLng>()
        for(item in routeInfo.polyLines){
            mPoints.addAll(PolyUtil.decode(item))
        }

        // Для построения упрощённого маршрута (меньше точек)
        //val mPoints = PolyUtil.decode(routeInfo.overviewPolyline)

        val line = PolylineOptions().width(10f).color(applicationContext.resources.getColor(R.color.colorPolyline))

        val latLngBuilder = LatLngBounds.Builder()
        for (i in mPoints.indices){
            if(i == 0){
                val startMakerOptions = MarkerOptions()
                        .position(mPoints.get(i))
                        .icon(BitmapDescriptorFactory.fromBitmap(bmPinA))
                googleMap.addMarker(startMakerOptions)
            } else if (i == mPoints.size - 1){
                val endMakerOptions = MarkerOptions()
                        .position(mPoints.get(i))
                        .icon(BitmapDescriptorFactory.fromBitmap(bmPinB))
                googleMap.addMarker(endMakerOptions)
            }
            line.add(mPoints.get(i))
            latLngBuilder.include(mPoints.get(i))
        }
        googleMap.addPolyline(line)
        val sizeWidth = resources.displayMetrics.widthPixels
        val sizeHeight = mapView.height
        val latLngBounds = latLngBuilder.build()
        val track = CameraUpdateFactory.newLatLngBounds(latLngBounds, sizeWidth, sizeHeight, 150)
        googleMap.moveCamera(track)
    }

    fun createBitmapFromView(v: View): Bitmap {
        v.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT)
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        v.layout(0, 0, v.measuredWidth, v.measuredHeight)
        val bitmap = Bitmap.createBitmap(v.measuredWidth,
                v.measuredHeight,
                Bitmap.Config.ARGB_8888)

        val c = Canvas(bitmap)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)
        return bitmap
    }
}
