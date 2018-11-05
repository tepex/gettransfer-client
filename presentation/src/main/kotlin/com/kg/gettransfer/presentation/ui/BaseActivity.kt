package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent
import android.graphics.Rect

import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatDelegate

import com.arellomobile.mvp.MvpAppCompatActivity

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.extensions.hideKeyboard
import com.kg.gettransfer.extensions.showKeyboard

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.presenter.BasePresenter
import com.kg.gettransfer.presentation.view.BaseView

import org.koin.android.ext.android.inject

import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportAppNavigator

import timber.log.Timber
import java.util.*
import android.content.BroadcastReceiver
import com.kg.gettransfer.presentation.NetworkStateChangeReceiver
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.view.View
import com.kg.gettransfer.presentation.NetworkStateChangeReceiver.Companion.IS_NETWORK_AVAILABLE


abstract class BaseActivity: MvpAppCompatActivity(), BaseView {
    internal val systemInteractor: SystemInteractor by inject()
    
    internal val coroutineContexts: CoroutineContexts by inject()
    internal val router: Router by inject()
    protected val navigatorHolder: NavigatorHolder by inject()

    private var rootView: View? = null
    private var rootViewHeight: Int? = null

    protected open lateinit var navigator: BaseNavigator

    protected var viewNetworkNotAvailable: View? = null

    /** [https://stackoverflow.com/questions/37615470/support-library-vectordrawable-resourcesnotfoundexception] */
    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
    
    abstract fun getPresenter(): BasePresenter<*>
	
    @CallSuper
    protected override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)

        val intentFilter = IntentFilter(NetworkStateChangeReceiver.NETWORK_AVAILABLE_ACTION)
        LocalBroadcastManager.getInstance(this).registerReceiver(object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false)
                getPresenter().changeNetworkState(isNetworkAvailable)
            }
        }, intentFilter)
    }
    
    @CallSuper
    protected override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }
    
	override fun onBackPressed() {
	    getPresenter().onBackCommandClick()
	}
    
    override fun blockInterface(block: Boolean, useSpinner: Boolean) {
        if(block) {
            if(useSpinner) LoadingFragment.showLoading(supportFragmentManager)
        }
        else {
            LoadingFragment.hideLoading(supportFragmentManager)
        }
    }
    
    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        var errMessage = getString(errId, *args)
        Timber.e(RuntimeException(errMessage))
        Utils.showError(this, finish, errMessage)
    }
    
    override fun setError(e: Throwable) {
        Timber.e(e)
        Utils.showError(this, false, getString(R.string.err_server, e.message))
    }

    protected fun showKeyboard() {
        val view = currentFocus
        view?.showKeyboard()
    }

    protected fun hideKeyboard() {
        val view = currentFocus
        view?.hideKeyboard()
        view?.clearFocus()
    }

    override fun showViewNetworkNotAvailable(isNetworkAvailable: Boolean) {
        if(viewNetworkNotAvailable != null){
            if(isNetworkAvailable) viewNetworkNotAvailable!!.visibility = View.GONE
            else viewNetworkNotAvailable!!.visibility = View.VISIBLE
        }
    }

    //здесь лучше ничего не трогать
    private fun countDifference(): Boolean {
        if(rootViewHeight == null) rootViewHeight = rootView!!.rootView.height

        val visibleRect = getRect()
        return (rootViewHeight!! - visibleRect.bottom) < rootViewHeight!! * 0.15
    }

    private fun getRect(): Rect{
        val rect = Rect()
        rootView!!.getWindowVisibleDisplayFrame(rect)
        return rect
    }

    fun addKeyBoardDismissListener(checkKeyBoardState: (Boolean) -> Unit) {
        rootView = findViewById(android.R.id.content)
        rootView!!.viewTreeObserver.addOnGlobalLayoutListener {
            checkKeyBoardState(countDifference())
        }
    }

    protected fun openScreen(screen: String){
        router.navigateTo(screen)

    }
}

open class BaseNavigator(activity: BaseActivity): SupportAppNavigator(activity, Screens.NOT_USED) {
    protected override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
        when(screenKey) {
            Screens.LOGIN -> return Intent(context, LoginActivity::class.java)
            Screens.DETAILS -> return Intent(context, TransferDetailsActivity::class.java)
            Screens.OFFERS -> return Intent(context, OffersActivity::class.java)
            Screens.PAYMENT_SETTINGS -> return context.getPaymentSettingsActivityLaunchIntent(data as Date)
        }
        return null
    }
    protected override fun createFragment(screenKey: String, data: Any?): Fragment? = null
}
