package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.domain.eventListeners.CounterEventListener
import com.kg.gettransfer.domain.interactor.*

import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.presentation.delegate.AccountManager

import com.kg.gettransfer.presentation.view.NewTransferMainView

import com.kg.gettransfer.presentation.view.Screens

import com.kg.gettransfer.utilities.MainState
import org.koin.core.KoinComponent

import org.koin.core.inject

@InjectViewState
class NewTransferMainPresenter : BaseNewTransferPresenter<NewTransferMainView>(), KoinComponent, CounterEventListener {
    private val accountManager: AccountManager by inject()
    private val transferInteractor: TransferInteractor by inject()
    private val countEventsInteractor: CountEventsInteractor by inject()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        systemInteractor.lastMode = Screens.PASSENGER_MODE
        systemInteractor.selectedField = FIELD_FROM

        geoInteractor.initGeocoder()
        geoInteractor.initGoogleApiClient()
        if (accountManager.isLoggedIn) {
            utils.launchSuspend {
                utils.asyncAwait { transferInteractor.getAllTransfers() }
                viewState.setEventCount(accountManager.hasAccount, countEventsInteractor.eventsCount)
            }
        }

        // Создать листенер для обновления текущей локации
        // https://developer.android.com/training/location/receive-location-updates
    }

    override fun updateView(isVisible: Boolean) {
        if (!isVisible) return

        if (nState.currentState == MainState.CHOOSE_POINT_ON_MAP) {//returned from search activity
            viewState.switchToMap()
        } else {
            countEventsInteractor.addCounterListener(this)
            updateCounter()

            if (fillAddressFieldsCheckIsEmpty()) setOwnLocation()
            changeUsedField(systemInteractor.selectedField)
            viewState.setHourlyDuration(orderInteractor.hourlyDuration)
        }
    }

    override fun detachView(view: NewTransferMainView?) {
        super.detachView(view)
        countEventsInteractor.removeCounterListener(this)
    }

    override fun updateCounter() {
        utils.launchSuspend { viewState.setEventCount(accountManager.hasAccount, countEventsInteractor.eventsCount) }
    }

    override fun changeUsedField(field: String) {
        super.changeUsedField(field)
        systemInteractor.selectedField = field
        when (systemInteractor.selectedField) {
            FIELD_FROM -> viewState.selectFieldFrom()
            FIELD_TO -> viewState.setFieldTo()
        }
    }

    override fun setPointAddress(currentAddress: GTAddress) {
        setAddressInSelectedField(currentAddress.cityPoint.name)
    }

    /**
     * Fill address fields
     * @return true - from address is empty
     */
    private fun fillAddressFieldsCheckIsEmpty(): Boolean {
        with(orderInteractor) {
            viewState.setAddressTo(to?.address ?: EMPTY_ADDRESS)
            return from.also {
                viewState.setAddressFrom(it?.address ?: EMPTY_ADDRESS)
            } == null
        }
    }

    override fun destroyView(view: NewTransferMainView) {
        geoInteractor.disconnectGoogleApiClient()
        super.destroyView(view)
    }

    override fun onDestroy() {
        compositeDisposable.cancel()
        super.onDestroy()
    }

    companion object {
        const val FIELD_FROM = "field_from"
        const val FIELD_TO = "field_to"
        const val EMPTY_ADDRESS = ""
    }
}
