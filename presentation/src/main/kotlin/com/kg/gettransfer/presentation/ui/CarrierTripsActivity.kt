package com.kg.gettransfer.presentation.ui

import android.content.res.Configuration

import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar

import android.view.MenuItem
import android.view.View

import android.widget.TextView

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.R

import com.kg.gettransfer.presentation.adapter.TripsRVAdapter

import com.kg.gettransfer.presentation.model.CarrierTripModel
import com.kg.gettransfer.presentation.model.ProfileModel

import com.kg.gettransfer.presentation.presenter.CarrierTripsPresenter

import com.kg.gettransfer.presentation.view.CarrierTripsView

import kotlinx.android.synthetic.main.activity_carrier_trips.*
import kotlinx.android.synthetic.main.view_navigation.*

import timber.log.Timber

class CarrierTripsActivity: BaseActivity(), CarrierTripsView {
    @InjectPresenter
    internal lateinit var presenter: CarrierTripsPresenter

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    @ProvidePresenter
    fun createCarrierTripsPresenter() = CarrierTripsPresenter()

    private val readMoreListener = View.OnClickListener { presenter.readMoreClick() }

    private val itemsNavigationViewListener = View.OnClickListener {
        when(it.id) {
            R.id.navCarrierTrips  -> presenter.onCarrierTripsClick()
            R.id.navAbout         -> presenter.onAboutClick()
            R.id.navSettings      -> presenter.onSettingsClick()
            R.id.navPassengerMode -> presenter.onPassengerModeClick()
            else -> Timber.d("No route")
        }
        drawer.closeDrawer(GravityCompat.START)
    }

    override fun getPresenter(): CarrierTripsPresenter = presenter

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_carrier_trips)

        setToolbar(toolbar as Toolbar, R.string.LNG_MENU_TITLE_TRIPS, false)
        drawer = drawerLayout as DrawerLayout
        toggle = ActionBarDrawerToggle(this, drawer, toolbar as Toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)

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

    override fun showReadMoreDialog() {
        ReadMoreFragment().show(supportFragmentManager, getString(R.string.tag_read_more))
    }
}