package com.kg.gettransfer


interface BasePresenter<T> {
    val view: T
    fun stop()
}