package com.kg.gettransfer.domain.model

data class Configs(val transportTypes: Map<String, TransportType>)

data class TransportType(val id: String, val paxMax: Int, val luggageMax: Int)
