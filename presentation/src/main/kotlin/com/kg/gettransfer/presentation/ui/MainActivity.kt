package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter

import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout

import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatDelegate

import android.transition.Fade

import android.view.Gravity
import android.view.MenuItem
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

import com.kg.gettransfer.domain.ApiException

import com.kg.gettransfer.presentation.model.ProfileModel
import com.kg.gettransfer.presentation.presenter.MainPresenter
import com.kg.gettransfer.presentation.view.MainView

import com.kg.gettransfer.service.OfferServiceConnection

import kotlinx.android.synthetic.main.a_b_view.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.search_address.view.*
import kotlinx.android.synthetic.main.search_form_main.*
import kotlinx.android.synthetic.main.view_navigation.*

import org.koin.android.ext.android.inject

import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward

import timber.log.Timber

class MainActivity: BaseGoogleMapActivity(), MainView {
    @InjectPresenter
    internal lateinit var presenter: MainPresenter

    private lateinit var drawer: DrawerLayout
    //private lateinit var toggle: ActionBarDrawerToggle

    private var isFirst = true
    private var centerMarker: Marker? = null

    @ProvidePresenter
    fun createMainPresenter() = MainPresenter()

    private val readMoreListener = View.OnClickListener { presenter.readMoreClick() }

    private val itemsNavigationViewListener = View.OnClickListener {
        when(it.id) {
            R.id.navLogin          -> presenter.onLoginClick()
            R.id.navAbout          -> presenter.onAboutClick()
            R.id.navSettings       -> presenter.onSettingsClick()
            R.id.navRequests       -> presenter.onRequestsClick()
            R.id.navBecomeACarrier -> presenter.onBecomeACarrierClick()
            else -> Timber.d("No route")
        }
        drawer.closeDrawer(GravityCompat.START)
    }

        /*
        @CallSuper
        protected override fun forward(command: Forward) {
            if(command.screenKey == Screens.READ_MORE) {
                drawer.closeDrawer(GravityCompat.START)
                ReadMoreDialog.newInstance(this@MainActivity).show()
            } else super.forward(command)
        }

        protected override fun createStartActivityOptions(command: Command, intent: Intent): Bundle? =
            ActivityOptionsCompat
                .makeSceneTransitionAnimation(this@MainActivity, search, getString(R.string.searchTransitionName))
                .toBundle()
                */

    companion object {
        @JvmField val MY_LOCATION_BUTTON_INDEX = 2
        @JvmField val COMPASS_BUTTON_INDEX = 5
        @JvmField val FADE_DURATION = 500L
        @JvmField val MAX_INIT_ZOOM = 2.0f
    }

    private val offerServiceConnection: OfferServiceConnection by inject()

    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun getPresenter(): MainPresenter = presenter

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) window.statusBarColor = Color.TRANSPARENT
        else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            viewGradient.visibility = View.GONE
        }

        _mapView = mapView
        initMapView(savedInstanceState)

        viewNetworkNotAvailable = textNetworkNotAvailable

        btnShowDrawerLayout.setOnClickListener { drawer.openDrawer(Gravity.START) }
        btnBack.setOnClickListener { presenter.onBackClick() }
        ivSelectFieldTo.setOnClickListener { presenter.switchUsedField() }
        drawer = drawerLayout as DrawerLayout
        drawer.addDrawerListener(object: DrawerLayout.SimpleDrawerListener() {
            @CallSuper
            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                if(newState == DrawerLayout.STATE_SETTLING) hideKeyboard()
            }
        })

        presenter.setAddressFields()

        initNavigation()

        isFirst = savedInstanceState == null

        val fade = Fade()
        fade.duration = FADE_DURATION
        window.setExitTransition(fade)
    }

    @CallSuper
    protected override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        search.elevation = resources.getDimension(R.dimen.search_elevation)
        searchFrom.setUneditable()
        searchTo.setUneditable()
        searchFrom.setOnClickListener {
            presenter.isClickTo = false
            presenter.onSearchClick(searchFrom.text, searchTo.text)
        }
        searchTo.setOnClickListener   {
            presenter.isClickTo = true
            presenter.onSearchClick(searchFrom.text, searchTo.text)
        }
        btnNext.setOnClickListener { presenter.onNextClick() }
        enableBtnNext()
    }

    @CallSuper
    protected override fun onStart() {
        super.onStart()
        hideKeyboard()
        systemInteractor.addListener(offerServiceConnection)
        offerServiceConnection.connectionChanged(systemInteractor.endpoint, systemInteractor.accessToken)
        offerServiceConnection.connect(this) { newOffer ->
            Timber.d("new Offer: $newOffer")
        }
    }

    @CallSuper
    override fun onBackPressed() {
        presenter.onBackClick()
    }

    @CallSuper
    protected override fun onStop() {
        enableBtnNext()
        systemInteractor.removeListener(offerServiceConnection)
        offerServiceConnection.disconnect(this)
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
        navFooterStamp.setOnClickListener(readMoreListener)
        navFooterReadMore.setOnClickListener(readMoreListener)

        navHeaderShare.setOnClickListener { Timber.d("Share action") }
        navLogin.setOnClickListener(itemsNavigationViewListener)
        navRequests.setOnClickListener(itemsNavigationViewListener)
        navSettings.setOnClickListener(itemsNavigationViewListener)
        navAbout.setOnClickListener(itemsNavigationViewListener)
        navBecomeACarrier.setOnClickListener(itemsNavigationViewListener)
        navPassengerMode.setOnClickListener(itemsNavigationViewListener)
    }


    protected override suspend fun customizeGoogleMaps() {
        super.customizeGoogleMaps()
        googleMap.setMyLocationEnabled(true)
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        btnMyLocation.setOnClickListener  { presenter.updateCurrentLocation() }
        googleMap.setOnCameraMoveListener { presenter.onCameraMove(googleMap.getCameraPosition()!!.target, true);  }
        googleMap.setOnCameraIdleListener { presenter.onCameraIdle(googleMap.projection.visibleRegion.latLngBounds) }
        googleMap.setOnCameraMoveStartedListener {
            if(it == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                presenter.enablePinAnimation()
                googleMap.setOnCameraMoveStartedListener(null)
            }
        }
    }

    /* MainView */
    override fun setMapPoint(point: LatLng, withAnimation: Boolean) {
        val zoom = resources.getInteger(R.integer.map_min_zoom).toFloat()
        processGoogleMap {
            if(centerMarker != null) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom))
                moveCenterMarker(point)
            } else {
                /* Грязный хак!!! */
                if(isFirst || googleMap.cameraPosition.zoom <= MAX_INIT_ZOOM) {
                    val zoom1 = resources.getInteger(R.integer.map_min_zoom).toFloat()
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom1))
                    isFirst = false
                    //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, zoom))
                }
                //else googleMap.moveCamera(CameraUpdateFactory.newLatLng(point))
                else {
                    if(withAnimation) googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, zoom))
                    else googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom))
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
                    if(!up) markerShadow.setImageDrawable(getDrawable(R.drawable.default_position_shadow))
                }
                .setDuration(150L)
                .translationYBy(px)
                .start()

        if(up) markerShadow.setImageDrawable(getDrawable(R.drawable.lifted_marker_shadow))
    }

    override fun moveCenterMarker(point: LatLng) {
        centerMarker?.let { it.setPosition(point) }
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        if(block) searchFrom.text = getString(R.string.search_start)
    }

    override fun blockSelectedField(block: Boolean, field: String) {
        if(block){
            when(field){
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
        searchTo.sub_title.text = getString(R.string.LNG_FIELD_DESTINATION)
    }

    override fun setAddressFrom(address: String) {
        searchFrom.text = address
        enableBtnNext()
        var iconRes: Int
        if(address.isNotEmpty()) iconRes = R.drawable.a_point_filled
        else iconRes = R.drawable.a_point_empty
        icons_container.a_point.setImageDrawable(getDrawable(iconRes))
    }

    override fun setAddressTo(address: String)   {
        searchTo.text = address
        enableBtnNext()
        var iconRes: Int
        if(address.isNotEmpty()) iconRes = R.drawable.b_point_filled
        else iconRes = R.drawable.b_point_empty
        icons_container.b_point.setImageDrawable(getDrawable(iconRes))
    }

    private fun enableBtnNext() {
        btnNext.isEnabled = searchFrom.text.isNotEmpty() && searchTo.text.isNotEmpty()
    }

    override fun setProfile(profile: ProfileModel) {
        if(!profile.isLoggedIn()) {
            navHeaderName.visibility = View.GONE
            navHeaderEmail.visibility = View.GONE
            navLogin.visibility = View.VISIBLE
            navRequests.visibility = View.GONE
        } else {
            navHeaderName.visibility = View.VISIBLE
            navHeaderEmail.visibility = View.VISIBLE
            navHeaderName.text = profile.name
            navHeaderEmail.text = profile.email
            navLogin.visibility = View.GONE
            navRequests.visibility = View.VISIBLE
        }
    }

    override fun selectFieldFrom() {
        mMarker.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_map_label_empty))
        btnShowDrawerLayout.visibility = View.VISIBLE
        btnBack.visibility = View.GONE
        searchFrom.addressField.setTextColor(ContextCompat.getColor(this, R.color.colorTextBlack))
        ivSelectFieldTo.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(this, R.color.colorTextBlack), PorterDuff.Mode.SRC_IN)
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.START)
    }

    override fun setFieldTo() {
        mMarker.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_map_label_b))
        btnShowDrawerLayout.visibility = View.GONE
        btnBack.visibility = View.VISIBLE
        searchFrom.addressField.setTextColor(ContextCompat.getColor(this, R.color.colorTextLightGray))
        ivSelectFieldTo.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(this, R.color.colorBtnGreen), PorterDuff.Mode.SRC_IN)
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.END)
    }

    override fun onBackClick() {
        if(drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }
}
