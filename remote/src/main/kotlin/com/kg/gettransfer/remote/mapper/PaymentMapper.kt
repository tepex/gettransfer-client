package com.kg.gettransfer.remote.mapper

import com.kg.gettransfer.data.model.ParamsEntity
import com.kg.gettransfer.data.model.PaymentEntity

import com.kg.gettransfer.remote.model.PaymentModel
import org.koin.standalone.get
import kotlin.UnsupportedOperationException

/**
 * Map a [PaymentModel] from an [PaymentEntity] instance when data is moving between this later and the Data layer.
 */
open class PaymentMapper : EntityMapper<PaymentModel, PaymentEntity> {

    private val paramsMapper = get<ParamsMapper>()

    override fun fromRemote(type: PaymentModel) =
            PaymentEntity(
                    type = type.type,
                    url = type.url,
                    id = type.id,
                    params = type.params?.let { paramsMapper.fromRemote(it) }
            )

    override fun toRemote(type: PaymentEntity): PaymentModel =
            throw UnsupportedOperationException()
}