package com.kg.gettransfer.presentation.presenter

import com.kg.gettransfer.core.domain.GTAddress

import com.kg.gettransfer.presentation.presenter.SearchPresenter.Companion.FIELD_FROM
import com.kg.gettransfer.presentation.presenter.SearchPresenter.Companion.FIELD_TO
import com.kg.gettransfer.presentation.presenter.SearchPresenter.Companion.EMPTY_ADDRESS
import com.kg.gettransfer.presentation.view.NewTransferMainView

import com.kg.gettransfer.sys.domain.SetSelectedFieldInteractor
import com.kg.gettransfer.utilities.Analytics
import com.kg.gettransfer.utilities.LocationManager

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import moxy.InjectViewState

import org.koin.core.inject

@InjectViewState
@Suppress("TooManyFunctions")
class NewTransferMainPresenter : BaseNewTransferPresenter<NewTransferMainView>() {

    private val setSelectedField: SetSelectedFieldInteractor by inject()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        geoInteractor.initGeocoder()
        geoInteractor.initGoogleApiClient()

        worker.main.launch {
            withContext(worker.bg) {
                setSelectedField(FIELD_FROM)
            }
        }
        initAddressListener()
        initEmptyAddressListener()
        // TODO Создать листенер для обновления текущей локации
        // https://developer.android.com/training/location/receive-location-updates
    }

    private fun initEmptyAddressListener() {
        locationManager.emptyAddressListener = object : LocationManager.OnGetEmptyAddressListener {
            override fun onGetEmptyAddress() {
                setAddressInSelectedField(null)
            }
        }
    }

    private fun initAddressListener() {
        locationManager.addressListener = object : LocationManager.OnGetAddressListener {
            override fun onGetAddress(currentAddress: GTAddress) {
                setAddressInSelectedField(currentAddress.cityPoint.name)
            }
        }
    }

    override fun updateView() {
        fillViewFromState(FIELD_FROM)
    }

    private fun fillViewFromState(selectField: String? = null) {
        worker.main.launch {
            if (selectField != null) {
                changeUsedField(selectField)
            } else {
                if (!orderInteractor.isAddressesValid()) {
                    changeUsedField(FIELD_FROM)
                } else {
                    changeUsedField(withContext(worker.bg) { getPreferences().getModel() }.selectedField)
                }
            }
        }

        if (fillAddressFieldsCheckIsEmpty()) {
            worker.main.launch {
                blockSelectedField(withContext(worker.bg) { getPreferences().getModel() }.selectedField)
            }
            locationManager.getCurrentLocation(true)
        }
        viewState.setHourlyDuration(orderInteractor.hourlyDuration)
        viewState.updateTripView(isHourly())
    }

    private fun changeUsedField(field: String) {
        worker.main.launch {
            withContext(worker.bg) { setSelectedField(field) }
        }
        when (field) {
            FIELD_FROM -> viewState.selectFieldFrom()
            FIELD_TO   -> viewState.setFieldTo()
        }
    }

    override fun fillAddressFieldsCheckIsEmpty(): Boolean {
        with(orderInteractor) {
            viewState.setAddressTo(to?.address ?: EMPTY_ADDRESS)
            return from.also { viewState.setAddressFrom(it?.address ?: EMPTY_ADDRESS) } == null
        }
    }

    private fun blockSelectedField(field: String) {
        when (field) {
            FIELD_FROM -> viewState.blockFromField()
            FIELD_TO   -> viewState.blockToField()
        }
    }

    private fun setAddressInSelectedField(address: String?) {
        worker.main.launch {
            with(address ?: EMPTY_ADDRESS) {
                when (withContext(worker.bg) { getPreferences().getModel() }.selectedField) {
                    FIELD_FROM -> viewState.setAddressFrom(this)
                    FIELD_TO   -> viewState.setAddressTo(this)
                }
            }
        }
    }

    fun tripModeSwitched(hourly: Boolean) {
        updateDuration(if (hourly) orderInteractor.hourlyDuration ?: MIN_HOURLY else null)
        viewState.updateTripView(hourly)
        worker.main.launch {
            if (withContext(worker.bg) { getPreferences().getModel() }.selectedField == FIELD_TO) {
                changeUsedField(FIELD_FROM)
            }
        }
    }

    fun updateDuration(hours: Int?) {
        orderInteractor.apply {
            hourlyDuration = hours
            viewState.setHourlyDuration(hourlyDuration)
        }
    }

    fun checkBtnNextState() {
        viewState.setBtnNextState(orderInteractor.isCanCreateOrder())
    }

    override fun onNextClick() {
        if (orderInteractor.isCanCreateOrder()) {
            viewState.goToCreateOrder()
        }
    }

    private fun isHourly() = orderInteractor.hourlyDuration != null

    fun showHourlyDurationDialog() {
        viewState.showHourlyDurationDialog(orderInteractor.hourlyDuration)
    }

    fun readMoreClick() {
        viewState.showReadMoreDialog()
        analytics.logEvent(Analytics.EVENT_MENU, Analytics.PARAM_KEY_NAME, Analytics.BEST_PRICE_CLICKED)
    }

    override fun destroyView(view: NewTransferMainView) {
        geoInteractor.disconnectGoogleApiClient()
        super.destroyView(view)
    }

    fun switchPointB(checked: Boolean) {
        orderInteractor.dropfOff = checked
        viewState.showPointB(checked)
    }

    fun clearToAddress() {
        orderInteractor.to = null
    }
}
