package com.kg.gettransfer.presentation.ui

import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.v4.content.ContextCompat

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar

import android.transition.Fade
import android.transition.Slide

import android.view.View

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.google.android.gms.maps.model.LatLngBounds

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.extensions.*

import com.kg.gettransfer.presentation.adapter.AddressAdapter
import com.kg.gettransfer.presentation.adapter.PopularAddressAdapter

import com.kg.gettransfer.presentation.model.PopularPlace
import com.kg.gettransfer.presentation.presenter.SearchPresenter
import com.kg.gettransfer.presentation.view.SearchView

import com.kg.gettransfer.utilities.Analytics

import kotlinx.android.synthetic.main.a_b_view.*
import kotlinx.android.synthetic.main.a_b_view.view.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.search_address.*
import kotlinx.android.synthetic.main.search_form.*
import kotlinx.android.synthetic.main.search_form.view.*
import kotlinx.android.synthetic.main.toolbar_search_address.*
import kotlinx.android.synthetic.main.toolbar_search_address.view.*

class SearchActivity : BaseActivity(), SearchView {

    @InjectPresenter
    internal lateinit var presenter: SearchPresenter

    private lateinit var current: SearchAddress

    // WTF?
    var mBounds: LatLngBounds? = null

    private lateinit var predefinedPopularPlaces: List<PopularPlace>

    @ProvidePresenter
    fun createSearchPresenter() = SearchPresenter()

    companion object {
        @JvmField val FADE_DURATION  = 500L
        @JvmField val SLIDE_DURATION = 500L

        //@JvmField val LATLON_BOUNDS = "latlon_map_bounds"
    }

    override fun getPresenter(): SearchPresenter = presenter

    @CallSuper
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupAnimation()
        setContentView(R.layout.activity_search)
        setupToolbar()

        scrollViewResults.setOnTouchListener(onTouchListener)
        rv_addressList.setOnTouchListener(onTouchListener)
        rv_popularList.setOnTouchListener(onTouchListener)

        rv_addressList.layoutManager = LinearLayoutManager(this)
        rv_popularList.layoutManager = LinearLayoutManager(this)

        mBounds = intent.getParcelableExtra(SearchView.EXTRA_BOUNDS)

        initSearchFields()
        predefinedPopularPlaces = initPredefinedPopularPlaces()
        ivInverseWay.setOnClickListener { presenter.inverseWay() }
        pointOnMap.setOnClickListener { presenter.selectFinishPointOnMap() }
    }

    private fun initSearchFields() {
        searchFrom.initWidget(this, false)
        searchFrom.sub_title.text = getString(R.string.LNG_FIELD_SOURCE_PICKUP)

        searchTo.initWidget(this, true)
        searchTo.sub_title.text = getString(R.string.LNG_FIELD_DESTINATION)
        changeFocusForSearch()
    }

    private fun setupAnimation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.enterTransition  = Fade().apply { duration = FADE_DURATION }
            window.returnTransition = Slide().apply { duration = SLIDE_DURATION }
        }
    }

    private fun changeFocusForSearch() {
        if(!intent.hasExtra(SearchView.EXTRA_IS_CLICK_TO)) return

        if(intent.getBooleanExtra(SearchView.EXTRA_IS_CLICK_TO, false)) searchTo.changeFocus()
        else searchFrom.changeFocus()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        toolbar.ivBack.setOnClickListener { presenter.onBackCommandClick() }
    }

    fun onSearchFieldEmpty(isTo: Boolean) {
        presenter.onSearchFieldEmpty()
        if(isTo) searchForm.icons_container.b_point.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.b_point_empty))
        else     searchForm.icons_container.a_point.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.a_point_empty))
    }

    override fun onBackPressed() {
        searchTo.clearFocusOnExit()
        searchFrom.clearFocusOnExit()
        presenter.onBackCommandClick()
    }

    private fun initPredefinedPopularPlaces() = listOf(
        PopularPlace(getString(R.string.LNG_SEARCH_POPULAR_AIRPORT), R.drawable.popular_place_airport),
        PopularPlace(getString(R.string.LNG_SEARCH_POPULAR_STATION), R.drawable.popular_place_railway),
        PopularPlace(getString(R.string.LNG_SEARCH_POPULAR_HOTEL), R.drawable.popular_place_hotel))

    /* SearchView */
    override fun setAddressFrom(address: String, sendRequest: Boolean, isEditing: Boolean) {
        searchFrom.initText(address, sendRequest, isEditing)
        if (address.isNotEmpty()) updateIcon(false)
    }

    override fun setAddressTo(address: String, sendRequest: Boolean, isEditing: Boolean) {
        searchTo.initText(address, sendRequest, isEditing)
        if (address.isNotEmpty()) updateIcon(true)
    }

    override fun changeFocusToDestField() = searchTo.changeFocus()

    override fun hideAddressTo() {
        searchTo.isGone  = true
        link_line.isGone = true
        b_point.isGone   = true
        separator.isGone = true
    }

    override fun setAddressListByAutoComplete(list: List<GTAddress>) {
        ll_popular.isVisible    = false
        address_title.isVisible = false
        rv_addressList.adapter?.let {
            (it as AddressAdapter).isLastAddresses = false
            it.updateList(list)
        }
    }

    override fun onFindPopularPlace(isTo: Boolean, place: String) {
        val searchField = if (isTo) searchTo else searchFrom
        searchField.initText(place, true, true)
    }

    override fun setSuggestedAddresses(addressesList: List<GTAddress>) {
        ll_popular.isVisible = true
        rv_popularList.adapter = PopularAddressAdapter(predefinedPopularPlaces) { presenter.onPopularSelected(it) }
        rv_addressList.adapter = AddressAdapter(addressesList) { presenter.onAddressSelected(it) }.apply { isLastAddresses = true }
        address_title.isVisible = addressesList.isNotEmpty()
    }

    override fun updateIcon(isTo: Boolean) {
        if (isTo) searchForm.icons_container.b_point.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.b_point_filled))
        else      searchForm.icons_container.a_point.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.a_point_filled))
    }

    override fun setFocus(isToField: Boolean) {
        if (isToField) searchTo.changeFocus() else searchFrom.changeFocus()
    }
}
