package com.kg.gettransfer.presentation.ui

import android.content.res.Configuration
import android.os.Build

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.design.widget.TabLayout
import android.support.v4.content.ContextCompat

import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar

import android.view.MenuItem
import android.view.View
import android.view.WindowManager

import android.widget.TextView

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.model.Offer

import com.kg.gettransfer.extensions.*

import com.kg.gettransfer.presentation.adapter.CarrierTripsRVAdapter

import com.kg.gettransfer.presentation.model.CarrierTripsRVItemModel
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.ProfileModel

import com.kg.gettransfer.presentation.presenter.CarrierTripsPresenter

import com.kg.gettransfer.presentation.view.CarrierTripsView

import kotlinx.android.synthetic.main.activity_carrier_trips.*
import kotlinx.android.synthetic.main.view_navigation.*

import timber.log.Timber

class CarrierTripsActivity : BaseActivity(), CarrierTripsView {
    @InjectPresenter
    internal lateinit var presenter: CarrierTripsPresenter

    private lateinit var drawer: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    private var isUserScrolling = false

    @ProvidePresenter
    fun createCarrierTripsPresenter() = CarrierTripsPresenter()

    private val readMoreListener = View.OnClickListener { presenter.readMoreClick() }

    private val itemsNavigationViewListener = View.OnClickListener {
        when (it.id) {
            R.id.navCarrierTrips -> presenter.onCarrierTripsClick()
            R.id.navAbout -> presenter.onAboutClick()
            R.id.navSettings -> presenter.onSettingsClick()
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        }

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

        initTabLayout()
    }

    private fun initTabLayout() {
        tabsForRecyclerView.addTab(tabsForRecyclerView.newTab().setText(getString(R.string.LNG_TRIPS_TODAY)))
        tabsForRecyclerView.addTab(tabsForRecyclerView.newTab().setText(getString(R.string.LNG_TRIPS_ALL)))
        tabsForRecyclerView.addTab(tabsForRecyclerView.newTab().setText(getString(R.string.LNG_TRIPS_COMPLETED)))
    }

    override fun setTrips(tripsItems: List<CarrierTripsRVItemModel>, startTodayPosition: Int, endTodayPosition: Int) {
        rvTrips.adapter = CarrierTripsRVAdapter(presenter, tripsItems)
        rvTrips.scrollToPosition(startTodayPosition)

        rvTrips.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val itemPosition = (rvTrips.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()

                if (isUserScrolling) {
                    val tab = when {
                        itemPosition in startTodayPosition..endTodayPosition -> tabsForRecyclerView.getTabAt(0)
                        itemPosition > endTodayPosition -> tabsForRecyclerView.getTabAt(1)
                        itemPosition < startTodayPosition -> tabsForRecyclerView.getTabAt(2)
                        else -> null
                    }
                    tab?.select()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> isUserScrolling = true
                    RecyclerView.SCROLL_STATE_IDLE -> isUserScrolling = false
                }
            }
        })
        tabsForRecyclerView.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}
            override fun onTabUnselected(p0: TabLayout.Tab?) {}
            override fun onTabSelected(tab: TabLayout.Tab) {
                if (isUserScrolling) return
                when (tab.position) {
                    0 -> (rvTrips.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(startTodayPosition, 0)
                    1 -> if (tripsItems.size >= endTodayPosition) (rvTrips.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(endTodayPosition, 0)
                    2 -> if (startTodayPosition > 0) (rvTrips.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(0, 0)
                }
            }
        })
    }

    override fun initNavigation(profile: ProfileModel) {
        navHeaderName.isVisible = true
        navHeaderEmail.isVisible = true
        navHeaderName.text = profile.name
        navHeaderEmail.text = profile.email

        navLogin.isVisible = false
        navCarrierTrips.isVisible = true
        navBecomeACarrier.isVisible = false
        navPassengerMode.isVisible = true
    }

    override fun showReadMoreDialog() {
        ReadMoreFragment().show(supportFragmentManager, getString(R.string.tag_read_more))
    }
}
