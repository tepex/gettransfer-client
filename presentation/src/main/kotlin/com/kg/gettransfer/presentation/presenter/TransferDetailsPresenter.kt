package com.kg.gettransfer.presentation.presenter

import android.content.Context

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.ApiException
import com.kg.gettransfer.domain.CoroutineContexts

import com.kg.gettransfer.domain.interactor.SystemInteractor
import com.kg.gettransfer.domain.interactor.TransferInteractor

import com.kg.gettransfer.presentation.model.Mappers
import com.kg.gettransfer.presentation.model.OfferModel
import com.kg.gettransfer.presentation.model.RouteModel
import com.kg.gettransfer.presentation.model.TransferModel

import com.kg.gettransfer.presentation.ui.Utils
import com.kg.gettransfer.presentation.view.TransferDetailsView

import ru.terrakok.cicerone.Router

import timber.log.Timber

import java.text.SimpleDateFormat
import java.util.Locale

@InjectViewState
class TransferDetailsPresenter(cc: CoroutineContexts,
                               router: Router,
                               systemInteractor: SystemInteractor,
                               private val transferInteractor: TransferInteractor): BasePresenter<TransferDetailsView>(cc, router, systemInteractor) {

    private var routeModel: RouteModel? = null
    private lateinit var transferModel: TransferModel
    private var offerModel: OfferModel? = null

    override fun onFirstViewAttach() {
        utils.launchAsyncTryCatchFinally({
            viewState.blockInterface(true)
	        if(transferInteractor.transfer.checkOffers) {
	            val offers = utils.asyncAwait { transferInteractor.getOffers() }
	            if(transferInteractor.transfer.offersCount!! >= 1 && offers.size >= 1)
	                offerModel = Mappers.getOfferModel(offers.first())
	        }
            
            val from = transferInteractor.transfer.from
            val to = transferInteractor.transfer.to!!
	        val routeInfo = utils.asyncAwait {
	            transferInteractor.getRouteInfo(from.point.toString(), to.point.toString(), true, false)
	        }
	        routeModel = Mappers.getRouteModel(routeInfo.distance,
                                               systemInteractor.getDistanceUnit(),
                                               routeInfo.polyLines,
                                               from.name,
                                               to.name)
	        viewState.setRoute(routeModel!!)
	    }, { e -> viewState.setError(false, R.string.err_server, e.message)
        }, { viewState.blockInterface(false) })        
	}
	
    @CallSuper
    override fun attachView(view: TransferDetailsView) {
        super.attachView(view)
        //val distance = Utils.formatDistance(view as Context, R.string.distance, transferInteractor.transfer.distance, distanceUnit)
        viewState.setTransfer(Mappers.getTransferModel(transferInteractor.transfer,
                                                       systemInteractor.getLocale(),
                                                       systemInteractor.getDistanceUnit(),
                                                       systemInteractor.getTransportTypes()))
        viewState.setRoute(routeModel!!)
        if(offerModel != null) viewState.setOffer(offerModel!!)
    }
        
            /*
            
            
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
            if(transferStatus == TransfersConstants.STATUS_NEW || transferStatus == TransfersConstants.STATUS_DRAFT) viewState.activateButtonCancel()
                
            if (transferStatus == TransfersConstants.STATUS_PERFORMED || transferStatus == TransfersConstants.STATUS_PENDING ||
                    transferStatus == TransfersConstants.STATUS_COMPLETED || transferStatus == TransfersConstants.STATUS_NOT_COMPLETED){

                viewState.setPaymentInfo(transferDetails.price!!.default, transferDetails.paidSum!!.default,
                        transferDetails.paidPercentage!!, transferDetails.remainsToPay!!.default)
                offers = utils.asyncAwait { apiInteractor.getOffers(transferDetails.id) }
                }
                if(transferDetails.offersCount!! >= 1 && offers.size == 1){
                    val driver = offers[0].driver
                    val vehicle = offers[0].vehicle
                    viewState.setOfferInfo(driver!!.email, driver.phone, driver.fullName,
                            setFirstCharToUpperCase(vehicle.transportTypeId), vehicle.name,
                            vehicle.registrationNumber, offers[0].price.base.default)
                }
            }
            */
    /*
    fun getPlaceNameFromAddress(address: String): String{
        val index = address.indexOf(",")
        if(index > 0) return address.substring(0, index)
        else return address
    }
    */
}
