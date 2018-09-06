package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.LayerDrawable

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.Toolbar

import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.R.id.ratingBar

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor

import com.kg.gettransfer.presentation.Screens

import com.kg.gettransfer.presentation.presenter.CreateOrderPresenter
import com.kg.gettransfer.presentation.presenter.OffersPresenter

import com.kg.gettransfer.presentation.view.OffersView

import kotlinx.android.synthetic.main.activity_offers.*
import kotlinx.android.synthetic.main.toolbar.view.*
import kotlinx.android.synthetic.main.view_offer.view.*
import kotlinx.android.synthetic.main.view_offer.*

import org.koin.android.ext.android.inject

import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.SupportAppNavigator

class OffersActivity: MvpAppCompatActivity(), OffersView {
    @InjectPresenter
    internal lateinit var presenter: OffersPresenter

    private val apiInteractor: ApiInteractor by inject()
    private val coroutineContexts: CoroutineContexts by inject()
    private val navigatorHolder: NavigatorHolder by inject()
    private val router: Router by inject()
    
    private val navigator: Navigator = object: SupportAppNavigator(this, Screens.NOT_USED) {
        protected override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
            when(screenKey) {
                Screens.LOGIN -> return Intent(this@OffersActivity, LoginActivity::class.java)
            }
            return null
        }
        protected override fun createFragment(screenKey: String, data: Any?): Fragment? = null
    }

    @ProvidePresenter
    fun createOffersPresenter(): OffersPresenter = OffersPresenter(coroutineContexts, router, apiInteractor)

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_offers)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).toolbar_title.setText(R.string.carrier_offers)
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }

        setOnClickListeners()

        val stars = ratingBarOffer.progressDrawable as LayerDrawable
        stars.getDrawable(2).setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP)
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
    
    fun setOnClickListeners(){
        layoutTransferRequestInfo.setOnClickListener{}
    }

    override fun blockInterface(block: Boolean) {}

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        Utils.showError(this, finish, getString(errId, *args))
    }

    override fun onBackPressed() {
        presenter.onBackCommandClick()
    }
}