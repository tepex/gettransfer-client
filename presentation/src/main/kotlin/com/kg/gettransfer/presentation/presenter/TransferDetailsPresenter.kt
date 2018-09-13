package com.kg.gettransfer.presentation.presenter

import android.content.Context
import android.support.annotation.CallSuper
import com.arellomobile.mvp.InjectViewState
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.model.*
import com.kg.gettransfer.presentation.TransfersConstants
import com.kg.gettransfer.presentation.model.ConfigsModel
import com.kg.gettransfer.presentation.model.TransportTypeModel
import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.TransferDetailsView
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@InjectViewState
class TransferDetailsPresenter(cc: CoroutineContexts,
                               router: Router,
                               apiInteractor: ApiInteractor): BasePresenter<TransferDetailsView>(cc, router, apiInteractor) {

    lateinit var routeInfo: RouteInfo
    lateinit var offers: List<Offer>
    lateinit var transferDetails: Transfer

    companion object {
        private val DATE_TIME_FULL_FORMAT = SimpleDateFormat(Utils.DATE_TIME_FULL_PATTERN, Locale.US)
        private val DATE_TIME_FORMAT = SimpleDateFormat(Utils.DATE_TIME_PATTERN, Locale.US)
    }

    @CallSuper
    override fun attachView(view: TransferDetailsView) {
        super.attachView(view)
        utils.launchAsyncTryCatchFinally({
            transferDetails = apiInteractor.transferDetails

            utils.asyncAwait {
                configs = ConfigsModel(apiInteractor.getConfigs())
                account = apiInteractor.getAccount()
                routeInfo = apiInteractor.getRouteInfo(arrayOf(transferDetails.from.point,
                        transferDetails.to!!.point), true, false)
            }
            val distance = Utils.formatDistance(view as Context, R.string.distance, account.distanceUnit, transferDetails.distance)
            val selectedTransportTypes = arrayListOf<TransportTypeModel>()
            configs.transportTypes[0].delegate
            transferDetails.transportTypeIds.forEach {
                selectedTransportTypes.add(configs.transportTypes.find { type -> it == type.delegate.id}!!)
            }

            viewState.setTransferInfo(transferDetails.id, transferDetails.from.name, transferDetails.to!!.name,
                    changeDateFormat(transferDetails.dateToLocal), distance)
            viewState.setPassengerInfo(transferDetails.pax!!, transferDetails.nameSign, transferDetails.childSeats,
                    transferDetails.flightNumber, transferDetails.comment, selectedTransportTypes)
            viewState.setMapInfo(routeInfo, getPlaceNameFromAddress(transferDetails.from.name),
                    getPlaceNameFromAddress(transferDetails.to!!.name), changeDateFormat(transferDetails.dateToLocal), distance)

            val transferStatus = transferDetails.status
            if (transferStatus == TransfersConstants.STATUS_NEW || transferStatus == TransfersConstants.STATUS_DRAFT) viewState.activateButtonCancel()
            if (transferStatus == TransfersConstants.STATUS_PERFORMED || transferStatus == TransfersConstants.STATUS_PENDING ||
                    transferStatus == TransfersConstants.STATUS_COMPLETED || transferStatus == TransfersConstants.STATUS_NOT_COMPLETED){

                viewState.setPaymentInfo(transferDetails.price!!.default, transferDetails.paidSum!!.default,
                        transferDetails.paidPercentage!!, transferDetails.remainsToPay!!.default)
                utils.asyncAwait {
                    offers = apiInteractor.getOffers(transferDetails.id)
                }
                if(transferDetails.offersCount!! >= 1 && offers.size == 1){
                    val driver = offers[0].driver
                    val vehicle = offers[0].vehicle
                    viewState.setOfferInfo(driver!!.email, driver.phone, driver.fullName,
                            setFirstCharToUpperCase(vehicle.transportTypeId), vehicle.name,
                            vehicle.registrationNumber, offers[0].price.base.default)
                }
            }

        }, { e ->
            Timber.e(e)
            if(e is ApiException) viewState.setError(false, R.string.err_server_code, e.code.toString(), e.details)
            else viewState.setError(false, R.string.err_server, e.message)
        }, { viewState.blockInterface(false) })
    }

    fun changeDateFormat(dateTime: String): String {
        val newDate = TransferDetailsPresenter.DATE_TIME_FULL_FORMAT.parse(dateTime)
        return TransferDetailsPresenter.DATE_TIME_FORMAT.format(newDate)
    }

    fun getPlaceNameFromAddress(address: String): String{
        val index = address.indexOf(",")
        if(index > 0) return address.substring(0, index)
        else return address
    }

    fun setFirstCharToUpperCase(str: String): String{
        return str.substring(0, 1).toUpperCase() + str.substring(1)
    }
}