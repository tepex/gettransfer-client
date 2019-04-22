package com.kg.gettransfer.presentation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color

import android.os.Build
import android.os.Bundle
import android.os.Handler

import android.support.annotation.CallSuper
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.FragmentTransaction

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

import com.kg.gettransfer.presentation.model.ProfileModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransferModel
import com.kg.gettransfer.presentation.presenter.MainPresenter
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper
import com.kg.gettransfer.presentation.view.MainRequestView
import com.kg.gettransfer.presentation.view.MainView
import com.kg.gettransfer.presentation.view.MainView.Companion.MAP_SCREEN
import com.kg.gettransfer.presentation.view.MainView.Companion.REQUEST_SCREEN
import com.kg.gettransfer.presentation.view.Screens
import kotlinx.android.synthetic.main.a_b_orange_view.*

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navigation_view_menu_item.view.*
import kotlinx.android.synthetic.main.search_address.view.*
import kotlinx.android.synthetic.main.search_form_main.*
import kotlinx.android.synthetic.main.view_hourly_picker.*
import kotlinx.android.synthetic.main.view_last_trip_rate.view.*
import kotlinx.android.synthetic.main.view_navigation.*
import kotlinx.android.synthetic.main.view_rate_dialog.view.*
import kotlinx.android.synthetic.main.view_rate_field.*
import kotlinx.android.synthetic.main.view_rate_in_store.view.*
import kotlinx.android.synthetic.main.view_switcher.*
import kotlinx.android.synthetic.main.view_thanks_for_rate.view.*
import pub.devrel.easypermissions.EasyPermissions

import timber.log.Timber

class MainActivity : BaseGoogleMapActivity(), MainView {
    @InjectPresenter
    internal lateinit var presenter: MainPresenter
    var requestView: MainRequestView? = null
    set(value) {
        field = value
        value?.let {
            initHourly()
            setRequestView() }
    }
    var screenType = REQUEST_SCREEN
    set(value) {
        field = value
        presenter.screenType = value
    }

    lateinit var drawer: DrawerLayout
    private lateinit var hourlySheet: BottomSheetBehavior<View>

    private var isFirst = true
    private var isPermissionRequested = false
    private var centerMarker: Marker? = null
    private var isGmTouchEnabled = true
    private var nextClicked = false

    @ProvidePresenter
    fun createMainPresenter() = MainPresenter()

    private val readMoreListener = View.OnClickListener { presenter.readMoreClick() }

    private val itemsNavigationViewListener = View.OnClickListener {
        with(presenter) {
            when (it.id) {
                R.id.navNewTransfer    -> drawer.closeDrawer(GravityCompat.START)
                R.id.navLogin          -> onLoginClick()
                R.id.navAbout          -> onAboutClick()
                R.id.navSettings       -> onSettingsClick()
                R.id.navSupport        -> onSupportClick()
                R.id.navRequests       -> onRequestsClick()
                R.id.navBecomeACarrier -> onBecomeACarrierClick()
                R.id.navHeaderShare    -> onShareClick()
                else -> Timber.d("No route")
            }
            drawer.closeDrawer(GravityCompat.START)
        }
    }

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun getPresenter(): MainPresenter = presenter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) window.statusBarColor = Color.TRANSPARENT
        else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            viewGradient.isVisible = false
        }

        _mapView = mapView
        _btnCenter = btnMyLocation
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
        initSearchForm()
        initNavigation()

        switchMain(withMap = systemInteractor.lastMainScreenMode == Screens.MAIN_WITH_MAP, firstAttach = true)
        switch_mode.setOnCheckedChangeListener { _, isChecked -> presenter.tripModeSwitched(isChecked) }
        switcher_map.switch_mode_.setOnCheckedChangeListener { _, isChecked -> screenModeChanged(isChecked) }

        isFirst = savedInstanceState == null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            window.exitTransition = Fade().apply { duration = FADE_DURATION }
        getIntents()
    }

    private fun getIntents() {
        with(intent) {
            if (getBooleanExtra(SplashActivity.EXTRA_SHOW_RATE, false)) {
                val transferId =getLongExtra(SplashActivity.EXTRA_TRANSFER_ID, 0)
                val rate = getIntExtra(SplashActivity.EXTRA_RATE, 0)
                presenter.rateTransfer(transferId, rate)
            }
            if (getBooleanExtra(Screens.MAIN_MENU, false))
                Handler().postDelayed( { drawer.openDrawer(Gravity.START, true) }, 500)
        }
    }

    @CallSuper
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) search_panel.elevation = resources.getDimension(R.dimen.search_elevation)
        searchFrom.setUneditable()
        searchTo.setUneditable()
        searchFrom.setOnClickListener { performClick(false) }
        searchTo.setOnClickListener   { performClick(true) }
        rl_hourly.setOnClickListener  { showNumberPicker(true) }
        btnNext.setOnClickListener    { performNextClick() }
        enableBtnNext()
    }

    override fun onResume() {
        super.onResume()
        presenter.setScreenState(requestView != null)
    }

    fun performNextClick() {
        if (!nextClicked) {
            presenter.onNextClick { process ->
                nextClicked = process
            }
        }
    }

    private fun screenModeChanged(isChecked: Boolean) {
        screenType = if (isChecked) MAP_SCREEN else REQUEST_SCREEN
        switchMain(isChecked)
        defineNavigationStrategy()
    }

    @SuppressLint("CommitTransaction")
    private fun switchMain(withMap: Boolean, firstAttach: Boolean = false) {
        with(supportFragmentManager.beginTransaction()) {
            if (!firstAttach)setAnimation(withMap, this)
            if (!withMap) {
                systemInteractor.lastMainScreenMode = Screens.MAIN_WITHOUT_MAP
                add(R.id.fragmentContainer, MainRequestFragment())
            }
            else {
                systemInteractor.lastMainScreenMode = Screens.MAIN_WITH_MAP
                if (!isPermissionRequested) {
                    isPermissionRequested = true
                    checkPermission()
                }
                supportFragmentManager.fragments.firstOrNull()?.let { requestView = null;remove(it) }
            }
        }?.commit()
    }

    private fun setRequestView () {
        val addressTo = if (rl_searchForm.isVisible) searchTo.text else null
        val duration = if (rl_hourly.isVisible) tvCurrent_hours.text.toString() else null
        requestView?.let {
            with(it) {
                setView(searchFrom.text, addressTo, duration, !(viewNetworkNotAvailable?.isVisible ?: false))
                setBadge(tvEventsCount.text.toString())
                showBadge(tvEventsCount.isVisible)
            }
        }
    }

    @SuppressLint("PrivateResource")
    private fun setAnimation(opens: Boolean, transaction: FragmentTransaction) =
            transaction.apply {
                val first = if(opens) R.anim.abc_fade_in else R.anim.abc_fade_in
                val second = if(opens) R.anim.abc_fade_out else R.anim.abc_fade_out
                setCustomAnimations(first, second)
            }

    fun performClick(clickedTo: Boolean, returnBack: Boolean = false) {
        presenter.isClickTo = clickedTo
        processGoogleMap(true) {
            presenter.onSearchClick(searchFrom.text,
                    searchTo.text,
                    it.projection.visibleRegion.latLngBounds,
                    returnBack)
        }
    }

    fun showNumberPicker(show: Boolean) {
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
    override fun onBackPressed() = presenter.onBackClick()

    @CallSuper
    protected override fun onStop() {
        super.onStop()
        nextClicked = false
        enableBtnNext()

    }

    private fun initNavigation() {
        navViewHeader.setPadding(0, getStatusBarHeight(), 0, 0)

        val versionName = BuildConfig.VERSION_NAME
        val versionCode = BuildConfig.VERSION_CODE
        (navFooterVersion as TextView).text =
                String.format(getString(R.string.nav_footer_version), versionName, versionCode)
        navHeaderMode.isVisible = false
        navNewTransfer.isVisible = true
        setMenuIconsColorFilter()

        readMoreListener.let {
            navFooterStamp.setOnClickListener   (it)
            navFooterReadMore.setOnClickListener(it)
        }
        itemsNavigationViewListener.let {
            navNewTransfer.setOnClickListener   (it)
            navHeaderShare.setOnClickListener   (it)
            navLogin.setOnClickListener         (it)
            navRequests.setOnClickListener      (it)
            navSettings.setOnClickListener      (it)
            navSupport.setOnClickListener       (it)
            navAbout.setOnClickListener         (it)
            navBecomeACarrier.setOnClickListener(it)
            navPassengerMode.setOnClickListener (it)
        }
    }

    private fun setMenuIconsColorFilter() {
        ContextCompat.getColor(this, R.color.color_gtr_orange).let {
            navNewTransfer.menu_item_image.setColorFilter(it)
            navLogin.menu_item_image.setColorFilter(it)
            navRequests.menu_item_image.setColorFilter(it)
            navSettings.menu_item_image.setColorFilter(it)
            navSupport.menu_item_image.setColorFilter(it)
            navAbout.menu_item_image.setColorFilter(it)
            navBecomeACarrier.menu_item_image.setColorFilter(it)
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
            tvCurrent_hours.text = displayedValues[value]
            requestView?.setNumberPickerValue(displayedValues[value])
            setOnValueChangedListener { _, _, newVal ->
                presenter.tripDurationSelected(HourlyValuesHelper.durationValues[newVal])
                tvCurrent_hours.text = displayedValues[newVal]
                requestView?.setNumberPickerValue(displayedValues[newVal])
            }
        }
        tv_okBtn.setOnClickListener { showNumberPicker(false) }
    }

    protected override suspend fun customizeGoogleMaps(gm: GoogleMap) {
        super.customizeGoogleMaps(gm)

        //checkPermission()
        btnMyLocation.setOnClickListener  {
            checkPermission()
            presenter.updateCurrentLocation()
        }
        gm.setOnCameraMoveListener        {
            presenter.onCameraMove(gm.cameraPosition!!.target, true)
        }
        gm.setOnCameraIdleListener        { presenter.onCameraIdle(gm.projection.visibleRegion.latLngBounds) }
        /*gm.setOnCameraMoveStartedListener {
            if (it == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                presenter.enablePinAnimation()
                gm.setOnCameraMoveStartedListener(null)
            }
        }*/
    }

    override fun enablePinAnimation() {
        super.enablePinAnimation()
        presenter.enablePinAnimation()
    }

    fun checkPermission() {
        if (!EasyPermissions.hasPermissions(this, *PERMISSIONS))
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.LNG_LOCATION_ACCESS),
                PERMISSION_REQUEST, *PERMISSIONS)
    }

    override fun defineAddressRetrieving(block: (withGps: Boolean) -> Unit) {
        block(EasyPermissions.hasPermissions(this, *PERMISSIONS))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        presenter.updateCurrentLocation()
    }

    override fun recreateRequestFragment() {
        switchMain(false, true)
    }

    override fun setMapPoint(point: LatLng, withAnimation: Boolean, showBtnMyLocation: Boolean) {
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
                }
                else {
                    if (withAnimation) it.animateCamera(CameraUpdateFactory.newLatLngZoom(point, zoom))
                    else it.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom))
                }
            }
        }
        btnMyLocation.isVisible = showBtnMyLocation
        requestView?.setVisibilityBtnMyLocation(showBtnMyLocation)
    }

    override fun openMapToSetPoint() {
        switchMain(true)
        setSwitchersVisibility(false)
        defineNavigationStrategy()
    }

    private fun setSwitchersVisibility(visible: Boolean) {
        switcher_map.isVisible = visible
        switch_panel.isVisible = visible
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
        centerMarker?.position = point
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        if (block) searchFrom.text = getString(R.string.LNG_LOADING)
    }

    override fun blockSelectedField(block: Boolean, field: String) {
        if (block) {
            requestView?.blockSelectedField(field)
            when (field) {
                MainPresenter.FIELD_FROM -> searchFrom.text = getString(R.string.LNG_LOADING)
                MainPresenter.FIELD_TO -> searchTo.text = getString(R.string.LNG_LOADING)
            }
        }
    }

    override fun setError(e: ApiException) {
        searchFrom.text = getString(R.string.search_nothing)
        if (e.isNotFound()) super.setError(e)
    }

    fun initSearchForm() {
        searchFrom.sub_title.text = getString(R.string.LNG_FIELD_SOURCE_PICKUP)
        searchTo.sub_title.text   = getString(R.string.LNG_FIELD_DESTINATION)
    }

    override fun setAddressFrom(address: String) {
        if (address != searchFrom.text) {
            searchFrom.text = address
            setRequestView()
            enableBtnNext()
            setPointsView(tv_a_point, address.isNotEmpty())
        }
    }

    override fun setAddressTo(address: String) {
        if (address != searchTo.text) {
            searchTo.text = address
            enableBtnNext()
            setRequestView()
            setPointsView(tv_b_point, address.isNotEmpty())
        }
    }

    fun setPointsView(textView: TextView, empty: Boolean) {
        with(textView) {
            background = if (empty) {
                setTextColor(ContextCompat.getColor(this@MainActivity, R.color.colorWhite))
                ContextCompat.getDrawable(this@MainActivity, R.drawable.back_circle_marker_orange_filled)
            } else {
                setTextColor(ContextCompat.getColor(this@MainActivity, R.color.colorTextBlack))
                ContextCompat.getDrawable(this@MainActivity, R.drawable.back_orange_empty)
            }
        }
    }

    private fun enableBtnNext() {
        btnNext.isEnabled = searchFrom.text.isNotEmpty() && (searchTo.text.isNotEmpty() || presenter.isHourly())
    }

    private fun defineNavigationStrategy() {
        if (screenType == REQUEST_SCREEN) definePointSelectionStrategy()
        else defineMapModeStrategy()
    }

    /*
    Is used when in no-map mode, but in SearchPresenter selectFinishPointOnMap() was called
     */
    private fun definePointSelectionStrategy() {
        btnNext.setOnClickListener {
            switchMain(false)
            setSwitchersVisibility(true)
        }
        btnBack.setOnClickListener {
            performClick(true, true)
            setSwitchersVisibility(true)
        }
    }

    private fun defineMapModeStrategy() {
        btnNext.setOnClickListener { performNextClick()}
        btnBack.setOnClickListener { presenter.onBackClick() }
    }

    override fun setProfile(profile: ProfileModel) {
        profile.apply {
            navHeaderMode.text = getString(R.string.LNG_MENU_TITLE_PASSENGER)
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
        mMarker.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.point_orange))
        switchButtons(false)
        setAlpha(ALPHA_FULL)
        ivSelectFieldTo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.btn_pin_location))
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.START)
    }

    override fun setFieldTo() {
        mMarker.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_map_label_b_orange))
        switchButtons(true)
        setAlpha(ALPHA_DISABLED)
        ivSelectFieldTo.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.btn_pin_enabled))
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END)
    }

    private fun switchButtons(isBackVisible: Boolean) {
        btnBack.isVisible = isBackVisible
        btnShowDrawerLayout.isVisible = !isBackVisible
    }

    private fun setAlpha(alpha: Float) {
        searchFrom.alpha = alpha
        tv_a_point.alpha    = alpha
    }

    override fun onBackClick(isAddressNavigating: Boolean, isTo: Boolean) {
        when {
            drawer.isDrawerOpen(GravityCompat.START) -> drawer.closeDrawer(GravityCompat.START)
            screenType == MainView.REQUEST_SCREEN && isAddressNavigating -> {
                performClick(isTo, true)
                setSwitchersVisibility(true)
            }
            hourlySheet.state == BottomSheetBehavior.STATE_COLLAPSED -> showNumberPicker(false)
            else -> super.onBackPressed()
        }
    }

    override fun showReadMoreDialog() {
        drawer.closeDrawer(GravityCompat.START)
        ReadMoreFragment().show(supportFragmentManager, getString(R.string.tag_read_more))
    }

    override fun changeFields(hourly: Boolean) {
        rl_hourly.isVisible    = hourly
        hourly_point.isVisible = hourly
        rl_searchForm.isGone   = hourly
        tv_b_point.isGone      = hourly
        link_line.isInvisible  = hourly
        enableBtnNext()
        if (!hourly) showNumberPicker(false)
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

    override fun openReviewForLastTrip(transfer: TransferModel, startPoint: LatLng, vehicle: String, color: String, routeModel: RouteModel?) {
        val view = showPopUpWindow(R.layout.view_last_trip_rate, contentMain)
        view?.apply {
            mDisMissAction = {
                _mapView = mapView
                isGmTouchEnabled = true
                initMapView(null)
                view.rate_map?.onDestroy()
                mapView.onResume()
                presenter.updateCurrentLocation()
                mDisMissAction = {}
            }

            tv_transfer_details.setOnClickListener {
                closePopUp()
                presenter.onTransferDetailsClick(transfer.id)
            }
            tv_close_lastTrip_rate.setOnClickListener { presenter.onReviewCanceled() }
            tv_transfer_number_rate.apply { text = text.toString().plus(" #${transfer.id}") }
            tv_transfer_date_rate.text = SystemUtils.formatDateTime(transfer.dateTime)
            tv_vehicle_model_rate.text = vehicle
            rate_bar_last_trip.setOnRatingChangeListener { _, fl ->
                closePopUp()
                presenter.onRateClicked(fl)
            }
            carColor_rate.setImageDrawable(Utils.getVehicleColorFormRes(this@MainActivity, color))
            drawMapForReview(view.rate_map, routeModel, transfer.from, startPoint)
        }

    }

    private fun drawMapForReview(map: MapView,  routeModel: RouteModel?, from: String, startPoint: LatLng) {
        _mapView = map
        isGmTouchEnabled = false
        initMapView(null)
        if(routeModel != null){
            setPolyline(Utils.getPolyline(routeModel), routeModel)
        } else {
            processGoogleMap(false) {
                setPinForHourlyTransfer(from, "", startPoint, Utils.getCameraUpdateForPin(startPoint))
            }
        }
        mapView.onPause()
        map.onResume()
    }

    override fun showDetailedReview(tappedRate: Float) {
        val popUpView = showPopUpWindow(R.layout.view_rate_dialog, contentMain)
        popUpView?.let {
            popUpView.tvCancelRate.setOnClickListener { presenter.onReviewCanceled() }
            popUpView.send_feedBack.setOnClickListener {
                closePopUp()
                presenter.sendReview(Utils.createListOfDetailedRates(popUpView), popUpView.et_reviewComment.text.toString())
            }
            setupDetailRatings(tappedRate, popUpView)
        }
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
        val popUpView = showPopUpWindow(R.layout.view_rate_in_store, contentMain)
        popUpView?.apply {
            tv_agree_store.setOnClickListener  { presenter.onRateInStore() }
            tv_reject_store.setOnClickListener { closePopUp() }
        }
    }

    override fun thanksForRate() {
        val popUpView = showPopUpWindow(R.layout.view_thanks_for_rate, contentMain)
        popUpView?.apply {
            tv_thanks_close.setOnClickListener { closePopUp() }
        }
    }

    override fun showRateInPlayMarket() = redirectToPlayMarket()

    override fun cancelReview() = closePopUp()

    override fun showBadge(show: Boolean) {
        tvEventsCount.isVisible = show
        navRequests.menu_item_counter.isVisible = show
        requestView?.showBadge(show)
    }

    override fun setCountEvents(count: Int) {
        tvEventsCount.text = count.toString()
        navRequests.menu_item_counter.text = count.toString()
        requestView?.setBadge(count.toString())
    }

    override fun setNetworkAvailability(context: Context) =
            super.setNetworkAvailability(context)
                    .also { requestView?.onNetworkWarning(!it) }

    companion object {
        @JvmField val PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        const val PERMISSION_REQUEST = 2211
        const val FADE_DURATION = 500L
        const val MAX_INIT_ZOOM = 2.0f

        const val ALPHA_FULL     = 1f
        const val ALPHA_DISABLED = 0.3f
    }
}
