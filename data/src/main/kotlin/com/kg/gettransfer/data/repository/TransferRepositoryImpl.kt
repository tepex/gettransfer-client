package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.ds.TransferDataStoreFactory

import com.kg.gettransfer.data.mapper.TransferMapper
import com.kg.gettransfer.data.mapper.TransferNewMapper

import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.TransferNew

import com.kg.gettransfer.domain.repository.TransferRepository

class TransferRepositoryImpl(private val factory: TransferDataStoreFactory,
                             private val transferNewMapper: TransferNewMapper,
                             private val transferMapper: TransferMapper): TransferRepository {
    override suspend fun createTransfer(transferNew: TransferNew): Transfer {
        val transferEntity = factory.retrieveRemoteDataStore().createTransfer(transferNewMapper.toEntity(transferNew))
        factory.retrieveCacheDataStore().addTransfer(transferEntity)
        return transferMapper.fromEntity(transferEntity)
    }
    
    override suspend fun cancelTransfer(id: Long, reason: String): Transfer {
        val transferEntity = factory.retrieveRemoteDataStore().cancelTransfer(id, reason)
        return transferMapper.fromEntity(transferEntity)
    }
    
    override suspend fun getTransfer(id: Long): Transfer {
        val transferEntity = factory.retrieveRemoteDataStore().getTransfer(id)
        return transferMapper.fromEntity(transferEntity)
    }
    
    override suspend fun getAllTransfers(): List<Transfer> {
        val all = factory.retrieveRemoteDataStore().getAllTransfers()
        return all.map { transferMapper.fromEntity(it) }
    }
    
    override suspend fun getTransfersArchive(): List<Transfer> {
        val archive = factory.retrieveRemoteDataStore().getTransfersArchive()
        return archive.map { transferMapper.fromEntity(it) }
    }

    override suspend fun getTransfersActive(): List<Transfer> {
        val active = factory.retrieveRemoteDataStore().getTransfersActive()
        return active.map { transferMapper.fromEntity(it) }
    }
}
