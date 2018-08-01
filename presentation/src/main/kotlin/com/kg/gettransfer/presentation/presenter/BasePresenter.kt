package com.kg.gettransfer.presentation.presenter

import android.support.annotation.CallSuper

import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView

import ru.terrakok.cicerone.Router

import timber.log.Timber

open class BasePresenter<V : MvpView>(val router: Router): MvpPresenter<V>() {
	fun onBackCommandClick() {
		router.exit()
	}
}
