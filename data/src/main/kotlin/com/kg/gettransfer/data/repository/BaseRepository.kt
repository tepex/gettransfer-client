package com.kg.gettransfer.data.repository

import org.slf4j.LoggerFactory

open class BaseRepository {
    companion object {
        @JvmField val TAG = "GTR-repository"
    }
    
    protected val log = LoggerFactory.getLogger(TAG)
}
