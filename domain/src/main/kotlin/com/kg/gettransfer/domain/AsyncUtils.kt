package com.kg.gettransfer.domain

import kotlinx.coroutines.*
import java.util.concurrent.TimeoutException

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
 *
 * root Job используем как предка для наших задач чтобы они не плодить безпризорщину.
 * В конце их всех удобно замочить одним ударом — прибив этого предка.
 */
class AsyncUtils(private val cc: CoroutineContexts, root: Job): CoroutineScope {
    companion object {
        suspend fun CoroutineScope.tryCatch(tryBlock: Task, catchBlock: TaskThrowable) {
            try { tryBlock() }
            catch(e: Throwable) { if(e !is CancellationException) catchBlock(e) else throw e }
        }

        suspend fun CoroutineScope.tryCatchFinally(tryBlock: Task,
                                                   catchBlock: TaskThrowable,
                                                   finallyBlock: Task) {
            var caughtThrowable: Throwable? = null

            try { tryBlock() }
            catch(e: TimeoutException) { catchBlock(e) }
            catch(e: InternetNotAvailableException) { catchBlock(e) }
            catch(e: ApiException) { catchBlock(e) }
            catch(e: CancellationException) { caughtThrowable = e }
            finally { if(caughtThrowable is CancellationException) throw caughtThrowable else finallyBlock() }
        }
    }
    
    override val coroutineContext = cc.ui + root
    
    @Synchronized
    fun launchAsyncTryCatch(tryBlock: Task, catchBlock: TaskThrowable) = launch { tryCatch(tryBlock, catchBlock) }
    
    @Synchronized
    fun launchAsyncTryCatchFinally(tryBlock: Task,
                                   catchBlock: TaskThrowable,
                                   finallyBlock: Task) = launch { tryCatchFinally(tryBlock, catchBlock, finallyBlock) }

    @Synchronized
    suspend fun <T> asyncAwait(bl: TaskGeneric<T>): T = coroutineScope { async(context = cc.bg, block = bl).await() }
    
    @Synchronized
    suspend fun <T> async(block: TaskGeneric<T>): Deferred<T> = coroutineScope { async(cc.bg) { block() } }
}

typealias Task = suspend CoroutineScope.() -> Unit
typealias TaskThrowable = suspend CoroutineScope.(Throwable) -> Unit
typealias TaskGeneric<T> = suspend CoroutineScope.() -> T
