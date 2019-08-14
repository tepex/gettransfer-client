package com.kg.gettransfer.utilities

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.OnLifecycleEvent
import com.kg.gettransfer.presentation.view.BaseNetworkWarning
import com.kg.gettransfer.receiver.NetworkChangeCallback
import org.koin.core.KoinComponent
import org.koin.core.inject

class NetworkLifeCycleObserver(private val lifecycle: LifecycleOwner, private val networkWarning: BaseNetworkWarning) : LifecycleObserver, KoinComponent {

    private val networkChangeCallback: NetworkChangeCallback by inject()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onViewResume() = networkChangeCallback.observe(lifecycle, Observer { networkWarning.onNetworkWarning(it) })

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onViewPause() = networkChangeCallback.removeObservers(lifecycle)
}