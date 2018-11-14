package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.mapper.ExceptionMapper
import com.kg.gettransfer.data.mapper.Mapper

import com.kg.gettransfer.data.model.ResultEntity

import com.kg.gettransfer.domain.model.Result

import org.slf4j.LoggerFactory

open class BaseRepository() {
    companion object {
        @JvmField val TAG = "GTR-repository"
    }
    
    protected val log = LoggerFactory.getLogger(TAG)
    
    protected suspend fun <E> retrieveEntity(getEntity: suspend (Boolean) -> E?): ResultEntity<E?> {
        var error: RemoteException? = null
        /* First, try retrieve from remote */
        val entity = try {
            log.debug("    [TEST] Retrieving from remote")
            getEntity(true)
        }
        catch(e: RemoteException) {
            error = e
            log.debug("    [TEST] Error retrieving from remote. Retrieve from cache", e)
            /* If false, get from cache */
            getEntity(false)
        }
        return ResultEntity(entity, error)
    }
    
    protected suspend fun <E, M> retrieveRemoteModel(mapper: Mapper<E, M>, getEntity: suspend () -> E): Result<M> {
        val entity = try { getEntity() }
        catch(e: RemoteException) { return Result(error = ExceptionMapper.map(e)) }
        return Result(entity?.let { mapper.fromEntity(it) })
    }
    
    protected suspend fun <E, M> retrieveRemoteListModel(mapper: Mapper<E, M>, getEntityList: suspend () -> List<E>): Result<List<M>> {
        val entityList = try { getEntityList() }
        catch(e: RemoteException) { return Result(error = ExceptionMapper.map(e)) }
        return Result(entityList.map { mapper.fromEntity(it) })
    }    
}
