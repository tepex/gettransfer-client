package com.kg.gettransfer.utilities

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * Custom LifecycleObserver that implements CoroutineScope and add this as our Activity/Fragment lifecycle observer.
 */
class CoroutineObserver: LifecycleObserver, CoroutineScope {
    private lateinit var job: Job
    
    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main
    
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        job = Job()
    }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        job.cancel()
    }
}