package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.data.TransferDataStore
import com.kg.gettransfer.data.ds.*

import com.kg.gettransfer.data.mapper.ExceptionMapper

import com.kg.gettransfer.data.mapper.TransferMapper
import com.kg.gettransfer.data.mapper.TransferNewMapper
import com.kg.gettransfer.data.model.ResultEntity

import com.kg.gettransfer.data.model.TransferEntity
import com.kg.gettransfer.domain.model.*

import com.kg.gettransfer.domain.repository.TransferRepository

import java.util.Date

import org.koin.standalone.get

class TransferRepositoryImpl(
    private val factory: DataStoreFactory<TransferDataStore, TransferDataStoreCache, TransferDataStoreRemote>)
    : BaseRepository(), TransferRepository {

    private val preferencesCache = get<PreferencesCache>()
    private val transferNewMapper = get<TransferNewMapper>()
    private val transferMapper = get<TransferMapper>()

    /*override suspend fun createTransfer(transferNew: TransferNew) =
        retrieveRemoteModel<TransferEntity, Transfer>(transferMapper, DEFAULT) {
            factory.retrieveRemoteDataStore().createTransfer(transferNewMapper.toEntity(transferNew))
        }

    override suspend fun cancelTransfer(id: Long, reason: String): Result<Transfer> =
        retrieveRemoteModel<TransferEntity, Transfer>(transferMapper, DEFAULT) {
            factory.retrieveRemoteDataStore().cancelTransfer(id, reason)
        }

    override suspend fun getTransfer(id: Long): Result<Transfer> =
        retrieveRemoteModel<TransferEntity, Transfer>(transferMapper, DEFAULT) {
            factory.retrieveRemoteDataStore().getTransfer(id)
        }

    override suspend fun getAllTransfers(): Result<List<Transfer>> =
        retrieveRemoteListModel<TransferEntity, Transfer>(transferMapper) {
            factory.retrieveRemoteDataStore().getAllTransfers()
        }

    override suspend fun getTransfersArchive(): Result<List<Transfer>> =
        retrieveRemoteListModel<TransferEntity, Transfer>(transferMapper) {
            factory.retrieveRemoteDataStore().getTransfersArchive()
        }

    override suspend fun getTransfersActive(): Result<List<Transfer>> =
        retrieveRemoteListModel<TransferEntity, Transfer>(transferMapper) {
            factory.retrieveRemoteDataStore().getTransfersActive()
        }*/

    override suspend fun createTransfer(transferNew: TransferNew): Result<Transfer> {
        val result: ResultEntity<TransferEntity?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().createTransfer(transferNewMapper.toEntity(transferNew))
        }
        result.entity?.let { if(result.error == null) factory.retrieveCacheDataStore().addTransfer(it) }
        return Result(result.entity?.let { transferMapper.fromEntity(it) }?: DEFAULT, result.error?.let { ExceptionMapper.map(it) })
    }

    override suspend fun cancelTransfer(id: Long, reason: String): Result<Transfer> {
        val result: ResultEntity<TransferEntity?> = retrieveRemoteEntity {
            factory.retrieveRemoteDataStore().cancelTransfer(id, reason)
        }
        result.entity?.let { if(result.error == null) factory.retrieveCacheDataStore().addTransfer(it) }
        return Result(result.entity?.let { transferMapper.fromEntity(it) }?: DEFAULT, result.error?.let { ExceptionMapper.map(it) })
    }

    override suspend fun getTransfer(id: Long, role: String): Result<Transfer> {
        val result: ResultEntity<TransferEntity?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getTransfer(id, role)
        }
        result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().addTransfer(it) }
        return Result(result.entity?.let { transferMapper.fromEntity(it) }?: DEFAULT,
                result.error?.let { ExceptionMapper.map(it) }, result.error != null && result.entity != null)
    }

    override suspend fun getTransferCached(id: Long, role: String): Result<Transfer> {
        val result: ResultEntity<TransferEntity?> = retrieveCacheEntity {
            factory.retrieveCacheDataStore().getTransfer(id, role)
        }
        return Result(result.entity?.let { transferMapper.fromEntity(it) }?: DEFAULT, null,
                result.entity != null, result.cacheError?.let { ExceptionMapper.map(it) })
    }

    override suspend fun getAllTransfers(): Result<List<Transfer>> {
        val result: ResultEntity<List<TransferEntity>?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getAllTransfers()
        }
        result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().addAllTransfers(it) }
        return Result(result.entity?.let { mapTransfersList(it) }?: emptyList(),
                result.error?.let { ExceptionMapper.map(it) }, result.error != null && result.entity != null)
    }

    private fun mapTransfersList(transfersList: List<TransferEntity>): List<Transfer> {
        val mapCountNewMessages = preferencesCache.mapCountNewMessages.toMutableMap()
        val mapCountNewOffers = preferencesCache.mapCountNewOffers.toMutableMap()

        var eventsCount = 0
        val mappedTransfers = transfersList.map {
            transferMapper.fromEntity(it).apply {
                eventsCount += checkNewMessagesAndOffersCount(this, mapCountNewMessages, mapCountNewOffers)
            }
        }
        preferencesCache.eventsCount = eventsCount

        preferencesCache.mapCountNewMessages = mapCountNewMessages
        preferencesCache.mapCountNewOffers = mapCountNewOffers

        return mappedTransfers
    }

    private fun checkNewMessagesAndOffersCount(transfer: Transfer, mapCountNewMessages: MutableMap<Long, Int>, mapCountNewOffers: MutableMap<Long, Int>): Int {
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

    override suspend fun getTransfersArchive(): Result<List<Transfer>> {
        val result: ResultEntity<List<TransferEntity>?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getTransfersArchive()
        }
        result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().addAllTransfers(it) }
        return Result(result.entity?.map { transferMapper.fromEntity(it) }?: emptyList(),
                result.error?.let { ExceptionMapper.map(it) }, result.error != null && result.entity != null)
    }

    override suspend fun getTransfersActive(): Result<List<Transfer>> {
        val result: ResultEntity<List<TransferEntity>?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getTransfersActive()
        }
        result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().addAllTransfers(it) }
        return Result(result.entity?.map { transferMapper.fromEntity(it) }?: emptyList(),
                result.error?.let { ExceptionMapper.map(it) }, result.error != null && result.entity != null)
    }

    override fun clearTransfersCache() {
        factory.retrieveCacheDataStore().clearTransfersCache()
    }

    companion object {
        private val DEFAULT =
            Transfer(
                id              = 0,
                createdAt       = Date(),
                duration        = null,
                distance        = null,
                status          = Transfer.Status.NEW,
                from            = CityPoint(null, null, null),
                to              = null,
                dateToLocal     = Date(),
                dateReturnLocal = null,
                flightNumber    = null,
/* ================================================== */
                flightNumberReturn    = null,
                transportTypeIds      = emptyList<TransportType.ID>(),
                pax                   = 0,
                bookNow               = null,
                time                  = 0,
                nameSign              = null,
                comment               = null,
                childSeats            = 0,
                childSeatsInfant      = 0,
                childSeatsConvertible = 0,
/* ================================================== */
                childSeatsBooster     = 0,
                promoCode             = null,
                passengerOfferedPrice = null,
                price                 = null,
                paidSum               = null,
                remainsToPay          = null,
                paidPercentage        = 0,
                watertaxi             = false,
                bookNowOffers         = emptyMap<TransportType.ID, BookNowOffer>(),
                offersCount           = 0,
/* ================================================== */
                relevantCarriersCount = 0,

                dateRefund            = null,
                paypalOnly            = null,
                carrierMainPhone      = null,
                pendingPaymentId      = null,
                analyticsSent         = false,
                rubPrice              = null,
                refundedPrice         = null,
                campaign              = null,
/* ================================================== */
                editableFields      = emptyList<String>(),
                airlineCard         = null,
                paymentPercentages  = emptyList<Int>(),
                unreadMessagesCount = 0,
                showOfferInfo       = false
            )
    }
}
