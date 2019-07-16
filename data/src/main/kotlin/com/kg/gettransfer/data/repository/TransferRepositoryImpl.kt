@file:Suppress("TooManyFunctions")
package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.TransferDataStore
import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.TransferDataStoreCache
import com.kg.gettransfer.data.ds.TransferDataStoreRemote
import com.kg.gettransfer.data.model.ResultEntity
import com.kg.gettransfer.data.model.TransferEntity
import com.kg.gettransfer.data.model.map

import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.TransferNew
import com.kg.gettransfer.domain.repository.SystemRepository
import com.kg.gettransfer.domain.repository.TransferRepository

import java.io.InputStream
import java.text.DateFormat
import java.util.Calendar

import org.koin.core.get
import org.koin.core.qualifier.named

class TransferRepositoryImpl(
    private val factory: DataStoreFactory<TransferDataStore, TransferDataStoreCache, TransferDataStoreRemote>
) : BaseRepository(), TransferRepository {

    private val preferencesCache = get<PreferencesCache>()
    private val transportTypes = get<SystemRepository>().configs.transportTypes

    private val dateFormat = get<ThreadLocal<DateFormat>>(named("iso_date"))
    private val dateFormatTZ = get<ThreadLocal<DateFormat>>(named("iso_date_TZ"))
    private val serverDateFormat = get<ThreadLocal<DateFormat>>(named("server_date"))
    private val serverTimeFormat = get<ThreadLocal<DateFormat>>(named("server_time"))

    override suspend fun createTransfer(transferNew: TransferNew): Result<Transfer> {
        val result: ResultEntity<TransferEntity?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().createTransfer(
                transferNew.map(serverDateFormat.get(), serverTimeFormat.get())
            )
        }
        result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().addTransfer(it) }
        return Result(
            result.entity?.map(transportTypes, dateFormat.get(), dateFormatTZ.get()) ?: Transfer.EMPTY,
            result.error?.map()
        )
    }

    override suspend fun cancelTransfer(id: Long, reason: String): Result<Transfer> {
        val result: ResultEntity<TransferEntity?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().cancelTransfer(id, reason)
        }
        result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().addTransfer(it) }
        return Result(
            result.entity?.map(transportTypes, dateFormat.get(), dateFormatTZ.get()) ?: Transfer.EMPTY,
            result.error?.map()
        )
    }

    override suspend fun setOffersUpdateDate(id: Long): Result<Unit> {
        val result: ResultEntity<TransferEntity?> = retrieveCacheEntity {
            factory.retrieveCacheDataStore().getTransfer(id, "")
        }
        result.entity?.let { entity ->
            if (entity.offersUpdatedAt != null) {
                entity.lastOffersUpdatedAt = dateFormatTZ.get().format(Calendar.getInstance().time)
                factory.retrieveCacheDataStore().addTransfer(entity)
            }
        }
        return Result(Unit)
    }

    override suspend fun getTransfer(id: Long, role: String): Result<Transfer> {
        val result: ResultEntity<TransferEntity?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getTransfer(id, role)
        }
        if (result.error == null) {
            result.entity?.apply {
                setLastOffersUpdate(this, role)
                factory.retrieveCacheDataStore().addTransfer(this)
            }
        }

        return Result(
            result.entity?.map(transportTypes, dateFormat.get(), dateFormatTZ.get()) ?: Transfer.EMPTY,
            result.error?.map(),
            result.error != null && result.entity != null
        )
    }

    override suspend fun getTransferCached(id: Long, role: String): Result<Transfer> {
        val result: ResultEntity<TransferEntity?> = retrieveCacheEntity {
            factory.retrieveCacheDataStore().getTransfer(id, role)
        }
        return Result(
            result.entity?.map(transportTypes, dateFormat.get(), dateFormatTZ.get()) ?: Transfer.EMPTY,
            null,
            result.entity != null,
            result.cacheError?.map()
        )
    }

    private fun mapTransfersList(transfersList: List<TransferEntity>): List<Transfer> {
        val mapCountNewMessages = preferencesCache.mapCountNewMessages.toMutableMap()
        val mapCountNewOffers = preferencesCache.mapCountNewOffers.toMutableMap()

        var eventsCount = 0
        val mappedTransfers = transfersList.map { entity ->
            entity.map(transportTypes, dateFormat.get(), dateFormatTZ.get()).apply {
                if (!entity.isBookNow()) {
                    eventsCount += checkNewMessagesAndOffersCount(this, mapCountNewMessages, mapCountNewOffers)
                }
            }
        }
        preferencesCache.eventsCount = eventsCount

        preferencesCache.mapCountNewMessages = mapCountNewMessages
        preferencesCache.mapCountNewOffers = mapCountNewOffers

        return mappedTransfers
    }

    private fun checkNewMessagesAndOffersCount(
        transfer: Transfer,
        mapCountNewMessages: MutableMap<Long, Int>,
        mapCountNewOffers: MutableMap<Long, Int>
    ): Int {
        var eventsCount = 0
        if (transfer.showOfferInfo) {
            if (transfer.unreadMessagesCount > 0) {
                mapCountNewMessages[transfer.id] = transfer.unreadMessagesCount
                eventsCount += transfer.unreadMessagesCount
            } else if (mapCountNewMessages[transfer.id] != null) {
                mapCountNewMessages.remove(transfer.id)
            }
        }

        if (transfer.status == Transfer.Status.NEW && transfer.offersCount > 0) {
            val newOffers = transfer.offersCount
            val viewedOffers = preferencesCache.mapCountViewedOffers[transfer.id]
            mapCountNewOffers[transfer.id] = newOffers
            eventsCount += newOffers - (viewedOffers ?: 0)
        } else if (mapCountNewOffers[transfer.id] != null) {
            mapCountNewOffers.remove(transfer.id)
            val mapCountViewedOffer = preferencesCache.mapCountViewedOffers.toMutableMap()
            if (mapCountViewedOffer[transfer.id] != null) {
                mapCountViewedOffer.remove(transfer.id)
                preferencesCache.mapCountViewedOffers = mapCountViewedOffer
            }
        }
        return eventsCount
    }

    override suspend fun getAllTransfers(): Result<List<Transfer>> {
        val result: ResultEntity<List<TransferEntity>?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getAllTransfers()
        }
        if (result.error == null) {
            result.entity?.apply {
                setLastOffersUpdate(this)
                factory.retrieveCacheDataStore().addAllTransfers(this)
            }
        }
        return Result(
            result.entity?.let { mapTransfersList(it) } ?: emptyList(),
            result.error?.map(),
            result.error != null && result.entity != null
        )
    }

    override suspend fun getTransfersArchive(): Result<List<Transfer>> {
        val result: ResultEntity<List<TransferEntity>?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getTransfersArchive()
        }
        if (result.error == null) {
            result.entity?.apply {
                setLastOffersUpdate(this)
                factory.retrieveCacheDataStore().addAllTransfers(this)
            }
        }
        return Result(
            result.entity?.let { mapTransfersList(it) } ?: emptyList(),
            result.error?.map(),
            result.error != null && result.entity != null
        )
    }

    override suspend fun getTransfersActive(): Result<List<Transfer>> {
        val result: ResultEntity<List<TransferEntity>?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getTransfersActive()
        }
        if (result.error == null) {
            result.entity?.apply {
                setLastOffersUpdate(this)
                factory.retrieveCacheDataStore().addAllTransfers(this)
            }
        }
        return Result(
            result.entity?.let { mapTransfersList(it) } ?: emptyList(),
            result.error?.map(),
            result.error != null && result.entity != null
        )
    }

    override fun clearTransfersCache() {
        factory.retrieveCacheDataStore().clearTransfersCache()
    }

    private suspend fun setLastOffersUpdate(remoteTransfers: List<TransferEntity>) {
        val result: ResultEntity<List<TransferEntity>?> = retrieveCacheEntity {
            factory.retrieveCacheDataStore().getAllTransfers()
        }
        if (result.entity == null) return

        remoteTransfers.forEach { remoteTransfer ->
            result.entity.find { it.id == remoteTransfer.id }?.let { cachedTransfer ->
                if (remoteTransfer.offersUpdatedAt == null) return
                remoteTransfer.lastOffersUpdatedAt = cachedTransfer.lastOffersUpdatedAt
            }
        }
    }

    private suspend fun setLastOffersUpdate(remoteTransfer: TransferEntity, role: String) {
        val resultCached: ResultEntity<TransferEntity?> = retrieveCacheEntity {
            factory.retrieveCacheDataStore().getTransfer(remoteTransfer.id, role)
        }
        resultCached.entity?.let { remoteTransfer.lastOffersUpdatedAt = it.lastOffersUpdatedAt }
    }

    override suspend fun downloadVoucher(transferId: Long): Result<InputStream?> {
        val result: ResultEntity<InputStream?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().downloadVoucher(transferId)
        }
        return Result(result.entity)
    }
}
