package com.kg.gettransfer.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View

import androidx.annotation.CallSuper
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigator

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.setTransparentStatusBar

import com.kg.gettransfer.extensions.setupWithNavController

import com.kg.gettransfer.presentation.listeners.GoToPlayMarketListener
import com.kg.gettransfer.presentation.presenter.MainNavigatePresenter
import com.kg.gettransfer.presentation.ui.dialogs.AboutNewDriverAppDialogFragment
import com.kg.gettransfer.presentation.ui.dialogs.RatingDetailDialogFragment
import com.kg.gettransfer.presentation.ui.dialogs.StoreDialogFragment
import com.kg.gettransfer.presentation.ui.newtransfer.NewTransferMainFragment
import com.kg.gettransfer.presentation.ui.newtransfer.NewTransferMapFragment
import com.kg.gettransfer.presentation.view.MainNavigateView
import com.kg.gettransfer.presentation.view.MainNavigateView.Companion.EXTRA_ERROR_RES_ID
import com.kg.gettransfer.presentation.view.MainNavigateView.Companion.EXTRA_RATE_TRANSFER_ID
import com.kg.gettransfer.presentation.view.MainNavigateView.Companion.EXTRA_RATE_VALUE
import com.kg.gettransfer.presentation.view.MainNavigateView.Companion.SHOW_ABOUT
import com.kg.gettransfer.presentation.view.Screens

import kotlinx.android.synthetic.main.activity_main_navigate.*
import kotlinx.android.synthetic.main.notification_badge_view.view.*

import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import org.jetbrains.anko.find

import pub.devrel.easypermissions.EasyPermissions

@Suppress("TooManyFunctions")
class MainNavigateActivity : BaseActivity(), MainNavigateView,
        StoreDialogFragment.OnStoreListener, GoToPlayMarketListener {

    @InjectPresenter
    internal lateinit var presenter: MainNavigatePresenter

    private var currentNavController: LiveData<NavController>? = null

    @ProvidePresenter
    fun createMainPresenter() = MainNavigatePresenter()

    override fun getPresenter(): MainNavigatePresenter = presenter

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_navigate)
        setTransparentStatusBar()
        getIntents(intent)

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
            showAbout(intent)
        } // Else, need to wait for onRestoreInstanceState
    }

    override fun onPause() {
        super.onPause()
        // TODO temporary solution
        (currentNavController?.value?.currentDestination as? FragmentNavigator.Destination)?.let { currentDest ->
            val backToMain =
                currentDest.className == NewTransferMapFragment::class.java.name ||
                currentDest.className == SearchFragment::class.java.name
            if (backToMain) currentNavController?.value?.navigateUp()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        getIntents(intent)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    private fun getIntents(intent: Intent?) {
        intent?.let { arguments ->
            val errorResId = arguments.getIntExtra(EXTRA_ERROR_RES_ID, 0)
            if (errorResId != 0) {
                showError(errorResId)
                return
            }

            val transferId = arguments.getLongExtra(EXTRA_RATE_TRANSFER_ID, 0L)
            val rate = arguments.getIntExtra(EXTRA_RATE_VALUE, 0)
            rateTransfer(transferId, rate)
        }
    }

    private fun showAbout(intent: Intent) {
        intent.getBooleanExtra(SHOW_ABOUT, false).also { showAbout ->
            if (showAbout) Screens.showAboutScreen(
                fragmentManager = supportFragmentManager,
                firstLaunch = true
            )
        }
    }

    private fun rateTransfer(transferId: Long, rate: Int) {
        if (transferId != 0L) presenter.rateTransfer(transferId, rate)
    }

    @CallSuper
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun showRateForLastTrip(transferId: Long, vehicle: String, color: String) {
        supportFragmentManager.fragments.firstOrNull { fragment ->
            fragment.tag == RatingLastTripFragment.RATING_LAST_TRIP_TAG
        } ?: RatingLastTripFragment
            .newInstance(transferId, vehicle, color)
            .show(supportFragmentManager, RatingLastTripFragment.RATING_LAST_TRIP_TAG)
    }

    override fun showDetailedReview() {
        supportFragmentManager.fragments.firstOrNull { fragment ->
            fragment.tag == RatingDetailDialogFragment.RATE_DIALOG_TAG
        } ?: RatingDetailDialogFragment
            .newInstance()
            .show(supportFragmentManager, RatingDetailDialogFragment.RATE_DIALOG_TAG)
    }

    override fun showNewDriverAppDialog() =
        AboutNewDriverAppDialogFragment.newInstance()
            .show(supportFragmentManager, AboutNewDriverAppDialogFragment.DIALOG_TAG)

    override fun onClickGoToDriverApp() {
        Utils.goToGooglePlay(this, getString(R.string.driver_app_market_package))
    }

    override fun askRateInPlayMarket() =
        StoreDialogFragment.newInstance().show(supportFragmentManager, StoreDialogFragment.STORE_DIALOG_TAG)

    override fun onClickGoToStore() {
        presenter.redirectToPlayMarket()
    }

    override fun goToGooglePlay() {
        Utils.goToGooglePlay(this, getString(R.string.app_market_package), BaseActivity.PLAY_MARKET_RATE)
    }

    override fun thanksForRate() =
        ThanksForRateFragment
            .newInstance()
            .show(supportFragmentManager, ThanksForRateFragment.TAG)

    override fun setEventCount(isVisible: Boolean, count: Int) {
        with(getNavTripsItem()) {
            notifications_badge.text = count.toString()
            setEventsCounterStyle(isVisible && count > 0, isNavTripsSelected(), this)
        }
    }

    private fun setEventsCounterStyle(isShow: Boolean, isNavTripsSelected: Boolean, view: View) {
        if (isShow) {
            if (isNavTripsSelected) {
                setNotificationItemsVisibility(view, showIcon = true, showBadge = false)
            } else {
                setNotificationItemsVisibility(view, showIcon = false, showBadge = true)
            }
        } else {
            setNotificationItemsVisibility(view, showIcon = false, showBadge = false)
        }
    }

    private fun setNotificationItemsVisibility(view: View, showIcon: Boolean, showBadge: Boolean) {
        with(view) {
            notifications_badge.isVisible = showBadge
            notifications_icon.isVisible = showIcon
        }
    }

    private fun getNavTripsItem() = bottom_nav.find<View>(R.id.nav_trips)

    private fun isNavTripsSelected() = bottom_nav.menu.getItem(1).isChecked

    private fun isNotificationShowed() = with(getNavTripsItem()) {
        notifications_badge.isVisible || notifications_icon.isVisible
    }

    val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
        (destination as? FragmentNavigator.Destination)?.let { newDestination ->
            when (newDestination.className) {
                // visible bottom menu
                NewTransferMainFragment::class.java.name,
                RequestsPagerFragment::class.java.name,
                SettingsFragment::class.java.name,
                SupportFragment::class.java.name ->
                    Handler().postDelayed({ bottomNavSliding(true) }, DELAY)
                // not visible bottom menu
                else -> bottomNavSliding(false)
            }
            setEventsCounterStyle(isNotificationShowed(),
                destination.className == RequestsPagerFragment::class.java.name, getNavTripsItem())
        }
    }

    private fun bottomNavSliding(show: Boolean) {
        bottom_nav.isVisible = show
        bottom_nav_shadow.isVisible = show
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        // Setup controller
        val navGraphIds = listOf(R.navigation.order, R.navigation.trips, R.navigation.help, R.navigation.settings)

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottom_nav.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )
        currentNavController = controller
        currentNavController?.observe(this, Observer { navController ->
            navController.removeOnDestinationChangedListener(listener)
            navController.addOnDestinationChangedListener(listener)
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    private fun showError(errorResId: Int) {
        BottomSheetDialog
            .newInstance()
            .apply {
                title = this@MainNavigateActivity.getString(R.string.LNG_ERROR)
                text = this@MainNavigateActivity.getString(errorResId)
            }
            .show(supportFragmentManager)
    }

    companion object {
        const val DELAY = 100L
    }
}
