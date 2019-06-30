package com.kg.gettransfer.data.repository

import com.kg.gettransfer.data.PreferencesCache
import com.kg.gettransfer.domain.eventListeners.CounterEventListener
import com.kg.gettransfer.domain.repository.CountEventsRepository
import org.koin.core.KoinComponent
import org.koin.core.get

class CountEventsRepositoryImpl : KoinComponent, CountEventsRepository {

    private val preferencesCache = get<PreferencesCache>()

    private val countListeners = mutableSetOf<CounterEventListener>()

    override var mapCountNewOffers: Map<Long, Int>
        get() = preferencesCache.mapCountNewOffers
        set(value) {
            preferencesCache.mapCountNewOffers = value
            notifyUpdateCounter()
        }

    override var mapCountNewMessages: Map<Long, Int>
        get() = preferencesCache.mapCountNewMessages
        set(value) {
            preferencesCache.mapCountNewMessages = value
            notifyUpdateCounter()
        }

    override var mapCountViewedOffers: Map<Long, Int>
        get() = preferencesCache.mapCountViewedOffers
        set(value) {
            preferencesCache.mapCountViewedOffers = value
            notifyUpdateCounter()
        }

    override var eventsCount: Int
        get() = preferencesCache.eventsCount
        set(value) { preferencesCache.eventsCount = value }

    override fun addCounterListener(listener: CounterEventListener) { countListeners.add(listener) }

    override fun removeCounterListener(listener: CounterEventListener) { countListeners.remove(listener) }

    private fun notifyUpdateCounter() {
        countListeners.forEach { it.updateCounter() }
    }
}
