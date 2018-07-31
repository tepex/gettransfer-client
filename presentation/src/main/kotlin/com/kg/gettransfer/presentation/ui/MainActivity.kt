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

import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager

import android.widget.Button
import android.widget.TextView

import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter

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

const val PERMISSION_REQUEST = 2211

const val START_SCREEN           = "start_screen"
const val ACTIVE_RIDES_SCREEN    = "active_rides"
const val ARCHIVED_RIDES_SCREEN  = "archived_rides"
const val SETTINGS_SCREEN        = "settings"
const val ABOUT_SCREEN           = "about"

class MainActivity: MvpAppCompatActivity(), MainView {
	@InjectPresenter
	internal lateinit var presenter: MainPresenter
	
	private lateinit var drawer: DrawerLayout
	var permissionsGranted = true
	
	private val navigatorHolder: NavigatorHolder by inject()
	private val router: Router by inject();
	
	private val readMoreListener: View.OnClickListener = View.OnClickListener {
		Timber.d("read more clicked")
	}


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
		
		setSupportActionBar(tb)
		getSupportActionBar()?.setDisplayShowTitleEnabled(false)
		
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
		
		if(savedInstanceState == null) drawer.openDrawer(GravityCompat.START)
		
		navigatorHolder.setNavigator(navigator)
	
		initNavigationFooter();
		
		Timber.d("Permissions granted: ${permissionsGranted}")
		if(permissionsGranted) router.newRootScreen(START_SCREEN)
		else Snackbar.make(drawerLayout, "Permissions not granted", Snackbar.LENGTH_SHORT).show()
	}

	private fun initNavigationFooter() {
		val versionName = packageManager.getPackageInfo(packageName, 0).versionName
		(navFooterVersion as TextView).text = 
			String.format(getString(R.string.nav_footer_version), versionName)
		navFooterStamp.setOnClickListener(readMoreListener)
		navFooterReadMore.setOnClickListener(readMoreListener)
	}

	@CallSuper
	override fun onBackPressed() {
		if(drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
		else super.onBackPressed();
	}
	
	@CallSuper
	override fun onDestroy() {
		navigatorHolder.removeNavigator();
		super.onDestroy()
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

	/* MainView */
	override fun onClickMyLocation() {
		presenter.updateCurrentLocation()
	}
}
