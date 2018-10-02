package com.kg.gettransfer.extensions

import com.kg.gettransfer.presentation.presenter.BasePresenter
import com.kg.gettransfer.presentation.view.BaseView
import org.koin.core.KoinContext
import org.koin.standalone.StandAloneContext

inline fun <reified T : Any, BV : BaseView> BasePresenter<BV>.inject(name: String = "") =
lazy{(StandAloneContext.koinContext as KoinContext).get<T>(name)}