package com.kg.gettransfer.presentation.ui

import android.os.Build
import android.os.Bundle

import android.support.annotation.CallSuper
import android.support.annotation.StringRes
import android.support.v4.content.ContextCompat

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar

import android.transition.Fade
import android.transition.Slide

import android.widget.Toast

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.extensions.*

import com.kg.gettransfer.presentation.adapter.AddressAdapter
import com.kg.gettransfer.presentation.adapter.PopularAddressAdapter

import com.kg.gettransfer.presentation.model.PopularPlace
import com.kg.gettransfer.presentation.presenter.SearchPresenter
import com.kg.gettransfer.presentation.view.Screens
import com.kg.gettransfer.presentation.view.SearchView

import kotlinx.android.synthetic.main.a_b_orange_view.*
import kotlinx.android.synthetic.main.a_b_orange_view.view.*
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.search_address.*
import kotlinx.android.synthetic.main.search_form.*
import kotlinx.android.synthetic.main.search_form.view.*
import kotlinx.android.synthetic.main.toolbar_nav_back.*
import kotlinx.android.synthetic.main.toolbar_nav_back.view.*

class SearchActivity : BaseActivity(), SearchView {

    @InjectPresenter
    internal lateinit var presenter: SearchPresenter

    private lateinit var current: SearchAddress

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

        getIntents()

        initSearchFields()
        predefinedPopularPlaces = initPredefinedPopularPlaces()

        if (presenter.isHourly()) fl_inverse.isVisible = false
        else ivInverseWay.setOnClickListener { presenter.inverseWay() }
        pointOnMap.setOnClickListener { presenter.selectFinishPointOnMap() }
    }

    private fun getIntents() {
        presenter.backwards = intent.getBooleanExtra(Screens.RETURN_MAIN, false)
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
        toolbar.ivBack.setThrottledClickListener { presenter.onBackCommandClick() }
        toolbar.toolbar_title.text = getString(R.string.LNG_SEARCH)
    }

    fun onSearchFieldEmpty(isTo: Boolean) {
        presenter.onSearchFieldEmpty()
        if(isTo) searchForm.icons_container.tv_b_point.background =
                ContextCompat.getDrawable(this, R.drawable.back_orange_empty)
                        .also { icons_container.tv_b_point.setTextColor(ContextCompat.getColor(this, R.color.colorTextBlack)) }
        else     searchForm.icons_container.tv_a_point.background =
                ContextCompat.getDrawable(this, R.drawable.back_orange_empty)
                        .also { icons_container.tv_a_point.setTextColor(ContextCompat.getColor(this, R.color.colorTextBlack)) }
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
        tv_b_point.isGone   = true
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
        if (isTo) searchForm.icons_container.tv_b_point.background =
                ContextCompat.getDrawable(this, R.drawable.back_circle_marker_orange_filled)
                .also { icons_container.tv_b_point.setTextColor(ContextCompat.getColor(this, R.color.colorWhite)) }
        else searchForm.icons_container.tv_a_point.background =
                ContextCompat.getDrawable(this, R.drawable.back_circle_marker_orange_filled)
                        .also { icons_container.tv_a_point.setTextColor(ContextCompat.getColor(this, R.color.colorWhite)) }
    }

    override fun setFocus(isToField: Boolean) {
        if (isToField) searchTo.changeFocus() else searchFrom.changeFocus()
    }

    override fun onAddressError(message: Int, address: GTAddress, fieldTo: Boolean) {
        (rv_addressList.adapter as AddressAdapter).removeItem(address)
        Toast.makeText(this, message, Toast.LENGTH_LONG)
                .show()
    }

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        Utils.showError(this, false, getString(errId))
    }
}
