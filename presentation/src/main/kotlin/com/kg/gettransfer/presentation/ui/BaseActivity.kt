package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar

import android.view.View

import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.presenter.LoginPresenter
import com.kg.gettransfer.presentation.view.LoginView

import kotlinx.android.synthetic.main.toolbar.view.*

import org.koin.android.ext.android.inject

import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportAppNavigator

open class BaseActivity: MvpAppCompatActivity(), BaseView {
    lateinit open var presenter: BasePresenter

    protected val apiInteractor: ApiInteractor by inject()
    protected val coroutineContexts: CoroutineContexts by inject()
    protected val navigatorHolder: NavigatorHolder by inject()
    protected val router: Router by inject()
    
    protected val navigator: Navigator = object: SupportAppNavigator(this, Screens.NOT_USED) {
        protected override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? = null
        protected override fun createFragment(screenKey: String, data: Any?): Fragment? = null
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
    
    override fun blockInterface(block: Boolean) {}
    
    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        Utils.showError(this, finish, getString(errId, *args))
    }
    
    override fun onBackPressed() { presenter.onBackCommandClick() }
}
