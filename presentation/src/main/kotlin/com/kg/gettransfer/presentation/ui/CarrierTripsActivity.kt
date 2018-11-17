package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent
import android.content.res.Configuration

import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar

import android.text.Html

import android.view.MenuItem
import android.view.View

import android.widget.TextView

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.CarrierTripInteractor

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.adapter.TripsRVAdapter

import com.kg.gettransfer.presentation.model.CarrierTripModel
import com.kg.gettransfer.presentation.model.ProfileModel

import com.kg.gettransfer.presentation.presenter.CarrierTripsPresenter
import com.kg.gettransfer.presentation.view.CarrierTripsView

import kotlinx.android.synthetic.main.activity_carrier_trips.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_navigation.*

import org.koin.android.ext.android.inject

import ru.terrakok.cicerone.commands.Forward

import timber.log.Timber

class CarrierTripsActivity: BaseActivity(), CarrierTripsView {
    @InjectPresenter
    internal lateinit var presenter: CarrierTripsPresenter

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private val carrierTripInteractor: CarrierTripInteractor by inject()

    @ProvidePresenter
    fun createCarrierTripsPresenter(): CarrierTripsPresenter = CarrierTripsPresenter(router, systemInteractor, carrierTripInteractor)

    private val readMoreListener = View.OnClickListener { presenter.readMoreClick() }

    private val itemsNavigationViewListener = View.OnClickListener { item ->
        when(item.id) {
            R.id.navCarrierTrips -> presenter.onCarrierTripsClick()
            R.id.navAbout -> presenter.onAboutClick()
            R.id.navSettings -> presenter.onSettingsClick()
            R.id.navPassengerMode -> presenter.onPassengerModeClick()
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
                Screens.SETTINGS -> return Intent(context, SettingsActivity::class.java)
                Screens.PASSENGER_MODE -> return Intent(context, MainActivity::class.java)
                                                 .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                 .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                Screens.TRIP_DETAILS -> return Intent(context, CarrierTripDetailsActivity::class.java)
            }
            return null
        }

        @CallSuper
        protected override fun forward(command: Forward) {
            when (command.screenKey){
                Screens.READ_MORE -> {
                    drawer.closeDrawer(GravityCompat.START)
                    ReadMoreDialog.newInstance(this@CarrierTripsActivity).show()
                }
                Screens.CARRIER_TRIPS -> drawer.closeDrawer(GravityCompat.START)
                else -> super.forward(command)
            }
        }
    }

    override fun getPresenter(): CarrierTripsPresenter = presenter

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_carrier_trips)

        val tb = this.toolbar as Toolbar

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).toolbar_title.setText(R.string.LNG_MENU_TITLE_TRIPS)

        drawer = drawerLayout as DrawerLayout
        toggle = ActionBarDrawerToggle(this, drawer, tb, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

        initNavigation()

        rvTrips.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    @CallSuper
    protected override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        toggle.syncState()
    }

    @CallSuper
    override fun onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START)
        else super.onBackPressed()
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

        navHeaderShare.setOnClickListener {Timber.d("Share action")}

        navCarrierTrips.setOnClickListener(itemsNavigationViewListener)
        navSettings.setOnClickListener(itemsNavigationViewListener)
        navAbout.setOnClickListener(itemsNavigationViewListener)
        navPassengerMode.setOnClickListener(itemsNavigationViewListener)
    }

    override fun setTrips(trips: List<CarrierTripModel>) {
        rvTrips.adapter = TripsRVAdapter(presenter, trips)
    }

    override fun initNavigation(profile: ProfileModel) {
        navHeaderName.visibility = View.VISIBLE
        navHeaderEmail.visibility = View.VISIBLE
        navHeaderName.text = profile.name
        navHeaderEmail.text = profile.email

        navLogin.visibility = View.GONE
        navCarrierTrips.visibility = View.VISIBLE
        navBecomeACarrier.visibility = View.GONE
        navPassengerMode.visibility = View.VISIBLE
    }
}