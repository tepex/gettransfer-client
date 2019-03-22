package com.kg.gettransfer.presentation.ui

import android.os.Bundle
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.R
import com.kg.gettransfer.presentation.presenter.SupportPresenter
import com.kg.gettransfer.presentation.view.SupportView

class SupportActivity : BaseActivity(), SupportView {

    @InjectPresenter
    internal lateinit var presenter: SupportPresenter

    @ProvidePresenter
    fun createSettingsPresenter() = SupportPresenter()

    override fun getPresenter(): SupportPresenter = presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support)
    }
}