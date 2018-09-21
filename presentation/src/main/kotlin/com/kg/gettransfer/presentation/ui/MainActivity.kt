package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.design.widget.AppBarLayout

import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout

import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar

import android.transition.Fade

import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager

import android.widget.RelativeLayout
import android.widget.TextView

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.LocationInteractor
import com.kg.gettransfer.domain.interactor.RouteInteractor

import com.kg.gettransfer.domain.model.Account

import com.kg.gettransfer.extensions.hideKeyboard

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.presenter.MainPresenter
import com.kg.gettransfer.presentation.view.MainView

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.search_form_main.*

import org.koin.android.ext.android.inject

import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward

import timber.log.Timber

class MainActivity: BaseGoogleMapActivity(), MainView {
    @InjectPresenter
    internal lateinit var presenter: MainPresenter

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private val locationInteractor: LocationInteractor by inject()
    private val routeInteractor: RouteInteractor by inject()
    
    private var isFirst = true
    private var centerMarker: Marker? = null
    
    @ProvidePresenter
    fun createMainPresenter(): MainPresenter = MainPresenter(coroutineContexts,
                                                             router,
                                                             systemInteractor,
                                                             locationInteractor,
                                                             routeInteractor)
    
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
					return searchIntent
				}
				Screens.CREATE_ORDER -> return Intent(context, CreateOrderActivity::class.java)
				Screens.SETTINGS -> return Intent(context, SettingsActivity::class.java)
				Screens.REQUESTS -> return Intent(context, RequestsActivity::class.java)
				Screens.CARRIER -> return  Intent(context, CarrierTripsActivitty::class.java)
			}
			return null
		}
		
		@CallSuper
		protected override fun forward(command: Forward) {
			if(command.screenKey == Screens.READ_MORE) {
				drawer.closeDrawer(GravityCompat.START)
				ReadMoreDialog.newInstance(this@MainActivity).show()
			}
			else super.forward(command)
		}
	
		protected override fun createStartActivityOptions(command: Command, intent: Intent): Bundle? =
			ActivityOptionsCompat
				.makeSceneTransitionAnimation(
					this@MainActivity, 
				    search,
				    getString(R.string.searchTransitionName))
				.toBundle()
	}

	companion object {
		@JvmField val MY_LOCATION_BUTTON_INDEX = 2
		@JvmField val COMPASS_BUTTON_INDEX = 5
		@JvmField val FADE_DURATION  = 500L
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
		
		_mapView = mapView
		initGoogleMap(savedInstanceState)
		
		val tb = this.toolbar as Toolbar
		
		setSupportActionBar(tb)
		supportActionBar?.setDisplayShowTitleEnabled(false)
		supportActionBar?.setDisplayHomeAsUpEnabled(false)
		supportActionBar?.setDisplayShowHomeEnabled(false)
		
		drawer = drawerLayout as DrawerLayout
		toggle = ActionBarDrawerToggle(this, drawer, tb, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
		drawer.addDrawerListener(object: DrawerLayout.SimpleDrawerListener() {
			@CallSuper
			override fun onDrawerStateChanged(newState: Int) {
				super.onDrawerStateChanged(newState)
				if(newState == DrawerLayout.STATE_SETTLING) {
				    val view = currentFocus
				    view?.hideKeyboard()
				    view?.clearFocus()
				}
			}
		})
		
        (appbar as AppBarLayout).bringToFront()
		
		initNavigation()
		
		isFirst = savedInstanceState == null
		
		search.elevation = resources.getDimension(R.dimen.search_elevation)
		searchFrom.setUneditable()
		searchTo.setUneditable()
		searchFrom.setOnClickListener { presenter.onSearchClick(Pair(searchFrom.text, searchTo.text)) }
		searchTo.setOnClickListener { presenter.onSearchClick(Pair(searchFrom.text, searchTo.text)) }

		val fade = Fade()
        fade.duration = FADE_DURATION
		window.setExitTransition(fade)
	}

	@CallSuper
	protected override fun onPostCreate(savedInstanceState: Bundle?) {
		super.onPostCreate(savedInstanceState)
		toggle.syncState()
	}
	
	@CallSuper
	protected override fun onResume() {
		super.onResume()
		val view = currentFocus
		view?.hideKeyboard()
		view?.clearFocus()
	}
	
	@CallSuper
	override fun onBackPressed() {
		if(drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
		else super.onBackPressed() 
	}
	
	@CallSuper
	protected override fun onStop() {
		searchTo.text = ""
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

		navHeaderShare.setOnClickListener {Timber.d("Share action")}
		navLogin.setOnClickListener(itemsNavigationViewListener)
		navRequests.setOnClickListener(itemsNavigationViewListener)
		navSettings.setOnClickListener(itemsNavigationViewListener)
		navAbout.setOnClickListener(itemsNavigationViewListener)
		navBecomeACarrier.setOnClickListener(itemsNavigationViewListener)
		navPassengerMode.setOnClickListener(itemsNavigationViewListener)
	}
	

	protected override fun customizeGoogleMaps() {
	    super.customizeGoogleMaps()
		googleMap.setMyLocationEnabled(true)
		googleMap.uiSettings.isMyLocationButtonEnabled = false
		btnMyLocation.setOnClickListener  { presenter.updateCurrentLocation() }
		googleMap.setOnCameraMoveListener { presenter.onCameraMove(googleMap.getCameraPosition()!!.target) }
		googleMap.setOnCameraIdleListener { presenter.onCameraIdle() }
    }
    
    /* MainView */
    override fun setMapPoint(point: LatLng) {
        if(centerMarker != null) {
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(point))
            moveCenterMarker(point)
        }
        else {
            /* Грязный хак!!! */
            if(isFirst || googleMap.cameraPosition.zoom <= MAX_INIT_ZOOM) {
                val zoom = resources.getInteger(R.integer.map_min_zoom).toFloat()
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom))
            }
            else googleMap.moveCamera(CameraUpdateFactory.newLatLng(point))
                centerMarker = googleMap.addMarker(MarkerOptions().position(point)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.map_label_empty)))
        }
    }

    override fun moveCenterMarker(point: LatLng) {
        centerMarker?.let { it.setPosition(point) }
    }

    override fun blockInterface(block: Boolean) {
        if(block) searchFrom.text = getString(R.string.search_start)
    }
	
	override fun setAddressFrom(address: String) {
		searchFrom.text = address }
	
	override fun setAccount(account: Account) {
	    if(!account.loggedIn) {
			navHeaderName.visibility = View.GONE
			navHeaderEmail.visibility = View.GONE
			navLogin.visibility = View.VISIBLE
			navRequests.visibility = View.GONE
	    }
	    else {
			navHeaderName.visibility = View.VISIBLE
			navHeaderEmail.visibility = View.VISIBLE
			navHeaderName.text = account.fullName
			navHeaderEmail.text = account.email
			navLogin.visibility = View.GONE
			navRequests.visibility = View.VISIBLE
	    }
	}
}
