package com.kg.gettransfer.presentation.ui

import android.Manifest

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.pm.PackageManager

import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.design.widget.AppBarLayout
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar

import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction

import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout

import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatDelegate

import android.support.v7.widget.Toolbar

import android.transition.Fade
import android.transition.Slide

import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager

import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.MapView

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.presenter.MainPresenter
import com.kg.gettransfer.presentation.view.MainView

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.search_form.*
import kotlinx.android.synthetic.main.nav_header_main.*

import org.koin.android.ext.android.inject

import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportAppNavigator

import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward
import ru.terrakok.cicerone.commands.Replace

import timber.log.Timber

class MainActivity: MvpAppCompatActivity(), MainView {
	@InjectPresenter
	internal lateinit var presenter: MainPresenter
	
	private lateinit var drawer: DrawerLayout
	private lateinit var toggle: ActionBarDrawerToggle
	
	var permissionsGranted = false
	
	private val navigatorHolder: NavigatorHolder by inject()
	internal val router: Router by inject()
	
	private var gmap: GoogleMap? = null
	private var centerMarker: Marker? = null
	
	@ProvidePresenter
	fun createMainPresenter(): MainPresenter = MainPresenter(router)

	
	private val focusListener = View.OnFocusChangeListener {_, hasFocus ->
		if(hasFocus) {
			presenter.onSearchClick()
		}
	}

	private val readMoreListener = View.OnClickListener {
		presenter.readMoreClick()
	}

	private val navigator: Navigator = object: SupportAppNavigator(this, NOT_USED) {
		protected override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
			when(screenKey) {
				Screens.ABOUT -> return Intent(this@MainActivity, AboutActivity::class.java)
				Screens.FIND_ADDRESS -> return Intent(this@MainActivity, SearchActivity::class.java)
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
		private val PERMISSIONS = arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
		private val NOT_USED = -1
		const val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"
		const val PERMISSION_REQUEST = 2211
		const val MY_LOCATION_BUTTON_INDEX = 2
		const val COMPASS_BUTTON_INDEX = 5
	}

	init {
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
	}
	
	@CallSuper
	protected override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
			permissionsGranted = checkPermission()
		presenter.granted = true
		
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
				if(newState == DrawerLayout.STATE_SETTLING) {
					// Андроид, такой андроид :-) 
					// https://stackoverflow.com/questions/1109022/close-hide-the-android-soft-keyboard
					val view = currentFocus
					if(view != null)
						(getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
							.hideSoftInputFromWindow(view.windowToken, 0)
				}
			}
		})
		
		(navView as NavigationView).setNavigationItemSelectedListener({ item ->
			Timber.d("nav view item ${item.title}")
			when(item.itemId) {
				R.id.nav_about -> presenter.onAboutClick()
				else -> Timber.d("No route for ${item.title}")
			}
			drawer.closeDrawer(GravityCompat.START)
			true
		})

		(appbar as AppBarLayout).bringToFront()
		
		initNavigation()
		
		Timber.d("Permissions granted: ${permissionsGranted}")
		if(!permissionsGranted) Snackbar.make(drawerLayout, "Permissions not granted", Snackbar.LENGTH_SHORT).show()
		else initGoogleMap(savedInstanceState)
		
		val fade = Fade()
		fade.setDuration(500)
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
		if(permissionsGranted) mapView?.onStart()
	}

	@CallSuper
	protected override fun onResume() {
		super.onResume()
		navigatorHolder.setNavigator(navigator)
		if(permissionsGranted) mapView?.onResume()
	}
	
	@CallSuper
	protected override fun onPause() {
		navigatorHolder.removeNavigator()
		if(permissionsGranted) mapView?.onPause()
		super.onPause()
	}
	
	@CallSuper
	override fun onBackPressed() {
		if(drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
		else presenter.onBackCommandClick() 
	}
	
	@CallSuper
	protected override fun onStop() {
		if(permissionsGranted) mapView?.onStop()
		super.onStop()
	}
	
	@CallSuper
	protected override fun onDestroy() {
		if(permissionsGranted) mapView?.onDestroy()
		super.onDestroy()
	}
	
	@CallSuper
	override fun onLowMemory() {
		if(permissionsGranted) mapView?.onLowMemory()
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
	
	/**
	 * @return true — не требуется разрешение пользователя
	 */
	private fun checkPermission(): Boolean {
		if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
		   ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQUEST)
				return false
		}
		return true
	}
	
	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
		if(requestCode != PERMISSION_REQUEST) return
		if(grantResults.size == 2 && 
			grantResults[0] == PackageManager.PERMISSION_GRANTED &&
			grantResults[1] == PackageManager.PERMISSION_GRANTED) recreate()
		else finish()
	}





	private fun initNavigation() {
		val versionName = packageManager.getPackageInfo(packageName, 0).versionName
		(navFooterVersion as TextView).text = 
			String.format(getString(R.string.nav_footer_version), versionName)
		navFooterStamp.setOnClickListener(readMoreListener)
		navFooterReadMore.setOnClickListener(readMoreListener)
		
		val shareBtn: ImageView = (navView as NavigationView).getHeaderView(0).findViewById(R.id.nav_header_share) as ImageView
		shareBtn.setOnClickListener {
			Timber.d("Share action")
		}
	}
	
	private fun initGoogleMap(savedInstanceState: Bundle?) {
		var mapViewBundle: Bundle? = null
		if(savedInstanceState != null)
			mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
		mapView?.onCreate(mapViewBundle)
		mapView?.getMapAsync({ gmap -> 
			this.gmap = gmap
			/*
			this.gmap!!.setOnMyLocationButtonClickListener(OnMyLocationButtonClickListener {
				onClickMyLocation()
				true
			})
			this.gmap!!.setOnCameraMoveListener(this)
			presenter.updateCurrentLocation()
			*/
			customizeGoogleMaps()
			//presenter.updateCurrentLocation()
		})
		searchFrom.address.setOnFocusChangeListener(focusListener)
		searchTo.address.setOnFocusChangeListener(focusListener)
	}
	
	/**
	 * Грязный хак — меняем положение нативной кнопки 'MyLocation'
	 * https://stackoverflow.com/questions/36785542/how-to-change-the-position-of-my-location-button-in-google-maps-using-android-st
	 */
	private fun customizeGoogleMaps() {
		gmap!!.setMyLocationEnabled(true)
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
	}

	/* MainView */
	override fun qqq() {
		Timber.d("MainActivity.qqq")
	}
}
