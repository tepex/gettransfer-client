package com.kg.gettransfer.presentation.ui

import android.Manifest

import android.content.pm.PackageManager

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable

import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.design.widget.AppBarLayout
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar

import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout

import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatDelegate

import android.support.v7.widget.Toolbar

import android.util.SparseArray

import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager

import android.widget.RelativeLayout

import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.MapView

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.MainPresenter
import com.kg.gettransfer.presentation.view.MainView

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*

import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent

import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportFragmentNavigator

import timber.log.Timber

const val MY_LOCATION_BUTTON_INDEX = 2
const val COMPASS_BUTTON_INDEX = 5
const val PERMISSION_REQUEST = 2211
const val MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey"

const val START_SCREEN           = "start_screen"
const val ACTIVE_RIDES_SCREEN    = "active_rides"
const val ARCHIVED_RIDES_SCREEN  = "archived_rides"
const val SETTINGS_SCREEN        = "settings"
const val ABOUT_SCREEN           = "about"

class MainActivity: MvpAppCompatActivity(), MainView {
	@InjectPresenter
	internal lateinit var presenter: MainPresenter
	
	private lateinit var drawer: DrawerLayout
	private var permissionsGranted = true
	private var isFirst = true
	private var gmap: GoogleMap? = null
	private var centerMarker: Marker? = null
	
	private val navigatorHolder: NavigatorHolder by inject()
	private val router: Router by inject();

	private val navigator = object: SupportFragmentNavigator(supportFragmentManager, R.id.container) {
		override protected fun createFragment(screenKey: String, data: Any?): Fragment {
			when(screenKey) {
				START_SCREEN -> return StartFragment.getNewInstance(data)
				ABOUT_SCREEN -> return AboutFragment.getNewInstance(data)
				else -> throw RuntimeException("Unknown screen key!")
			}
		}

		override protected fun showSystemMessage(message: String) {
			Snackbar.make(drawerLayout, message, Snackbar.LENGTH_SHORT).show()
		}

		override protected fun exit() {
			finish()
		}
	}

	companion object
	{
		private val PERMISSIONS = arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
	}

	init {
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
	}
	
	@CallSuper
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
		{
			permissionsGranted = checkPermission()
			presenter.setPermissionsGranted(permissionsGranted)
		}
		
		setContentView(R.layout.activity_main)
		
		val tb = this.toolbar as Toolbar
//		tb.setTitle(R.string.app_name)
		
		setSupportActionBar(tb)
		/*
		getSupportActionBar()?.setDisplayShowTitleEnabled(true)
		getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
		getSupportActionBar()?.setHomeButtonEnabled(true);
		*/
		drawer = drawerLayout as DrawerLayout
		val toggle = ActionBarDrawerToggle(this, drawer, tb, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.addDrawerListener(toggle);
		toggle.syncState();
		var navigationView = navView as NavigationView
		navigationView.setNavigationItemSelectedListener({ item ->
			Timber.d("nav view item ${item.title}")
			when(item.itemId) {
				R.id.nav_about -> router.navigateTo(ABOUT_SCREEN)
				else -> Timber.d("No route for ${item.title}")
			}
			drawer.closeDrawer(GravityCompat.START)
			true
		})

		val abl: AppBarLayout = this.appbar as AppBarLayout
		abl.bringToFront()
		
		/* Transparent status bar */
		window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
		
		var mapViewBundle: Bundle? = null
		if(savedInstanceState != null)
		{
			isFirst = false
			mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY)
		}
		else drawer.openDrawer(GravityCompat.START);
		
		navigatorHolder.setNavigator(navigator)
		
		Timber.d("Permissions granted: ${permissionsGranted}")
//		if(permissionsGranted) startGoogleMap(mapViewBundle)
		if(permissionsGranted) router.newRootScreen(START_SCREEN)
		else Snackbar.make(drawerLayout, "Permissions not granted", Snackbar.LENGTH_SHORT).show()
	}
	
/*
	private fun startGoogleMap(mapViewBundle: Bundle?) {
		mapView.onCreate(mapViewBundle)
		mapView.getMapAsync({ gmap -> 
			this.gmap = gmap
			this.gmap!!.setOnMyLocationButtonClickListener(OnMyLocationButtonClickListener {
				onClickMyLocation()
				true
			})
//			this.gmap!!.setOnCameraMoveListener(this)
			presenter.updateCurrentLocation()
			customizeGoogleMaps()
		})
	}
	
	@CallSuper
	override fun onSaveInstanceState(outState: Bundle) {
		super.onSaveInstanceState(outState)
		var mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY)
		if(mapViewBundle == null) {
			mapViewBundle = Bundle()
			outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle)
		}
		if(permissionsGranted) mapView.onSaveInstanceState(mapViewBundle)
	}
	*/

	@CallSuper
	override fun onBackPressed() {
		if(drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
		else super.onBackPressed();
	}
	
	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.share_menu, menu)
		
		for(i in 0 until menu.size()) {
			val drawable = menu.getItem(i).icon
			if(drawable != null) {
				drawable.mutate()
				drawable.setColorFilter(resources.getColor(android.R.color.black, null), PorterDuff.Mode.SRC_ATOP)
			}
		}
		return true
	}
	
	@CallSuper
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when(item.itemId) {
			R.id.action_share -> {
				Snackbar.make(drawerLayout, "Share clicked", Snackbar.LENGTH_SHORT).show()
				return true
			}
		}
		return super.onOptionsItemSelected(item)
	}

	/*
	@CallSuper
	override fun onStart() {
		super.onStart()
		if(permissionsGranted) mapView.onStart()
	}
	
	@CallSuper
	override fun onResume() {
		super.onResume()
		if(permissionsGranted) mapView.onResume()
	}
	
	@CallSuper
	override fun onPause() {
		if(permissionsGranted) mapView.onPause()
		super.onPause()
	}
	
	@CallSuper
	override fun onStop() {
		if(permissionsGranted) mapView.onStop()
		super.onStop()
	}
	*/
		
	@CallSuper
	override fun onDestroy() {
//		if(permissionsGranted) mapView.onDestroy()
		navigatorHolder.removeNavigator();
		super.onDestroy()
	}
	
	/*
	@CallSuper
	override fun onLowMemory() {
		if(permissionsGranted) mapView.onLowMemory()
		super.onLowMemory()
	}
	*/
	
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

	/* MainView */
	override fun onClickMyLocation() {
		presenter.updateCurrentLocation()
	}
	
	/**
	 * Грязный хак — меняем положение нативной кнопки 'MyLocation'
	 * https://stackoverflow.com/questions/36785542/how-to-change-the-position-of-my-location-button-in-google-maps-using-android-st
	 */
	 
	 /*
	private fun customizeGoogleMaps() {
		gmap!!.setMyLocationEnabled(true)
		val parent = (mapView.findViewById(1) as View).parent as View
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
	*/
}
