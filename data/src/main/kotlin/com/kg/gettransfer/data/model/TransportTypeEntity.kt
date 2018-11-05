package com.kg.gettransfer.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TransportTypeEntity(val id: String, val paxMax: Int, val luggageMax: Int)
