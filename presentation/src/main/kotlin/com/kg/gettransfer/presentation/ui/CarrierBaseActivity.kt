package com.kg.gettransfer.presentation.ui

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.support.annotation.CallSuper
import android.util.Log
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.kg.gettransfer.presentation.presenter.BasePresenter
import com.kg.gettransfer.presentation.view.CarrierBaseView
import com.kg.gettransfer.presentation.view.CarrierBaseView.Companion.SERVICE_ACTION
import com.kg.gettransfer.presentation.view.CarrierBaseView.Companion.PACKAGE

abstract class CarrierBaseActivity: BaseActivity(), CarrierBaseView {

    private val connectionService = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {}
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {}
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun openCoordinateService() {
        Intent(SERVICE_ACTION).also {
            it.setPackage(PACKAGE)
            startService(it)
            bindService(it, connectionService, Context.BIND_AUTO_CREATE)
        }
    }

    override fun stopCoordinateService() {
        Intent(SERVICE_ACTION).also {
            it.setPackage(PACKAGE)
            unbindService(connectionService)
            stopService(it)
        }
    }
}