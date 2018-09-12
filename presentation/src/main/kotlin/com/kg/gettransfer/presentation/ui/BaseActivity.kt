package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar

import android.support.v4.app.Fragment

import android.view.View

import com.arellomobile.mvp.MvpAppCompatActivity

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.presenter.BasePresenter
import com.kg.gettransfer.presentation.view.BaseView

import org.koin.android.ext.android.inject

import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportAppNavigator

abstract class BaseActivity: MvpAppCompatActivity(), BaseView {
    internal val apiInteractor: ApiInteractor by inject()
    internal val coroutineContexts: CoroutineContexts by inject()
    internal val router: Router by inject()
    protected val navigatorHolder: NavigatorHolder by inject()
    
    protected open lateinit var navigator: BaseNavigator
    
    abstract fun getPresenter(): BasePresenter<*>
	
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
    
	override fun onBackPressed() {
	    getPresenter().onBackCommandClick()
	}
    
    override fun blockInterface(block: Boolean) {}
    
    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        Utils.showError(this, finish, getString(errId, *args))
    }
}

open class BaseNavigator(activity: BaseActivity): SupportAppNavigator(activity, Screens.NOT_USED) {
    protected override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
        when(screenKey) {
            Screens.LOGIN -> return Intent(context, LoginActivity::class.java)
        }
        return null
    }
    protected override fun createFragment(screenKey: String, data: Any?): Fragment? = null
}
