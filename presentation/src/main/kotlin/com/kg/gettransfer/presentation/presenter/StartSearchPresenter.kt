package com.kg.gettransfer.presentation.presenter

import android.location.Location

import android.support.annotation.CallSuper

import com.arellomobile.mvp.InjectViewState

import com.kg.gettransfer.presentation.view.StartSearchView

import ru.terrakok.cicerone.Router

import timber.log.Timber

@InjectViewState
class StartSearchPresenter(router: Router): BasePresenter<StartSearchView>(router) {
}
