package com.kg.gettransfer


interface BasePresenter<T> {
    fun start()
    var view: T
    fun stop()
}