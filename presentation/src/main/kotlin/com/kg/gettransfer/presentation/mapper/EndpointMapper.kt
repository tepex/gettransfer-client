package com.kg.gettransfer.presentation.mapper

import com.kg.gettransfer.domain.model.Endpoint

import com.kg.gettransfer.presentation.model.EndpointModel

open class EndpointMapper : Mapper<EndpointModel, Endpoint> {
    override fun toView(type: Endpoint) = EndpointModel(type)
    override fun fromView(type: EndpointModel): Endpoint { throw UnsupportedOperationException() }
}
