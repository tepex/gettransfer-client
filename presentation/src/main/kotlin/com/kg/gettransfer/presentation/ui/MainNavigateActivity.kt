package com.kg.gettransfer.presentation.ui

import android.arch.lifecycle.LiveData
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper

import android.view.WindowManager

import androidx.navigation.NavController
import androidx.navigation.fragment.FragmentNavigator

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.extensions.setupWithNavController

import com.kg.gettransfer.presentation.presenter.MainNavigatePresenter
import com.kg.gettransfer.presentation.ui.dialogs.RatingDetailDialogFragment

import com.kg.gettransfer.presentation.ui.dialogs.StoreDialogFragment
import com.kg.gettransfer.presentation.view.MainNavigateView
import com.kg.gettransfer.presentation.view.MainNavigateView.Companion.EXTRA_RATE_TRANSFER_ID
import com.kg.gettransfer.presentation.view.MainNavigateView.Companion.EXTRA_RATE_VALUE
import kotlinx.android.synthetic.main.activity_main_navigate.*

import pub.devrel.easypermissions.EasyPermissions
import android.view.LayoutInflater
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.ui.newtransfer.NewTransferMainFragment
import kotlinx.android.synthetic.main.notification_badge_view.view.*


@Suppress("TooManyFunctions")
class MainNavigateActivity : BaseActivity(), MainNavigateView,
        StoreDialogFragment.OnStoreListener {

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.statusBarColor = Color.TRANSPARENT
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        getIntents()

        if (savedInstanceState == null) {
            setupBottomNavigationBar()
        } // Else, need to wait for onRestoreInstanceState
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
    }

    /**
     * Checking existing transfer for rate
     */
    private fun getIntents() {
        with(intent) {
            val transferId = getLongExtra(EXTRA_RATE_TRANSFER_ID, 0L)
            val rate = getIntExtra(EXTRA_RATE_VALUE, 0)
            if (transferId != 0L) presenter.rateTransfer(transferId, rate)
        }
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

    override fun askRateInPlayMarket() =
            StoreDialogFragment.newInstance().show(supportFragmentManager, StoreDialogFragment.STORE_DIALOG_TAG)

    override fun onClickGoToStore() = redirectToPlayMarket()

    override fun thanksForRate() =
            ThanksForRateFragment
                    .newInstance()
                    .show(supportFragmentManager, ThanksForRateFragment.TAG)

    override fun setEventCount(isVisible: Boolean, count: Int) {
        //Setup badge for trips tab
        val bottomNavigationMenuView = bottom_nav.getChildAt(0) as BottomNavigationMenuView
        //Index of trips tab = 1 in bottom navigation
        val v = bottomNavigationMenuView.getChildAt(1)
        val itemView = v as BottomNavigationItemView
        val badge = LayoutInflater.from(this)
                .inflate(R.layout.notification_badge_view, itemView, true)
        badge.notifications_badge.text = count.toString()
        badge.notifications_badge.isVisible = isVisible && count > 0
    }

    override fun setNetworkAvailability(context: Context): Boolean {
        val available = super.setNetworkAvailability(context)
//        if (newTransferFragment is BaseNetworkWarning)
//            (newTransferFragment as BaseNetworkWarning).onNetworkWarning(available)
        return available
    }

    @CallSuper
    override fun onBackPressed() = onBackClick()

    private fun onBackClick() {
        presenter.onBackCommandClick()
    }


    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        //Setup controller
        val navGraphIds = listOf(R.navigation.order, R.navigation.trips, R.navigation.help, R.navigation.settings)

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottom_nav.setupWithNavController(
                navGraphIds = navGraphIds,
                fragmentManager = supportFragmentManager,
                containerId = R.id.container,
                intent = intent
        )
        currentNavController = controller
        currentNavController?.value?.addOnDestinationChangedListener { _, destination, _ ->
            when ((destination as FragmentNavigator.Destination).className) {
                //visible bottom menu
                NewTransferMainFragment::class.java.name -> {
                    bottom_nav.isVisible = true
                }
                //not visible bottom menu
                else -> {
                    bottom_nav.isVisible = false
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }
}
