package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.TransferDataStore

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.TransferDataStoreCache
import com.kg.gettransfer.data.ds.TransferDataStoreRemote

import com.kg.gettransfer.data.mapper.TransferMapper
import com.kg.gettransfer.data.mapper.TransferNewMapper

import com.kg.gettransfer.data.model.TransferEntity

import com.kg.gettransfer.domain.model.CityPoint
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.TransferNew

import com.kg.gettransfer.domain.repository.TransferRepository

import java.util.Date

import org.koin.standalone.get

class TransferRepositoryImpl(private val factory: DataStoreFactory<TransferDataStore, TransferDataStoreCache, TransferDataStoreRemote>):
                                        BaseRepository(), TransferRepository {
    private val transferNewMapper = get<TransferNewMapper>()
    private val transferMapper    = get<TransferMapper>()

    override suspend fun createTransfer(transferNew: TransferNew) =
        retrieveRemoteModel<TransferEntity, Transfer>(transferMapper, defaultTransfer) {
            factory.retrieveRemoteDataStore().createTransfer(transferNewMapper.toEntity(transferNew))
        }

    override suspend fun cancelTransfer(id: Long, reason: String): Result<Transfer> =
        retrieveRemoteModel<TransferEntity, Transfer>(transferMapper, defaultTransfer) {
            factory.retrieveRemoteDataStore().cancelTransfer(id, reason)
        }

    override suspend fun getTransfer(id: Long): Result<Transfer> =
        retrieveRemoteModel<TransferEntity, Transfer>(transferMapper, defaultTransfer) {
            factory.retrieveRemoteDataStore().getTransfer(id)
        }

    override suspend fun getAllTransfers(): Result<List<Transfer>> =
        retrieveRemoteListModel<TransferEntity, Transfer>(transferMapper) { factory.retrieveRemoteDataStore().getAllTransfers() }

    override suspend fun getTransfersArchive(): Result<List<Transfer>> =
        retrieveRemoteListModel<TransferEntity, Transfer>(transferMapper) { factory.retrieveRemoteDataStore().getTransfersArchive() }

    override suspend fun getTransfersActive(): Result<List<Transfer>> =
        retrieveRemoteListModel<TransferEntity, Transfer>(transferMapper) { factory.retrieveRemoteDataStore().getTransfersActive() }

    companion object {
        private val defaultTransfer =
            Transfer(0,
                     Date(),
                     null,
                     null,
                     "",
                     CityPoint(null, null, null),
                     null,
                     Date(),
                     null,
                     null,

                     null,
                     null,
                     null,
                     null,
                     null,
                     0,
                     0,
                     0,
                     0,
                     null,

                     0,
                     null,
                     null,
                     0,
                     null,
                     false,
                     null,
                     emptyList<String>(),
                     null,
                     null,
                    listOf<Int>(),

                     emptyList<String>())
    }
}
