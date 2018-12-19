package com.kg.gettransfer.presentation.ui

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter

import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.DrawableRes
import android.support.design.widget.BottomSheetBehavior

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

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.*

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.presentation.model.ProfileModel
import com.kg.gettransfer.presentation.presenter.MainPresenter
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper
import com.kg.gettransfer.presentation.view.MainView

import kotlinx.android.synthetic.main.a_b_view.*
import kotlinx.android.synthetic.main.a_b_view.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.search_address.view.*
import kotlinx.android.synthetic.main.search_form_main.*
import kotlinx.android.synthetic.main.view_hourly_picker.*
import kotlinx.android.synthetic.main.view_hourly_picker.view.*
import kotlinx.android.synthetic.main.view_navigation.*

import timber.log.Timber

class MainActivity : BaseGoogleMapActivity(), MainView {
    @InjectPresenter
    internal lateinit var presenter: MainPresenter

    private lateinit var drawer: DrawerLayout
    //private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var hourlySheet: BottomSheetBehavior<View>

    private var isFirst = true
    private var centerMarker: Marker? = null

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
                if(newState == DrawerLayout.STATE_SETTLING) hideKeyboard()
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
                tvCurrent_hours.text = displayedValues[newVal] }
        }
        tv_okBtn.setOnClickListener { showNumberPicker(false) }
    }

    protected override suspend fun customizeGoogleMaps(gm: GoogleMap) {
        super.customizeGoogleMaps(gm)
        if (systemInteractor.locationPermissionsGranted ?: true) {
            gm.setMyLocationEnabled(true)
            gm.uiSettings.isMyLocationButtonEnabled = false
        }

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
                MainPresenter.FIELD_TO   -> searchTo.text   = getString(R.string.search_start)
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
        navHeaderName.isVisible  = profile.isLoggedIn()
        navHeaderEmail.isVisible = profile.isLoggedIn()
        navRequests.isVisible    = profile.isLoggedIn()
        navLogin.isVisible       = !profile.isLoggedIn()
        if (profile.isLoggedIn()) {
            navHeaderName.text = profile.name
            navHeaderEmail.text = profile.email
        }
    }

    override fun selectFieldFrom() {
        mMarker.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_map_label_empty))
        switchButtons(false)
        setAlpha(ALPHA_FULL)
        ivSelectFieldTo.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(this, R.color.colorTextBlack), PorterDuff.Mode.SRC_IN)
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.START)
    }

    override fun setFieldTo() {
        mMarker.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_map_label_b))
        switchButtons(true)
        setAlpha(ALPHA_DISABLED)
        ivSelectFieldTo.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(this, R.color.colorBtnGreen), PorterDuff.Mode.SRC_IN)
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
        rl_hourly.isVisible    = hourly
        hourly_point.isVisible = hourly

        rl_searchForm.isGone = hourly
        b_point.isGone       = hourly
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

    companion object {
        @JvmField val MY_LOCATION_BUTTON_INDEX = 2
        @JvmField val COMPASS_BUTTON_INDEX = 5
        @JvmField val FADE_DURATION = 500L
        @JvmField val MAX_INIT_ZOOM = 2.0f

        const val ALPHA_FULL = 1f
        const val ALPHA_DISABLED = 0.3f
    }
}
