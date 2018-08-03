package com.kg.gettransfer.presentation.ui

import android.Manifest

import android.content.res.Configuration
import android.content.pm.PackageManager

import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.design.widget.AppBarLayout
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar

import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction

import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout

import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatDelegate

import android.support.v7.widget.Toolbar

import android.view.MenuItem
import android.view.View

import android.widget.ImageView
import android.widget.TextView

import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.presenter.MainPresenter
import com.kg.gettransfer.presentation.view.MainView

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

import org.koin.android.ext.android.inject

import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportFragmentNavigator

import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward
import ru.terrakok.cicerone.commands.Replace

import timber.log.Timber

const val PERMISSION_REQUEST = 2211
const val MOVE_TIME: Long = 300

class MainActivity: MvpAppCompatActivity(), MainView {
	@InjectPresenter
	internal lateinit var presenter: MainPresenter
	
	private lateinit var drawer: DrawerLayout
	private lateinit var toggle: ActionBarDrawerToggle
	
	var permissionsGranted = false
	
	private val navigatorHolder: NavigatorHolder by inject()
	internal val router: Router by inject();
	
	private val readMoreListener: View.OnClickListener = View.OnClickListener {
		presenter.readMoreClick()
	}

	private val navigator: Navigator = object: SupportFragmentNavigator(supportFragmentManager, R.id.container) {
		override fun createFragment(screenKey: String, data: Any?): Fragment {
			when(screenKey) {
				Screens.START_SCREEN -> return StartFragment.getNewInstance(data)
				else -> throw RuntimeException("Unknown screen key!")
			}
		}

		override fun showSystemMessage(message: String) {
			Snackbar.make(drawerLayout, message, Snackbar.LENGTH_SHORT).show()
		}

		override fun exit() {
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
	protected override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
			permissionsGranted = checkPermission()
		
		setContentView(R.layout.activity_main)
		
		val tb = this.toolbar as Toolbar
		
		setSupportActionBar(tb)
		supportActionBar?.setDisplayShowTitleEnabled(false)
		supportActionBar?.setDisplayHomeAsUpEnabled(false)
		supportActionBar?.setDisplayShowHomeEnabled(false)
		
		drawer = drawerLayout as DrawerLayout
		toggle = ActionBarDrawerToggle(this, drawer, tb, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		
		(navView as NavigationView).setNavigationItemSelectedListener({ item ->
			Timber.d("nav view item ${item.title}")
			when(item.itemId) {
				R.id.nav_about -> router.navigateTo(Screens.ABOUT_SCREEN)
				else -> Timber.d("No route for ${item.title}")
			}
			drawer.closeDrawer(GravityCompat.START)
			true
		})

		(appbar as AppBarLayout).bringToFront()
		
		//navigatorHolder.setNavigator(navigator)
	
		initNavigation()
		
		Timber.d("Permissions granted: ${permissionsGranted}")
		if(permissionsGranted) {
			if(savedInstanceState == null) navigator.applyCommands(arrayOf<Command>(Replace(Screens.START_SCREEN, null)))
		}
		else Snackbar.make(drawerLayout, "Permissions not granted", Snackbar.LENGTH_SHORT).show()
	}

	@CallSuper
	protected override fun onPostCreate(savedInstanceState: Bundle?) {
		super.onPostCreate(savedInstanceState)
		toggle.syncState()
	}

	@CallSuper
	protected override fun onResume() {
		super.onResume()
		navigatorHolder.setNavigator(navigator)
	}
	
	@CallSuper
	protected override fun onPause() {
		navigatorHolder.removeNavigator()
		super.onPause()
	}
	
	@CallSuper
	override fun onBackPressed() {
		if(drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
		else super.onBackPressed() 
	}
	
	@CallSuper
	protected override fun onDestroy() {
		
		
		
		super.onDestroy()
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


	/* MainView */
	override fun qqq() {
		Timber.d("MainActivity.qqq")
	}
}
