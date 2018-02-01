package com.kg.gettransfer.login


import com.kg.gettransfer.BasePresenter
import com.kg.gettransfer.BaseView


/**
 * Created by denisvakulenko on 01/02/2018.
 */


interface LoginContract {

    interface View : BaseView<Presenter> {
        fun showError(message: String)
        fun updateLoadingIndicator(visible: Boolean)
    }

    interface Presenter : BasePresenter<View> {
        fun login(email: String, pass: String)
    }

}