package com.kg.gettransfer.domain

import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Deferred

/**
 * https://medium.com/@andrea.bresolin/playing-with-kotlin-in-android-coroutines-and-how-to-get-rid-of-the-callback-hell-a96e817c108b
 */
class Utils {
	companion object {
		/*
		suspend fun <T> async(block: suspend CoroutineScope.() -> T): Deferred<T> {
			return async(CommonPool) { block() }
		}
		
		suspend fun <T> asyncAwait(block: suspend CoroutineScope.() -> T): T {
			return async(block).await()
		}
		*/
		/*
		fun launchAsync(block: suspend CoroutineScope.() -> Unit): Job {
			return launch(UI) { block() }
		}
		*/
	}
}
