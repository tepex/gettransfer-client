package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.CacheException
import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.model.ResultEntity
import org.koin.standalone.KoinComponent
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class BaseRepository : KoinComponent {

    protected val log: Logger = LoggerFactory.getLogger(TAG)

    protected suspend fun <E> retrieveEntity(getEntity: suspend (Boolean) -> E?): ResultEntity<E?> {
        /* First, try retrieve from remote */
        return try {
            ResultEntity(getEntity(true))
        } catch (e: RemoteException) { /* If error, get from cache */
            try {
                ResultEntity(getEntity(false), e)
            } catch (cacheE: CacheException) {
                ResultEntity(null, e, cacheE)
            }
        }
    }

    protected suspend fun <E> retrieveRemoteEntity(getEntity: suspend () -> E): ResultEntity<E?> {
        return try {
            ResultEntity(getEntity())
        } catch (e: RemoteException) {
            ResultEntity(null, e)
        }
    }

    protected suspend fun <E> retrieveCacheEntity(getEntity: suspend () -> E): ResultEntity<E?> {
        return try {
            ResultEntity(getEntity())
        } catch (e: CacheException) {
            ResultEntity(null, null, e)
        }
    }

    companion object {
        const val TAG = "GTR-repository"
    }
}
