package com.kg.gettransfer.login


import com.kg.gettransfer.BasePresenter
import com.kg.gettransfer.BaseView


/**
 * Created by denisvakulenko on 01/02/2018.
 */


interface LoginContract {

    interface View : BaseView<Presenter> {
        fun loginSuccess()
        fun showError(message: String?)

        fun busyChanged(busy: Boolean)
    }

    interface Presenter : BasePresenter<View> {
        val busy: Boolean
        fun login(email: String, pass: String)
    }

}