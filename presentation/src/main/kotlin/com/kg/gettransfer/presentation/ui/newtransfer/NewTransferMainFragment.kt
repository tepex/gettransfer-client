package com.kg.gettransfer.presentation.ui.newtransfer

import android.Manifest
import android.content.Context
import android.os.Bundle
import androidx.annotation.CallSuper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R

import com.kg.gettransfer.common.NewTransferSwitchListener

import com.kg.gettransfer.presentation.delegate.DateTimeDelegate
import com.kg.gettransfer.presentation.presenter.NewTransferMainPresenter
import com.kg.gettransfer.presentation.ui.ReadMoreFragment
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.dialogs.HourlyDurationDialogFragment
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper
import com.kg.gettransfer.presentation.view.CreateOrderView
import com.kg.gettransfer.presentation.view.NewTransferMainView

import kotlinx.android.synthetic.main.fragment_new_transfer_main.*
import kotlinx.android.synthetic.main.search_form_main.*
import kotlinx.android.synthetic.main.view_switcher.*

import org.koin.core.KoinComponent
import org.koin.core.inject
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber


@Suppress("TooManyFunctions")
class NewTransferMainFragment : MvpAppCompatFragment(),
    KoinComponent, NewTransferMainView {

    @InjectPresenter
    internal lateinit var presenter: NewTransferMainPresenter

    var listener: NewTransferSwitchListener? = null

    private val readMoreListener = View.OnClickListener { presenter.readMoreClick() }

    @ProvidePresenter
    fun createMainRequestPresenter() = NewTransferMainPresenter()

    private val dateDelegate: DateTimeDelegate by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_new_transfer_main, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClickListeners()

        enableBtnNext()
    }

    override fun setUserVisibleHint(visible: Boolean) {
        super.setUserVisibleHint(visible)
        presenter.updateView(visible && isResumed)
    }

    override fun onAttach(activity: Context) {
        super.onAttach(activity)
        try {
            listener = parentFragment as NewTransferSwitchListener
        } catch (e: ClassCastException) {
            Timber.e("%s must implement NavigationMenuListener", activity.toString())
        }
    }

    private fun initClickListeners() {
        // Switchers
        switcher_hourly.switch_mode_.setOnCheckedChangeListener { _, isChecked ->
            presenter.tripModeSwitched(isChecked)
        }

        // Address panel
        request_search_panel.setSearchFromClickListener {
            presenter.navigateToFindAddress(searchFrom.text, searchTo.text)}
        request_search_panel.setSearchToClickListener {
            presenter.navigateToFindAddress(searchFrom.text, searchTo.text, true) }
        request_search_panel.setHourlyClickListener { presenter.showHourlyDurationDialog() }
        request_search_panel.setIvSelectFieldFromClickListener {  switchToMap() }

        // Buttons
        btnNextFragment.setOnClickListener { onNextClick() }
        bestPriceLogo.setOnClickListener(readMoreListener)
        layoutBestPriceText.setOnClickListener(readMoreListener)
    }

    override fun switchToMap() {
        listener?.switchToMap()
    }

    override fun blockFromField() {
        request_search_panel.searchFrom.text = getString(R.string.LNG_LOADING)
    }

    override fun blockToField() {
        request_search_panel.searchTo.text = getString(R.string.LNG_LOADING)
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

    private fun onNextClick() {
        if (dateDelegate.validateWith {
            Utils.getAlertDialogBuilder(requireActivity())
                .setTitle(getString(R.string.LNG_RIDE_CANT_CREATE))
                .setMessage(getString(CreateOrderView.FieldError.RETURN_TIME.stringId))
                .setPositiveButton(R.string.LNG_OK) { dialog, _ -> dialog.dismiss() }
                .show()
        }) {
            presenter.onNextClick { process ->
                btnNextFragment?.isEnabled = false
            }
        }
    }

    override fun setHourlyDuration(duration: Int?) {
        if (duration != null) {
            switcher_hourly.switch_mode_.isChecked = true
            request_search_panel.setCurrentHoursText( HourlyValuesHelper.getValue(duration, requireContext()))
        } else {
            switcher_hourly.switch_mode_.isChecked = false
        }
    }

    override fun updateTripView(isHourly: Boolean) {
        request_search_panel.hourlyMode(isHourly)
        promoText.text = getString(
            when (isHourly) {
                true -> R.string.LNG_MAIN_SCREEN_HOURLY_TRANSFER_TITLE
                false -> R.string.LNG_MAIN_SCREEN_POINT_TO_POINT_TRANSFER_TITLE
            }
        )
        enableBtnNext()
    }

    private fun enableBtnNext() {
        btnNextFragment.isEnabled =
                !request_search_panel.isEmptySearchFrom() &&
            (!request_search_panel.isEmptySearchTo() || switcher_hourly.switch_mode_.isChecked)
    }

    override fun onNetworkWarning(available: Boolean) {
        layoutTextNetworkNotAvailable.changeViewVisibility(!available)
    }

    override fun setAddressFrom(address: String) {
        request_search_panel.setSearchFrom(address)
        enableBtnNext()
    }

    override fun setAddressTo(address: String) {
        request_search_panel.setSearchTo(address)
        enableBtnNext()
    }

    override fun selectFieldFrom() {
        request_search_panel.selectSearchFrom()
    }

    override fun setFieldTo() {
        request_search_panel.selectSearchTo()
    }

    override fun defineAddressRetrieving(block: (withGps: Boolean) -> Unit) {
        block(EasyPermissions.hasPermissions(requireContext(), *PERMISSIONS))
    }

    override fun showReadMoreDialog() {
        ReadMoreFragment().show(fragmentManager, getString(R.string.tag_read_more))
    }

    @CallSuper
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        presenter.updateCurrentLocation()
    }

    companion object {
        @JvmField val PERMISSIONS =
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    }
}
