package com.kg.gettransfer.presentation.ui

import android.animation.Animator
import android.os.Bundle
import android.os.Handler

import androidx.annotation.CallSuper
import androidx.annotation.StringRes

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
import com.kg.gettransfer.presentation.ui.utils.FragmentUtils
import com.kg.gettransfer.presentation.view.SearchView

import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.search_form.*
import kotlinx.android.synthetic.main.toolbar_nav_back.*
import kotlinx.android.synthetic.main.toolbar_nav_back.view.*

class SearchFragment : BaseFragment(), SearchView {

    @InjectPresenter
    internal lateinit var presenter: SearchPresenter

    private lateinit var predefinedPopularPlaces: List<PopularPlace>

    @ProvidePresenter
    fun createSearchPresenter() = SearchPresenter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_search, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()

        predefinedPopularPlaces = initPredefinedPopularPlaces()

        pointOnMap.setOnClickListener { presenter.selectFinishPointOnMap() }
    }

    /**
     * Init view after fragment started
     */
    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator {
        return FragmentUtils.onCreateAnimation(requireContext(), enter) {

            searchForm.visibleFade(true)

            scrollViewResults.setOnTouchListener(onTouchListener)
            rv_addressList.setOnTouchListener(onTouchListener)
            rv_popularList.setOnTouchListener(onTouchListener)

            initSearchFields()
            if (!presenter.isHourly()) {
                ivInverseWay.isVisible = true
                ivInverseWay.setOnClickListener { presenter.inverseWay() }
            }
            presenter.init()
        }
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
        searchForm.initFromWidget(this, getString(R.string.LNG_FIELD_SOURCE_PICKUP))
        searchForm.initToWidget(this, getString(R.string.LNG_FIELD_DESTINATION))

        changeFocusForSearch()
    }

    private fun changeFocusForSearch() {
        if(SearchFragmentArgs.fromBundle(requireArguments()).isClickTo)
            searchTo.changeFocus()
        else
            searchFrom.changeFocus()
    }

    private fun setupToolbar() {
        toolbar.ivBack.setThrottledClickListener {
            hideKeyboard()
            Handler().postDelayed( {goToBack()}, 500)
        }
        toolbar.toolbar_title.text = getString(R.string.LNG_SEARCH)

    }


    fun onSearchFieldEmpty(isToField: Boolean) {
        presenter.onSearchFieldEmpty()
        searchForm.markFieldEmpty(isToField)
    }

    private fun initPredefinedPopularPlaces() = listOf(
        PopularPlace(getString(R.string.LNG_SEARCH_POPULAR_AIRPORT), R.drawable.popular_place_airport),
        PopularPlace(getString(R.string.LNG_SEARCH_POPULAR_STATION), R.drawable.popular_place_railway),
        PopularPlace(getString(R.string.LNG_SEARCH_POPULAR_HOTEL), R.drawable.popular_place_hotel))

    /* SearchView */
    override fun setAddressFrom(address: String, sendRequest: Boolean, isEditing: Boolean) {
        searchFrom.initText(address, sendRequest, isEditing)
        if (address.isNotEmpty()) markFieldFilled(false)
    }

    override fun setAddressTo(address: String, sendRequest: Boolean, isEditing: Boolean) {
        searchTo.initText(address, sendRequest, isEditing)
        if (address.isNotEmpty()) markFieldFilled(true)
    }

    override fun changeFocusToDestField() = searchTo.changeFocus()

    override fun hideAddressTo() = searchForm.hideToField()

    override fun setAddressListByAutoComplete(list: List<GTAddress>) {
        popular_title.isVisible  = false
        rv_popularList.isVisible = false
        address_title.isVisible  = false
        rv_addressList.adapter?.let {
            (it as AddressAdapter).isLastAddresses = false
            it.updateList(list)
        }
    }

    override fun onFindPopularPlace(isToField: Boolean, place: String) = searchForm.findPopularPlace(isToField, place)

    override fun setSuggestedAddresses(addressesList: List<GTAddress>) {
        popular_title.isVisible  = true
        rv_popularList.isVisible = true
        rv_popularList.adapter = PopularAddressAdapter(predefinedPopularPlaces) { presenter.onPopularSelected(it) }
        rv_addressList.adapter = AddressAdapter(addressesList) { presenter.onAddressSelected(it) }.apply { isLastAddresses = true }
        address_title.isVisible = addressesList.isNotEmpty()
    }

    override fun markFieldFilled(isToField: Boolean) = searchForm.markFiledFilled(isToField)

    override fun setFocus(isToField: Boolean) = searchForm.changeFocus(isToField)

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
//        hideKeyboard()
        findNavController().navigateUp()
    }

    override fun goToCreateOrder() {
        hideKeyboard()
        if (!SearchFragmentArgs.fromBundle(requireArguments()).isCameFromMap)
            findNavController().navigate(SearchFragmentDirections.goToCreateOrder())
        else
            goToBack()
    }
}
