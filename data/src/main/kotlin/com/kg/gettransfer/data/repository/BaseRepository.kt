package com.kg.gettransfer.data.repository

import org.slf4j.LoggerFactory

open class BaseRepository() {
    companion object {
        @JvmField val TAG = "GTR-repository"
    }
    
    protected suspend fun <E> tryRetrieveEntity(getEntity: suspend () -> E?, error: (ApiException) -> E?): E? {
        return try { getEntity() }
        catch(ae: ApiException) { error(ae) }
    }
}
