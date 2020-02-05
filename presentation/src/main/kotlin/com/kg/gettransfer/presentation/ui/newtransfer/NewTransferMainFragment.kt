package com.kg.gettransfer.presentation.ui.newtransfer

import android.animation.Animator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.annotation.CallSuper
import androidx.navigation.fragment.findNavController

import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.setThrottledClickListener

import com.kg.gettransfer.presentation.presenter.NewTransferMainPresenter
import com.kg.gettransfer.presentation.presenter.SearchPresenter
import com.kg.gettransfer.presentation.ui.BaseFragment
import com.kg.gettransfer.presentation.ui.ReadMoreFragment
import com.kg.gettransfer.presentation.ui.dialogs.HourlyDurationDialogFragment
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper
import com.kg.gettransfer.presentation.ui.utils.FragmentUtils
import com.kg.gettransfer.presentation.view.NewTransferMainView

import com.kg.gettransfer.utilities.NetworkLifeCycleObserver

import kotlinx.android.synthetic.main.content_new_transfer.*
import kotlinx.android.synthetic.main.fragment_new_transfer_main.*
import kotlinx.android.synthetic.main.search_form_main.*
import kotlinx.android.synthetic.main.view_switcher.*
// import leakcanary.AppWatcher

import pub.devrel.easypermissions.EasyPermissions

@Suppress("TooManyFunctions")
class NewTransferMainFragment : BaseFragment(), NewTransferMainView {

    @InjectPresenter
    internal lateinit var presenter: NewTransferMainPresenter

    private val readMoreListener = View.OnClickListener { presenter.readMoreClick() }

    @ProvidePresenter
    fun createMainRequestPresenter() = NewTransferMainPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(NetworkLifeCycleObserver(this, this))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_new_transfer_main, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scrollDownContent()
        initClickListeners()
        presenter.checkBtnNextState()
    }

    override fun onResume() {
        super.onResume()
        presenter.updateView()
        val hourly = switcher_hourly.switch_mode_.isChecked
        presenter.tripModeSwitched(hourly)
        if (hourly) {
            presenter.switchPointB(request_search_panel.switchPointB.isChecked)
        }
    }

    private fun initClickListeners() {
        // Switchers
        switcher_hourly.switch_mode_.setOnCheckedChangeListener { _, isChecked ->
            scrollDownContent()
            presenter.tripModeSwitched(isChecked)
        }
        request_search_panel.switchPointB.setOnCheckedChangeListener { _, isChecked ->
            scrollDownContent()
            presenter.switchPointB(isChecked)
        }

        // Address panel
        request_search_panel.setSearchFromClickListener { presenter.navigateToFindAddress() }
        request_search_panel.setSearchToClickListener   { presenter.navigateToFindAddress(true) }
        request_search_panel.setHourlyClickListener     { presenter.showHourlyDurationDialog() }
        request_search_panel.setIvSelectFieldFromClickListener { switchToMap() }

        // Buttons
        btnNextFragment.setThrottledClickListener(THROTTLED_DELAY) { presenter.onNextClick() }
        bestPriceLogo.setOnClickListener(readMoreListener)
        layoutBestPriceText.setOnClickListener(readMoreListener)
    }

    private fun scrollDownContent() {
        scrollContent.post { scrollContent.fullScroll(View.FOCUS_DOWN) }
    }

    /**
     * Request update layout after fragment started
     */
    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator {
        return FragmentUtils.onCreateAnimation(requireContext(), enter) {
            presenter.updateView()
        }
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

    /*private fun onNextClick() {
        if (dateDelegate.validateWith {
            Utils.getAlertDialogBuilder(requireActivity())
                .setTitle(getString(R.string.LNG_RIDE_CANT_CREATE))
                .setMessage(getString(CreateOrderView.FieldError.RETURN_TIME.stringId))
                .setPositiveButton(R.string.LNG_OK) { dialog, _ -> dialog.dismiss() }
                .show()
        }) {
            presenter.onNextClick()
        }
    }*/

    override fun setHourlyDuration(duration: Int?) {
        duration?.let { dur ->
            request_search_panel.setCurrentHoursText(HourlyValuesHelper.getValue(dur, requireContext()))
        }
    }

    override fun updateTripView(isHourly: Boolean) {
        request_search_panel.hourlyMode(isHourly)
        promoText.text = getString(
            when (isHourly) {
                true  -> R.string.LNG_MAIN_SCREEN_HOURLY_TRANSFER_TITLE
                false -> R.string.LNG_MAIN_SCREEN_POINT_TO_POINT_TRANSFER_TITLE
            }
        )
        presenter.checkBtnNextState()
    }

    override fun setBtnNextState(enable: Boolean) {
        btnNextFragment.isEnabled = enable
    }

    override fun onNetworkWarning(available: Boolean) {
        layoutTextNetworkNotAvailable.changeViewVisibility(!available)
    }

    override fun setAddressFrom(address: String) {
        request_search_panel.setSearchFrom(address)
        presenter.checkBtnNextState()
    }

    override fun setAddressTo(address: String) {
        request_search_panel.setSearchTo(address)
        presenter.checkBtnNextState()
    }

    override fun selectFieldFrom() {
        request_search_panel.selectSearchFrom()
    }

    override fun setFieldTo() {
        request_search_panel.selectSearchTo()
    }

    override fun showReadMoreDialog() {
        ReadMoreFragment().show(parentFragmentManager, getString(R.string.tag_read_more))
    }

    @CallSuper
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        presenter.updateCurrentLocation(true)
    }

    override fun switchToMap() {
        findNavController().navigate(NewTransferMainFragmentDirections.goToMap())
    }

    override fun showPointB(checked: Boolean) {
        request_search_panel.switchPointB(checked)
    }

    override fun goToSearchAddress(isClickTo: Boolean) {
        findNavController().navigate(NewTransferMainFragmentDirections.goToSearchAddress(isClickTo))
    }

    override fun goToCreateOrder() {
        clearToAddress()
        findNavController().navigate(NewTransferMainFragmentDirections.goToCreateOrder())
    }

    private fun clearToAddress() {
        val hourly = switcher_hourly.switch_mode_.isChecked
        val needPointB = request_search_panel.switchPointB.isChecked
        if (hourly && !needPointB) {
            request_search_panel.setSearchTo(SearchPresenter.EMPTY_ADDRESS)
            presenter.clearToAddress()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
//        AppWatcher.objectWatcher.watch(this)
    }

    companion object {
        private const val THROTTLED_DELAY = 1500L
    }
}
