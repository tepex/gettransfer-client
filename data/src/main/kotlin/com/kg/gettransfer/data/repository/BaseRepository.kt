package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.model.ResultEntity

import org.slf4j.LoggerFactory

open class BaseRepository() {
    companion object {
        @JvmField val TAG = "GTR-repository"
    }
    
    protected val log = LoggerFactory.getLogger(TAG)
    
    protected suspend fun ResultEntity<E> retrieveEntity(getEntity: suspend (Boolean) -> E?): ResultEntity<E> {
        var error: RemoteException? = null
        /* First, try retrieve from remote */
        val entity = try { getEntity(true) }
        catch(e: RemoteException) {
            /* If false, get from cache */
            entity = getEntity(false)
            error = e
        }
        return ResultEntity(entity, error)
    }
}
