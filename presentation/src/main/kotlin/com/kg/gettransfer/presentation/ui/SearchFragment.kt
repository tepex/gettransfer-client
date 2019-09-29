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
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.extensions.hideKeyboard
import com.kg.gettransfer.extensions.setThrottledClickListener

import com.kg.gettransfer.presentation.adapter.AddressAdapter
import com.kg.gettransfer.presentation.adapter.PopularAddressAdapter

import com.kg.gettransfer.presentation.model.PopularPlace
import com.kg.gettransfer.presentation.presenter.SearchPresenter
import com.kg.gettransfer.presentation.ui.utils.FragmentUtils
import com.kg.gettransfer.presentation.ui.dialogs.HourlyDurationDialogFragment
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper
import com.kg.gettransfer.presentation.view.SearchView

import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.android.synthetic.main.search_form.*
import kotlinx.android.synthetic.main.toolbar_search.view.*

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

        rv_addressList.setOnTouchListener(onTouchListener)

        initSearchFields()
        if (!presenter.isHourly()) {
            ivInverseWay.isVisible = true
            ivInverseWay.setOnClickListener { presenter.inverseWay() }
        }
    }

    /**
     * Init view after fragment started
     */
    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator {
        return FragmentUtils.onCreateAnimation(requireContext(), enter) {
            presenter.initSuggestedAddresses()
            showKeyboard()
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
        toolbarLayout.ivBack.setThrottledClickListener {
            view?.hideKeyboard()
            Handler().postDelayed( {goToBack()}, 400)
        }
    }

    fun onSearchFieldEmpty(isToField: Boolean) {
        presenter.initSuggestedAddresses()
        searchForm.markFieldEmpty(isToField)
    }

    private fun initPredefinedPopularPlaces() = listOf(
        PopularPlace(getString(R.string.LNG_SELECT_POINT_MAP), R.drawable.btn_pin_location),
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

    override fun changeViewToHourlyDuration(durationValue: Int?) {
        searchForm.changeViewToHourlyDuration()
        setHourlyDuration(durationValue)
        rl_hourly.setOnClickListener { presenter.showHourlyDurationDialog() }
    }

    override fun showHourlyDurationDialog(durationValue: Int?) {
        HourlyDurationDialogFragment
            .newInstance(durationValue, object : HourlyDurationDialogFragment.OnHourlyDurationListener {
                override fun onDone(durationValue: Int) {
                    presenter.updateDuration(durationValue)
                }
            })
            .show(requireFragmentManager(), HourlyDurationDialogFragment.DIALOG_TAG)
    }

    override fun setHourlyDuration(duration: Int?) {
        duration?.let {
            tvCurrent_hours.text = HourlyValuesHelper.getValue(duration, requireContext())
        }
    }

    override fun setAddressListByAutoComplete(list: List<GTAddress>) {
        rv_addressList.adapter?.let {
            (it as AddressAdapter).updateList(list)
        }
    }

    override fun onFindPopularPlace(isToField: Boolean, place: String) = searchForm.findPopularPlace(isToField, place)

    override fun setSuggestedAddresses(addressesList: List<GTAddress>) {
        rv_popularList.adapter = PopularAddressAdapter(predefinedPopularPlaces) {
            if (it == predefinedPopularPlaces[0]) presenter.selectFinishPointOnMap()
            else presenter.onPopularSelected(it)
        }
        rv_addressList.adapter = AddressAdapter(addressesList) { presenter.onAddressSelected(it) }
    }

    override fun markFieldFilled(isToField: Boolean) = searchForm.markFiledFilled(isToField)

    override fun setFocus(isToField: Boolean) = searchForm.changeFocus(isToField)

    override fun onAddressError(message: Int, address: GTAddress, fieldTo: Boolean) {
        (rv_addressList.adapter as AddressAdapter).removeItem(address)
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun setError(finish: Boolean, @StringRes errId: Int, vararg args: String?) {
        Utils.showError(requireContext(), false, getString(errId))
    }

    override fun goToMap() {
        view?.hideKeyboard()
        Handler().postDelayed({
            if (SearchFragmentArgs.fromBundle(requireArguments()).isCameFromMap)
                goToBack()
            else
                findNavController().navigate(SearchFragmentDirections.goToMap())
        }, 400)
    }

    override fun goToBack() {
        findNavController().navigateUp()
    }

    override fun goToCreateOrder() {
        view?.hideKeyboard()
        Handler().postDelayed({
        if (!SearchFragmentArgs.fromBundle(requireArguments()).isCameFromMap)
            findNavController().navigate(SearchFragmentDirections.goToCreateOrder())
        else
            goToBack()
        }, 400)
    }
}
