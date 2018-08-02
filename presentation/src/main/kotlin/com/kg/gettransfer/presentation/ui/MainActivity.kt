package com.kg.gettransfer.presentation.ui

import android.Manifest

import android.content.pm.PackageManager

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable

import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.design.widget.AppBarLayout
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar

import android.support.transition.AutoTransition

import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction

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
import android.widget.ImageView
import android.widget.TextView

import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.MainPresenter
import com.kg.gettransfer.presentation.view.MainView

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*

import org.koin.android.ext.android.inject
import org.koin.standalone.KoinComponent

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
	internal var toolbarTransparent = true
		set(value) {
			toggle.setDrawerIndicatorEnabled(value)
			supportActionBar?.setDisplayHomeAsUpEnabled(!value)
			supportActionBar?.setDisplayShowHomeEnabled(!value)
			if(value) {
				appbar.setBackgroundColor(android.R.color.transparent)
				toolbar.setBackground(null)
				drawer.addDrawerListener(toggle)
				toggle.syncState()
			}
			else {
				val toolbarBackground = resources.getColor(R.color.colorPrimary, null)
				appbar.setBackgroundColor(toolbarBackground)
				toolbar.setBackgroundColor(toolbarBackground)
				drawer.removeDrawerListener(toggle)
				(toolbar as Toolbar).setNavigationOnClickListener{_ ->
					Timber.d("navigation on click listener. isTransparent: $toolbarTransparent")
					onBackPressed()
				}
			}
		}
	var permissionsGranted = false
	
	private val navigatorHolder: NavigatorHolder by inject()
	internal val router: Router by inject();
	
	private val readMoreListener: View.OnClickListener = View.OnClickListener {
		Timber.d("read more clicked")
	}

	private val navigator: Navigator = object: SupportFragmentNavigator(supportFragmentManager, R.id.container) {
		override fun createFragment(screenKey: String, data: Any?): Fragment {
			when(screenKey) {
				START_SCREEN -> return StartFragment.getNewInstance(data)
				START_SEARCH_SCREEN -> return StartSearchFragment.getNewInstance(data)
				ABOUT_SCREEN -> return AboutFragment.getNewInstance(data)
				else -> throw RuntimeException("Unknown screen key!")
			}
		}

		override fun showSystemMessage(message: String) {
			Snackbar.make(drawerLayout, message, Snackbar.LENGTH_SHORT).show()
		}

		override fun exit() {
			finish()
		}
		
		@CallSuper
		override fun applyCommand(command: Command) {
			super.applyCommand(command)
		}
		
		override fun setupFragmentTransactionAnimation(command: Command, currentFragment: Fragment?, nextFragment: Fragment?, fragmentTransaction: FragmentTransaction) {
			if(command is Forward &&
			   currentFragment is StartFragment &&
		       nextFragment is StartSearchFragment)
		    		setupSharedElement(currentFragment, nextFragment, fragmentTransaction)
        }
	}

	companion object
	{
		private val PERMISSIONS = arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
		
		public val START_SCREEN           = "start_screen"
		public val START_SEARCH_SCREEN    = "start_search_screen"
		public val ACTIVE_RIDES_SCREEN    = "active_rides"
		public val ARCHIVED_RIDES_SCREEN  = "archived_rides"
		public val SETTINGS_SCREEN        = "settings"
		public val ABOUT_SCREEN           = "about" 
	}

	init {
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
	}
	
	@ProvidePresenter
	fun createMainPresenter(): MainPresenter = MainPresenter(router)
	
	@CallSuper
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
			permissionsGranted = checkPermission()
		
		setContentView(R.layout.activity_main)
		
		val tb = this.toolbar as Toolbar
		
		setSupportActionBar(tb)
		getSupportActionBar()?.setDisplayShowTitleEnabled(false)
		
		drawer = drawerLayout as DrawerLayout
		toggle = ActionBarDrawerToggle(this, drawer, tb, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		
		(navView as NavigationView).setNavigationItemSelectedListener({ item ->
			Timber.d("nav view item ${item.title}")
			when(item.itemId) {
				R.id.nav_about -> router.navigateTo(ABOUT_SCREEN)
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
			if(savedInstanceState == null) navigator.applyCommands(arrayOf<Command>(Replace(START_SCREEN, null)))
		}
		else Snackbar.make(drawerLayout, "Permissions not granted", Snackbar.LENGTH_SHORT).show()
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
	
	@CallSuper
	override fun onResumeFragments() {
		super.onResumeFragments()
		navigatorHolder.setNavigator(navigator)
	}
	
	@CallSuper
	override fun onPause() {
		navigatorHolder.removeNavigator()
		super.onPause()
	}
	
	@CallSuper
	override fun onBackPressed() {
		if(drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
		else {
			val fragment = supportFragmentManager.findFragmentById(R.id.container)
			if(fragment != null && 
			   fragment is BackButtonListener && 
		       (fragment as BackButtonListener).onBackPressed()) return
		    else super.onBackPressed() 
		}
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

	private fun setupSharedElement(currentFragment: StartFragment?,
		    			           nextFragment: StartSearchFragment?,
		    			           fragmentTransaction: FragmentTransaction) {
		if(currentFragment == null || nextFragment == null) return
		//val changeBounds = ChangeBounds()
		val changeBounds = AutoTransition()
		currentFragment.setSharedElementEnterTransition(changeBounds)
		currentFragment.setSharedElementReturnTransition(changeBounds)
		nextFragment.setSharedElementEnterTransition(changeBounds)
		nextFragment.setSharedElementReturnTransition(changeBounds)
		changeBounds.duration = MOVE_TIME
		changeBounds.ordering = AutoTransition.ORDERING_TOGETHER
        val search = currentFragment.getSearchForm()
        fragmentTransaction.addSharedElement(search, search.transitionName)
	}
	
	/* MainView */
	override fun qqq() {
		Timber.d("MainActivity.qqq")
	}
}
