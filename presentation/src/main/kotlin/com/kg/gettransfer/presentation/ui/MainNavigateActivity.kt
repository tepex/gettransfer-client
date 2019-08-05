package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler

import android.support.annotation.CallSuper
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout

import android.transition.Fade
import android.view.Gravity

import android.view.View
import android.view.WindowManager

import android.widget.TextView

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.BuildConfig
import com.kg.gettransfer.R
import com.kg.gettransfer.common.NavigationMenuListener

import com.kg.gettransfer.extensions.isVisible

import com.kg.gettransfer.presentation.model.ProfileModel
import com.kg.gettransfer.presentation.presenter.MainNavigatePresenter
import com.kg.gettransfer.presentation.ui.custom.LockableSwipeDrawerLayout
import com.kg.gettransfer.presentation.ui.dialogs.RatingDetailDialogFragment

import com.kg.gettransfer.presentation.ui.dialogs.StoreDialogFragment
import com.kg.gettransfer.presentation.view.BaseNetworkWarning
import com.kg.gettransfer.presentation.view.MainNavigateView
import com.kg.gettransfer.presentation.view.MainNavigateView.Companion.EXTRA_RATE_TRANSFER_ID
import com.kg.gettransfer.presentation.view.MainNavigateView.Companion.EXTRA_RATE_VALUE
import com.kg.gettransfer.presentation.view.Screens

import kotlinx.android.synthetic.main.activity_main_navigate.*
import kotlinx.android.synthetic.main.navigation_view_menu_item.view.*
import kotlinx.android.synthetic.main.view_navigation.*

import pub.devrel.easypermissions.EasyPermissions

import timber.log.Timber


@Suppress("TooManyFunctions")
class MainNavigateActivity : BaseActivity(), MainNavigateView,
        NavigationMenuListener,
        StoreDialogFragment.OnStoreListener {

    @InjectPresenter
    internal lateinit var presenter: MainNavigatePresenter

    lateinit var drawer: LockableSwipeDrawerLayout

    @ProvidePresenter
    fun createMainPresenter() = MainNavigatePresenter()

    private val readMoreListener = View.OnClickListener { presenter.readMoreClick() }

    private val itemsNavigationViewListener = View.OnClickListener { view ->
        with(presenter) {
            when (view.id) {
                R.id.navNewTransfer -> onNewTransferClick()
                R.id.navLogin -> onLoginClick()
                R.id.navAbout -> onAboutClick()
                R.id.navSettings -> onSettingsClick()
                R.id.navSupport -> onSupportClick()
                R.id.navRequests -> onRequestsClick()
                R.id.navBecomeACarrier -> onBecomeACarrierClick()
                R.id.navHeaderShare -> onShareClick()
                else -> Timber.d("No route")
            }
            drawer.closeDrawer(GravityCompat.START)
        }
    }

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

        @Suppress("UnsafeCast")
        drawer = drawerLayout as LockableSwipeDrawerLayout
        drawer.addDrawerListener(object : DrawerLayout.SimpleDrawerListener() {
            @CallSuper
            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                if (newState == DrawerLayout.STATE_SETTLING) hideKeyboard()
            }
        })

        initNavigation()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.exitTransition = Fade().apply { duration = FADE_DURATION }
        }
        getIntents()
    }

    /**
     * Checking existing transfer for rate
     */
    private fun getIntents() {
        with(intent) {
            val transferId = getLongExtra(EXTRA_RATE_TRANSFER_ID, 0L)
            val rate = getIntExtra(EXTRA_RATE_VALUE, 0)
            if (transferId != 0L) presenter.rateTransfer(transferId, rate)

            if (getBooleanExtra(Screens.MAIN_MENU, false)) {
                Handler().postDelayed({ openMenu() }, MAGIC_DELAY)
            }
        }
    }

    private fun initNavigation() {
        navViewHeader.setPadding(0, getStatusBarHeight(), 0, 0)

        val versionName = BuildConfig.VERSION_NAME
        val versionCode = BuildConfig.VERSION_CODE
        @Suppress("UnsafeCast")
        (navFooterVersion as TextView).text =
                String.format(getString(R.string.nav_footer_version), versionName, versionCode)
        navHeaderMode.isVisible = false
        navNewTransfer.isVisible = true
        navRequests.isVisible = true

        with(readMoreListener) {
            navFooterStamp.setOnClickListener(this)
            navFooterReadMore.setOnClickListener(this)
        }
        with(itemsNavigationViewListener) {
            navNewTransfer.setOnClickListener(this)
            navHeaderShare.setOnClickListener(this)
            navLogin.setOnClickListener(this)
            navRequests.setOnClickListener(this)
            navSettings.setOnClickListener(this)
            navSupport.setOnClickListener(this)
            navAbout.setOnClickListener(this)
            navBecomeACarrier.setOnClickListener(this)
            navPassengerMode.setOnClickListener(this)
        }
    }

    override fun openMenu() {
        drawer.openDrawer(Gravity.START, true)
    }

    override fun enablingNavigation() {
        drawer.isSwipeOpenEnabled = true
    }

    override fun disablingNavigation() {
        drawer.isSwipeOpenEnabled = false
    }

    @CallSuper
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun setProfile(profile: ProfileModel, isLoggedIn: Boolean, hasAccount: Boolean) {
        navHeaderMode.text = getString(R.string.LNG_MENU_TITLE_PASSENGER)
        with(profile) {
            navHeaderName.isVisible = isLoggedIn && !name.isNullOrEmpty()
            navHeaderEmail.isVisible = isLoggedIn && !email.isNullOrEmpty()
            navLogin.isVisible = !isLoggedIn && hasAccount || !hasAccount
            layoutAccountInfo.isVisible = navHeaderName.isVisible || navHeaderEmail.isVisible
            if (isLoggedIn) {
                name?.let  { navHeaderName.text = it }
                email?.let { navHeaderEmail.text = it }
            }
        }
    }

    override fun setBalance(balance: String?) {
        navHeaderBalance.apply {
            isGone = balance.isNullOrEmpty()
            text = getString(R.string.LNG_PAYMENT_BALANCE, balance)
        }
    }

    override fun showReadMoreDialog() {
        drawer.closeDrawer(GravityCompat.START)
        ReadMoreFragment().show(supportFragmentManager, getString(R.string.tag_read_more))
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
        navRequests.menu_item_counter.isVisible = isVisible && count > 0
        navRequests.menu_item_counter.text = count.toString()
    }

    override fun setNetworkAvailability(context: Context): Boolean {
        val available = super.setNetworkAvailability(context)
        if (newTransferFragment is BaseNetworkWarning)
            (newTransferFragment as BaseNetworkWarning).onNetworkWarning(available)
        return available
    }

    @CallSuper
    override fun onBackPressed() = onBackClick()

    private fun onBackClick() {
        when {
            drawer.isDrawerOpen(GravityCompat.START) -> drawer.closeDrawer(GravityCompat.START)
            else -> presenter.onBackCommandClick()
        }
    }

    companion object {
        const val FADE_DURATION = 500L
        const val MAGIC_DELAY = 500L
    }
}
