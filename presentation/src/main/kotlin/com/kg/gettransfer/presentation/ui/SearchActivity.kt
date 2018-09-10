package com.kg.gettransfer.presentation.ui

import android.content.Context
import android.content.Intent

import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar

import android.transition.Fade
import android.transition.Slide

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.AddressInteractor
import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.extensions.hideKeyboard
import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.adapter.AddressAdapter
import com.kg.gettransfer.presentation.presenter.SearchPresenter
import com.kg.gettransfer.presentation.view.SearchView

import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.search_form.*
import kotlinx.android.synthetic.main.toolbar.*

import org.koin.android.ext.android.inject

class SearchActivity: BaseActivity(), SearchView {
    @InjectPresenter
    internal lateinit var presenter: SearchPresenter
    
    internal val addressInteractor: AddressInteractor by inject()
    private lateinit var current: SearchAddress
    
    @ProvidePresenter
    fun createSearchPresenter(): SearchPresenter = SearchPresenter(coroutineContexts,
                                                                   router,
                                                                   addressInteractor)
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
    
    init {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }
    
    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* Animation */
        val fade = Fade()
        fade.setDuration(FADE_DURATION)
        getWindow().setEnterTransition(fade)

        val slide = Slide()
        slide.setDuration(SLIDE_DURATION)
        getWindow().setReturnTransition(slide)

        /* init UI */
        setContentView(R.layout.activity_search)

        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        (toolbar as Toolbar).setNavigationOnClickListener { presenter.onBackCommandClick() }

        addressList.layoutManager = LinearLayoutManager(this)

        searchFrom.initWidget(this, false)
        searchFrom.text = intent.getStringExtra(EXTRA_ADDRESS_FROM)
        searchTo.initWidget(this, true)
        searchTo.text = intent.getStringExtra(EXTRA_ADDRESS_TO)
    }

    override fun onBackPressed() {
        Utils.hideKeyboard(this, currentFocus)
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
