package com.kg.gettransfer.cache

import com.kg.gettransfer.cache.model.map
import com.kg.gettransfer.data.TransferCache
import com.kg.gettransfer.data.model.TransferEntity
import org.koin.core.KoinComponent
import org.koin.core.inject

@Suppress("PreferToOverPairSyntax")
class TransferCacheImpl : TransferCache, KoinComponent {

    private val db: CacheDatabase by inject()

    override fun insertAllTransfers(transfers: List<TransferEntity>) =
        db.transferCacheDao().insertAll(transfers.map { it.map() })

    override fun insertTransfer(transfer: TransferEntity) =
        db.transferCacheDao().insert(transfer.map())

    override fun getTransfer(id: Long) = db.transferCacheDao().getTransfer(id)?.map()

    override fun getAllTransfers() = Pair(db.transferCacheDao().getAllTransfers().map { it.map() }, 0)

    override fun getTransfersArchive() = db.transferCacheDao().getTransfersArchive().map { it.map() }

    override fun getTransfersActive() = db.transferCacheDao().getTransfersActive().map { it.map() }

    override fun deleteAllTransfers() = db.transferCacheDao().deleteAll()
}
