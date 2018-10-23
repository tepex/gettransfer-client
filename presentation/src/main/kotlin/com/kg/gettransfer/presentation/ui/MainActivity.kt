package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration

import android.graphics.Color

import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout

import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatDelegate

import android.transition.Fade
import android.util.Log

import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.WindowManager

import android.widget.TextView

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker

import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.RouteInteractor

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.model.ProfileModel
import com.kg.gettransfer.presentation.presenter.MainPresenter
import com.kg.gettransfer.presentation.view.MainView

import kotlinx.android.synthetic.main.activity_main.*
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
    private lateinit var toggle: ActionBarDrawerToggle

    private val routeInteractor: RouteInteractor by inject()

    private var isFirst = true
    private var centerMarker: Marker? = null

    private var fromClick = false
    private var toClick = false

    @ProvidePresenter
    fun createMainPresenter(): MainPresenter =
        MainPresenter(coroutineContexts, router, systemInteractor, routeInteractor)

    private val readMoreListener = View.OnClickListener { presenter.readMoreClick() }

    private val itemsNavigationViewListener = View.OnClickListener { item ->
        when(item.id) {
            R.id.navLogin -> presenter.onLoginClick()
            R.id.navAbout -> presenter.onAboutClick()
            R.id.navSettings -> presenter.onSettingsClick()
            R.id.navRequests -> presenter.onRequestsClick()
            R.id.navBecomeACarrier -> presenter.onBecomeACarrierClick()
            else -> Timber.d("No route")
        }
        drawer.closeDrawer(GravityCompat.START)
    }

    protected override var navigator = object: BaseNavigator(this) {
        protected override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
            val intent = super.createActivityIntent(context, screenKey, data)
            if(intent != null) return intent

            when(screenKey) {
                Screens.ABOUT -> return Intent(context, AboutActivity::class.java)
                Screens.FIND_ADDRESS -> {
                    val searchIntent = Intent(context, SearchActivity::class.java)
                    @Suppress("UNCHECKED_CAST")
                    val pair = data as Pair<String, String>
                    searchIntent.putExtra(SearchActivity.EXTRA_ADDRESS_FROM, pair.first)
                    searchIntent.putExtra(SearchActivity.EXTRA_ADDRESS_TO, pair.second)
                    searchIntent.putExtra(SearchActivity.EXTRA_FROM_CLICK, fromClick)
                    searchIntent.putExtra(SearchActivity.EXTRA_TO_CLICK, toClick)

                    val bounds = googleMap.projection.visibleRegion.latLngBounds
                    searchIntent.putExtra(SearchActivity.LATLON_BOUNDS, bounds)
                    return searchIntent
                }
                Screens.CREATE_ORDER -> return Intent(context, CreateOrderActivity::class.java)
                Screens.SETTINGS -> return Intent(context, SettingsActivity::class.java)
                Screens.REQUESTS -> return Intent(context, RequestsActivity::class.java)
                Screens.CARRIER_MODE -> return Intent(context, CarrierTripsActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                Screens.REG_CARRIER -> return Intent(context, WebPageActivity()::class.java)
                        .putExtra(WebPageActivity.SCREEN, WebPageActivity.SCREEN_REG_CARRIER)
            }
            return null
        }

        @CallSuper
        protected override fun forward(command: Forward) {
            if (command.screenKey == Screens.READ_MORE) {
                drawer.closeDrawer(GravityCompat.START)
                ReadMoreDialog.newInstance(this@MainActivity).show()
            } else super.forward(command)
        }

        protected override fun createStartActivityOptions(command: Command, intent: Intent): Bundle? =
            ActivityOptionsCompat
                .makeSceneTransitionAnimation(this@MainActivity, search, getString(R.string.searchTransitionName))
                .toBundle()
    }

    companion object {
        @JvmField val MY_LOCATION_BUTTON_INDEX = 2
        @JvmField val COMPASS_BUTTON_INDEX = 5
        @JvmField val FADE_DURATION = 500L
        @JvmField val MAX_INIT_ZOOM = 2.0f
    }

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

        btnShowDrawerLayout.setOnClickListener { drawer.openDrawer(Gravity.START) }
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
    override fun onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
    }

    @CallSuper
    protected override fun onStop() {
        enableBtnNext()
        super.onStop()
    }

    /** @see {@link android.support.v7.app.ActionBarDrawerToggle} */
    @CallSuper
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        toggle.onConfigurationChanged(newConfig)
    }

    /** @see {@link android.support.v7.app.ActionBarDrawerToggle} */
    override fun onOptionsItemSelected(item: MenuItem) = toggle.onOptionsItemSelected(item)

    private fun initNavigation() {
        //val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        val versionName = BuildConfig.VERSION_NAME
        val versionCode = BuildConfig.VERSION_CODE
        (navFooterVersion as TextView).text =
                String.format(getString(R.string.nav_footer_version), versionName, versionCode)
        navFooterStamp.setOnClickListener(readMoreListener)
        navFooterReadMore.setOnClickListener(readMoreListener)

        /*headerView = navView.getHeaderView(0)
        val shareBtn: ImageView = headerView.findViewById(R.id.nav_header_share) as ImageView
        shareBtn.setOnClickListener {
            Timber.d("Share action")
        }*/

        navHeaderShare.setOnClickListener { Timber.d("Share action") }
        navLogin.setOnClickListener(itemsNavigationViewListener)
        navRequests.setOnClickListener(itemsNavigationViewListener)
        navSettings.setOnClickListener(itemsNavigationViewListener)
        navAbout.setOnClickListener(itemsNavigationViewListener)
        navBecomeACarrier.setOnClickListener(itemsNavigationViewListener)
        navPassengerMode.setOnClickListener(itemsNavigationViewListener)
    }


    protected suspend override fun customizeGoogleMaps() {
        super.customizeGoogleMaps()
        googleMap.setMyLocationEnabled(true)
        googleMap.uiSettings.isMyLocationButtonEnabled = false
        btnMyLocation.setOnClickListener  { presenter.updateCurrentLocation() }
        googleMap.setOnCameraMoveListener { presenter.onCameraMove(googleMap.getCameraPosition()!!.target, true);  }
        googleMap.setOnCameraIdleListener { presenter.onCameraIdle() }
    }

    /* MainView */
    override fun setMapPoint(point: LatLng) {
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
                else googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, zoom))
            }
        }
    }

    override fun setMarkerElevation(up: Boolean, elevation: Float) {
        mMarker.animate()
                .withStartAction { presenter.setMarkerAnimating(true) }
                .withEndAction {
                    presenter.setMarkerAnimating(false)
                    if(!up) markerShadow.setImageDrawable(getDrawable(R.drawable.default_position_shadow))
                }
                .translationYBy(-elevation)
                .start()

        if(up) markerShadow.setImageDrawable(getDrawable(R.drawable.lifted_marker_shadow))
    }

    override fun moveCenterMarker(point: LatLng) {
        centerMarker?.let { it.setPosition(point) }
    }

    override fun blockInterface(block: Boolean) {
        if(block) searchFrom.text = getString(R.string.search_start)
    }

    override fun setError(e: Throwable) {
        searchFrom.text = getString(R.string.search_nothing)
    }

    override fun setAddressFrom(address: String) {
        searchFrom.text = address
        enableBtnNext()
    }
    
    override fun setAddressTo(address: String)   {
        searchTo.text = address
        enableBtnNext()
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
}
