package com.kg.gettransfer.presentation.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
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

import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.TextView

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.model.ProfileModel
import com.kg.gettransfer.presentation.presenter.CarrierTripsMainPresenter
import com.kg.gettransfer.presentation.ui.dialogs.AboutDriverAppDialogFragment
import com.kg.gettransfer.presentation.view.CarrierTripsMainView
import com.kg.gettransfer.presentation.view.Screens

import kotlinx.android.synthetic.main.activity_carrier_trips_main.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_navigation.*

import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

import timber.log.Timber

@Suppress("TooManyFunctions")
class CarrierTripsMainActivity : BaseActivity(),
    AboutDriverAppDialogFragment.OnAboutDriverAppListener,
    CarrierTripsMainView {

    @InjectPresenter
    internal lateinit var presenter: CarrierTripsMainPresenter

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private var isServiceStart = false

    @ProvidePresenter
    fun createCarrierTripsMainPresenter() = CarrierTripsMainPresenter()

    private val readMoreListener = View.OnClickListener { presenter.readMoreClick() }

    private val itemsNavigationViewListener = View.OnClickListener { view ->
        with(presenter) {
            when (view.id) {
                R.id.navCarrierTrips     -> onCarrierTripsClick()
                R.id.navAbout            -> onAboutClick()
                R.id.navSettings         -> onSettingsClick()
                R.id.navSupport          -> onSupportClick()
                R.id.navPassengerMode    -> onPassengerModeClick()
                R.id.navCarrierTransfers -> onTransfersClick()
                R.id.navHeaderShare      -> onShareClick()
                else                     -> Timber.d("No route")
            }
            drawer.closeDrawer(GravityCompat.START)
        }
    }

    override fun getPresenter(): CarrierTripsMainPresenter = presenter

    @CallSuper
    @Suppress("UnsafeCast")
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_carrier_trips_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorWhite)
        }

        viewNetworkNotAvailable = textNetworkNotAvailable

        setToolbar(toolbar as Toolbar, R.string.LNG_MENU_TITLE_TRIPS, false, true)
        drawer = drawerLayout as DrawerLayout
        toggle = ActionBarDrawerToggle(
            this,
            drawer,
            toolbar as Toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        with(toolbar as Toolbar) {
            carrierTripsChangingTypeViewButtons.isVisible = true
            buttonListView.setOnClickListener { presenter.changeTypeView(Screens.CARRIER_TRIPS_TYPE_VIEW_LIST) }
            buttonCalendarView.setOnClickListener { presenter.changeTypeView(Screens.CARRIER_TRIPS_TYPE_VIEW_CALENDAR) }
        }
        setViewColor(toolbar as Toolbar, R.color.colorWhite)
        initNavigation()
        try {
            checkPermission()
        } catch (e: IllegalStateException) {
            Timber.e("Unexpected start of Coordiante service in ${this::class.java.name}")
        }
    }

    @AfterPermissionGranted(RC_LOCATION)
    private fun checkPermission() {
        if (!EasyPermissions.hasPermissions(this, *PERMISSIONS)) {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.LNG_LOCATION_ACCESS),
                RC_LOCATION, *PERMISSIONS
            )
        } else {
            presenter.initGoogleApiClient()
            startCoordinateService()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @CallSuper
    protected override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
        presenter.checkDriverAppNotify()
    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
        if (isServiceStart) {
            isServiceStart = false
            stopCoordinateService()
            presenter.disconnectGoogleApiClient()
        }
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

    override fun showDriverAppNotify() {
        AboutDriverAppDialogFragment.newInstance().show(supportFragmentManager, AboutDriverAppDialogFragment.DIALOG_TAG)
    }

    private fun initNavigation() {
        val versionName = BuildConfig.VERSION_NAME
        val versionCode = BuildConfig.VERSION_CODE
        @Suppress("UnsafeCast")
        (navFooterVersion as TextView).text =
            String.format(getString(R.string.nav_footer_version), versionName, versionCode)
        // navFooterReadMore.text = Html.fromHtml(Utils.convertMarkdownToHtml(getString(R.string.LNG_READMORE)))

        with(readMoreListener) {
            navFooterStamp.setOnClickListener(this)
            navFooterReadMore.setOnClickListener(this)
        }

        with(itemsNavigationViewListener) {
            navHeaderShare.setOnClickListener(this)
            navCarrierTrips.setOnClickListener(this)
            navSettings.setOnClickListener(this)
            navSupport.setOnClickListener(this)
            navAbout.setOnClickListener(this)
            navPassengerMode.setOnClickListener(this)
            navCarrierTransfers.setOnClickListener(this)
        }
    }

    override fun initNavigation(profile: ProfileModel) {
        navHeaderMode.text = getString(R.string.LNG_MENU_TITLE_DRIVE)
        groupMode.isVisible = true
        navHeaderName.isVisible = true
        navHeaderEmail.isVisible = true
        navHeaderName.text = profile.name
        navHeaderEmail.text = profile.email

        navLogin.isVisible = false
        navCarrierTrips.isVisible = true
        navBecomeACarrier.isVisible = false
        navPassengerMode.isVisible = true
        navCarrierTransfers.isVisible = true
        navNewTransfer.isVisible = false

        navFooterStamp.isVisible = false
        navMessage.isVisible = false
        navFooterReadMore.isVisible = false
    }

    override fun showReadMoreDialog() {
        ReadMoreFragment().show(supportFragmentManager, getString(R.string.tag_read_more))
    }

    override fun changeTypeView(type: String) {
        val transaction = supportFragmentManager.beginTransaction()
        if (type == Screens.CARRIER_TRIPS_TYPE_VIEW_LIST) {
            transaction.replace(fragmentsFrameLayout.id, CarrierTripsListFragment())
            changeButtonModeInTitle(false)
        } else {
            transaction.replace(fragmentsFrameLayout.id, CarrierTripsCalendarFragment())
            changeButtonModeInTitle(true)
        }
        transaction.commitAllowingStateLoss()
    }

    private fun changeButtonModeInTitle(isCalendarMode: Boolean) {
        @Suppress("UnsafeCast")
        with(toolbar as Toolbar) {
            buttonListView.isVisible = isCalendarMode
            buttonCalendarView.isVisible = !isCalendarMode
        }
    }

    override fun askForBackGroundCoordinates() {
        Utils.showBackGroundPermissionDialog(this) { presenter.permissionResult(it) }
    }

    override fun onClickGoToDriverApp() = Utils.goToGooglePlay(this, getString(R.string.driver_app_market_package))

    private val connectionService = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {}
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {}
    }

    private fun startCoordinateService() {
        Intent(SERVICE_ACTION).also { intent ->
            intent.setPackage(PACKAGE)
            startService(intent)
            bindService(intent, connectionService, Context.BIND_AUTO_CREATE)
        }
        isServiceStart = true
    }

    private fun stopCoordinateService() {
        Intent(SERVICE_ACTION).also { intent ->
            intent.setPackage(PACKAGE)
            unbindService(connectionService)
            stopService(intent)
        }
    }

    companion object {
        const val SERVICE_ACTION = "com.kg.gettransfer.service.CoordinateService"
        const val PACKAGE        = "com.kg.gettransfer"
        val PERMISSIONS = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        const val RC_LOCATION = 100
    }
}
