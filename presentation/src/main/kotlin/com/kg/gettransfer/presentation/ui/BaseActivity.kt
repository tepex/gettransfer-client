package com.kg.gettransfer.presentation.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

import android.graphics.Rect

import android.net.ConnectivityManager

import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatDelegate

import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager

import com.arellomobile.mvp.MvpAppCompatActivity

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.SystemInteractor

import com.kg.gettransfer.extensions.hideKeyboard
import com.kg.gettransfer.extensions.showKeyboard

import com.kg.gettransfer.presentation.IntentKeys
import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.presenter.BasePresenter
import com.kg.gettransfer.presentation.presenter.PaymentSettingsPresenter
import com.kg.gettransfer.presentation.view.BaseView

import com.kg.gettransfer.utilities.LocaleManager

import java.util.Date

import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

import kotlinx.serialization.json.JSON

import org.koin.android.ext.android.inject

import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportAppNavigator

import timber.log.Timber

abstract class BaseActivity: MvpAppCompatActivity(), BaseView {
    internal val systemInteractor: SystemInteractor by inject()
    
    internal val coroutineContexts: CoroutineContexts by inject()
    internal val router: Router by inject()
    protected val navigatorHolder: NavigatorHolder by inject()
    protected val localeManager: LocaleManager by inject()

    private var rootView: View? = null
    private var rootViewHeight: Int? = null

    protected open lateinit var navigator: BaseNavigator

    protected var viewNetworkNotAvailable: View? = null

    protected val onTouchListener = View.OnTouchListener{ view, event ->
        if(event.action == MotionEvent.ACTION_MOVE) hideKeyboardWithoutClearFocus(this, view)
        else return@OnTouchListener false
    }
    
    private val compositeDisposable = Job()
    private val utils = AsyncUtils(coroutineContexts, compositeDisposable)
    
    private val inetReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) = setNetworkAvailability(context)
        
        fun setNetworkAvailability(context: Context) {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val available = cm.activeNetworkInfo?.let { it.isConnected() } ?: false
            viewNetworkNotAvailable?.let { if(available) it.visibility = View.GONE else it.visibility = View.VISIBLE }
        }
    }
    
    /** [https://stackoverflow.com/questions/37615470/support-library-vectordrawable-resourcesnotfoundexception] */
    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
    
    abstract fun getPresenter(): BasePresenter<*>
	
    @CallSuper
    protected override fun onStart() {
        super.onStart()
        registerReceiver(inetReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        inetReceiver.setNetworkAvailability(this) 
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
        unregisterReceiver(inetReceiver)
        super.onStop()
	}

    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        if(block) {
            if(useSpinner) LoadingFragment.showLoading(supportFragmentManager)
        }
        else  LoadingFragment.hideLoading(supportFragmentManager)
    }
    
    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        var errMessage = getString(errId, *args)
        Timber.e(RuntimeException(errMessage))
        Utils.showError(this, finish, errMessage)
    }
    
    override fun setError(e: ApiException) {
        Timber.e("code: ${e.code}", e)
        Utils.showError(this, false, getString(R.string.err_server, e.message))
    }

    protected fun showKeyboard() {
        currentFocus?.showKeyboard()
    }

    protected fun hideKeyboard() {
        currentFocus?.let {
            it.hideKeyboard()
            it.clearFocus()
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
        if(rootViewHeight == null) rootViewHeight = rootView!!.rootView.height

        val visibleRect = getRect()
        return (rootViewHeight!! - visibleRect.bottom) < rootViewHeight!! * 0.15
    }

    private fun getRect(): Rect {
        val rect = Rect()
        rootView!!.getWindowVisibleDisplayFrame(rect)
        return rect
    }

    fun addKeyBoardDismissListener(checkKeyBoardState: (Boolean) -> Unit) {
        rootView = findViewById(android.R.id.content)
        rootView!!.viewTreeObserver.addOnGlobalLayoutListener { checkKeyBoardState(countDifference()) }
    }

    protected fun openScreen(screen: String) { router.navigateTo(screen) }


    override fun attachBaseContext(newBase: Context?) {
        if(newBase != null) super.attachBaseContext(localeManager.updateResources(newBase, systemInteractor.locale))
        else super.attachBaseContext(null)
    }
}

open class BaseNavigator(activity: BaseActivity): SupportAppNavigator(activity, Screens.NOT_USED) {
    protected override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
        when(screenKey) {
            Screens.DETAILS -> return Intent(context, TransferDetailsActivity::class.java).apply {
                putExtra(TransferDetailsActivity.TRANSFER_ID, data as Long)
            }
            Screens.OFFERS -> return Intent(context, OffersActivity::class.java).apply {
                putExtra(OffersActivity.TRANSFER_ID, data as Long)
            }
            Screens.PAYMENT_SETTINGS -> Intent(context, PaymentSettingsActivity::class.java).apply {
                val params = data!! as PaymentSettingsPresenter.Params
                putExtra(PaymentSettingsPresenter.PARAMS, JSON.stringify(PaymentSettingsPresenter.Params.serializer(), params))
            }
            Screens.LOGIN -> return Intent(context, LoginActivity::class.java).apply {
                val nextScreenAndEmailPair = data as Pair<*, *>
                val nextScreen = nextScreenAndEmailPair.first as String
                val email = nextScreenAndEmailPair.second as String
                addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                putExtra(IntentKeys.SCREEN_FOR_RETURN, nextScreen)
                putExtra(IntentKeys.EMAIL_TO_LOGIN, email)
            }
        }
        return null
    }
    protected override fun createFragment(screenKey: String, data: Any?): Fragment? = null
}
