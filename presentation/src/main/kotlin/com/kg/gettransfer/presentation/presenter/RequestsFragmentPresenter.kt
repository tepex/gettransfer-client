package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.presentation.view.RequestsFragmentView

@InjectViewState
class RequestsFragmentPresenter: MvpPresenter<RequestsFragmentView>(){

    lateinit var transfers: ArrayList<Transfer>
    lateinit var distanceUnit: String

    fun setData(transfers: ArrayList<Transfer>, distanceUnit: String){
        this.transfers = transfers
        this.distanceUnit = distanceUnit
        viewState.setRequests(transfers, distanceUnit)
    }
}