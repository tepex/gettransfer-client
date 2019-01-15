package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.TransferDataStore

import com.kg.gettransfer.data.ds.DataStoreFactory
import com.kg.gettransfer.data.ds.TransferDataStoreCache
import com.kg.gettransfer.data.ds.TransferDataStoreRemote
import com.kg.gettransfer.data.mapper.ExceptionMapper

import com.kg.gettransfer.data.mapper.TransferMapper
import com.kg.gettransfer.data.mapper.TransferNewMapper
import com.kg.gettransfer.data.model.ResultEntity

import com.kg.gettransfer.data.model.TransferEntity

import com.kg.gettransfer.domain.model.BookNowOffer
import com.kg.gettransfer.domain.model.CityPoint
import com.kg.gettransfer.domain.model.Result
import com.kg.gettransfer.domain.model.Transfer
import com.kg.gettransfer.domain.model.TransferNew
import com.kg.gettransfer.domain.model.TransportType

import com.kg.gettransfer.domain.repository.TransferRepository

import java.util.Date

import org.koin.standalone.get

class TransferRepositoryImpl(
    private val factory: DataStoreFactory<TransferDataStore, TransferDataStoreCache, TransferDataStoreRemote>
) : BaseRepository(), TransferRepository {

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
        val result: ResultEntity<TransferEntity?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).createTransfer(transferNewMapper.toEntity(transferNew))
        }
        return if (result.error == null){
            result.entity?.let { factory.retrieveCacheDataStore().addTransfer(result.entity) }
            Result(result.entity?.let { transferMapper.fromEntity(it) }?: DEFAULT)
        } else Result(DEFAULT, ExceptionMapper.map(result.error))
    }

    override suspend fun cancelTransfer(id: Long, reason: String): Result<Transfer> {
        val result: ResultEntity<TransferEntity?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).cancelTransfer(id, reason)
        }
        return if (result.error == null){
            result.entity?.let { factory.retrieveCacheDataStore().addTransfer(result.entity) }
            Result(result.entity?.let { transferMapper.fromEntity(it) }?: DEFAULT)
        } else Result(DEFAULT, ExceptionMapper.map(result.error))
    }

    override suspend fun getTransfer(id: Long): Result<Transfer> {
        val result: ResultEntity<TransferEntity?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getTransfer(id)
        }
        result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().addTransfer(result.entity) }
        return if(result.error != null) Result(factory.retrieveCacheDataStore().getTransfer(id).let { transferMapper.fromEntity(it) })
        else Result(result.entity?.let { transferMapper.fromEntity(it) }?: DEFAULT)
    }

    override suspend fun getAllTransfers(): Result<List<Transfer>> {
        val result: ResultEntity<List<TransferEntity>?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getAllTransfers()
        }
        result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().addAllTransfers(result.entity) }
        return if(result.error != null) Result(factory.retrieveCacheDataStore().getAllTransfers().map { transferMapper.fromEntity(it) })
        else Result(result.entity?.map { transferMapper.fromEntity(it) }?: emptyList())
    }

    override suspend fun getTransfersArchive(): Result<List<Transfer>> {
        val result: ResultEntity<List<TransferEntity>?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getTransfersArchive()
        }
        result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().addAllTransfers(result.entity) }
        return if(result.error != null) Result(factory.retrieveCacheDataStore().getTransfersArchive().map { transferMapper.fromEntity(it) })
        else Result(result.entity?.map { transferMapper.fromEntity(it) }?: emptyList())
    }

    override suspend fun getTransfersActive(): Result<List<Transfer>> {
        val result: ResultEntity<List<TransferEntity>?> = retrieveEntity { fromRemote ->
            factory.retrieveDataStore(fromRemote).getTransfersActive()
        }
        result.entity?.let { if (result.error == null) factory.retrieveCacheDataStore().addAllTransfers(result.entity) }
        return if(result.error != null) Result(factory.retrieveCacheDataStore().getTransfersActive().map { transferMapper.fromEntity(it) })
        else Result(result.entity?.map { transferMapper.fromEntity(it) }?: emptyList())
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
                editableFields     = emptyList<String>(),
                airlineCard        = null,
                paymentPercentages = emptyList<Int>()
            )
    }
}
