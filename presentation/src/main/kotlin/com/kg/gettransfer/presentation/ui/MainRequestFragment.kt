package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import android.support.v4.content.ContextCompat
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
import com.kg.gettransfer.presentation.delegate.DateTimeDelegate.Companion.RETURN_DATE
import com.kg.gettransfer.presentation.delegate.DateTimeDelegate.Companion.START_DATE
import com.kg.gettransfer.presentation.presenter.MainPresenter
import com.kg.gettransfer.presentation.ui.helpers.DateTimeScreen
import com.kg.gettransfer.presentation.view.CreateOrderView
import com.kg.gettransfer.presentation.view.MainRequestView
import kotlinx.android.synthetic.main.a_b_orange_view.view.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.create_order_field.view.*
import kotlinx.android.synthetic.main.fragment_main_request.*
import kotlinx.android.synthetic.main.search_address.view.*
import kotlinx.android.synthetic.main.search_form.view.*
import kotlinx.android.synthetic.main.view_switcher.*

import org.koin.standalone.KoinComponent
import org.koin.standalone.get

//TODO add presenter
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
        initUi()
        initClickListeners()
        initDateTimeFields()
    }

    private fun initUi() {
        with(request_search_panel) {
            searchFrom.sub_title.text = mParent.getString(R.string.LNG_FIELD_SOURCE_PICKUP)
            searchTo.sub_title.text = mParent.getString(R.string.LNG_FIELD_DESTINATION)
            searchFrom.setPadding(0, 0, Utils.dpToPxInt(mParent, 26F), 0)
            searchTo.setPadding(0, 0, Utils.dpToPxInt(mParent, 26F), 0)
        }
    }

    private fun initClickListeners() {
        //Switchers
        switcher_hourly.switch_mode_.setOnCheckedChangeListener { _, isChecked ->
            mParent.switch_mode.isChecked = isChecked
            tripModeSwitched(isChecked)
        }
        switcher_map_.switch_mode_.setOnCheckedChangeListener { _, isChecked ->
            mParent.switcher_map.switch_mode_.performClick()
        }

        //Address panel
        with(request_search_panel) {
            rl_hourly.setOnClickListener  { mParent.showNumberPicker(true) }
            searchFrom.setOnClickListener { mParent.performClick(false, true) }
            searchTo.setOnClickListener   { mParent.performClick(true, true) }
            searchFrom.setUneditable()
            searchTo.setUneditable()
        }

        //Time
        order_time_view.setOnClickListener  { openPicker(START_DATE) }

        //Buttons
        btnShowDrawerFragment.setOnClickListener { mParent.drawer.openDrawer(Gravity.START) }
        btnNextFragment.setOnClickListener       { onNextClick() }
        ivSetMyLocation.setOnClickListener       {
            mParent.checkPermission()
            mPresenter.updateCurrentLocation()
        }
        fl_DeleteReturnDate.setOnClickListener   { clearReturnDate() }
    }

    private fun onNextClick() {
        if (dateDelegate.validateWith { dateErrorBlock() }) {
            mParent.performNextClick()
            mPresenter.onStartScreenOrderNote()
        }
    }

    private val dateErrorBlock = { Utils.getAlertDialogBuilder(mParent)
            .setTitle(getString(R.string.LNG_RIDE_CANT_CREATE))
            .setMessage(getString(CreateOrderView.FieldError.RETURN_TIME.stringId))
            .setPositiveButton(R.string.LNG_OK) { dialog, _ -> dialog.dismiss() }
            .show() }

    override fun onResume() {
        super.onResume()
        initDateTimeFields()
        with(dateDelegate) {
            if (startOrderedTime == null) order_time_view.hint_title.text = getText(R.string.LNG_RIDE_DATE)
            if (returnOrderedTime == null) return_time_view.hint_title.text = getText(R.string.LNG_RIDE_DATE_RETURN)
        }
    }

    private fun initDateTimeFields() =
        with(dateDelegate) {
            startOrderedTime?.let {
                order_time_view.hint_title.text = it
                return_time_view.setOnClickListener { openPicker(RETURN_DATE) }
            }
            returnOrderedTime?.let {
                return_time_view.hint_title.text = it
                setReturnTimeIcon(true)
            }
            setReturnTimeIcon(returnOrderedTime != null)
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
        rl_returnTime.isGone    = hourly
        field_divider.isGone    = hourly
        enableBtnNext()
    }

    private fun enableBtnNext() {
        btnNextFragment.isEnabled = request_search_panel.searchFrom.text.isNotEmpty() &&
                (request_search_panel.searchTo.text.isNotEmpty() || switcher_hourly.switch_mode_.isChecked)
    }

    private fun setReturnTimeIcon(hasDate: Boolean = true) {
        val image = if (hasDate) R.drawable.ic_calendar_return else R.drawable.ic_return_time
        return_time_view.img_icon.setImageDrawable(ContextCompat.getDrawable(mParent, image))
        fl_DeleteReturnDate.isVisible = hasDate
        return_time_view.img_arrow.isVisible = !hasDate
    }

    private fun clearReturnDate() {
        dateDelegate.returnDate = null
        return_time_view.hint_title.text = getText(R.string.LNG_RIDE_DATE)
        setReturnTimeIcon(false)
    }

    override fun setView(addressFrom: String?, addressTo: String?, duration: String?, networkAvailable: Boolean) {
        addressFrom?.let { editAddressField(request_search_panel.searchFrom, it) }
        addressTo?.let { editAddressField(request_search_panel.searchTo, it) }
        duration?.let {
            tripModeSwitched(true)
            switcher_hourly.switch_mode_.isChecked = true
            request_search_panel.tvCurrent_hours.text = duration
        }
        tv_internet_warning.isVisible = !networkAvailable
        enableBtnNext()
    }

    override fun setBadge(count: String) {
        tvEventsCountFragment.text = count
    }

    override fun showBadge(show: Boolean) {
        tvEventsCountFragment.isVisible = show
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
        val dateField = if (field == START_DATE) order_time_view else return_time_view
        dateField.hint_title.text = date
        if (field == RETURN_DATE) setReturnTimeIcon(date.isNotEmpty())
        else return_time_view.setOnClickListener { openPicker(RETURN_DATE) }
        enableBtnNext()
    }

    override fun onNetworkWarning(disconnected: Boolean) {
        tv_internet_warning.isVisible = disconnected
    }

    override fun blockSelectedField(field: String) {
        with(request_search_panel) {
            when (field) {
                MainPresenter.FIELD_FROM -> searchFrom.text = getString(R.string.LNG_LOADING)
                MainPresenter.FIELD_TO -> searchTo.text = getString(R.string.LNG_LOADING)
            }
        }
    }

    override fun setVisibilityBtnMyLocation(isVisible: Boolean) {
        ivSetMyLocation.setImageDrawable(ContextCompat.getDrawable(mParent, when(isVisible) {
            true -> R.drawable.ic_pin_orange_border
            false -> R.drawable.ic_pin_gray_border
        }))
    }
}