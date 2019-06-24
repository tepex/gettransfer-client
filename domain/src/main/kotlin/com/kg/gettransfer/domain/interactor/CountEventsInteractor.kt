package com.kg.gettransfer.domain.interactor

import com.kg.gettransfer.domain.eventListeners.CounterEventListener
import com.kg.gettransfer.domain.repository.CountEventsRepository

class CountEventsInteractor(private val countEventsRepository: CountEventsRepository) {

    var mapCountNewOffers: Map<Long, Int>
        get() = countEventsRepository.mapCountNewOffers
        set(value) { countEventsRepository.mapCountNewOffers = value }

    var mapCountNewMessages: Map<Long, Int>
        get() = countEventsRepository.mapCountNewMessages
        set(value) { countEventsRepository.mapCountNewMessages = value }

    var mapCountViewedOffers: Map<Long, Int>
        get() = countEventsRepository.mapCountViewedOffers
        set(value) { countEventsRepository.mapCountViewedOffers = value }

    var eventsCount: Int
        get() = countEventsRepository.eventsCount
        set(value) { countEventsRepository.eventsCount = value }

    fun addCounterListener(listener: CounterEventListener) { countEventsRepository.addCounterListener(listener) }
    fun removeCounterListener(listener: CounterEventListener) { countEventsRepository.removeCounterListener(listener) }

    fun clearCountEvents() {
        mapCountNewOffers = emptyMap()
        mapCountNewMessages = emptyMap()
        mapCountViewedOffers = emptyMap()
        eventsCount = 0
    }
}
