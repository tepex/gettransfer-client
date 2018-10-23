package com.kg.gettransfer.data.ds

import com.kg.gettransfer.data.RemoteException
import com.kg.gettransfer.data.TransferRemote
import com.kg.gettransfer.data.TransferDataStore

import com.kg.gettransfer.data.mapper.ExceptionMapper

import com.kg.gettransfer.data.model.TransferEntity
import com.kg.gettransfer.data.model.TransferNewEntity

/**
 * Implementation of the [TransferDataStore] interface to provide a means of communicating with the remote data source.
 */
open class TransferRemoteDataStore(private val remote: TransferRemote): TransferDataStore {
    override suspend fun createTransfer(transferNew: TransferNewEntity): TransferEntity {
        try { return remote.createTransfer(transferNew) }
        catch(e: RemoteException) { throw ExceptionMapper.map(e) }
    }
    
    override suspend fun cancelTransfer(id: Long, reason: String): TransferEntity {
        try { return remote.cancelTransfer(id, reason) }
        catch(e: RemoteException) { throw ExceptionMapper.map(e) }
    }
    
    override suspend fun getTransfer(id: Long): TransferEntity {
        try { return remote.getTransfer(id) }
        catch(e: RemoteException) { throw ExceptionMapper.map(e) }
    }
    
    override suspend fun getAllTransfers(): List<TransferEntity> {
        try { return remote.getAllTransfers() }
        catch(e: RemoteException) { throw ExceptionMapper.map(e) }
    }
        
    override suspend fun getTransfersArchive(): List<TransferEntity> {
        try { return remote.getTransfersArchive() }
        catch(e: RemoteException) { throw ExceptionMapper.map(e) }
    }
    
    override suspend fun getTransfersActive(): List<TransferEntity> {
        try { return remote.getTransfersActive() }
        catch(e: RemoteException) { throw ExceptionMapper.map(e) }
    }
}
