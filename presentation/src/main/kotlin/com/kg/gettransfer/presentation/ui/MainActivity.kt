package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import android.support.design.widget.AppBarLayout
import android.support.design.widget.Snackbar

import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout

import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar

import android.transition.Fade
import android.util.Pair
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView

import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.R

import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.AddressInteractor
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.interactor.LocationInteractor
import com.kg.gettransfer.domain.model.Account

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.presenter.MainPresenter
import com.kg.gettransfer.presentation.view.MainView

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.search_form.*
import kotlinx.coroutines.experimental.Job

import org.koin.android.ext.android.inject

import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportAppNavigator
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward

import timber.log.Timber

import kotlin.coroutines.experimental.suspendCoroutine

class MainActivity: MvpAppCompatActivity(), MainView {
	@InjectPresenter
	internal lateinit var presenter: MainPresenter
	
	private lateinit var drawer: DrawerLayout
	private lateinit var toggle: ActionBarDrawerToggle
	private lateinit var headerView: View
	
	private val navigatorHolder: NavigatorHolder by inject()
	private val router: Router by inject()
	private val addressInteractor: AddressInteractor by inject()
	private val locationInteractor: LocationInteractor by inject()
	private val coroutineContexts: CoroutineContexts by inject()
	private val apiInteractor: ApiInteractor by inject()
	
	private val compositeDisposable = Job()
	private val utils = AsyncUtils(coroutineContexts)
	private lateinit var googleMap: GoogleMap
	
	private var isFirst = true
	private var centerMarker: Marker? = null
	
	var pos: LatLng? = null

	@ProvidePresenter
	fun createMainPresenter(): MainPresenter = MainPresenter(coroutineContexts,
			router,
			locationInteractor,
			addressInteractor,
			apiInteractor)

	private val readMoreListener = View.OnClickListener {
		presenter.readMoreClick()
	}

	private val itemsNavigationViewListener = View.OnClickListener { item ->
		when(item.id){
			R.id.navLogin -> presenter.onLoginClick()
			R.id.navAbout -> presenter.onAboutClick()
			R.id.navSettings -> presenter.onSettingsClick()
			R.id.navRequests -> presenter.onRequestsClick()
			else -> Timber.d("No route")
		}
		drawer.closeDrawer(GravityCompat.START)
	}

	private val navigator: Navigator = object: SupportAppNavigator(this, Screens.NOT_USED) {
		protected override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
			when(screenKey) {
				Screens.ABOUT -> return Intent(this@MainActivity, AboutActivity::class.java)
				Screens.FIND_ADDRESS -> {
					val intent = Intent(this@MainActivity, SearchActivity::class.java)
					@Suppress("UNCHECKED_CAST")
					val pair = data as Pair<String, String>
					intent.putExtra(SearchActivity.EXTRA_ADDRESS_FROM, pair.first)
					intent.putExtra(SearchActivity.EXTRA_ADDRESS_TO, pair.second)
					return intent
				}
				Screens.CREATE_ORDER -> return Intent(this@MainActivity, CreateOrderActivity::class.java)
				Screens.SETTINGS -> return Intent(this@MainActivity, SettingsActivity::class.java)
				Screens.LOGIN -> return Intent(this@MainActivity, LoginActivity::class.java)
				Screens.REQUESTS -> return Intent(this@MainActivity, RequestsActivity::class.java)
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

		protected override fun showSystemMessage(message: String) {
			Snackbar.make(drawerLayout, message, Snackbar.LENGTH_SHORT).show()
		}
		
		protected override fun createFragment(screenKey: String, data: Any?): Fragment? = null
	}
	
	companion object {
		@JvmField val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
		@JvmField val MY_LOCATION_BUTTON_INDEX = 2
		@JvmField val COMPASS_BUTTON_INDEX = 5
		@JvmField val FADE_DURATION  = 500L
		@JvmField val MAX_INIT_ZOOM = 2.0f
	}


	init {
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
	}
	
	@CallSuper
	protected override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		
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
				if(newState == DrawerLayout.STATE_SETTLING) Utils.hideKeyboard(this@MainActivity, currentFocus)
			}
		})
		
		/*(navView as NavigationView).setNavigationItemSelectedListener { item ->
            when(item.itemId) {
				R.id.navLogin -> presenter.onLoginClick()
                R.id.navAbout -> presenter.onAboutClick()
                R.id.navSettings -> presenter.onSettingsClick()
				R.id.nav_archived_rides -> presenter.onRequestsClick()
                else -> Timber.d("No route for ${item.title}")
            }
            drawer.closeDrawer(GravityCompat.START)
            true
        }*/

        (appbar as AppBarLayout).bringToFront()
		
		initNavigation()
		
		val mapViewBundle = savedInstanceState?.getBundle(MAP_VIEW_BUNDLE_KEY)
		isFirst = savedInstanceState == null
		
		initGoogleMap(mapViewBundle)
		
		search.elevation = resources.getDimension(R.dimen.search_elevation)
		searchFrom.setUneditable()
		searchTo.setUneditable()
		searchFrom.setOnClickListener { presenter.onSearchClick(Pair(searchFrom.text, searchTo.text)) }
		searchTo.setOnClickListener { presenter.onSearchClick(Pair(searchFrom.text, searchTo.text)) }

		val fade = Fade()
		fade.setDuration(FADE_DURATION)
		getWindow().setExitTransition(fade)
	}

	@CallSuper
	protected override fun onPostCreate(savedInstanceState: Bundle?) {
		super.onPostCreate(savedInstanceState)
		toggle.syncState()
	}
	
	@CallSuper
	protected override fun onStart() {
		super.onStart()
		mapView.onStart()
	}

	@CallSuper
	protected override fun onResume() {
		super.onResume()
		Utils.hideKeyboard(this, currentFocus)
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
	override fun onBackPressed() {
		if(drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
		else presenter.onBackCommandClick() 
	}
	
	@CallSuper
	protected override fun onStop() {
		mapView.onStop()
		searchTo.text = ""
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
	
	/** @see {@link android.support.v7.app.ActionBarDrawerToggle} */
	@CallSuper
	override fun onConfigurationChanged(newConfig: Configuration) {
	 	 super.onConfigurationChanged(newConfig)
	 	 toggle.onConfigurationChanged(newConfig)
	}
	
	/** @see {@link android.support.v7.app.ActionBarDrawerToggle} */
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return toggle.onOptionsItemSelected(item)
	}
	
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
	
	/**
	 * Грязный хак — меняем положение нативной кнопки 'MyLocation'
	 * https://stackoverflow.com/questions/36785542/how-to-change-the-position-of-my-location-button-in-google-maps-using-android-st
	 */
	private fun customizeGoogleMaps() {
	    googleMap.uiSettings.setRotateGesturesEnabled(false)
		googleMap.setMyLocationEnabled(true)
		googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json))
		
		val parent = (mapView?.findViewById(1) as View).parent as View
		val myLocationBtn = parent.findViewById(MY_LOCATION_BUTTON_INDEX) as View
		val rlp = myLocationBtn.getLayoutParams() as RelativeLayout.LayoutParams 
		// position on right bottom
		rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
		rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
		
		rlp.setMargins(0, 0, 
			resources.getDimension(R.dimen.location_button_margin_end).toInt(),
			resources.getDimension(R.dimen.location_button_margin_bottom).toInt())
		
		val compassBtn = parent.findViewById(COMPASS_BUTTON_INDEX) as View
		val rlp1 = compassBtn.getLayoutParams() as RelativeLayout.LayoutParams 
		// position on right bottom
		rlp1.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
		rlp1.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
		
		rlp1.setMargins(resources.getDimension(R.dimen.compass_button_margin_start).toInt(), 0, 
			0, resources.getDimension(R.dimen.compass_button_margin_bottom).toInt())
		
        googleMap.setOnMyLocationButtonClickListener {
            presenter.updateCurrentLocation()
            true
        }
        googleMap.setOnCameraMoveListener { presenter.onCameraMove(googleMap.getCameraPosition()!!.target) }
        googleMap.setOnCameraIdleListener { presenter.onCameraIdle() }
    }
    
    /* MainView */
    
    override fun blockInterface(block: Boolean) {}
    
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
        if(centerMarker != null) centerMarker!!.setPosition(point)
    }
	
	override fun setAddressFrom(address: String) { searchFrom.text = address }
	
	override fun setError(@StringRes errId: Int, finish: Boolean) {
		Utils.showError(this, errId, finish)
    }

	override fun showLoginInfo(account: Account) {
	    Timber.d("show Login: %s", account)
	    if(account.email == null) {
	        /*headerView.navHeaderName.visibility = View.GONE
	        headerView.navHeaderEmail.visibility = View.GONE
	        navView.menu.findItem(R.id.navLogin).isVisible = true*/

			navHeaderName.visibility = View.GONE
			navHeaderEmail.visibility = View.GONE
			navLogin.visibility = View.VISIBLE
			navRequests.visibility = View.GONE
	    }
	    else {
	        /*headerView.navHeaderName.visibility = View.VISIBLE
	        headerView.navHeaderEmail.visibility = View.VISIBLE
	        headerView.navHeaderName.text = account.fullName
	        headerView.navHeaderEmail.text = account.email
	        navView.menu.findItem(R.id.navLogin).isVisible = false*/

			navHeaderName.visibility = View.VISIBLE
			navHeaderEmail.visibility = View.VISIBLE
			navHeaderName.text = account.fullName
			navHeaderEmail.text = account.email
			navLogin.visibility = View.GONE
			navRequests.visibility = View.VISIBLE
	    }
	}
}
