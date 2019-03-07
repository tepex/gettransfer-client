package com.kg.gettransfer.presentation.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import com.kg.gettransfer.R
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.support.annotation.CallSuper
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.model.ProfileModel
import com.kg.gettransfer.presentation.presenter.CarrierTripsMainPresenter
import com.kg.gettransfer.presentation.view.CarrierTripsMainView
import com.kg.gettransfer.presentation.view.Screens
import kotlinx.android.synthetic.main.activity_carrier_trips_main.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_navigation.*
import kotlinx.android.synthetic.main.view_navigation.view.*
import timber.log.Timber

class CarrierTripsMainActivity : BaseActivity(), CarrierTripsMainView {
    @InjectPresenter
    internal lateinit var presenter: CarrierTripsMainPresenter

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    @ProvidePresenter
    fun createCarrierTripsMainPresenter() = CarrierTripsMainPresenter()

    private val readMoreListener = View.OnClickListener { presenter.readMoreClick() }

    private val itemsNavigationViewListener = View.OnClickListener {
        with(presenter) {
            when (it.id) {
                R.id.navCarrierTrips     -> onCarrierTripsClick()
                R.id.navAbout            -> onAboutClick()
                R.id.navSettings         -> onSettingsClick()
                R.id.navPassengerMode    -> onPassengerModeClick()
                R.id.navCarrierTransfers -> onTransfersClick()
                else -> Timber.d("No route")
            }
            drawer.closeDrawer(GravityCompat.START)
        }
    }

    override fun getPresenter(): CarrierTripsMainPresenter = presenter

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_carrier_trips_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorWhite)
        }

        setToolbar(toolbar as Toolbar, R.string.LNG_MENU_TITLE_TRIPS, false, true)
        drawer = drawerLayout as DrawerLayout
        toggle = ActionBarDrawerToggle(this, drawer, toolbar as Toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        (toolbar as Toolbar).carrierTripsChangingTypeViewButtons.isVisible = true
        (toolbar as Toolbar).buttonListView.setOnClickListener { presenter.changeTypeView(Screens.CARRIER_TRIPS_TYPE_VIEW_LIST) }
        (toolbar as Toolbar).buttonCalendarView.setOnClickListener { presenter.changeTypeView(Screens.CARRIER_TRIPS_TYPE_VIEW_CALENDAR) }
        setViewColor((toolbar as Toolbar), R.color.colorWhite)
        initNavigation()
        startCoordinateService()
    }

    @CallSuper
    protected override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        stopCoordinateService()
    }

    @CallSuper
    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START) else super.onBackPressed()
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
        val versionName = BuildConfig.VERSION_NAME
        val versionCode = BuildConfig.VERSION_CODE
        (navFooterVersion as TextView).text =
                String.format(getString(R.string.nav_footer_version), versionName, versionCode)
        //navFooterReadMore.text = Html.fromHtml(Utils.convertMarkdownToHtml(getString(R.string.LNG_READMORE)))
        navFooterStamp.setOnClickListener(readMoreListener)
        navFooterReadMore.setOnClickListener(readMoreListener)

        navHeaderShare.setOnClickListener { Timber.d("Share action") }

        navCarrierTrips.setOnClickListener(itemsNavigationViewListener)
        navSettings.setOnClickListener(itemsNavigationViewListener)
        navAbout.setOnClickListener(itemsNavigationViewListener)
        navPassengerMode.setOnClickListener(itemsNavigationViewListener)
        navCarrierTransfers.setOnClickListener(itemsNavigationViewListener)

        setViewColor(navViewHeader, R.color.primaryDriver)
        navViewHeader.navHeaderMode.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        navViewHeader.navHeaderName.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
        navViewHeader.navHeaderEmail.setTextColor(ContextCompat.getColor(this, R.color.colorWhite))
    }

    override fun initNavigation(profile: ProfileModel) {
        navHeaderMode.text = getString(R.string.LNG_MENU_TITLE_DRIVE)
        navHeaderName.isVisible = true
        navHeaderEmail.isVisible = true
        navHeaderName.text = profile.name
        navHeaderEmail.text = profile.email

        navLogin.isVisible = false
        navCarrierTrips.isVisible = true
        navBecomeACarrier.isVisible = false
        navPassengerMode.isVisible = true
        navCarrierTrips.isVisible  = true
        navCarrierTransfers.isVisible = true

        navFooterStamp.isVisible = false
        navMessage.isVisible = false
        navFooterReadMore.isVisible = false
    }

    override fun showReadMoreDialog() {
        ReadMoreFragment().show(supportFragmentManager, getString(R.string.tag_read_more))
    }

    override fun changeTypeView(type: String) {
        val transaction: FragmentTransaction
        when(type){
            Screens.CARRIER_TRIPS_TYPE_VIEW_CALENDAR -> {
                transaction = supportFragmentManager.beginTransaction()
                transaction.replace(fragmentsFrameLayout.id, CarrierTripsCalendarFragment())
                changeButtonModeInTitle(true)
            }
            Screens.CARRIER_TRIPS_TYPE_VIEW_LIST -> {
                transaction = supportFragmentManager.beginTransaction()
                transaction.replace(fragmentsFrameLayout.id, CarrierTripsListFragment())
                changeButtonModeInTitle(false)
            }
            else -> {
                transaction = supportFragmentManager.beginTransaction()
                transaction.replace(fragmentsFrameLayout.id, CarrierTripsCalendarFragment())
                changeButtonModeInTitle(true)
            }
        }
        transaction.commit()
    }

    private fun changeButtonModeInTitle(isCalendarMode: Boolean){
        with((toolbar as Toolbar)) {
            buttonListView.isVisible = isCalendarMode
            buttonCalendarView.isVisible = !isCalendarMode
        }
    }

    override fun askForBackGroundCoordinates() {
        Utils.showBackGroundPermissionDialog(this) { result ->
            presenter.permissionResult(result)
        }
    }

    /* Service */

    private val connectionService = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {}
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {}
    }

    private fun startCoordinateService() {
        Intent(SERVICE_ACTION).also {
            it.setPackage(PACKAGE)
            startService(it)
            bindService(it, connectionService, Context.BIND_AUTO_CREATE)
        }
    }

    private fun stopCoordinateService() {
        Intent(SERVICE_ACTION).also {
            it.setPackage(PACKAGE)
            unbindService(connectionService)
            stopService(it)
        }
    }

    companion object {
        const val SERVICE_ACTION = "com.kg.gettransfer.service.CoordinateService"
        const val PACKAGE        = "com.kg.gettransfer"
    }
}