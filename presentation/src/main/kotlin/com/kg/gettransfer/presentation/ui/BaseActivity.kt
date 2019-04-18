package com.kg.gettransfer.presentation.ui

import android.annotation.SuppressLint
import android.content.*

import android.graphics.Color
import android.graphics.Point
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable

import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.annotation.*

import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.Toolbar

import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.*

import android.view.inputmethod.InputMethodManager

import android.widget.LinearLayout
import android.widget.PopupWindow

import com.arellomobile.mvp.MvpAppCompatActivity

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.DatabaseException
import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.extensions.*

import com.kg.gettransfer.presentation.presenter.BasePresenter
import com.kg.gettransfer.presentation.view.BaseView
import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.AppLifeCycleObserver

import com.kg.gettransfer.utilities.LocaleManager
import io.sentry.Sentry
import io.sentry.event.BreadcrumbBuilder

import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_navigation.*

import org.koin.android.ext.android.inject

import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.support.SupportAppNavigator

import timber.log.Timber

abstract class BaseActivity : MvpAppCompatActivity(), BaseView {

    internal val systemInteractor: SystemInteractor by inject()

    internal val router: Router by inject()
    protected val navigatorHolder: NavigatorHolder by inject()
    protected val localeManager: LocaleManager by inject()

    private var rootView: View? = null
    private var rootViewHeight: Int? = null
    protected var isKeyBoardOpened = false

    private lateinit var popupWindowRate: PopupWindow

    protected open var navigator = SupportAppNavigator(this, Screens.NOT_USED)

    protected var viewNetworkNotAvailable: View? = null

    private var displayCutout: DisplayCutout? = null
    private var cutoutOffset: Int = 0

    protected lateinit var _tintBackground: View
    protected val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(@NonNull bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN)
                _tintBackground.isVisible = false
        }

        override fun onSlide(@NonNull bottomSheet: View, slideOffset: Float) {
            _tintBackground.isVisible = true
            _tintBackground.alpha = slideOffset
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

    protected fun hideBottomSheet(bottomSheet: BottomSheetBehavior<View>, bottomSheetLayout: View, hiddenState: Int, event: MotionEvent): Boolean{
        val outRect = Rect()
        bottomSheetLayout.getGlobalVisibleRect(outRect)
        if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
            bottomSheet.state = hiddenState
            return true
        }
        return false
    }

    protected val onTouchListener = View.OnTouchListener { view, event ->
        if (event.action == MotionEvent.ACTION_MOVE) hideKeyboardWithoutClearFocus(this, view)
        else return@OnTouchListener false
    }
    /* BroadCast receivers */
    private val inetReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //if(setNetworkAvailability(context)) getPresenter().checkNewMessagesCached()
            setNetworkAvailability(context)
        }
    }

    private val appStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let { if (it.action == AppLifeCycleObserver.APP_STATE)
                              it.getBooleanExtra(AppLifeCycleObserver.STATUS, false)
                              .also { state -> getPresenter().onAppStateChanged(state) } } }


    }


    protected open fun setNetworkAvailability(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val available = cm.activeNetworkInfo?.isConnected ?: false
        viewNetworkNotAvailable?.let { it.isGone = available }
        return available
    }

    private val loadingFragment by lazy { LoadingFragment() }

    /** [https://stackoverflow.com/questions/37615470/support-library-vectordrawable-resourcesnotfoundexception] */
    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    abstract fun getPresenter(): BasePresenter<*>

    protected fun setToolbar(toolbar: Toolbar, @StringRes titleId: Int = TOOLBAR_NO_TITLE, hasBackAction: Boolean = true, firstLetterToUpperCase: Boolean = false) {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        if (titleId != TOOLBAR_NO_TITLE) {
            val title = getString(titleId)
            toolbar.toolbar_title.text =
                if (!firstLetterToUpperCase) title else title.substring(0, 1).toUpperCase().plus(title.substring(1))
        }
        if (hasBackAction) toolbar.setNavigationOnClickListener { getPresenter().onBackCommandClick() }
    }



    /********************************************************************************************************/
    /************************************************ Life cycles *******************************************/
    /********************************************************************************************************/

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

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
        navigatorHolder.setNavigator(navigator)
    }

    @CallSuper
    protected override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    @CallSuper
    protected override fun onStop() {
        super.onStop()
        Handler().postDelayed({
            LocalBroadcastManager.getInstance(this).apply {
    //            unregisterReceiver(offerReceiver)
                unregisterReceiver(appStateReceiver)
            }
        }, 2000)
        unregisterReceiver(inetReceiver)

    }

    @CallSuper
    override fun onDestroy() {
        super.onDestroy()
    }

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        if (block) {
            if(useSpinner) showLoading()
        } else hideLoading()
    }

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        val errMessage = getString(errId, *args)
        Timber.e(RuntimeException(errMessage))
        Utils.showError(this, finish, errMessage)
    }

    override fun setError(e: ApiException) {
        Timber.e("code: ${e.code}", e)
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage(e.details).build())
        Sentry.capture(e)
        if (e.code != ApiException.NETWORK_ERROR) Utils.showError(this, false, getString(R.string.LNG_ERROR) + ": " + e.message)
    }

    override fun setError(e: DatabaseException) {
        Sentry.getContext().recordBreadcrumb(BreadcrumbBuilder().setMessage("CacheError: ${e.details}").build())
        Sentry.capture(e)
    }

    protected fun showKeyboard() {
        currentFocus?.showKeyboard()
    }

    protected fun hideKeyboard(): Boolean {
        currentFocus?.run {
            hideKeyboard()
            clearFocus()
        }
        return true
    }

    fun hideKeyboardWithoutClearFocus(context: Context, view: View): Boolean {
        val imm = context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        return false
    }

    //здесь лучше ничего не трогать
    private fun countDifference(): Boolean {
        if (rootViewHeight == null) rootViewHeight = rootView!!.rootView.height

        val visibleRect = Rect().also { rootView!!.getWindowVisibleDisplayFrame(it) }
        return (rootViewHeight!! - visibleRect.bottom) < rootViewHeight!! * 0.15
    }

    fun addKeyBoardDismissListener(checkKeyBoardState: (Boolean) -> Unit) {
        rootView = findViewById(android.R.id.content)
        rootView!!.viewTreeObserver.addOnGlobalLayoutListener {
            val state = countDifference()
            if (!state && !isKeyBoardOpened){
                isKeyBoardOpened = true
                checkKeyBoardState(isKeyBoardOpened)
            } else if (state && isKeyBoardOpened){
                isKeyBoardOpened = false
                checkKeyBoardState(isKeyBoardOpened)
            }
        }
    }

    //protected fun openScreen(screen: String) { router.navigateTo(screen) }

    override fun attachBaseContext(newBase: Context?) {
        if (newBase != null) super.attachBaseContext(localeManager.updateResources(newBase, systemInteractor.locale))
        else super.attachBaseContext(null)
    }

    private fun showLoading() {
        if (loadingFragment.isAdded) return
        supportFragmentManager.beginTransaction().apply {
            add(android.R.id.content, loadingFragment)
            commit()
        }
    }

    private fun hideLoading() {
        if (!loadingFragment.isAdded) return
        supportFragmentManager.beginTransaction().apply {
            remove(loadingFragment)
            commit()
        }
    }
    /*
    Use this method to fix UI, which depends on screen size.
    Returns Point, that has x = display width, y = display height
     */
    protected open fun fixUIDependedOnScreenSize() =
            Point().also { windowManager.defaultDisplay.getSize(it) }

    protected fun getScreenSide(height: Boolean): Int {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return if (height) displayMetrics.heightPixels else displayMetrics.widthPixels
    }

    protected fun applyDim(parent: ViewGroup, dimAmount: Float) {
        parent.overlay.add(ColorDrawable(Color.BLACK).apply {
            setBounds(0, 0, parent.width, parent.height)
            alpha = (dimAmount * 255).toInt()
        })
    }

    protected fun clearDim(parent: ViewGroup) = parent.overlay.clear()

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            displayCutout = window?.decorView?.rootWindowInsets?.displayCutout
            displayCutout?.let { cutoutOffset = displayCutout?.safeInsetTop!! }
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

        val statusBarResource = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight = if(statusBarResource > 0) resources.getDimensionPixelSize(statusBarResource) else 0

        return if (displayCutout != null) (screenHeight - actionBarHeight - statusBarHeight) + cutoutOffset
        else screenHeight - actionBarHeight - statusBarHeight
    }

    protected fun showPopUpWindow(@LayoutRes res: Int, parent: View): View? {
        if(isResumed()) {
            applyDim(window.decorView.rootView as ViewGroup, DIM_AMOUNT)
            val layoutPopUp = LayoutInflater.from(this).inflate(res, null)
            val widthPx = getScreenSide(false) - 40

            popupWindowRate = PopupWindow(layoutPopUp, widthPx, LinearLayout.LayoutParams.WRAP_CONTENT, true).apply {
                setOnDismissListener {
                    clearDim(window.decorView.rootView as ViewGroup)
                    mDisMissAction()
                }
                softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED

            }
            popupWindowRate.showAtLocation(parent, Gravity.CENTER, 0, 100)
            popupWindowRate.isOutsideTouchable = false
            return layoutPopUp
        }

        return null
    }




    protected var mDisMissAction = { }    // used in popup dismiss event, need later init, when view with map would be created

    protected fun redirectToPlayMarket() {
        val url = getString(R.string.market_link) + getString(R.string.app_package)
        startActivityForResult(
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }, PLAY_MARKET_RATE)
    }

    @CallSuper
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLAY_MARKET_RATE) thanksForRate()
    }

    open fun thanksForRate() {}

    protected fun closePopUp() = popupWindowRate.dismiss()

    companion object {
        const val TOOLBAR_NO_TITLE = 0
        const val PLAY_MARKET_RATE = 42

        const val DIM_AMOUNT = 0.5f
        const val SCREEN_WIDTH_REQUIRING_SMALL_TEXT_SIZE = 768
    }

    private fun isResumed(): Boolean {
        val fieldPaused = FragmentActivity::class.java.getDeclaredField("mResumed"); //NoSuchFieldException
        fieldPaused.setAccessible(true)
        return fieldPaused.get(this) as Boolean
    }
}
