package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.AsyncUtils
import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.interactor.ApiInteractor
import com.kg.gettransfer.domain.model.*
import com.kg.gettransfer.presentation.view.TransferDetailsView
import kotlinx.coroutines.experimental.Job
import java.text.SimpleDateFormat

@InjectViewState
class TransferDetailsPresenter(private val cc: CoroutineContexts,
                               private val apiInteractor: ApiInteractor): MvpPresenter<TransferDetailsView>() {

    private val compositeDisposable = Job()
    private val utils = AsyncUtils(cc)

    lateinit var account: Account
    lateinit var routeInfo: RouteInfo
    lateinit var configs: Configs

    lateinit var transferDetails: Transfer

    companion object {
        @JvmField val DATE_TIME_PATTERN_SERVER = "yyyy-MM-dd'T'HH:mm:ss"
        @JvmField val DATE_TIME_PATTERN_APP = "dd MMMM yyyy, HH:mm"
    }

    override fun onFirstViewAttach() {
        utils.launchAsyncTryCatchFinally(compositeDisposable, {
            transferDetails = apiInteractor.transferDetails


            utils.asyncAwait {
                configs = apiInteractor.getConfigs()
                account = apiInteractor.getAccount()
            }

            viewState.setTransferInfo(transferDetails.id, transferDetails.from.name, transferDetails.to!!.name,
                    changeDateFormat(transferDetails.dateToLocal), transferDetails.distance!!, account.distanceUnit!!)

            val selectedTransportTypes = arrayListOf<TransportType>()
            transferDetails.transportTypeIds.forEach {
                selectedTransportTypes.add(configs.transportTypes.find { type -> it == type.id}!!)
            }
            viewState.setPassengerInfo(transferDetails.pax!!, transferDetails.nameSign, transferDetails.childSeats,
                    transferDetails.flightNumber, transferDetails.comment, selectedTransportTypes)

            /*when (transferDetails.status){
                "new" -> viewState.activateButtonCancel()
                "performed" -> {
                    viewState.activateButtonCancel()
                    viewState.setPaymentInfo()
                    viewState.setOfferInfo()
                }

            }*/

            val transferStatus = transferDetails.status
            if (transferStatus == "new") viewState.activateButtonCancel()
            if (transferStatus == "performed" || transferStatus == "pending_confirmation" ||
                    transferStatus == "completed" || transferStatus == "not_completed"){
                viewState.setPaymentInfo()
                viewState.setOfferInfo()
            }

            utils.asyncAwait {
                routeInfo = apiInteractor.getRouteInfo(arrayOf(transferDetails.from.point,
                        transferDetails.to!!.point), true, false)
            }

            val fromName = if(transferDetails.from.name.indexOf(",") >= 0) transferDetails.from.name.substring(0, transferDetails.from.name.indexOf(","))
                           else transferDetails.from.name

            val toName = if(transferDetails.to!!.name.indexOf(",") >= 0) transferDetails.to!!.name.substring(0, transferDetails.to!!.name.indexOf(","))
                         else transferDetails.to!!.name

            viewState.setMapInfo(routeInfo, fromName, toName, changeDateFormat(transferDetails.dateToLocal), account.distanceUnit!!)

        }, { e ->
            if(e is ApiException) viewState.setError(false, R.string.err_server_code, e.code.toString(), e.details)
            else viewState.setError(false, R.string.err_server, e.message)
        }, { viewState.blockInterface(false) })
    }

    fun changeDateFormat(dateTime: String): String{
        val formatDate = SimpleDateFormat(DATE_TIME_PATTERN_SERVER)
        val newDate = formatDate.parse(dateTime)

        val dateTimeFormat = SimpleDateFormat(DATE_TIME_PATTERN_APP)
        return dateTimeFormat.format(newDate)
    }

    fun onBackCommandClick() {
        viewState.finish()
    }
}