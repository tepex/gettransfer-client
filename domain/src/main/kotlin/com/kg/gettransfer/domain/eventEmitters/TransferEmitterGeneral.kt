package com.kg.gettransfer.domain.eventEmitters

interface TransferEmitterGeneral {
    fun initLocationReceiving()

    fun <P>sendClientLocation(point: P)
}