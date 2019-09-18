package com.kg.gettransfer.presentation.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ClipboardManager
import android.content.ClipData

import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable

import android.net.ConnectivityManager

import android.os.Build
import android.os.Handler

import android.util.DisplayMetrics
import android.util.TypedValue

import android.view.DisplayCutout
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager

import android.widget.PopupWindow

import androidx.annotation.CallSuper
import androidx.annotation.ColorRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

import androidx.localbroadcastmanager.content.LocalBroadcastManager

import com.arellomobile.mvp.MvpAppCompatActivity

import com.google.android.material.bottomsheet.BottomSheetBehavior

import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.domain.interactor.SessionInteractor
import com.kg.gettransfer.domain.model.Account

import com.kg.gettransfer.extensions.hideKeyboard
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.extensions.showKeyboard

import com.kg.gettransfer.presentation.presenter.BasePresenter
import com.kg.gettransfer.presentation.ui.custom.NetworkNotAvailableView
import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.AppLifeCycleObserver
import com.kg.gettransfer.utilities.LocaleManager

import io.sentry.Sentry
import io.sentry.event.BreadcrumbBuilder

import java.util.Locale

import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.android.synthetic.main.toolbar.view.*

import org.koin.android.ext.android.inject

import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator

import timber.log.Timber

@Suppress("TooManyFunctions")
abstract class BaseActivity : MvpAppCompatActivity(), BaseView {

    internal val sessionInteractor: SessionInteractor by inject()

    protected val navigatorHolder: NavigatorHolder by inject()
    protected val localeManager: LocaleManager by inject()

    private var rootView: View? = null
    private var rootViewHeight: Int? = null
    protected var isKeyBoardOpened = false

    private lateinit var popupWindowRate: PopupWindow

    protected var baseNavigator = SupportAppNavigator(this, Screens.NOT_USED)

    protected var viewNetworkNotAvailable: NetworkNotAvailableView? = null

    private var displayCutout: DisplayCutout? = null
    private var cutoutOffset: Int = 0

    protected lateinit var tintBackgroundShadow: View

    protected val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                tintBackgroundShadow.isVisible = false
                hideKeyboard()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            tintBackgroundShadow.isVisible = true
            tintBackgroundShadow.alpha = slideOffset
        }
    }

    /*TODO use when increase min sdk version
    private val networkCallback: ConnectivityManager.NetworkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onUnavailable() {
            viewNetworkNotAvailable?.let { it.isGone = true }
        }

        override fun onAvailable(network: Network?) {
            viewNetworkNotAvailable?.let { it.isGone = false }
        }
    }*/

    protected fun setViewColor(view: View, @ColorRes color: Int) {
        view.setBackgroundColor(ContextCompat.getColor(this, color))
    }

    protected fun hideBottomSheet(
        bottomSheet: BottomSheetBehavior<View>,
        bottomSheetLayout: View,
        hiddenState: Int,
        event: MotionEvent
    ): Boolean {
        val outRect = Rect()
        bottomSheetLayout.getGlobalVisibleRect(outRect)
        return if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
            bottomSheet.state = hiddenState
            true
        } else {
            false
        }
    }

    protected val onTouchListener = View.OnTouchListener { view, event ->
        if (event.action == MotionEvent.ACTION_MOVE) {
            view.hideKeyboard()
            return@OnTouchListener false
        } else {
            return@OnTouchListener false
        }
    }
    /* BroadCast receivers */
    private val inetReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // if(setNetworkAvailability(context)) getPresenter().checkNewMessagesCached()
            setNetworkAvailability(context)
        }
    }

    private val appStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { i ->
                if (i.action == AppLifeCycleObserver.APP_STATE) {
                    getPresenter().onAppStateChanged(i.getBooleanExtra(AppLifeCycleObserver.STATUS, false))
                }
            }
        }
    }

    protected open fun setNetworkAvailability(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
        var available = false
        if (cm is ConnectivityManager) {
            available = cm.activeNetworkInfo?.isConnected ?: false
            if (available) getPresenter().networkConnected()
            viewNetworkNotAvailable?.changeViewVisibility(!available)
        }
        return available
    }

    private val loadingFragment by lazy { LoadingFragment() }

    /** [https://stackoverflow.com/questions/37615470/support-library-vectordrawable-resourcesnotfoundexception] */
    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    abstract fun getPresenter(): BasePresenter<*>

    protected fun setToolbar(
        toolbar: Toolbar,
        @StringRes titleId: Int = TOOLBAR_NO_TITLE,
        hasBackAction: Boolean = true,
        firstLetterToUpperCase: Boolean = false,
        subTitle: String? = null
    ) {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            if (hasBackAction) setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        if (titleId != TOOLBAR_NO_TITLE) {
            val title = getString(titleId)
            toolbar.toolbar_title.text =
                if (!firstLetterToUpperCase) title else title.substring(0, 1).toUpperCase().plus(title.substring(1))
            subTitle?.let { st ->
                with(toolbar_subtitle) {
                    isVisible = true
                    text = st
                }
            }
        }
        if (hasBackAction) toolbar.setNavigationOnClickListener { getPresenter().onBackCommandClick() }
    }

    /********************************************************************************************************/
    /************************************************ Life cycles *******************************************/
    /********************************************************************************************************/

    @CallSuper
    protected override fun onStart() {
        LocalBroadcastManager.getInstance(applicationContext)
            .registerReceiver(appStateReceiver, IntentFilter(AppLifeCycleObserver.APP_STATE))
        super.onStart()

        registerReceiver(inetReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        setNetworkAvailability(this)
    }

    @CallSuper
    protected override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(baseNavigator)
    }

    @CallSuper
    protected override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    @CallSuper
    protected override fun onStop() {
        super.onStop()
        //            unregisterReceiver(offerReceiver)
        Handler().postDelayed(
            { LocalBroadcastManager.getInstance(this).unregisterReceiver(appStateReceiver) },
            DELAY
        )
        unregisterReceiver(inetReceiver)
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        if (block) {
            if (useSpinner) showLoading()
        } else {
            hideLoading()
        }
    }

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        val errMessage = getString(errId, *args)
        Timber.e(RuntimeException(errMessage))
        Utils.showError(this, finish, errMessage)
    }

    override fun setError(e: ApiException) {
        Timber.e(e, "code: ${e.code}")
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage(e.details).build())
        Sentry.capture(e)
        if (e.code != ApiException.NETWORK_ERROR) {
            Utils.showError(
                this,
                false,
                getString(R.string.LNG_ERROR) + ": " + e.message
            )
        }
    }

    override fun setError(e: DatabaseException) {
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage("CacheError: ${e.details}").build())
        Sentry.capture(e)
    }

    override fun setTransferNotFoundError(transferId: Long) {
        BottomSheetDialog
            .newInstance()
            .apply {
                imageId = R.drawable.transfer_error
                title = this@BaseActivity.getString(R.string.LNG_ERROR)
                text = this@BaseActivity.getString(R.string.LNG_TRANSFER_NOT_FOUND, transferId.toString())
                isShowCloseButton = true
                isShowOkButton = false
            }
            .show(supportFragmentManager)
    }

    protected fun showKeyboard() {
        currentFocus?.showKeyboard()
    }

    protected fun hideKeyboard(): Boolean {
        currentFocus?.let { focus ->
            focus.hideKeyboard()
            focus.clearFocus()
        }
        return true
    }

    // здесь лучше ничего не трогать
    private fun countDifference() = rootView?.let { rv ->
        val height = rootViewHeight ?: rv.rootView.height
        rootViewHeight = height

        val visibleRect = Rect().also { rv.getWindowVisibleDisplayFrame(it) }
        height - visibleRect.bottom < height * HEIGHT_DIFF_FACTOR
    } ?: false

    fun addKeyBoardDismissListener(checkKeyBoardState: (Boolean) -> Unit) {
        rootView = findViewById<View>(android.R.id.content)?.also { view ->
            view.viewTreeObserver.addOnGlobalLayoutListener {
                val state = countDifference()
                if (!state && !isKeyBoardOpened) {
                    isKeyBoardOpened = true
                    checkKeyBoardState(isKeyBoardOpened)
                } else if (state && isKeyBoardOpened) {
                    isKeyBoardOpened = false
                    checkKeyBoardState(isKeyBoardOpened)
                }
            }
        }
    }

    // protected fun openScreen(screen: String) { router.navigateTo(screen) }

    @CallSuper
    override fun attachBaseContext(newBase: Context) {
        val account = sessionInteractor.account

        // if account is empty  we need to use locale from preferences
        // because locale from Account.EMPTY is equals Locale.getDefault()
        // and then it'll be always show wrong locale
        val locale = if (account == Account.EMPTY) Locale(sessionInteractor.appLanguage) else sessionInteractor.locale

        super.attachBaseContext(localeManager.updateResources(newBase, locale))
    }

    private fun showLoading() {
        if (!loadingFragment.isAdded) {
            supportFragmentManager.beginTransaction().apply {
                replace(android.R.id.content, loadingFragment)
                commit()
            }
        }
    }

    private fun hideLoading() {
        if (loadingFragment.isAdded) {
            supportFragmentManager.beginTransaction().apply {
                remove(loadingFragment)
                commit()
            }
        }
    }

    /*
    Use this method to fix UI, which depends on screen size.
    Returns Point, that has x = display width, y = display height
     */
    protected open fun fixUIDependedOnScreenSize() = Point().also { windowManager.defaultDisplay.getSize(it) }

    protected fun getScreenSide(height: Boolean): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return if (height) displayMetrics.heightPixels else displayMetrics.widthPixels
    }

    @ExperimentalUnsignedTypes
    protected fun applyDim(parent: ViewGroup, dimAmount: Float) {
        parent.overlay.add(ColorDrawable(Color.BLACK).apply {
            setBounds(0, 0, parent.width, parent.height)
            alpha = (dimAmount * UByte.MAX_VALUE.toInt()).toInt()
        })
    }

    protected fun clearDim(parent: ViewGroup) = parent.overlay.clear()

    @CallSuper
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            displayCutout = window?.decorView?.rootWindowInsets?.displayCutout
            displayCutout?.let { cutoutOffset = it.safeInsetTop }
        }
    }

    protected fun getHeightForBottomSheetDetails(): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenHeight = displayMetrics.heightPixels

        val tv = TypedValue()
        val actionBarHeight = if (theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        } else 0

        val statusBarHeight = getStatusBarHeight()
        return if (displayCutout != null) {
            screenHeight - actionBarHeight - statusBarHeight + cutoutOffset
        } else {
            screenHeight - actionBarHeight - statusBarHeight
        }
    }

    protected fun getStatusBarHeight(): Int {
        val statusBarResource = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (statusBarResource > 0) resources.getDimensionPixelSize(statusBarResource) else 0
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLAY_MARKET_RATE) {
            thanksForRate()
        }
    }

    open fun thanksForRate() {}

    protected fun closePopUp() = popupWindowRate.dismiss()

    protected fun setStatusBarColor(@ColorRes color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(this, color)
        }
    }

    protected fun copyText(text: String) {
        val systemService = getSystemService(Context.CLIPBOARD_SERVICE)
        if (systemService is ClipboardManager) {
            systemService.primaryClip = ClipData.newPlainText("Copied Text", text)
        }
    }

    protected fun popupShowAtLocation(popup: PopupWindow, parent: View, y: Int) {
        if (isResumed()) {
            try {
                popup.showAtLocation(parent, Gravity.CENTER, 0, y)
            } catch (e: WindowManager.BadTokenException) {
                e.printStackTrace()
            }
        }
    }

    private fun isResumed(): Boolean {
        val fieldPaused = FragmentActivity::class.java.getDeclaredField("mResumed") // NoSuchFieldException
        fieldPaused.setAccessible(true)
        val value = fieldPaused.get(this)
        return if (value is Boolean) value else false
    }

    @Deprecated(message = "we are going to replace with FragmentUtils.replaceFragment")
    protected fun replaceFragment(fragment: Fragment, @IdRes id: Int, tag: String? = null) =
        supportFragmentManager.beginTransaction().replace(id, fragment, tag).commitAllowingStateLoss()

    override fun onDestroy() {
        super.onDestroy()
//        AppWatcher.objectWatcher.watch(this)
    }

    companion object {
        const val TOOLBAR_NO_TITLE = 0
        const val PLAY_MARKET_RATE = 42
        const val DELAY = 2_000L
        const val HEIGHT_DIFF_FACTOR = 0.15f
    }
}
