package com.kg.gettransfer.presentation.ui.newtransfer

import android.Manifest
import android.animation.Animator
import android.os.Bundle
import androidx.annotation.CallSuper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.extensions.visibleFade

import com.kg.gettransfer.presentation.delegate.DateTimeDelegate
import com.kg.gettransfer.presentation.presenter.NewTransferMainPresenter
import com.kg.gettransfer.presentation.ui.BaseFragment
import com.kg.gettransfer.presentation.ui.ReadMoreFragment
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.ui.dialogs.HourlyDurationDialogFragment
import com.kg.gettransfer.presentation.ui.helpers.HourlyValuesHelper
import com.kg.gettransfer.presentation.ui.utils.FragmentUtils
import com.kg.gettransfer.presentation.view.CreateOrderView
import com.kg.gettransfer.presentation.view.NewTransferMainView
import com.kg.gettransfer.utilities.NetworkLifeCycleObserver

import kotlinx.android.synthetic.main.fragment_new_transfer_main.*
import kotlinx.android.synthetic.main.search_form_main.*
import kotlinx.android.synthetic.main.view_switcher.*
//import leakcanary.AppWatcher

import org.koin.core.inject
import pub.devrel.easypermissions.EasyPermissions

@Suppress("TooManyFunctions")
class NewTransferMainFragment : BaseFragment(), NewTransferMainView {

    @InjectPresenter
    internal lateinit var presenter: NewTransferMainPresenter

    private val readMoreListener = View.OnClickListener { presenter.readMoreClick() }

    @ProvidePresenter
    fun createMainRequestPresenter() = NewTransferMainPresenter()

    private val dateDelegate: DateTimeDelegate by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Added network change listener
        lifecycle.addObserver(NetworkLifeCycleObserver(this, this))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        inflater.inflate(R.layout.fragment_new_transfer_main, container, false)

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClickListeners()

        presenter.checkBtnNextState()
    }

    override fun onResume() {
        super.onResume()
        presenter.updateView()
    }

    private fun initClickListeners() {
        // Switchers
        switcher_hourly.switch_mode_.setOnCheckedChangeListener { _, isChecked ->
            presenter.tripModeSwitched(isChecked)
        }

        // Address panel
        request_search_panel.setSearchFromClickListener {
            presenter.navigateToFindAddress()}
        request_search_panel.setSearchToClickListener {
            presenter.navigateToFindAddress(true) }
        request_search_panel.setHourlyClickListener { presenter.showHourlyDurationDialog() }
        request_search_panel.setIvSelectFieldFromClickListener {  switchToMap() }

        // Buttons
        btnNextFragment.setOnClickListener { presenter.onNextClick() }
        bestPriceLogo.setOnClickListener(readMoreListener)
        layoutBestPriceText.setOnClickListener(readMoreListener)
    }

    /**
     * Request update layout after fragment started
     */
    override fun onCreateAnimator(transit: Int, enter: Boolean, nextAnim: Int): Animator {
        return FragmentUtils.onCreateAnimation(requireContext(), enter) {

            request_search_panel.visibleFade(true)
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

    override fun defineAddressRetrieving(block: (withGps: Boolean) -> Unit) {
        block(EasyPermissions.hasPermissions(requireContext(), *PERMISSIONS))
    }

    override fun showReadMoreDialog() {
        ReadMoreFragment().show(requireFragmentManager(), getString(R.string.tag_read_more))
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

    override fun goToSearchAddress(isClickTo: Boolean, isCameFromMap: Boolean) {
        findNavController().navigate(NewTransferMainFragmentDirections.goToSearchAddress(isClickTo, isCameFromMap))
    }

    override fun goToCreateOrder() {
        findNavController().navigate(NewTransferMainFragmentDirections.goToCreateOrder())
    }

    override fun onDestroy() {
        super.onDestroy()
//        AppWatcher.objectWatcher.watch(this)
    }

    companion object {
        @JvmField val PERMISSIONS =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    }
}
