package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.CacheException
import com.kg.gettransfer.data.RemoteException

import com.kg.gettransfer.data.model.ResultEntity

import com.kg.gettransfer.domain.model.Result

import org.koin.standalone.KoinComponent

import org.slf4j.LoggerFactory

abstract class BaseRepository : KoinComponent {
    protected val log = LoggerFactory.getLogger(TAG)

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

    /*
    protected suspend fun <E, M> retrieveRemoteModel(mapper: Mapper<E, M>, defaultModel: M, getEntity: suspend () -> E): Result<M> {
        val entity = try { getEntity() }
        catch (e: RemoteException) {
            log.error("error for $defaultModel", e)
            return Result(defaultModel, e.map())
        }
        return Result(mapper.fromEntity(entity))
    }

    protected suspend fun <E, M> retrieveRemoteListModel(mapper: Mapper<E, M>, getEntityList: suspend () -> List<E>): Result<List<M>> {
        val entityList = try { getEntityList() }
        catch (e: RemoteException) {
            log.error("remote list error", e)
            return Result(emptyList<M>(), e.map())
        }
        return Result(entityList.map { mapper.fromEntity(it) })
    }
    */

    companion object {
        const val TAG = "GTR-repository"
    }
}
