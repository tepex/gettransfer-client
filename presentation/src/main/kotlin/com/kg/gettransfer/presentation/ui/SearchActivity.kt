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

import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.google.android.gms.maps.model.LatLngBounds

import com.google.android.gms.maps.model.LatLngBounds

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.interactor.RouteInteractor

import com.kg.gettransfer.domain.model.GTAddress

import com.kg.gettransfer.extensions.hideKeyboard

import com.kg.gettransfer.presentation.Screens

import com.kg.gettransfer.presentation.adapter.AddressAdapter
import com.kg.gettransfer.presentation.adapter.PopularAddressAdapter

import com.kg.gettransfer.presentation.model.PopularPlace
import com.kg.gettransfer.presentation.presenter.SearchPresenter
import com.kg.gettransfer.presentation.view.SearchView
import kotlinx.android.synthetic.main.a_b_view.view.*

import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.search_form.*
import kotlinx.android.synthetic.main.search_form.view.*
import kotlinx.android.synthetic.main.toolbar_search_address.*
import kotlinx.android.synthetic.main.toolbar_search_address.view.*

import org.koin.android.ext.android.inject

class SearchActivity: BaseActivity(), SearchView {
    @InjectPresenter
    internal lateinit var presenter: SearchPresenter
    
    internal val routeInteractor: RouteInteractor by inject()
    private lateinit var current: SearchAddress

    var mBounds: LatLngBounds? = null
    
    private lateinit var predefinedPopularPlaces: List<PopularPlace>

    @ProvidePresenter
    fun createSearchPresenter(): SearchPresenter = SearchPresenter(coroutineContexts,
                                                                   router,
                                                                   systemInteractor,
                                                                   routeInteractor)
    companion object {
        @JvmField val FADE_DURATION  = 500L
        @JvmField val SLIDE_DURATION = 500L
        
        @JvmField val EXTRA_ADDRESS_FROM = "address_from"
        @JvmField val EXTRA_ADDRESS_TO   = "address_to"
        @JvmField val EXTRA_FROM_CLICK   = "from_click"
        @JvmField val EXTRA_TO_CLICK     = "to_click"

        @JvmField val LATLON_BOUNDS = "latlon_map_bounds"
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
        setupAnimation()
        setContentView(R.layout.activity_search)
        setupToolbar()

        rv_addressList.layoutManager = LinearLayoutManager(this)
        rv_popularList.layoutManager = LinearLayoutManager(this)

        mBounds = intent.getParcelableExtra(LATLON_BOUNDS)

        initSearchFields()
//        changeFocusForSearch()
        predefinedPopularPlaces = initPredefinedPopularPlaces()
        ivInverseWay.setOnClickListener { presenter.inverseWay() }
    }

    private fun initSearchFields() {
        searchFrom.initWidget(this, false)
//        searchFrom.text = intent.getStringExtra(EXTRA_ADDRESS_FROM)
        searchTo.initWidget(this, true)
  //      searchTo.text = intent.getStringExtra(EXTRA_ADDRESS_TO)

        changeFocusForSearch()
    }

    private fun setupAnimation() {
        val fade = Fade()
        fade.duration = FADE_DURATION
        window.enterTransition = fade

        val slide = Slide()
        slide.duration = SLIDE_DURATION
        window.returnTransition = slide
    }

    private fun changeFocusForSearch() {
        if(intent.getBooleanExtra(EXTRA_FROM_CLICK, false)) searchFrom.changeFocus()
        else if(intent.getBooleanExtra(EXTRA_TO_CLICK, false)) searchTo.changeFocus()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.ivBack.setOnClickListener { presenter.onBackCommandClick() }
    }

    fun onSearchFieldEmpty(isTo: Boolean) {
        presenter.onSearchFieldEmpty()
        var iconRes: Int
        if (isTo){
            iconRes = R.drawable.b_point_empty
            searchForm.icons_container.b_point.setImageDrawable(getDrawable(iconRes))
        } else {
            iconRes = R.drawable.a_point_empty
            searchForm.icons_container.a_point.setImageDrawable(getDrawable(iconRes))
        }
    }

    override fun onBackPressed() {
        searchTo.clearFocusOnExit()
        searchFrom.clearFocusOnExit()
        presenter.onBackCommandClick()
    }

    private fun initPredefinedPopularPlaces() = listOf(
        PopularPlace(getString(R.string.airport), R.drawable.popular_place_airport),
        PopularPlace(getString(R.string.railway), R.drawable.popular_place_railway),
        PopularPlace(getString(R.string.hotel),   R.drawable.popular_place_hotel))        

    /* SearchView */
    override fun setAddressFrom(address: String, sendRequest: Boolean, isEditing: Boolean) {
        searchFrom.initText(address, sendRequest, isEditing)
        updateIcon(false)
    }
    override fun setAddressTo(address: String, sendRequest: Boolean, isEditing: Boolean)   {
        searchTo.initText(address, sendRequest, isEditing)
        updateIcon(true)
    }
    
    override fun setAddressListByAutoComplete(list: List<GTAddress>) {
        ll_popular.visibility = View.GONE
        address_title.visibility = View.GONE
        if(rv_addressList.adapter != null) {
            (rv_addressList.adapter as AddressAdapter).isLastAddresses = false
            (rv_addressList.adapter as AddressAdapter).updateList(list)
        }
    }
    
    override fun onFindPopularPlace(isTo: Boolean, place: String) {
        val searchField = if(isTo) searchTo else searchFrom
        searchField.initText(place, true, true)
    }

    override fun setSuggestedAddresses(addressesList: List<GTAddress>) {
        ll_popular.visibility = View.VISIBLE
        rv_popularList.adapter = PopularAddressAdapter(presenter, predefinedPopularPlaces)
        val addressAdapter = AddressAdapter(presenter, addressesList)
        addressAdapter.isLastAddresses = true
        
        rv_addressList.adapter = addressAdapter
        if(addressesList.isEmpty()) address_title.visibility = View.GONE
        else address_title.visibility = View.VISIBLE
    }

    override fun updateIcon(isTo: Boolean) {
        var iconRes: Int
        if (isTo){
            iconRes = R.drawable.b_point_filled
            searchForm.icons_container.b_point.setImageDrawable(getDrawable(iconRes))
        } else {
            iconRes = R.drawable.a_point_filled
            searchForm.icons_container.a_point.setImageDrawable(getDrawable(iconRes))
        }
    }
}
