package com.kg.gettransfer.presentation.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kg.gettransfer.presentation.view.SettingsView

@InjectViewState
class SettingsPresenter: MvpPresenter<SettingsView>() {
    fun onBackCommandClick() {
        viewState.finish()
    }

    fun changeCurrency(textSelectedCurrency: String){
        viewState.setSettingsCurrency(textSelectedCurrency)
    }

    fun changeLanguage(textSelectedLanguage: String){
        viewState.setSettingsLanguage(textSelectedLanguage)
    }

    fun changeDistanceUnit(which: Int){
        val distanceUnits = arrayOf("km", "ml")
        viewState.setSettingsDistanceUnits(distanceUnits[which])
    }
}