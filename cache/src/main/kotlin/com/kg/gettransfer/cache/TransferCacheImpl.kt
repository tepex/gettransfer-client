package com.kg.gettransfer.cache

import com.kg.gettransfer.cache.mapper.TransferEntityMapper
import com.kg.gettransfer.data.TransferCache
import com.kg.gettransfer.data.model.TransferEntity
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class TransferCacheImpl: TransferCache, KoinComponent {
    private val db: CacheDatabase by inject()
    private val transferMapper: TransferEntityMapper by inject()

    override fun insertAllTransfers(transfers: List<TransferEntity>) = db.transferCacheDao().insertAll(transfers.map { transferMapper.toCached(it) })
    override fun insertTransfer(transfer: TransferEntity) = db.transferCacheDao().insert(transferMapper.toCached(transfer))

    override fun getTransfer(id: Long) = db.transferCacheDao().getTransfer(id)?.let { transferMapper.fromCached(it) }
    override fun getAllTransfers() = db.transferCacheDao().getAllTransfers().map { transferMapper.fromCached(it) }
    override fun getTransfersArchive() = db.transferCacheDao().getTransfersArchive().map { transferMapper.fromCached(it) }
    override fun getTransfersActive() = db.transferCacheDao().getTransfersActive().map { transferMapper.fromCached(it) }

    override fun deleteAllTransfers() = db.transferCacheDao().deleteAll()
}