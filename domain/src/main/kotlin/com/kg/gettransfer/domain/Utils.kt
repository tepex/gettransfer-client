package com.kg.gettransfer.domain

import kotlinx.coroutines.experimental.*

/**
 * https://medium.com/@andrea.bresolin/playing-with-kotlin-in-android-coroutines-and-how-to-get-rid-of-the-callback-hell-a96e817c108b
 * Sequential code
 * 
 * val result1 = asyncAwait { ...do something asynchronous... }
 * val result2 = asyncAwait { ...do something asynchronous... }
 * processResults(result1, result2)
 *
 * Parallel code
 * val result1 = async { ...do something asynchronous... }
 * val result2 = async { ...do something asynchronous... }
 * processResults(result1.await(), result2.await())
 */
class Utils(private val cc: CoroutineContexts) {
	fun launchAsync(root: Job, block: suspend CoroutineScope.() -> Unit): Job {
		return launch(cc.ui, parent = root) { block() }
	}
	
	suspend fun <T> asyncAwait(bl: suspend CoroutineScope.() -> T): T {
		return async(context = cc.bg, block = bl).await()
	}
	
	suspend fun <T> async(block: suspend CoroutineScope.() -> T): Deferred<T> {
		return async(cc.bg) { block() }
	}
}
