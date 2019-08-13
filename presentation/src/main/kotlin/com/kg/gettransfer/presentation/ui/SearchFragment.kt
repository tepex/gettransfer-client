package com.kg.gettransfer.presentation.ui

import android.os.Bundle

import androidx.annotation.CallSuper
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup

import android.widget.Toast
import androidx.navigation.fragment.findNavController

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
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.search_address.*
import kotlinx.android.synthetic.main.search_form.*
import kotlinx.android.synthetic.main.search_form.view.*
import kotlinx.android.synthetic.main.toolbar_nav_back.*
import kotlinx.android.synthetic.main.toolbar_nav_back.view.*
import org.koin.core.inject
import ru.terrakok.cicerone.Router

class SearchFragment : BaseFragment(), SearchView {

    @InjectPresenter
    internal lateinit var presenter: SearchPresenter

    protected val router: Router by inject()

    private lateinit var predefinedPopularPlaces: List<PopularPlace>

    @ProvidePresenter
    fun createSearchPresenter() = SearchPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_search, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        scrollViewResults.setOnTouchListener(onTouchListener)
        rv_addressList.setOnTouchListener(onTouchListener)
        rv_popularList.setOnTouchListener(onTouchListener)

        rv_addressList.layoutManager = LinearLayoutManager(requireContext())
        rv_popularList.layoutManager = LinearLayoutManager(requireContext())

        initSearchFields()
        predefinedPopularPlaces = initPredefinedPopularPlaces()

        if (presenter.isHourly()) fl_inverse.isVisible = false
        else ivInverseWay.setOnClickListener { presenter.inverseWay() }
        pointOnMap.setOnClickListener { presenter.selectFinishPointOnMap() }
    }

    private val onTouchListener = View.OnTouchListener { view, event ->
        if (event.action == MotionEvent.ACTION_MOVE) {
            view.hideKeyboard()
            return@OnTouchListener false
        } else {
            return@OnTouchListener false
        }
    }

    private fun initSearchFields() {
        searchFrom.initWidget(this, false)
        searchFrom.sub_title.text = getString(R.string.LNG_FIELD_SOURCE_PICKUP)

        searchTo.initWidget(this, true)
        searchTo.sub_title.text = getString(R.string.LNG_FIELD_DESTINATION)
        changeFocusForSearch()
    }

    private fun changeFocusForSearch() {
        if(SearchFragmentArgs.fromBundle(requireArguments()).isClickTo)
            searchTo.changeFocus()
        else
            searchFrom.changeFocus()
    }

    private fun setupToolbar() {
        toolbar.ivBack.setThrottledClickListener { goToBack() }
        toolbar.toolbar_title.text = getString(R.string.LNG_SEARCH)
    }

    fun onSearchFieldEmpty(isTo: Boolean) {
        presenter.onSearchFieldEmpty()
        if(isTo) searchForm.icons_container.tv_b_point.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.back_orange_empty)
                        .also { icons_container.tv_b_point.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTextBlack)) }
        else     searchForm.icons_container.tv_a_point.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.back_orange_empty)
                        .also { icons_container.tv_a_point.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorTextBlack)) }
    }

    fun onClearFocus() {
        searchTo.hideKeyboard()
        searchFrom.hideKeyboard()
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
                ContextCompat.getDrawable(requireContext(), R.drawable.back_circle_marker_orange_filled)
                .also { icons_container.tv_b_point.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorWhite)) }
        else searchForm.icons_container.tv_a_point.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.back_circle_marker_orange_filled)
                        .also { icons_container.tv_a_point.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorWhite)) }
    }

    override fun setFocus(isToField: Boolean) {
        if (isToField) searchTo.changeFocus() else searchFrom.changeFocus()
    }

    override fun onAddressError(message: Int, address: GTAddress, fieldTo: Boolean) {
        (rv_addressList.adapter as AddressAdapter).removeItem(address)
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
                .show()
    }

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        Utils.showError(requireContext(), false, getString(errId))
    }

    override fun goToMap() {
        hideKeyboard()
        if (SearchFragmentArgs.fromBundle(requireArguments()).isCameFromMap)
            goToBack()
        else
            findNavController().navigate(SearchFragmentDirections.goToMap())
    }

    override fun goToBack() {
        hideKeyboard()
        findNavController().popBackStack()
    }

    override fun goToCreateOrder() {
        hideKeyboard()
        if (!SearchFragmentArgs.fromBundle(requireArguments()).isCameFromMap)
            findNavController().navigate(SearchFragmentDirections.goToCreateOrder())
        else
            goToBack()
    }
}
