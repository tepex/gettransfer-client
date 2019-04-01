package com.kg.gettransfer.domain.repository

import com.kg.gettransfer.domain.eventListeners.CounterEventListener

interface CountEventsRepository {
    var mapCountNewOffers: Map<Long, Int>
    var mapCountNewMessages: Map<Long, Int>
    var mapCountViewedOffers: Map<Long, Int>

    var eventsCount: Int

    fun addCounterListener(listener: CounterEventListener)
    fun removeCounterListener(listener: CounterEventListener)
}