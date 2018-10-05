package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent

import android.os.Bundle

import android.support.annotation.CallSuper

import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar

import android.transition.Fade
import android.transition.Slide

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.RouteInteractor

import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.extensions.hideKeyboard

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.adapter.AddressAdapter
import com.kg.gettransfer.presentation.presenter.SearchPresenter
import com.kg.gettransfer.presentation.view.SearchView

import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.search_form.*
import kotlinx.android.synthetic.main.toolbar_search_address.*
import kotlinx.android.synthetic.main.toolbar_search_address.view.*

import org.koin.android.ext.android.inject

class SearchActivity: BaseActivity(), SearchView {
    @InjectPresenter
    internal lateinit var presenter: SearchPresenter
    
    internal val routeInteractor: RouteInteractor by inject()
    private lateinit var current: SearchAddress
    
    @ProvidePresenter
    fun createSearchPresenter(): SearchPresenter = SearchPresenter(coroutineContexts,
                                                                   router,
                                                                   systemInteractor,
                                                                   routeInteractor)
    companion object {
        @JvmField val FADE_DURATION  = 500L
        @JvmField val SLIDE_DURATION = 500L
        @JvmField val EXTRA_ADDRESS_FROM = "address_from"
        @JvmField val EXTRA_ADDRESS_TO = "address_to"
    }
    
    protected override var navigator = object: BaseNavigator(this) {
        protected override fun createActivityIntent(context: Context, screenKey: String, data: Any?): Intent? {
            when(screenKey) {
                Screens.CREATE_ORDER -> return Intent(context, CreateOrderActivity::class.java)
            }
            return null
        }
    }
    
    override fun getPresenter(): SearchPresenter = presenter
    
    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* Animation */
        val fade = Fade()
        fade.duration = FADE_DURATION
        window.enterTransition = fade

        val slide = Slide()
        slide.duration = SLIDE_DURATION
        window.returnTransition = slide

        setContentView(R.layout.activity_search)

        setupToolbar()

        addressList.layoutManager = LinearLayoutManager(this)

        searchFrom.initWidget(this, false)
        searchFrom.text = intent.getStringExtra(EXTRA_ADDRESS_FROM)
        searchTo.initWidget(this, true)
        searchTo.text = intent.getStringExtra(EXTRA_ADDRESS_TO)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.ivBack.setOnClickListener { presenter.onBackCommandClick() }
    }

    override fun onBackPressed() {
        searchTo.clearFocusOnExit()
        searchFrom.clearFocusOnExit()
        presenter.onBackCommandClick()
    }

    /* SearchView */
    override fun blockInterface(block: Boolean) {}
    override fun setAddressFrom(address: String, sendRequest: Boolean) { searchFrom.initText(address, sendRequest) }
    override fun setAddressTo(address: String, sendRequest: Boolean) { searchTo.initText(address, sendRequest) }
    override fun setAddressList(list: List<GTAddress>) { addressList.adapter = AddressAdapter(presenter, list) }
}
