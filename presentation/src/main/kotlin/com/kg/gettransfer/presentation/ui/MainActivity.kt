package com.kg.gettransfer.presentation.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter

import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.ActivityCompat

import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout

import android.support.v7.app.AppCompatDelegate

import android.transition.Fade

import android.view.Gravity
import android.view.View
import android.view.WindowManager

import android.widget.TextView

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.*

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.presentation.model.PolylineModel

import com.kg.gettransfer.presentation.model.ProfileModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.presenter.MainPresenter
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper
import com.kg.gettransfer.presentation.view.MainView

import java.util.Date

import kotlinx.android.synthetic.main.a_b_view.*
import kotlinx.android.synthetic.main.a_b_view.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nav_item_requests.view.*
import kotlinx.android.synthetic.main.notification_offer.*
import kotlinx.android.synthetic.main.search_address.view.*
import kotlinx.android.synthetic.main.search_form_main.*
import kotlinx.android.synthetic.main.view_hourly_picker.*
import kotlinx.android.synthetic.main.view_last_trip_rate.view.*
import kotlinx.android.synthetic.main.view_navigation.*
import kotlinx.android.synthetic.main.view_rate_dialog.view.*
import kotlinx.android.synthetic.main.view_rate_field.*
import kotlinx.android.synthetic.main.view_rate_in_store.view.*
import kotlinx.android.synthetic.main.view_thanks_for_rate.view.*

import timber.log.Timber

class MainActivity : BaseGoogleMapActivity(), MainView {
    @InjectPresenter
    internal lateinit var presenter: MainPresenter

    private lateinit var drawer: DrawerLayout
    //private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var hourlySheet: BottomSheetBehavior<View>

    private var isFirst = true
    private var centerMarker: Marker? = null
    private var isGmTouchEnabled = true

    private lateinit var map: GoogleMap

    @ProvidePresenter
    fun createMainPresenter() = MainPresenter()

    private val readMoreListener = View.OnClickListener { presenter.readMoreClick() }

    private val itemsNavigationViewListener = View.OnClickListener {
        when (it.id) {
            R.id.navLogin          -> presenter.onLoginClick()
            R.id.navAbout          -> presenter.onAboutClick()
            R.id.navSettings       -> presenter.onSettingsClick()
            R.id.navRequests       -> presenter.onRequestsClick()
            R.id.navBecomeACarrier -> presenter.onBecomeACarrierClick()
            R.id.navHeaderShare    -> presenter.onShareClick()
            else -> Timber.d("No route")
        }
        drawer.closeDrawer(GravityCompat.START)
    }

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun getPresenter(): MainPresenter = presenter

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) window.statusBarColor = Color.TRANSPARENT
        else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            viewGradient.isVisible = false
        }

        _mapView = mapView
        initMapView(savedInstanceState)

        viewNetworkNotAvailable = textNetworkNotAvailable

        btnShowDrawerLayout.setOnClickListener { drawer.openDrawer(Gravity.START) }
        btnBack.setOnClickListener { presenter.onBackClick() }
        ivSelectFieldTo.setOnClickListener { presenter.switchUsedField() }
        drawer = drawerLayout as DrawerLayout
        drawer.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            @CallSuper
            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                if (newState == DrawerLayout.STATE_SETTLING) hideKeyboard()
            }
        })

        presenter.setAddressFields()

        initNavigation()
        initHourly()

        switch_mode.setOnCheckedChangeListener { _, isChecked -> presenter.tripModeSwitched(isChecked) }

        isFirst = savedInstanceState == null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) window.exitTransition = Fade().apply { duration = FADE_DURATION }
    }

    @CallSuper
    protected override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) search_panel.elevation = resources.getDimension(R.dimen.search_elevation)
        searchFrom.setUneditable()
        searchTo.setUneditable()
        searchFrom.setOnClickListener { performClick(false) }
        searchTo.setOnClickListener   { performClick(true) }
        rl_hourly.setOnClickListener  { showNumberPicker(true) }
        btnNext.setOnClickListener    { presenter.onNextClick() }
        enableBtnNext()
    }

    private fun performClick(clickedTo: Boolean) {
        presenter.isClickTo = clickedTo
        processGoogleMap(true) { presenter.onSearchClick(searchFrom.text, searchTo.text, it.projection.visibleRegion.latLngBounds) }
    }

    private fun showNumberPicker(show: Boolean) {
        hourlySheet.state = if (show) BottomSheetBehavior.STATE_COLLAPSED else BottomSheetBehavior.STATE_HIDDEN
        onPickerExpanded(show)
    }

    private fun onPickerExpanded(expanded: Boolean) {
        expanded.let {
            switch_mode.isEnabled    = !it
            search_panel.isClickable = !it
        }
    }

    @CallSuper
    protected override fun onStart() {
        super.onStart()
        hideKeyboard()
    }

    @CallSuper
    override fun onBackPressed() {
        presenter.onBackClick()
    }

    @CallSuper
    protected override fun onStop() {
        enableBtnNext()
        super.onStop()
    }

    /** @see {@link android.support.v7.app.ActionBarDrawerToggle} *//*
    @CallSuper
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    *//** @see {@link android.support.v7.app.ActionBarDrawerToggle} *//*
    override fun onOptionsItemSelected(item: MenuItem) = toggle.onOptionsItemSelected(item)*/

    private fun initNavigation() {
        //val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        val versionName = BuildConfig.VERSION_NAME
        val versionCode = BuildConfig.VERSION_CODE
        (navFooterVersion as TextView).text =
                String.format(getString(R.string.nav_footer_version), versionName, versionCode)
        //navFooterReadMore.text = Html.fromHtml(Utils.convertMarkdownToHtml(getString(R.string.LNG_READMORE)))

        readMoreListener.let {
            navFooterStamp.setOnClickListener   (it)
            navFooterReadMore.setOnClickListener(it)
        }
        itemsNavigationViewListener.let {
            navHeaderShare.setOnClickListener   (it)
            navLogin.setOnClickListener         (it)
            navRequests.setOnClickListener      (it)
            navSettings.setOnClickListener      (it)
            navAbout.setOnClickListener         (it)
            navBecomeACarrier.setOnClickListener(it)
            navPassengerMode.setOnClickListener (it)
        }
    }

    private fun initHourly() {
        hourlySheet = BottomSheetBehavior.from(hourly_sheet)
        hourlySheet.state = BottomSheetBehavior.STATE_HIDDEN
        with(np_hours) {
            displayedValues = HourlyValuesHelper.getHourlyValues(this@MainActivity).toTypedArray()
            minValue = 0
            maxValue = displayedValues.size - 1
            wrapSelectorWheel = false
            tvCurrent_hours.text = displayedValues[0]
            setOnValueChangedListener { _, _, newVal ->
                presenter.tripDurationSelected(HourlyValuesHelper.durationValues[newVal])
                tvCurrent_hours.text = displayedValues[newVal]
            }
        }
        tv_okBtn.setOnClickListener { showNumberPicker(false) }
    }

    protected override suspend fun customizeGoogleMaps(gm: GoogleMap) {
        super.customizeGoogleMaps(gm)
        Timber.d("Permissions: ${systemInteractor.locationPermissionsGranted}")

        map = gm
        if (isPermissionGranted()) return
        btnMyLocation.setOnClickListener  { presenter.updateCurrentLocation() }
        gm.setOnCameraMoveListener        { presenter.onCameraMove(gm.cameraPosition!!.target, true)  }
        gm.setOnCameraIdleListener        { presenter.onCameraIdle(gm.projection.visibleRegion.latLngBounds) }
        gm.setOnCameraMoveStartedListener {
            if (it == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                presenter.enablePinAnimation()
                gm.setOnCameraMoveStartedListener(null)
            }
        }
    }

    private fun isPermissionGranted(): Boolean {
        if (systemInteractor.locationPermissionsGranted == null &&
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                (!check(Manifest.permission.ACCESS_FINE_LOCATION) || !check(Manifest.permission.ACCESS_COARSE_LOCATION))) {
            ActivityCompat.requestPermissions(this, SplashActivity.PERMISSIONS, SplashActivity.PERMISSION_REQUEST)
            return true
        }
        return false
    }

    private fun check(permission: String) =
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode != SplashActivity.PERMISSION_REQUEST) return
        if(grantResults.size == 2 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {

            systemInteractor.locationPermissionsGranted = true
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = false
        } else {
            systemInteractor.locationPermissionsGranted = false
        }
        recreate()
        presenter.updateCurrentLocation()
    }

    /* MainView */
    override fun setMapPoint(point: LatLng, withAnimation: Boolean) {
        val zoom = resources.getInteger(R.integer.map_min_zoom).toFloat()
        processGoogleMap(false) {
            if (centerMarker != null) {
                it.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom))
                moveCenterMarker(point)
            } else {
                /* Грязный хак!!! */
                if (isFirst || it.cameraPosition.zoom <= MAX_INIT_ZOOM) {
                    val zoom1 = resources.getInteger(R.integer.map_min_zoom).toFloat()
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom1))
                    isFirst = false
                    //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, zoom))
                }
                //else googleMap.moveCamera(CameraUpdateFactory.newLatLng(point))
                else {
                    if (withAnimation) it.animateCamera(CameraUpdateFactory.newLatLngZoom(point, zoom))
                    else it.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom))
                }
            }
        }
    }

    override fun setMarkerElevation(up: Boolean, elevation: Float) {
        val px = -1 * Utils.convertDpToPixels(this, elevation)
        mMarker.animate()
                .withStartAction { presenter.isMarkerAnimating = true }
                .withEndAction {
                    presenter.isMarkerAnimating = false
                    if (!up) markerShadow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_position_shadow))
                }
                .setDuration(150L)
                .translationYBy(px)
                .start()

        if (up) markerShadow.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.lifted_marker_shadow))
    }

    override fun moveCenterMarker(point: LatLng) {
        centerMarker?.let { it.setPosition(point) }
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        if (block) searchFrom.text = getString(R.string.search_start)
    }

    override fun blockSelectedField(block: Boolean, field: String) {
        if (block) {
            when (field) {
                MainPresenter.FIELD_FROM -> searchFrom.text = getString(R.string.search_start)
                MainPresenter.FIELD_TO -> searchTo.text = getString(R.string.search_start)
            }
        }
    }

    override fun setError(e: ApiException) {
        searchFrom.text = getString(R.string.search_nothing)
    }

    override fun initSearchForm() {
        searchFrom.sub_title.text = getString(R.string.LNG_FIELD_SOURCE_PICKUP)
        searchTo.sub_title.text   = getString(R.string.LNG_FIELD_DESTINATION)
    }

    override fun setAddressFrom(address: String) {
        searchFrom.text = address
        enableBtnNext()
        icons_container.a_point.setImageDrawable(ContextCompat.getDrawable(
            this,
            if (address.isNotEmpty()) R.drawable.a_point_filled else R.drawable.a_point_empty
        ))
    }

    override fun setAddressTo(address: String) {
        searchTo.text = address
        enableBtnNext()
        icons_container.b_point.setImageDrawable(ContextCompat.getDrawable(
            this,
            if (address.isNotEmpty()) R.drawable.b_point_filled else R.drawable.b_point_empty
        ))
    }

    private fun enableBtnNext() {
        btnNext.isEnabled = searchFrom.text.isNotEmpty() && (searchTo.text.isNotEmpty() || presenter.isHourly())
    }

    override fun setProfile(profile: ProfileModel) {
        profile.apply {
            navHeaderName.isVisible  = isLoggedIn()
            navHeaderEmail.isVisible = isLoggedIn()
            navRequests.isVisible    = isLoggedIn()
            navLogin.isVisible       = !isLoggedIn()
            if(isLoggedIn()){
                navHeaderName.text = name
                navHeaderEmail.text = email
            }
        }
    }

    override fun selectFieldFrom() {
        mMarker.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_map_label_empty))
        switchButtons(false)
        setAlpha(ALPHA_FULL)
        ivSelectFieldTo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pin_default))
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.START)
    }

    override fun setFieldTo() {
        mMarker.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_map_label_b))
        switchButtons(true)
        setAlpha(ALPHA_DISABLED)
        ivSelectFieldTo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_pin_chosen))
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END)
    }

    private fun switchButtons(isBackVisible: Boolean) {
        btnBack.isVisible = isBackVisible
        btnShowDrawerLayout.isVisible = !isBackVisible
    }

    private fun setAlpha(alpha: Float) {
        searchFrom.alpha = alpha
        a_point.alpha    = alpha
    }

    override fun onBackClick() {
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START) else super.onBackPressed()
    }

    override fun showReadMoreDialog() {
        drawer.closeDrawer(GravityCompat.START)
        ReadMoreFragment().show(supportFragmentManager, getString(R.string.tag_read_more))
    }

    override fun changeFields(hourly: Boolean) {
        hourly.let {
            rl_hourly.isVisible    = it
            hourly_point.isVisible = it
            rl_searchForm.isGone   = it
            b_point.isGone         = it
        }

        enableBtnNext()
//        AnimationHelper(this).hourlyAnim(viewOut, imgOut, viewIn, imgIn)
        link_line.isInvisible = hourly
    }

    override fun setTripMode(duration: Int?) {
        duration?.let {
            switch_mode.isChecked = true
            with (HourlyValuesHelper) {
                np_hours.value = durationValues.indexOf(it)
                tvCurrent_hours.text = getValue(it, this@MainActivity)
            }
        }
    }

    override fun openReviewForLastTrip(transferId: Long, date: Date, vehicle: String, color: String, routeModel: RouteModel) {
        val view = showPopUpWindow(R.layout.view_last_trip_rate, contentMain)
        mDisMissAction = {
            _mapView = mapView
            isGmTouchEnabled = true
            initMapView(null)
            view.rate_map.onDestroy()
            mapView.onResume()
            presenter.updateCurrentLocation()
            mDisMissAction = {}
        }

        view.apply {
            tv_transfer_details.setOnClickListener {
                closePopUp()
                presenter.onTransferDetailsClick(transferId)
            }
            tv_close_lastTrip_rate.setOnClickListener { presenter.onReviewCanceled() }
            tv_transfer_number_rate.apply { text = text.toString().plus(" #$transferId") }
            tv_transfer_date_rate.text = SystemUtils.formatDateTime(date)
            tv_vehicle_model_rate.text = vehicle
            rate_bar_last_trip.setOnRatingChangeListener { _, fl ->
                closePopUp()
                presenter.onRateClicked(fl)
            }
            carColor_rate.setImageDrawable(Utils.getVehicleColorFormRes(this@MainActivity, color))
        }
        drawMapForReview(view.rate_map, Utils.getPolyline(routeModel), routeModel)

    }

    private fun drawMapForReview(map: MapView, polyline: PolylineModel, routeModel: RouteModel) {
        _mapView = map
        isGmTouchEnabled = false
        initMapView(null)
        setPolyline(polyline, routeModel)
        mapView.onPause()
        map.onResume()
    }

    override fun showDetailedReview(tappedRate: Float) {
        val popUpView = showPopUpWindow(R.layout.view_rate_dialog, contentMain)
        popUpView.tvCancelRate.setOnClickListener { presenter.onReviewCanceled() }
        popUpView.send_feedBack.setOnClickListener {
            closePopUp()
            presenter.sendReview(Utils.createListOfDetailedRates(popUpView), popUpView.et_reviewComment.text.toString())
        }
        setupDetailRatings(tappedRate, popUpView)
    }

    private fun setupDetailRatings(rateForFill: Float, view: View) {
        with(view) {
            main_rate.rating                 = rateForFill
            driver_rate.rate_bar.rating      = rateForFill
            punctuality_rate.rate_bar.rating = rateForFill
            vehicle_rate.rate_bar.rating     = rateForFill
        }
    }

    override fun askRateInPlayMarket() {
        showPopUpWindow(R.layout.view_rate_in_store, contentMain).apply {
            tv_agree_store.setOnClickListener  { presenter.onRateInStore() }
            tv_reject_store.setOnClickListener { closePopUp() }
        }
    }

    override fun thanksForRate() {
        showPopUpWindow(R.layout.view_thanks_for_rate, contentMain).apply {
            tv_thanks_close.setOnClickListener { closePopUp() }
        }
    }

    override fun showRateInPlayMarket() = redirectToPlayMarket()

    override fun cancelReview() = closePopUp()

    override fun showBadge(show: Boolean) {
        if (show) {
            tvEventsCount.isVisible = true
            navRequests.tvEventsCount.isVisible = true
        } else {
            tvEventsCount.isVisible = false
            navRequests.tvEventsCount.isVisible = false
        }
    }

    override fun setCountEvents(count: Int) {
        tvEventsCount.text = count.toString()
        navRequests.tvEventsCount.text = count.toString()
    }

    companion object {
        const val MY_LOCATION_BUTTON_INDEX = 2
        const val COMPASS_BUTTON_INDEX     = 5
        const val FADE_DURATION = 500L
        const val MAX_INIT_ZOOM = 2.0f

        const val ALPHA_FULL     = 1f
        const val ALPHA_DISABLED = 0.3f
    }
}
