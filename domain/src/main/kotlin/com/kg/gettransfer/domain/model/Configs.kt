package com.kg.gettransfer.domain.model

data class Configs(val transportTypes: Map<String, TransportType>,
                   val paypalCredentials: PaypalCredentials)

data class TransportType(val id: String, val paxMax: Int, val luggageMax: Int)
data class PaypalCredentials(val id: String, val env: String)
