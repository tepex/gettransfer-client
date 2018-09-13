package com.kg.gettransfer.presentation.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.PolyUtil
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.model.RouteInfo
import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.presenter.TransferDetailsPresenter
import com.kg.gettransfer.presentation.view.TransferDetailsView
import kotlinx.android.synthetic.main.activity_transfer_details.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_maps_pin.view.*
import kotlinx.android.synthetic.main.view_transfer_request_info.view.*
import kotlinx.android.synthetic.main.view_transport_type_transfer_details.view.*
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import timber.log.Timber
import kotlin.coroutines.experimental.suspendCoroutine

class TransferDetailsActivity: BaseActivity(), TransferDetailsView {

    @InjectPresenter
    internal lateinit var presenter: TransferDetailsPresenter

    private val compositeDisposable = Job()
    private val utils = AsyncUtils(coroutineContexts, compositeDisposable)

    private lateinit var googleMap: GoogleMap

    @ProvidePresenter
    fun createTransferDetailsPresenter(): TransferDetailsPresenter = TransferDetailsPresenter(coroutineContexts, router, apiInteractor)

    protected override var navigator = object: BaseNavigator(this){}

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun getPresenter(): TransferDetailsPresenter = presenter
    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_transfer_details)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }
        (toolbar as Toolbar).toolbar_title.text = resources.getString(R.string.activity_transfer_details_title)

        layoutTransferInfo.ivChevron.visibility = View.GONE

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
        mapView.onResume()
    }

    @CallSuper
    protected override fun onPause() {
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

        utils.launch {
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
        transparentImage.setOnTouchListener(View.OnTouchListener { _, motionEvent ->
            when (motionEvent.action) {
                MotionEvent.ACTION_DOWN -> {
                    svTransferDetails.requestDisallowInterceptTouchEvent(true)
                    return@OnTouchListener false
                }
                MotionEvent.ACTION_UP -> {
                    svTransferDetails.requestDisallowInterceptTouchEvent(false)
                    return@OnTouchListener true
                }
                MotionEvent.ACTION_MOVE -> {
                    svTransferDetails.requestDisallowInterceptTouchEvent(true)
                    return@OnTouchListener false
                }
            }
            return@OnTouchListener true
        })
    }

    override fun setTransferInfo(transferId: Long, from: String, to: String, dateTimeString: String, distance: String) {
        layoutTransferInfo.tvTransferRequestNumber.text = String.format(resources.getString(R.string.transfer_request_num), transferId)
        layoutTransferInfo.tvFrom.text = from
        layoutTransferInfo.tvTo.text = to
        layoutTransferInfo.tvOrderDateTime.text = dateTimeString
        layoutTransferInfo.tvDistance.text = distance
    }

    override fun setPassengerInfo(countPassengers: Int, personName: String?, countChilds: Int?, flightNumber: String?, comment: String?, transportTypes: List<TransportTypeModel>) {
        tvCountPassengers.text = countPassengers.toString()

        if (personName != null && !personName.isEmpty()){
            tvPassengerName.text = personName
            layoutName.visibility = View.VISIBLE
        }

        if (countChilds != null && countChilds > 0){
            tvCountChilds.text = countChilds.toString()
            layoutChilds.visibility = View.VISIBLE
        }

        if (flightNumber != null && !flightNumber.isEmpty()){
            tvFlightNumber.text = flightNumber
            layoutFlightNumber.visibility = View.VISIBLE
        }

        if (comment != null && !comment.isEmpty()){
            tvComment.text = comment
            layoutComment.visibility = View.VISIBLE
        }

        transportTypes.forEach {
            var viewTransportType = layoutInflater.inflate(R.layout.view_transport_type_transfer_details, null, false)
            viewTransportType.tvNameTransportType.text = it.delegate.id.substring(0, 1).toUpperCase() + it.delegate.id.substring(1) + " "
            viewTransportType.tvCountPersons.text = String.format(getString(R.string.count_persons_and_baggage), it.delegate.paxMax)
            viewTransportType.tvCountBaggage.text = String.format(getString(R.string.count_persons_and_baggage), it.delegate.luggageMax)
            layoutTransportTypesList.addView(viewTransportType)
        }
    }

    override fun activateButtonCancel() {
        btnCancel.visibility = View.VISIBLE
    }

    override fun setPaymentInfo(price: String, paidSum: String, paidPercentage: Int, remainToPay: String) {
        paymentInfoPaid.text = String.format(getString(R.string.activity_transfer_details_paid_sum), paidSum, paidPercentage)
        paymentInfoPay.text = remainToPay
        paymentInfoSum.text = price
        layoutPaymentInfo.visibility = View.VISIBLE
    }

    override fun setOfferInfo(driverEmail: String, driverPhone: String, driverName: String,
                              transportType: String, transportName: String, transportNumber: String,
                              price: String) {
        offerDriverInfoEmail.text = driverEmail
        offerDriverInfoPhone.text = driverPhone
        offerDriverInfoName.text = driverName
        layoutOfferDriverInfo.visibility = View.VISIBLE

        offerTransportInfoCarType.text = transportType
        offerTransportInfoCarName.text = transportName
        offerTransportInfoCarNumber.text = transportNumber
        offerTransportInfoPrice.text = price
        layoutOfferTransportInfo.visibility = View.VISIBLE
    }

    override fun setMapInfo(routeInfo: RouteInfo, from: String, to: String, dateTimeString: String, distance: String) {
        //Создание пинов с информацией
        val ltInflater = layoutInflater
        val pinLayout = ltInflater.inflate(R.layout.view_maps_pin, null)

        pinLayout.tvPlace.text = from
        pinLayout.tvInfo.text = dateTimeString
        pinLayout.tvPlaceMirror.text = from
        pinLayout.tvInfoMirror.text = dateTimeString
        pinLayout.imgPin.setImageResource(R.drawable.map_label_a)
        val bmPinA = createBitmapFromView(pinLayout)

        pinLayout.tvPlace.text = to
        pinLayout.tvInfo.text = distance
        pinLayout.tvPlaceMirror.text = to
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

        val line = PolylineOptions().width(10f).color(ContextCompat.getColor(this, R.color.colorPolyline))

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
        try { googleMap.moveCamera(track) }
        catch(e: Exception) { Timber.e(e) }
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

    override fun onBackPressed() {
        presenter.onBackCommandClick()
    }

    override fun blockInterface(block: Boolean) {
    }

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        Timber.e(Exception(args[0]))
        Utils.showError(this, finish, getString(errId, *args))
    }
}