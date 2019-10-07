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

import com.kg.gettransfer.domain.repository.TransferRepository
import com.kg.gettransfer.sys.domain.ConfigsRepository

import java.io.InputStream
import java.text.DateFormat
import java.util.Calendar

import org.koin.core.get
import org.koin.core.inject
import org.koin.core.qualifier.named

@Suppress("PreferToOverPairSyntax")
class TransferRepositoryImpl(
    private val factory: DataStoreFactory<TransferDataStore, TransferDataStoreCache, TransferDataStoreRemote>
) : BaseRepository(), TransferRepository {

    private val preferencesCache = get<PreferencesCache>()
    private val configsRepository: ConfigsRepository by inject()

    private val dateFormat = get<ThreadLocal<DateFormat>>(named("iso_date"))
    private val dateFormatTZ = get<ThreadLocal<DateFormat>>(named("iso_date_TZ"))
    private val serverDateFormat = get<ThreadLocal<DateFormat>>(named("server_date"))
    private val serverTimeFormat = get<ThreadLocal<DateFormat>>(named("server_time"))

    private var tempEventsCount: Int? = null

    override suspend fun createTransfer(transferNew: TransferNew): Result<Transfer> {
        val result: ResultEntity<TransferEntity?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().createTransfer(
                transferNew.map(serverDateFormat.get(), serverTimeFormat.get())
            )
        }
        result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().addTransfer(it) }
        return Result(
            result.entity?.map(
                configsRepository.getResult().getModel().transportTypes,
                dateFormat.get(),
                dateFormatTZ.get()
            ) ?: Transfer.EMPTY,
            result.error?.map()
        )
    }

    override suspend fun cancelTransfer(id: Long, reason: String): Result<Transfer> {
        val result: ResultEntity<TransferEntity?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().cancelTransfer(id, reason)
        }
        result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().addTransfer(it) }
        return Result(
            result.entity?.map(
                configsRepository.getResult().getModel().transportTypes,
                dateFormat.get(),
                dateFormatTZ.get()
            ) ?: Transfer.EMPTY,
            result.error?.map()
        )
    }

    override suspend fun setOffersUpdateDate(id: Long): Result<Unit> {
        val result: ResultEntity<TransferEntity?> = retrieveCacheEntity {
            factory.retrieveCacheDataStore().getTransfer(id)
        }
        result.entity?.let { entity ->
            if (entity.offersUpdatedAt != null) {
                entity.lastOffersUpdatedAt = dateFormatTZ.get().format(Calendar.getInstance().time)
                factory.retrieveCacheDataStore().addTransfer(entity)
            }
        }
        return Result(Unit)
    }

    override suspend fun getTransfer(id: Long): Result<Transfer> {
        val result: ResultEntity<TransferEntity?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getTransfer(id)
        }
        if (result.error == null) {
            result.entity?.apply {
                setLastOffersUpdate(this)
                factory.retrieveCacheDataStore().addTransfer(this)
            }
        }

        return Result(
            result.entity?.map(
                configsRepository.getResult().getModel().transportTypes,
                dateFormat.get(),
                dateFormatTZ.get()
            ) ?: Transfer.EMPTY,
            result.error?.map(),
            result.error != null && result.entity != null
        )
    }

    override suspend fun getTransferCached(id: Long): Result<Transfer> {
        val result: ResultEntity<TransferEntity?> = retrieveCacheEntity {
            factory.retrieveCacheDataStore().getTransfer(id)
        }
        return Result(
            result.entity?.map(
                configsRepository.getResult().getModel().transportTypes,
                dateFormat.get(),
                dateFormatTZ.get()
            ) ?: Transfer.EMPTY,
            null,
            result.entity != null,
            result.cacheError?.map()
        )
    }

    private suspend fun checkTransfersEvents(
        transfersList: List<TransferEntity>,
        isAllTransfersList: Boolean
    ): List<Transfer> {

        val mapCountNewMessages = preferencesCache.mapCountNewMessages.toMutableMap()
        val mapCountNewOffers = preferencesCache.mapCountNewOffers.toMutableMap()

        var eventsCount = 0
        val mappedTransfers = transfersList.map { entity ->
            mapTransfer(entity).apply {
                if (!entity.isBookNow()) {
                    eventsCount += checkNewMessagesAndOffersCount(this, mapCountNewMessages, mapCountNewOffers)
                }
            }
        }

        if (isAllTransfersList) {
            preferencesCache.eventsCount = eventsCount
        } else {
            if (tempEventsCount != null) {
                tempEventsCount?.let { preferencesCache.eventsCount = eventsCount + it }
                tempEventsCount = null
            } else {
                tempEventsCount = eventsCount
            }
        }

        preferencesCache.mapCountNewMessages = mapCountNewMessages
        preferencesCache.mapCountNewOffers = mapCountNewOffers

        return mappedTransfers
    }

    private suspend fun mapTransfer(transfer: TransferEntity) =
        transfer.map(
            configsRepository.getResult().getModel().transportTypes,
            dateFormat.get(),
            dateFormatTZ.get()
        )

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

    override suspend fun getAllTransfers(
        role: String,
        page: Int,
        status: String?
    ): Result<Pair<List<Transfer>, Int?>> {

        val result: ResultEntity<Pair<List<TransferEntity>, Int?>?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getAllTransfers(role, page, status)
        }
        if (result.error == null) {
            val list = result.entity?.first
            list?.let { transfers ->
                setLastOffersUpdate(transfers)
                factory.retrieveCacheDataStore().addAllTransfers(transfers)
            }
        }
        val resultList = result.entity?.let { checkTransfersEvents(it.first, true) } ?: emptyList()
        return Result(
            Pair(resultList, result.entity?.second),
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
            result.entity?.let { checkTransfersEvents(it, false) } ?: emptyList(),
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
            result.entity?.let { checkTransfersEvents(it, false) } ?: emptyList(),
            result.error?.map(),
            result.error != null && result.entity != null
        )
    }

    override fun clearTransfersCache() {
        factory.retrieveCacheDataStore().clearTransfersCache()
    }

    private suspend fun setLastOffersUpdate(remoteTransfers: List<TransferEntity>) {
        val result: ResultEntity<Pair<List<TransferEntity>, Int?>?> = retrieveCacheEntity {
            factory.retrieveCacheDataStore().getAllTransfers()
        }
        if (result.entity == null) return

        remoteTransfers.forEach { remoteTransfer ->
            result.entity.first.find { it.id == remoteTransfer.id }?.let { cachedTransfer ->
                if (remoteTransfer.offersUpdatedAt == null) return
                remoteTransfer.lastOffersUpdatedAt = cachedTransfer.lastOffersUpdatedAt
            }
        }
    }

    private suspend fun setLastOffersUpdate(remoteTransfer: TransferEntity) {
        val resultCached: ResultEntity<TransferEntity?> = retrieveCacheEntity {
            factory.retrieveCacheDataStore().getTransfer(remoteTransfer.id)
        }
        resultCached.entity?.let { remoteTransfer.lastOffersUpdatedAt = it.lastOffersUpdatedAt }
    }

    override suspend fun downloadVoucher(transferId: Long): Result<InputStream?> {
        val result: ResultEntity<InputStream?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().downloadVoucher(transferId)
        }
        return Result(result.entity)
    }

    override suspend fun sendAnalytics(transferId: Long, role: String): Result<Unit> {
        factory.retrieveRemoteDataStore().sendAnalytics(transferId, role)
        return Result(Unit)
    }
}
