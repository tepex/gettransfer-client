package com.kg.gettransfer.core.presentation

import kotlinx.coroutines.*

import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject

class WorkerManager(val name: String) : KoinComponent {
    private val job = SupervisorJob()
    val main = MainScope() + CoroutineName(name)
    val backgroundScope = CoroutineScope(get<CoroutineDispatcher>() + job)
    val bg = backgroundScope.coroutineContext

    fun cancel() {
        // background.coroutineContext.cancelChildren()
        job.cancel()
    }
}
