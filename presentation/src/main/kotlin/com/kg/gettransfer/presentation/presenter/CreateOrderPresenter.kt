package com.kg.gettransfer.presentation.presenter

import android.widget.TextView

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import com.kg.gettransfer.R

import com.kg.gettransfer.domain.CoroutineContexts
import com.kg.gettransfer.domain.model.GTAddress
import com.kg.gettransfer.domain.interactor.AddressInteractor

import com.kg.gettransfer.presentation.Screens
import com.kg.gettransfer.presentation.view.CreateOrderView

import ru.terrakok.cicerone.Router

@InjectViewState
class CreateOrderPresenter(private val cc: CoroutineContexts,
                           private val router: Router,
                           private val addressInteractor: AddressInteractor): MvpPresenter<CreateOrderView>() {

	override fun onFirstViewAttach() {
		viewState.setRoute(addressInteractor.route)
	}
	
    fun changeCounter(counterTextView: TextView, num: Int){
        var counter = counterTextView.text.toString().toInt()
        var minCounter = 0
        if (counterTextView.id == R.id.tvCountPerson) minCounter = 1
        if (counter + num >= minCounter) counter += num
        viewState.setCounters(counterTextView, counter)
    }

    fun changeCurrency(which: Int){
        val currencySimbols = arrayOf("$", "€", "£", "\u20BD", "฿", "¥")
        viewState.setCurrency(currencySimbols[which])
    }

    fun changeDateTimeTransfer(year: Int, month: Int, day: Int, hour: Int, minute: Int){
        val months = arrayOf("January", "February", "March", "April", "May", "June", "Jule", "August",
                "September", "October", "November", "December")
        val dateTimeString = StringBuilder()
        dateTimeString.append(day).append(" ").append(months[month]).append(" ").append(year)
                .append(", ").append(hour).append(":").append(minute)
        viewState.setDateTimeTransfer(dateTimeString.toString())
    }

    fun setComment(comment: String) { viewState.setComment(comment) }
    fun showLicenceAgreement() { router.navigateTo(Screens.LICENCE_AGREE) }
    fun onBackCommandClick() { viewState.finish() }
}
