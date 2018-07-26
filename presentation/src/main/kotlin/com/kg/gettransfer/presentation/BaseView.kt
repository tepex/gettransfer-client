package com.kg.gettransfer


interface BaseView<out T : BasePresenter<*>> {
    val presenter: T
}
