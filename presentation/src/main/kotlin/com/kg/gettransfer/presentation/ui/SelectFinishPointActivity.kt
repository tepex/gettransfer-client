package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent
import com.kg.gettransfer.R

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v7.app.AppCompatDelegate
import android.view.View
import android.view.WindowManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.kg.gettransfer.domain.interactor.RouteInteractor
import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.presenter.SelectFinishPointPresenter
import com.kg.gettransfer.presentation.view.SelectFinishPointView
import kotlinx.android.synthetic.main.activity_select_finish_point.*
import kotlinx.android.synthetic.main.search_form_main.*
import org.koin.android.ext.android.inject

class SelectFinishPointActivity: BaseGoogleMapActivity(), SelectFinishPointView{
    @InjectPresenter
    internal lateinit var presenter: SelectFinishPointPresenter

    private val routeInteractor: RouteInteractor by inject()

    private var isFirst = true
    private var centerMarker: Marker? = null

    private var fromClick = false
    private var toClick = false

    @ProvidePresenter
    fun createSelectFinishPointPresenter(): SelectFinishPointPresenter = SelectFinishPointPresenter(coroutineContexts,
                                                                                                    router,
                                                                                                    systemInteractor,
                                                                                                    routeInteractor)

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    protected override var navigator = object: BaseNavigator(this) {
        protected override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
            val intent = super.createActivityIntent(context, screenKey, data)
            if (intent != null) return intent

            when (screenKey) {
                Screens.CREATE_ORDER -> return Intent(context, CreateOrderActivity::class.java)
            }
            return null
        }
    }

    override fun getPresenter(): SelectFinishPointPresenter = presenter

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_finish_point)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) window.statusBarColor = Color.TRANSPARENT
        else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            viewGradient.visibility = View.GONE
        }

        _mapView = mapView
        initGoogleMap(savedInstanceState)

        presenter.setAddressFields()

        search.elevation = resources.getDimension(R.dimen.search_elevation)
        searchFrom.setUneditable()
        searchTo.setUneditable()
        searchFrom.setOnClickListener {
            fromClick = true
            toClick = false
            presenter.onSearchClick(Pair(searchFrom.text, searchTo.text))
        }
        searchTo.setOnClickListener   {
            toClick = true
            fromClick = false
            presenter.onSearchClick(Pair(searchFrom.text, searchTo.text))
        }
        btnNext.setOnClickListener { presenter.onNextClick() }
        enableBtnNext()
    }

    @CallSuper
    protected override fun onResume() {
        super.onResume()
        hideKeyboard()
    }

    @CallSuper
    protected override fun onStop() {
        searchTo.text = ""
        enableBtnNext()
        super.onStop()
    }

    protected override fun customizeGoogleMaps() {
        super.customizeGoogleMaps()
        googleMap.setMyLocationEnabled(true)
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        btnMyLocation.setOnClickListener  { presenter.updateCurrentLocation() }
        googleMap.setOnCameraMoveListener { presenter.onCameraMove(googleMap.getCameraPosition()!!.target) }
        googleMap.setOnCameraIdleListener { presenter.onCameraIdle() }
        presenter.initFinishLocation()
    }

    override fun setAddressFrom(address: String) {
        searchFrom.text = address
        enableBtnNext()
    }
    override fun setAddressTo(address: String)   {
        searchTo.text = address
        enableBtnNext()
    }

    override fun moveCenterMarker(point: LatLng) {
        centerMarker?.let { it.setPosition(point) }
    }

    override fun setMapPoint(point: LatLng) {
        val zoom = resources.getInteger(R.integer.map_min_zoom).toFloat()
        if(centerMarker != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom))
            moveCenterMarker(point)
        } else {
            /* Грязный хак!!! */
            if(isFirst || googleMap.cameraPosition.zoom <= MainActivity.MAX_INIT_ZOOM) {
                val zoom1 = resources.getInteger(R.integer.map_min_zoom).toFloat()
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom1))
                isFirst = false
                //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, zoom))
            }
            //else googleMap.moveCamera(CameraUpdateFactory.newLatLng(point))
            else googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, zoom))
        }
    }

    private fun enableBtnNext() {
        btnNext.isEnabled = searchFrom.text.isNotEmpty() && searchTo.text.isNotEmpty()
    }
}