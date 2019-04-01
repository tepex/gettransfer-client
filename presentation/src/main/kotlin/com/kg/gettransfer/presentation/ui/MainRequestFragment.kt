package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.isGone
import com.kg.gettransfer.extensions.isInvisible
import com.kg.gettransfer.extensions.isVisible
import com.kg.gettransfer.presentation.delegate.DateTimeDelegate
import com.kg.gettransfer.presentation.presenter.MainPresenter
import com.kg.gettransfer.presentation.ui.helpers.DateTimeScreen
import com.kg.gettransfer.presentation.view.MainRequestView
import kotlinx.android.synthetic.main.a_b_orange_view.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.create_order_field.view.*
import kotlinx.android.synthetic.main.fragment_main_request.*
import kotlinx.android.synthetic.main.search_form.view.*

import kotlinx.android.synthetic.main.view_switcher.view.*
import org.jetbrains.anko.longToast
import org.koin.standalone.KoinComponent
import org.koin.standalone.get

class MainRequestFragment :
        MvpAppCompatFragment(),
        KoinComponent,
        MainRequestView,
        DateTimeScreen
{
    private lateinit var mParent: MainActivity
    private lateinit var mPresenter: MainPresenter
    private val dateDelegate: DateTimeDelegate = get()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.fragment_main_request, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mParent = activity as MainActivity
        mPresenter = mParent.presenter
        mParent.requestView = this
        initClickListeners()
        initDateTimeFields()
    }

    private fun initClickListeners() {
        switcher_hourly.switch_mode_.setOnCheckedChangeListener { _, isChecked ->
            mParent.switch_mode.isChecked = isChecked
            tripModeSwitched(isChecked)
        }
        with(request_search_panel) {
            rl_hourly.setOnClickListener  { mParent.showNumberPicker(true) }
            searchFrom.setOnClickListener { mParent.performClick(false, true) }
            searchTo.setOnClickListener   { mParent.performClick(true, true) }
            searchFrom.setUneditable()
            searchTo.setUneditable()
        }

        order_time_view.setOnClickListener  { openPicker(FIELD_START) }
        return_time_view.setOnClickListener { dateReturnClickListenerDisabled }

        btnShowDrawerFragment.setOnClickListener { mParent.drawer.openDrawer(Gravity.START) }
        btnNextFragment.setOnClickListener       { mPresenter.onNextClick() }
        ivSetMyLocation.setOnClickListener       { mPresenter.updateCurrentLocation() }
    }

    private val dateReturnClickListenerEnabled  = { openPicker(FIELD_RETURN) }
    private val dateReturnClickListenerDisabled = { mParent.longToast("Choose start time") }

    private fun initDateTimeFields() =
        with(dateDelegate) {
            startOrderedTime?.let { order_time_view.hint_title.text = it }
            returnOrderedTime?.let { return_time_view.hint_title.text = it }
            enableBtnNext()
        }


    private fun openPicker(field: Boolean) =
            dateDelegate.chooseOrderTime(mParent, field, this)

    private fun tripModeSwitched(hourly: Boolean) {
        with(request_search_panel) {
            rl_hourly.isVisible    = hourly
            hourly_point.isVisible = hourly
            searchTo.isGone        = hourly
            tv_b_point.isGone      = hourly
            link_line.isInvisible  = hourly
        }
        return_time_view.isGone = hourly
        field_divider.isGone    = hourly
        enableBtnNext()
    }

    private fun enableBtnNext() {
        btnNextFragment.isEnabled = request_search_panel.searchFrom.text.isNotEmpty() &&
                (request_search_panel.searchTo.text.isNotEmpty() || switcher_hourly.switch_mode_.isChecked)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun setView(addressFrom: String?, addressTo: String?, duration: String?) {
        addressFrom?.let { editAddressField(request_search_panel.searchFrom, it) }
        addressTo?.let { editAddressField(request_search_panel.searchTo, it) }
        duration?.let {
            tripModeSwitched(true)
            switcher_hourly.switch_mode_.isChecked = true
            request_search_panel.tvCurrent_hours.text = duration
        }
        enableBtnNext()
    }

    private fun editAddressField(searchField: SearchAddress, address: String) =
            with(request_search_panel.icons_container) {
                searchField.text = address
                mParent.setPointsView(
                        if (searchField == searchFrom) tv_a_point else tv_b_point,
                        address.isNotEmpty())
            }


    override fun setNumberPickerValue(duration: String) {
        request_search_panel.tvCurrent_hours.text = duration
    }

    override fun setFieldDate(date: String, field: Boolean) {
        val dateField = if (field == FIELD_START) order_time_view else return_time_view
        dateField.hint_title.text = date
        if (field == FIELD_START && date.isNotEmpty())
            return_time_view.setOnClickListener { dateReturnClickListenerEnabled }
    }

    companion object {
        const val FIELD_START  = true
        const val FIELD_RETURN = false
    }
}